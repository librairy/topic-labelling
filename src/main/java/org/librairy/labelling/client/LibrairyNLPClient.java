package org.librairy.labelling.client;

import com.google.common.base.CharMatcher;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.rest.model.AnnotationsRequest;
import org.librairy.service.nlp.facade.rest.model.TokensRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class LibrairyNLPClient extends HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(LibrairyNLPClient.class);

    private final String endpoint       = "http://librairy.linkeddata.es/en";

    public List<String> lemmasFrom(String text){
        TokensRequest request = new TokensRequest();
        request.setText(text);
        request.setForm(Form.LEMMA);
        request.setFilter(Arrays.asList(new PoS[]{PoS.NOUN, PoS.ADJECTIVE, PoS.VERB}));
        try {
            HttpResponse<JsonNode> response = Unirest.post(endpoint+"/tokens").body(request).asJson();
            String lemmas = response.getBody().getObject().getString("tokens");
            return Arrays.stream(lemmas.split(" ")).filter(w -> CharMatcher.javaLetterOrDigit().matchesAnyOf(w)).collect(Collectors.toList());
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

}
