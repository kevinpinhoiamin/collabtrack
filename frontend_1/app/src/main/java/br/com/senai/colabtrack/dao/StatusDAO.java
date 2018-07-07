package br.com.senai.colabtrack.dao;

import br.com.senai.colabtrack.domain.Status;

/**
 * Created by kevin on 8/8/17.
 */

public interface StatusDAO extends GenericDAO<Status, Long>{
    Status findByMonitorado(Long idMonitorado);
    int deleteByMonitorado(Long idMonitorado);
}
