package org.librairy.labelling.service;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Word;
import org.librairy.labelling.model.WordPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DensityBasedConceptualizationService implements ConceptualizationService {

    private static final Logger LOG = LoggerFactory.getLogger(DensityBasedConceptualizationService.class);

    private final DBSCANClusterer clusterer;
    private final double ratioSizePerCluster;
    private final double ratioRelevancePerCluster;

    public DensityBasedConceptualizationService(Double ratioSizePerCluster, Double ratioRelevancePerCluster, Double eps) {
        this.ratioSizePerCluster         = ratioSizePerCluster; //1.0
        this.ratioRelevancePerCluster    = ratioRelevancePerCluster; //2.5
        //Double eps                  = eps; // 0.0005
        Integer minPts                   = 1;
        DistanceMeasure distanceMeasure = new MonoDimensionalDistanceMeasure();

        clusterer = new DBSCANClusterer<WordPoint>(eps,minPts,distanceMeasure);
    }

    @Override
    public List<String> conceptsFrom(List<Relevance<String>> words, List<Relevance<String>> contextWords) {

        Double threshold = words.size()/ ratioSizePerCluster;

        List<WordPoint> points = words.stream().map(w -> new WordPoint(new Word(w.getElement(), w.getScore()))).collect(Collectors.toList());

        List<Cluster<WordPoint>> clusterList = clusterer.cluster(points);

        List<Word> candidateLabels = new ArrayList<>();

        for(Cluster<WordPoint> cluster: clusterList){
            if (cluster.getPoints().size() >= threshold) continue;
            String label = cluster.getPoints().stream().map(wp -> wp.getWord().getValue()).collect(Collectors.joining("+"));
            Double score = cluster.getPoints().stream().map(wp -> wp.getWord().getScore()).sorted((a,b) -> -a.compareTo(b)).collect(Collectors.toList()).get(0);
            candidateLabels.add(new Word(label,score));
        }

        List<String> clusterPoints = clusterList.stream().flatMap(c -> c.getPoints().stream()).map(wp -> wp.getWord().getValue()).collect(Collectors.toList());

        List<WordPoint> outliers = points.stream().filter(wp -> !clusterPoints.contains(wp.getWord().getValue())).collect(Collectors.toList());

        for(WordPoint outlier : outliers){
            candidateLabels.add(outlier.getWord());
        }


        List<Word> sortedCandidateLabels = candidateLabels.stream().sorted((a, b) -> -a.getScore().compareTo(b.getScore())).collect(Collectors.toList());

        if (sortedCandidateLabels.isEmpty()) return Collections.emptyList();

        Double scoreThreshold = sortedCandidateLabels.get(0).getScore() / ratioRelevancePerCluster;

        List<String> labels = sortedCandidateLabels.stream().filter(w -> w.getScore() >= scoreThreshold).filter(w -> w.getValue().split("\\+").length < 5).map(w -> w.getValue()).collect(Collectors.toList());

        return labels;
    }

    private class MonoDimensionalDistanceMeasure implements DistanceMeasure{

        @Override
        public double compute(double[] doubles, double[] doubles1) {
            return Math.abs(doubles[0]-doubles1[0]);
        }
    }

}
