package org.librairy.labelling.service;

import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DensityBasedLabellingService implements LabellingService {

    private static final Logger LOG = LoggerFactory.getLogger(DensityBasedLabellingService.class);

    private final DensityBasedConceptualizationService wordConceptualization;
    private final DBpediaBasedConceptualizationService dbpediaConceptualization;

    public DensityBasedLabellingService(Integer numTopics) {
        Double ratioSizePerCluster      = 1.5; // 2.0
        Double ratioRelevancePerCluster = 3.5; // 3.5
        Double eps                      = (1.0 / numTopics)/ 100.0;
        this.wordConceptualization      = new DensityBasedConceptualizationService(ratioSizePerCluster, ratioRelevancePerCluster, eps);
        this.dbpediaConceptualization   = new DBpediaBasedConceptualizationService(10);
    }

    @Override
    public List<Relevance<String>> labelsOf(List<Relevance<String>> topicWords, List<String> context) {

        // Getting list of main word-concepts
        List<String> wordConcepts = this.wordConceptualization.conceptsFrom(topicWords, Collections.EMPTY_LIST);
        LOG.info("Word-based Concepts: " + wordConcepts + " and context words: " + context);

        List<Relevance<String>> contextWords = context.stream().map(w -> new Relevance<String>(w,1.0)).collect(Collectors.toList());


        List<List<String>> plainConcepts = wordConcepts.parallelStream().map(c -> (c.contains("+") ? dbpediaConceptualization.conceptsFrom(Arrays.stream(c.split("\\+")).map(w -> new Relevance<String>(w,1.0)).collect(Collectors.toList()),contextWords) : Arrays.asList(new String[]{c}))).collect(Collectors.toList());

        List<String> wordGroups = CollectionsService.combine(plainConcepts);

        // Getting DBpedia concepts from a list of words
        // permutations?
        List<String> dbpediaConcepts = wordGroups.parallelStream().map(w -> Arrays.asList(w.split("\\+"))).flatMap(l -> dbpediaConceptualization.conceptsFrom(l.stream().map(w -> new Relevance<String>(w, 1.0)).collect(Collectors.toList()),contextWords).stream()).distinct().collect(Collectors.toList());
        LOG.info("DBpedia-based Concepts: " + dbpediaConcepts);


        return dbpediaConcepts.stream().map(w -> new Relevance<String>(w,1.0)).collect(Collectors.toList());
    }
}
