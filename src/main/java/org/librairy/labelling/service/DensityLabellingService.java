package org.librairy.labelling.service;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
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

public class DensityLabellingService implements LabellingService {

    private static final Logger LOG = LoggerFactory.getLogger(DensityLabellingService.class);

    private final DBSCANClusterer clusterer;

    public DensityLabellingService() {
        Double eps = 0.0001;
        Integer minPts = 1;
        DistanceMeasure distanceMeasure = new MonoDimensionalDistanceMeasure();

        clusterer = new DBSCANClusterer<WordPoint>(eps,minPts,distanceMeasure);
    }

    @Override
    public List<String> labelsOf(Topic topic) {

        Integer threshold = topic.getElements().size()/5;

        List<WordPoint> points = topic.getElements().stream().map(w -> new WordPoint(w)).collect(Collectors.toList());

        List<Cluster<WordPoint>> clusterList = clusterer.cluster(points);

        List<String> labels = new ArrayList<>();

        for(Cluster<WordPoint> cluster: clusterList){
            if (cluster.getPoints().size() >= threshold) continue;
            String label = cluster.getPoints().stream().map(wp -> wp.getWord().getValue()).collect(Collectors.joining("+"));
            labels.add(label);
        }

        return labels;
    }

    private class MonoDimensionalDistanceMeasure implements DistanceMeasure{

        @Override
        public double compute(double[] doubles, double[] doubles1) {
            return Math.abs(doubles[0]-doubles1[0]);
        }
    }

    private class WordPoint implements Clusterable {

        private final Word word;
        double[] point;

        public WordPoint(Word word) {
            this.word = word;
            point = new double[1];
            point[0] = word.getScore();
        }

        @Override
        public double[] getPoint() {
            return point;
        }

        public Word getWord() {
            return word;
        }
    }
}
