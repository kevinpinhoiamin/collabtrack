package br.com.senai.colabtrack.dao;

import br.com.senai.colabtrack.domain.Monitor;

/**
 * Created by kevin on 13/06/17.
 */

public interface MonitorDAO extends GenericDAO<Monitor,Long> {
    Monitor findAutenticado();
}
