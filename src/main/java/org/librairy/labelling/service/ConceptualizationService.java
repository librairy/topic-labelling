package org.librairy.labelling.service;

import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Topic;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public interface ConceptualizationService {

    List<String> conceptsFrom(List<Relevance<String>> words, List<Relevance<String>> context );

}
