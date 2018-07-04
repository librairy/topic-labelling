package org.librairy.labelling.service;

import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Topic;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public interface LabellingService {

    List<Relevance<String>> labelsOf(List<Relevance<String>> words, List<String> context);

}
