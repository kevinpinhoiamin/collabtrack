package br.senai.collabtrack.dao;

import br.senai.collabtrack.domain.Monitor;

/**
 * Created by kevin on 13/06/17.
 */

public interface MonitorDAO extends GenericDAO<Monitor,Long> {
    Monitor findAutenticado();
}
