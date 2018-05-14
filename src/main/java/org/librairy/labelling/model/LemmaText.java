package org.librairy.labelling.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.librairy.labelling.Config;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.rest.model.ProcessRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class LemmaText {

    private static final Logger LOG = LoggerFactory.getLogger(LemmaText.class);

    static{
        Unirest.setDefaultHeader("Accept", "application/json");
        Unirest.setDefaultHeader("Content-Type", "application/json");

        com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jacksonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {

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

    public static String from(String raw, List<PoS> posList, String language) {
        String text = raw;
        try {
            ProcessRequest request = new ProcessRequest();
            request.setFilter(posList);
            request.setForm(Form.LEMMA);
            request.setText(text);
            String endpoint = Config.get("nlp.endpoint");
            HttpResponse<JsonNode> response = Unirest.post(endpoint).
                    body(request).
                    asJson();
            if (response.getStatus() == 200) {
                text = response.getBody().getObject().getString("processedText");
            }
        } catch (Exception e) {
            LOG.warn("Error getting lemma from NLP service",e);
        }finally {
            return text;
        }
    }

}
