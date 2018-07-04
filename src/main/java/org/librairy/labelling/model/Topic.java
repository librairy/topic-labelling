package org.librairy.labelling.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Topic {

    private static final Logger LOG = LoggerFactory.getLogger(Topic.class);

    private String id;

    private List<String> labels;

    List<Word> elements;

    public Topic() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Word> getElements() {
        return elements;
    }

    public void setElements(List<Word> elements) {
        this.elements = elements;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id='" + id + '\'' +
                ", labels=" + labels +
                ", elements=" + elements +
                '}';
    }
}
