package br.senai.collabtrack.service.mapa;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.VolleyError;

import java.util.List;
import java.util.Map;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.domain.Chat;
import br.senai.collabtrack.domain.Localizacao;
import br.senai.collabtrack.domain.Mensagem;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.to.RespostaTO;
import br.senai.collabtrack.domain.util.LocalizacaoUtil;
import br.senai.collabtrack.request.CadastrarAvisoAreaSeguraRequest;
import br.senai.collabtrack.request.HttpCallback;
import br.senai.collabtrack.service.AreaSeguraService;
import br.senai.collabtrack.service.LocalizacaoService;
import br.senai.collabtrack.service.MensagemService;
import br.senai.collabtrack.service.MonitorMonitoradoService;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.util.ColorUtil;
import br.senai.collabtrack.util.NotificationUtil;

/**
 * Created by kevin on 8/22/17.
 */

public class MapAreaService {

    /**
     * Método responsável por verificar se o monitorado está fora de todas suas áreas seguras
     * @param localizacao Instância do objeto com a localização do monitrado
     * @param areaSeguraList Lista das áreas seguras do monitorado
     * @return boolean TRUE se estiver fora de todas as áreas seguras
     */
    public static boolean foraDasAreasSeguras(Localizacao localizacao, List<AreaSegura> areaSeguraList){

        // Para cada área segura
        if(areaSeguraList != null && areaSeguraList.size() > 0){

            int count = 0;
            int countAreasMonitorado = 0;

            for(AreaSegura areaSegura : areaSeguraList){

                // Localização do monitorado
                Location location1 = new Location("");
                location1.setLatitude(localizacao.getLatitude());
                location1.setLongitude(localizacao.getLongitude());
                // Localização da área segura
                Location location2 = new Location("");
                location2.setLatitude(areaSegura.getLatitude());
                location2.setLongitude(areaSegura.getLongitude());

                // Contador com o total de áreas seguras do monitorado
                countAreasMonitorado++;

                // Se estiver fora
                if(location1.distanceTo(location2) > areaSegura.getRaio()){
                    count++;
                }

            }

            // Se o usuário está fora de todas as áreas seguras
            return count == countAreasMonitorado;

        } else {
            return false;
        }

    }

    /**
     * Remove as áreas segura do mapa
     * @param areasSegurasRemovidas Lista de áreas seguras que serão removidas do mapa
     * @param mapAreas Lista das áreas seguras que estão sendo exibidas no mapa
     * @return Áreas seguras do mapa sem as áreas seguras que foram removidas
     */
    public static Map<String, MapArea> removerAreasSeguras(List<AreaSegura> areasSegurasRemovidas, Map<String, MapArea> mapAreas){

        for (AreaSegura areaSegura : areasSegurasRemovidas){
            String circle_id = "";
            for (MapArea mapArea : mapAreas.values()){
                if(mapArea.getAreaSegura().getId() == areaSegura.getId()){
                    circle_id = mapArea.getCircle().getId();
                    mapArea.getCircle().remove();
                    mapArea.getMarker().remove();
                }
            }
            if(circle_id != ""){
                mapAreas.remove(circle_id);
            }
        }

        return mapAreas;

    }

    /**
     * Edita as áreas seguras do mapa
     * @param areaSegurasEditadas Lista de áreas seguras que terão seu dados editados
     * @param mapAreas Lista das áreas seguras que estão sendo exibidas no mapa
     * @return Áreas seguras do mapa sem as áreas seguras que foram removidas
     */
    public static Map<String, MapArea> editarAreasSeguras(List<AreaSegura> areaSegurasEditadas, Map<String, MapArea> mapAreas){

        for (AreaSegura areaSegura : areaSegurasEditadas){
            String circle_id = "";
            for (MapArea mapArea : mapAreas.values()){
                if(mapArea.getAreaSegura().getId() == areaSegura.getId()){
                    circle_id = mapArea.getCircle().getId();
                    mapArea.getMarker().setTitle(areaSegura.getNome());
                    mapArea.getCircle().setRadius(areaSegura.getRaio());
                    mapArea.getCircle().setFillColor(ColorUtil.getOpacityColor(areaSegura.getCor()));
                    mapArea.getCircle().setStrokeColor(areaSegura.getCorBorda());

                    MapArea editedMapArea = new MapArea(mapArea.getCircle(), mapArea.getMarker(), areaSegura, mapArea.getAreaSeguraMonitorados());
                    mapAreas.put(circle_id, editedMapArea);
                }
            }
        }

        return mapAreas;

    }

    /**
     * Método responsável por verificar s a localização é a última localização do monitorado
     * @param localizacao Instância da localização do monitorado
     * @param localizacaoList Lista de localizações
     * @return TRUE, caso seja a última localização do monitorado na lista, FALSE caso contrário
     */
    public static boolean ehUltimaLocalizacao(Localizacao localizacao, List<Localizacao> localizacaoList){

        if(localizacao == null || localizacaoList == null || localizacaoList.size() == 0){
            return false;
        }

        long ultimoID = 0;
        for(Localizacao localizacaoIteracao : localizacaoList){

            if(localizacao.getMonitorado().getId() == localizacaoIteracao.getMonitorado().getId()){
                ultimoID = localizacaoIteracao.getId() > ultimoID ? localizacaoIteracao.getId() : ultimoID;
            }

        }

        return localizacao.getId() == ultimoID;

    }

    /**
     * Método que remove os marcadores de todas as localizações do monitorado no mapa
     * @param mapMonitorado Instância da lolização do monitorado no mapa
     * @param mapLocalizacoes Map com todas as licalizações do mapa
     */
    public static void removerMarkers(MapMonitorado mapMonitorado, Map<String, MapMonitorado> mapLocalizacoes, Localizacao ultimaLocalizacao){

        if (mapMonitorado != null && mapLocalizacoes != null && mapLocalizacoes.size() > 0) {

            for(Map.Entry<String, MapMonitorado> entry : mapLocalizacoes.entrySet()){
                MapMonitorado mapMonitoradoIteracao = entry.getValue();
                if(
                        mapMonitorado.getLocalizacao().getMonitorado().getId() == mapMonitoradoIteracao.getLocalizacao().getMonitorado().getId() &&
                        ultimaLocalizacao.getId() != mapMonitoradoIteracao.getLocalizacao().getId() &&
                        mapMonitoradoIteracao.getMarker() != null
                ){
                    mapMonitoradoIteracao.getMarker().remove();
                    mapMonitoradoIteracao.setMarker(null);
                    mapLocalizacoes.put(entry.getKey(), mapMonitoradoIteracao);
                }
            }
        }

    }

    /**
     * Método que faz o cadastro da localização, verifica se o monitorado está fora, imprime a notificação e manda o broadcast para o mapa
     * @param localizacao Instância da localização do monitorado
     */
    public static void cadastrarLocalizacao(Localizacao localizacao){

        // Classe de serviço
        LocalizacaoService localizacaoService = new LocalizacaoService(CollabtrackApplication.getContext());
        AreaSeguraService areaSeguraService = new AreaSeguraService(CollabtrackApplication.getContext());
        MonitorService monitorService = new MonitorService(CollabtrackApplication.getContext());
        MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(CollabtrackApplication.getContext());
        final MensagemService mensagemService = new MensagemService(CollabtrackApplication.getContext());

        // Busca o monitorado
        MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorService.findAutenticado().getId(), localizacao.getMonitorado().getId());

        // Busca a última mensagem
        Mensagem ultimaMensagem = mensagemService.findUltimaMensagem(localizacao.getMonitorado().getId());

        // Salva a localização
        localizacaoService.save(localizacao);
        localizacao.setCor(monitorMonitorado.getCor());

        // Verifica se está dentro ou fora das áreas seguras e se a última mensagem não é uma mensagem do mesmo tipo
        if(
                monitorMonitorado.isPrincipal() &&
                foraDasAreasSeguras(localizacao, areaSeguraService.findAtivasByMonitorado(monitorMonitorado.getMonitorado().getId())) &&
                (ultimaMensagem == null || ultimaMensagem.getTipo() != Chat.TIPO_AREA_SEGURA)
        ) {
            NotificationUtil.showNotification(CollabtrackApplication.getContext().getString(R.string.aviso), monitorMonitorado.getMonitorado().getNome() + " " + CollabtrackApplication.getContext().getString(R.string.fora_de_todas_area_seguras));

            Monitor monitorMsg = monitorMonitorado.getMonitor();
            Monitorado monitoradoMsg = monitorMonitorado.getMonitorado();

            final RespostaTO respostaTO = new RespostaTO();
            final Mensagem mensagem = new Mensagem();
            respostaTO.setId(monitoradoMsg.getId());
            respostaTO.setResposta(true);

            mensagem.setMensagem("Fora da Área Segura");
            mensagem.setTipo(Chat.TIPO_AREA_SEGURA);
            mensagem.setMonitor(monitorMsg);
            mensagem.setMonitorado(monitoradoMsg);
            mensagem.setData(localizacao.getData());

            new CadastrarAvisoAreaSeguraRequest(respostaTO, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    mensagemService.save(mensagem);
                    Intent it = new Intent("REFRESH_CHAT");
                    LocalBroadcastManager.getInstance(CollabtrackApplication.getContext()).sendBroadcast(it);
                }

                @Override
                public void onError(VolleyError error) {
                }
            }).execute();
        }
        // Dispara o broadcast para o mapa com a localização
        Intent intent = new Intent(CollabtrackApplication.LOCALIZACAO_BROADCAST_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putParcelable("localizacao", LocalizacaoUtil.writeToParcel(localizacao));
        intent.putExtras(bundle);
        CollabtrackApplication.getContext().sendBroadcast(intent);
    }

}
