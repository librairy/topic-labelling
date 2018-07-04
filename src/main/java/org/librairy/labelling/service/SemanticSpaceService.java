package org.librairy.labelling.service;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.librairy.labelling.client.LibrairyNLPClient;
import org.librairy.labelling.model.Word;
import org.librairy.labelling.model.WordPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class SemanticSpaceService {

    private static final Logger LOG = LoggerFactory.getLogger(SemanticSpaceService.class);

    private final DictionaryService dictionaryService;
    private final List<String> axisList;
    private final LibrairyNLPClient librairyNLPClient;


    public SemanticSpaceService(List<String> axisList) {
        this.dictionaryService = new WordnetService();
        this.librairyNLPClient = new LibrairyNLPClient();

        this.axisList = axisList;
    }

    public List<WordPoint> getShapes(List<String> words){
        return words.parallelStream().map(word -> getShape(word)).filter(wp -> wp.getPointAsList().stream().map(val -> val < 100).reduce((x,y) -> x && y).get()).filter(wp -> Arrays.stream(wp.getPoint()).reduce((a, b) -> a+b).getAsDouble() > 0.0).collect(Collectors.toList());
    }


    public List<List<WordPoint>> getClusters(List<WordPoint> points, Double minDistance){

        Double eps                          = minDistance; // 2.0
        Integer minPts                      = 1;

        DBSCANClusterer<WordPoint> clusterer = new DBSCANClusterer<WordPoint>(eps, minPts);

        List<Cluster<WordPoint>> clusterList = clusterer.cluster(points);

        List<List<WordPoint>> result = clusterList.stream().map(cluster -> cluster.getPoints()).collect(Collectors.toList());

        return result;
    }

    public WordPoint getShape(String word){



        //List<Double> vector = IntStream.range(0, axisList.size()).mapToDouble(i -> (Double) librairyNLPClient.lemmasFrom(text).stream().map(w -> dictionaryService.distance(w, axisList.get(i), DictionaryService.Relation.HYPERNYM)).reduce((a, b) -> (a + b) / 2.0).get()).boxed().collect(Collectors.toList());

        List<Double> vector = IntStream.range(0, axisList.size()).mapToDouble(i -> (Double) dictionaryService.distance(word, axisList.get(i), DictionaryService.Relation.HYPERNYM)).boxed().collect(Collectors.toList());

        WordPoint wordPoint = new WordPoint(new Word(word,1.0));
        wordPoint.setPoint(vector);

        return wordPoint;

    }

    public List<Double> getCentroid(List<WordPoint> list){
        return list.stream().map(wp -> wp.getPointAsList()).reduce((a,b) -> CollectionsService.sum(a,b)).get().stream().map( val -> val/Double.valueOf(list.size())).collect(Collectors.toList());
    }

}
