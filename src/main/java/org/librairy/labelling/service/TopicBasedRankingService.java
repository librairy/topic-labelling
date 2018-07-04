package org.librairy.labelling.service;

import com.google.common.base.Strings;
import org.librairy.labelling.client.LibrairyModelClient;
import org.librairy.labelling.client.WikipediaClient;
import org.librairy.labelling.model.Relevance;
import org.librairy.service.modeler.facade.rest.model.Inference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TopicBasedRankingService {

    private static final Logger LOG = LoggerFactory.getLogger(TopicBasedRankingService.class);

    private final LibrairyModelClient librairyClient;
    private final WikipediaClient wikipediaClient;


    public TopicBasedRankingService(String modelEndpoint) {
        this.librairyClient     = new LibrairyModelClient(modelEndpoint);
        this.wikipediaClient    = new WikipediaClient();
    }

    public List<Relevance<String>> sortBy(List<Relevance<String>> topicWords, List<Integer> refTopics) {


        List<Relevance<String>> labels = new ArrayList<>();

        for(Relevance<String> word: topicWords){

            // Getting wikipedia content
            String label    = word.getElement();
            String uri      = this.wikipediaClient.getUri(label);
            String content  = this.wikipediaClient.getContent(uri);

            if (!Strings.isNullOrEmpty(content)){
                Inference topicDistribution = this.librairyClient.inferTopicsFrom(content);

                Double accScore = refTopics.stream().map(i -> topicDistribution.getDimensions().get(i).getScore()).reduce((a, b) -> a + b).get();

                labels.add(new Relevance<String>(label,accScore));
            }
        }

        return labels.stream().sorted((a,b) -> -a.getScore().compareTo(b.getScore())).collect(Collectors.toList());
    }
}
