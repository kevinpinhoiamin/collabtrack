package br.com.senai.colabtrack.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.dao.MonitoradoDAO;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.domain.MonitorMonitorado;
import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.domain.Status;
import br.com.senai.colabtrack.domain.to.DataFirebaseTO;
import br.com.senai.colabtrack.domain.to.MonitoradoTO;
import br.com.senai.colabtrack.domain.util.LocalizacaoUtil;
import br.com.senai.colabtrack.domain.util.MonitoradoUtil;
import br.com.senai.colabtrack.sql.MonitoradoSQL;

/**
 * Created by kevin on 22/06/17.
 */

public class MonitoradoService {

    private Context context;
    private MonitoradoDAO monitoradoDAO;

    public MonitoradoService(Context context){
        this.context = context;
        this.monitoradoDAO = new MonitoradoSQL(this.context);
    }

    public Long save(Monitorado monitorado){
        return monitoradoDAO.save(monitorado);
    }

    public List<Monitorado> findAll(){
        return monitoradoDAO.findAll();
    }

    public List<Monitorado> findPrincipal(){
        return monitoradoDAO.findPrincipal();
    }

    public List<Monitorado> findActives(){
        return monitoradoDAO.findActives();
    }

    public Monitorado find(Long id){
        return monitoradoDAO.find(id);
    }


    public Long update(Monitorado monitorado){
        return monitoradoDAO.update(monitorado);
    }
    public int deletarMonitorado(Long idMonitorado){
        return monitoradoDAO.deleteByMonitorado(idMonitorado);
    }

    public static void save(MonitoradoTO monitoradoTO){

        // Instância das classes de serviço
        MonitoradoService monitoradoService = new MonitoradoService(ColabTrackApplication.getContext());
        MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(ColabTrackApplication.getContext());
        StatusService statusService = new StatusService(ColabTrackApplication.getContext());
        MonitorService monitorService = new MonitorService(ColabTrackApplication.getContext());

        // Faz o cadastro do monitorado
        monitoradoService.save(monitoradoTO.getMonitorado());

        // Faz o cadastro da relação entre o monitor e o monitorado
        MonitorMonitorado monitorMonitorado = new MonitorMonitorado();
        monitorMonitorado.setAtivo(true);
        monitorMonitorado.setCor(monitoradoTO.getCor());
        monitorMonitorado.setEmMonitoramento(false);
        monitorMonitorado.setMonitor(monitorService.findAutenticado());
        monitorMonitorado.setMonitorado(monitoradoTO.getMonitorado());
        // Identifica se o monitor principal é o monitor que está autenticado
        monitorMonitorado.setPrincipal(monitoradoTO.getMonitorPrincipal().getId() == monitorMonitorado.getMonitor().getId());
        monitorMonitoradoService.save(monitorMonitorado);

        // Cadastra o status
        statusService.saveOrUpdate(monitoradoTO.getStatus());

        // Faz o broadcast das informações
        Intent intent = new Intent(ColabTrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putParcelable("monitorado_to", MonitoradoUtil.writeToParcel(monitoradoTO));
        bundle.putInt("acao", DataFirebaseTO.ACAO_CADASTRO_MONITORADO);
        bundle.putBoolean("principal", monitorMonitorado.isPrincipal());
        intent.putExtras(bundle);
        ColabTrackApplication.getContext().sendBroadcast(intent);

    }

    public static void update(MonitoradoTO monitoradoTO, Long idStatus){

        // Instância das classes de serviço
        MonitoradoService monitoradoService = new MonitoradoService(ColabTrackApplication.getContext());
        MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(ColabTrackApplication.getContext());
        MonitorService monitorService = new MonitorService(ColabTrackApplication.getContext());
        StatusService statusService = new StatusService(ColabTrackApplication.getContext());

        // Busca o monitor autenticado
        Monitor monitorAutenticado = monitorService.findAutenticado();

        // Cadastra/altera os dados do monitorado
        Monitorado monitorado = monitoradoService.find(monitoradoTO.getMonitorado().getId());
        if(monitorado != null){
            monitorado.setNome(monitoradoTO.getMonitorado().getNome());
            monitorado.setCelular(monitoradoTO.getMonitorado().getCelular());
            monitoradoService.update(monitorado);

            // Coloca o status no objeto de transferência
            monitoradoTO.setStatus(statusService.findByMonitorado(monitorado.getId()));
        }else{
            monitorado = monitoradoTO.getMonitorado();
            monitoradoService.save(monitorado);

            // Se não possui monitorado, faz o cadastro do status
            Status status = new Status();
            status.setId(idStatus);
            status.setMonitorado(monitorado);
            statusService.saveOrUpdate(status);

            monitoradoTO.setStatus(status);
        }
        monitoradoTO.setMonitorado(monitorado);

        // Cadastra/altera a relação entre o monitor e o monitorado
        MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), monitorado.getId());
        if(monitorMonitorado != null){
            monitorMonitorado.setCor(monitoradoTO.getCor());
            monitorMonitorado.setPrincipal(monitoradoTO.getMonitorPrincipal().getId() == monitorAutenticado.getId());
            monitorMonitoradoService.update(monitorMonitorado);
        }else {
            monitorMonitorado = new MonitorMonitorado();
            monitorMonitorado.setCor(monitoradoTO.getCor());
            monitorMonitorado.setPrincipal(monitoradoTO.getMonitorPrincipal().getId() == monitorAutenticado.getId());
            monitorMonitorado.setAtivo(true);
            monitorMonitorado.setEmMonitoramento(false);
            monitorMonitorado.setMonitor(monitorAutenticado);
            monitorMonitorado.setMonitorado(monitorado);
            monitorMonitoradoService.save(monitorMonitorado);
        }
        monitoradoTO.getStatus().setAtivo(monitorMonitorado.isAtivo());

        // Faz o broadcast das informações
        Intent intent = new Intent(ColabTrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putParcelable("monitorado_to", MonitoradoUtil.writeToParcel(monitoradoTO));
        bundle.putInt("acao", DataFirebaseTO.ACAO_EDICAO_MONITORADO);
        bundle.putBoolean("ativo", monitorMonitorado.isAtivo());
        bundle.putBoolean("principal", monitorMonitorado.isPrincipal());
        intent.putExtras(bundle);
        ColabTrackApplication.getContext().sendBroadcast(intent);

    }

    public static void delete(long idMonitorado){

        // Classes de serviço
        LocalizacaoService localizacaoService = new LocalizacaoService(ColabTrackApplication.getContext());
        StatusService statusService = new StatusService(ColabTrackApplication.getContext());
        MensagemService mensagemService = new MensagemService(ColabTrackApplication.getContext());
        AreaSeguraMonitoradoService areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(ColabTrackApplication.getContext());
        MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(ColabTrackApplication.getContext());
        MonitoradoService monitoradoService = new MonitoradoService(ColabTrackApplication.getContext());

        // Deleta os registros associados
        localizacaoService.deletarLocalizacoes(idMonitorado);
        statusService.deletarStatus(idMonitorado);
        mensagemService.deletarMensamgens(idMonitorado);
        areaSeguraMonitoradoService.deletarAssociacoes(idMonitorado);
        monitorMonitoradoService.deletarAssociacoes(idMonitorado);
        monitoradoService.deletarMonitorado(idMonitorado);

        // Faz o broadcast das informações
        Intent intent = new Intent(ColabTrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putLong("id_monitorado", idMonitorado);
        bundle.putInt("acao", DataFirebaseTO.ACAO_REMOCAO_MONITORADO);
        intent.putExtras(bundle);
        ColabTrackApplication.getContext().sendBroadcast(intent);
        LocalBroadcastManager.getInstance(ColabTrackApplication.getContext()).sendBroadcast(intent);

    }

}
