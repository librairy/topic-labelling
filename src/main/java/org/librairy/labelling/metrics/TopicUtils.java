package org.librairy.labelling.metrics;

import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Topic;
import org.librairy.labelling.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TopicUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TopicUtils.class);


    public static Integer multiplier(Integer numTopics){

        int length = String.valueOf(numTopics).length();

        int ratio = 3;

        double value = Math.pow(10.0, Double.valueOf(length+ratio));

        return Double.valueOf(value).intValue();
    }

    public static Double similarity(List<Word> tw1, List<Word> tw2){
        List<Double> w1 = tw1.stream().sorted((a, b) -> a.getValue().compareTo(b.getValue())).map(w -> w.getScore()).collect(Collectors.toList());
        List<Double> w2 = tw2.stream().sorted((a, b) -> a.getValue().compareTo(b.getValue())).map(w -> w.getScore()).collect(Collectors.toList());
        return JensenShannon.similarity(w1,w2);
    }

    public static Double similarity(Topic t1, Topic t2){
        List<Word> w1 = new ArrayList<>(t1.getElements());
        List<Word> w2 = new ArrayList<>(t2.getElements());

        List<Word> tw1addons = w2.stream().filter(w -> !w1.contains(w)).collect(Collectors.toList());
        List<Word> tw2addons = w1.stream().filter(w -> !w2.contains(w)).collect(Collectors.toList());

        LOG.debug("t1.size/addons: " + t1.getElements().size() +  "/" + tw1addons.size() + " , t2.size/addons: " + t2.getElements().size() + "/" + tw2addons.size());

        w1.addAll(tw1addons);
        w2.addAll(tw2addons);


        return similarity(w1, w2);

    }

}
