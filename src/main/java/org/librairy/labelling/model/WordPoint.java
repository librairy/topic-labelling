package org.librairy.labelling.model;

import com.google.common.primitives.Doubles;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordPoint implements Clusterable {

    private static final Logger LOG = LoggerFactory.getLogger(WordPoint.class);

    private final Word word;
    List<Double> point;

    public WordPoint(Word word) {
        this.word = word;
        point = new ArrayList<>();
        point.add(word.getScore());
    }

    public WordPoint setPoint(List<Double> point) {
        this.point = point;
        return this;
    }

    @Override
    public double[] getPoint() {
        return Doubles.toArray(point);
    }

    public List<Double> getPointAsList() {
        return point;
    }

    public Word getWord() {
        return word;
    }


    public WordPoint addDimension(Double value){
        this.point.add(value);
        return this;
    }

    @Override
    public String toString() {
        return "WordPoint{" +
                "word=" + word +
                ", point=" + point +
                '}';
    }
}
