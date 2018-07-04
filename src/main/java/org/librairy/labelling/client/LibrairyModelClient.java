package org.librairy.labelling.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.librairy.labelling.model.Topic;
import org.librairy.labelling.model.Word;
import org.librairy.service.modeler.facade.rest.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class LibrairyModelClient extends HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(LibrairyModelClient.class);

    private final String endpoint;

    private static final String user    = System.getenv("LIBRAIRY_USER");

    private static final String pwd     = System.getenv("LIBRAIRY_PWD");

    public LibrairyModelClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public Inference inferTopicsFrom(String text){
        InferenceRequest request = new InferenceRequest();
        request.setText(text);
        try {
            HttpResponse<Inference> response = Unirest.post(endpoint+"/inference").basicAuth(user,pwd).body(request).asObject(Inference.class);

            Inference inference = response.getBody();
            return inference;

        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Topic> getTopics(Integer maxWords){
        try {
            HttpResponse<DimensionList> response = Unirest.get(endpoint+"/dimensions").basicAuth(user,pwd).asObject(DimensionList.class);

            if (response.getStatus() != 200 ){
                LOG.warn("Error getting topic list");
                return Collections.emptyList();
            }
            List<Topic> topics = new ArrayList<>();
            DimensionList dimensionList = response.getBody();
            for(Dimension dimension : dimensionList.getDimensions()){
                Topic topic = new Topic();
                topic.setId(String.valueOf(dimension.getId()));
                topic.setLabels(Arrays.asList(dimension.getName()));

                HttpResponse<ElementList> respElements = Unirest.get(endpoint+"/dimensions/"+ dimension.getId()+"?maxWords="+maxWords).basicAuth(user,pwd).asObject(ElementList.class);

                if (respElements.getStatus() != 200 ){
                    LOG.warn("Error getting topic elements");
                    return Collections.emptyList();
                }

                List<Word> words = respElements.getBody().getElements().stream().map(el -> new Word(el.getValue(), el.getScore())).collect(Collectors.toList());
                topic.setElements(words);
                topics.add(topic);
            }
            return topics;

        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
