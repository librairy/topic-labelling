package org.librairy.labelling.service;

import org.librairy.labelling.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TopLabellingService implements LabellingService {

    private static final Logger LOG = LoggerFactory.getLogger(TopLabellingService.class);

    @Override
    public List<String> labelsOf(Topic topic) {
        return topic.getElements().stream().map(w -> w.getValue()).limit(1).collect(Collectors.toList());
    }
}
