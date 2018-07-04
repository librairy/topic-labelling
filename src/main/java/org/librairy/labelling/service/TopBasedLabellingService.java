package org.librairy.labelling.service;

import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TopBasedLabellingService implements LabellingService {

    private static final Logger LOG = LoggerFactory.getLogger(TopBasedLabellingService.class);

    @Override
    public List<Relevance<String>> labelsOf(List<Relevance<String>> words, List<String> context) {
        return words.stream().limit(1).collect(Collectors.toList());
    }
}
