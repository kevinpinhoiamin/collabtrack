package br.com.senai.colabtrack.dao;

import java.util.List;

import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;

/**
 * Created by kevin on 22/06/17.
 */

public interface AreaSeguraMonitoradoDAO extends GenericDAO<AreaSeguraMonitorado, Long> {
    int delete(Long idAreaSegura);
    int delete(Long idAreaSegura, Long idMonitorado);
    AreaSeguraMonitorado find(Long idAreaSegura, Long idMonitorado);
    List<AreaSeguraMonitorado> findPrincipalByAreaSegura(Long idAreaSegura);
    List<AreaSeguraMonitorado> findByAreaSegura(Long idAreaSegura);
    int deleteByMonitorado(Long idMonitorado);
}
