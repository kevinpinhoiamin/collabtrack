package br.senai.collabtrack.dao;

import java.util.List;

import br.senai.collabtrack.domain.AreaSegura;

/**
 * Created by kevin on 22/06/17.
 */

public interface AreaSeguraDAO extends GenericDAO<AreaSegura, Long>{
    List<AreaSegura> findByMonitorado(Long idMonitorado);
    List<AreaSegura> findAtivasByMonitorado(Long idMonitorado);
    List<AreaSegura> search(String search);
}
