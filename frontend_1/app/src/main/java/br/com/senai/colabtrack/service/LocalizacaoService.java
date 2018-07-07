package br.com.senai.colabtrack.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import br.com.senai.colabtrack.dao.LocalizacaoDAO;
import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.Localizacao;
import br.com.senai.colabtrack.sql.LocalizacaoSQL;

/**
 * Created by kevin on 8/11/17.
 */

public class LocalizacaoService {

    private Context context;
    private LocalizacaoDAO localizacaoDAO;

    public LocalizacaoService(Context context){
        this.context = context;
        localizacaoDAO = new LocalizacaoSQL(this.context);
    }

    public Long save(Localizacao localizacao){
        return localizacaoDAO.save(localizacao);
    }

    public List<Localizacao> findAll(){
        return localizacaoDAO.findAll();
    }

    public List<Localizacao> findActives(){
        return localizacaoDAO.findActives();
    }

    public void deletarLocalizacoes(){
        localizacaoDAO.deletarLocalizacoes();
    }

    public void deletarLocalizacoes(long idMonitorado){
        localizacaoDAO.deletarLocalizacoes(idMonitorado);
    }

    public List<Localizacao> findByMonitorado(long idMonitorado){
        return localizacaoDAO.findByMonitorado(idMonitorado);
    }

    public Localizacao find(long id){
        return localizacaoDAO.find(id);
    }

    public List<Localizacao> findAll(List<Localizacao> localizacaosEncontradas, int periodo, int pontos) {

        List<Localizacao> localizacaos = new ArrayList<>();
        if (localizacaosEncontradas != null && localizacaosEncontradas.size() > 0) {

            Map<Long, Integer> localizacoesPorMonitorado = new HashMap<>();

            // Para cada localização encontrada
            ListIterator<Localizacao> iterator = localizacaosEncontradas.listIterator();
            while (iterator.hasNext()) {

                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                Localizacao localizacao = (Localizacao) iterator.next();

                // Se o monitorado não está no HashMap
                // Conta quantas localizações foram encontradas para cada monitorado
                if (!localizacoesPorMonitorado.containsKey(localizacao.getMonitorado().getId())) {
                    localizacoesPorMonitorado.put(localizacao.getMonitorado().getId(), 1);
                } else {
                    localizacoesPorMonitorado.put(localizacao.getMonitorado().getId(), localizacoesPorMonitorado.get(localizacao.getMonitorado().getId()) + 1);
                }

            }

            Iterator it = localizacoesPorMonitorado.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                long idMonitorado = Long.parseLong(String.valueOf(pair.getKey()));
                int pontosMonitorado = pontos - Integer.parseInt(String.valueOf(pair.getValue()));
                localizacaos.addAll(this.localizacaoDAO.findAll(idMonitorado, periodo, pontosMonitorado));
                it.remove(); // avoids a ConcurrentModificationException
            }

        }

        return localizacaos;

    }

}
