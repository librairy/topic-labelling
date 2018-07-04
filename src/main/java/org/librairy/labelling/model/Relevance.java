package org.librairy.labelling.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Relevance<T> {

    private static final Logger LOG = LoggerFactory.getLogger(Relevance.class);

    private T element;

    private Double score;

    public Relevance(T element, Double score) {
        this.element = element;
        this.score = score;
    }

    public T getElement() {
        return element;
    }

    public Double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Relevance{" +
                "element=" + element +
                ", score=" + score +
                '}';
    }
}
