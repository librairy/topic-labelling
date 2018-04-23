package org.librairy.labelling.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.librairy.service.modeler.facade.rest.model.Inference;
import org.librairy.service.modeler.facade.rest.model.InferenceRequest;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.rest.model.ProcessRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class LibrairyClient {

    private static final Logger LOG = LoggerFactory.getLogger(LibrairyClient.class);

    static{
        Unirest.setDefaultHeader("Accept", "application/json");
        Unirest.setDefaultHeader("Content-Type", "application/json");

        com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jacksonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Unirest.setObjectMapper(new ObjectMapper() {

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Inference inferTopicsFrom(String text){
        InferenceRequest request = new InferenceRequest();
        request.setText(text);
        try {
            HttpResponse<Inference> response = Unirest.post("http://librairy.linkeddata.es/20news-model/inference").body(request).asObject(Inference.class);

            Inference inference = response.getBody();
            return inference;

        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> lemmasFrom(String text){
        ProcessRequest request = new ProcessRequest();
        request.setText(text);
        request.setForm(Form.LEMMA);
        request.setFilter(Collections.emptyList());
        try {
            HttpResponse<JsonNode> response = Unirest.post("http://librairy.linkeddata.es/nlp-en/process").body(request).asJson();
            String lemmas = response.getBody().getObject().getString("processedText");
            return Arrays.asList(lemmas.split(" "));
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

}
