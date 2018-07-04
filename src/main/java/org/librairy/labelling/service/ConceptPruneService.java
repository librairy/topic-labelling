package org.librairy.labelling.service;

import org.librairy.labelling.model.WordPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ConceptPruneService {

    private static final Logger LOG = LoggerFactory.getLogger(ConceptPruneService.class);


    public List<String> prune(Map<String,Double> concepts, List<String> context){

        SemanticSpaceService service = new SemanticSpaceService(context);

        List<WordPoint> contextPoints = service.getShapes(concepts.entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toList()));

        List<WordPoint> spacePoints = contextPoints.stream().map(p -> p.addDimension((1.0-concepts.get(p.getWord().getValue())) * 10.0)).collect(Collectors.toList());

        List<List<WordPoint>> clusters = service.getClusters(spacePoints, 2.0);

        clusters.forEach(list -> {
            LOG.debug("list -> " + list);
            LOG.debug("Centroid: " + service.getCentroid(list));
        });


        List<List<WordPoint>> sortedConcepts = clusters.stream().sorted((a, b) -> service.getCentroid(a).stream().reduce((x, y) -> x + y).get().compareTo(service.getCentroid(b).stream().reduce((x, y) -> x + y).get())).collect(Collectors.toList());

        if (sortedConcepts.isEmpty()) return Collections.emptyList();

        return sortedConcepts.get(0).stream().map(wp -> wp.getWord().getValue()).collect(Collectors.toList());
    }



}
