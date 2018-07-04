package org.librairy.labelling.service;

import org.junit.Test;
import org.librairy.labelling.client.DBpediaClient;
import org.librairy.labelling.client.LibrairyModelClient;
import org.librairy.labelling.client.WikipediaClient;
import org.librairy.labelling.metrics.TopicUtils;
import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.Topic;
import org.librairy.labelling.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TopicLabelingIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(TopicLabelingIntTest.class);

    @Test
    public void execute(){


//        String endpoint = "http://librairy.linkeddata.es/meteor-topics";
        String endpoint = "http://librairy.linkeddata.es/lightpollution-model";
//        String endpoint = "http://librairy.linkeddata.es/meteor-topics";

        LibrairyModelClient librairyClient                      = new LibrairyModelClient(endpoint);
        WikipediaClient wikipediaClient                         = new WikipediaClient();
        DBpediaClient dBpediaClient                             = new DBpediaClient();

        TopicBasedRankingService rankingService                 = new TopicBasedRankingService(endpoint);
        ConceptPruneService conceptPruneService                 = new ConceptPruneService();


        List<Topic> topics = librairyClient.getTopics(50);

        DensityBasedLabellingService densityLabelingService     = new DensityBasedLabellingService(topics.size());

        List<String> context = Arrays.asList(new String[]{"light","pollution"});

        for(Topic topic: topics){

            List<Word> topTopics = IntStream.range(0,topics.size()-1).mapToObj(i -> new Word(String.valueOf(i),TopicUtils.similarity(topic, topics.get(i)))).sorted((a, b) -> -a.getScore().compareTo(b.getScore())).limit(topics.size()/3).collect(Collectors.toList());

            LOG.info("Topic-"+topic.getId()+" : " + topic.getElements().stream().limit(10).collect(Collectors.toList()));

            List<Relevance<String>> topicWords  = topic.getElements().stream().map(w -> new Relevance<String>(w.getValue(), w.getScore())).collect(Collectors.toList());

            List<Relevance<String>> labels      = densityLabelingService.labelsOf(topicWords, context);

            List<Relevance<String>> topicLabels = rankingService.sortBy(labels, topTopics.stream().map(w -> Integer.valueOf(w.getValue())).collect(Collectors.toList()));

            Map<String,Double> topicConcepts = new HashMap<>();
            for (Relevance<String> label: topicLabels){
                LOG.info("\t ["+label.getScore()+"] - '" + label.getElement()+"' ("+dBpediaClient.getURI(label.getElement())+" / " + wikipediaClient.getUri(label.getElement()) + " )");
                topicConcepts.put(label.getElement(), label.getScore());
            }

            LOG.info(" Topic Concepts by Context: " + context);
            LOG.info("\t - " + conceptPruneService.prune(topicConcepts, context));

            List<String> topWordsContext = topic.getElements().subList(0, 1).stream().map(w -> w.getValue()).collect(Collectors.toList());
            LOG.info(" Topic Concepts by TopWord Context: " + topWordsContext);
            LOG.info("\t - " + conceptPruneService.prune(topicConcepts, topWordsContext));





        }


    }

}
