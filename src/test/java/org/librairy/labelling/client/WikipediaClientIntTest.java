package org.librairy.labelling.client;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikipediaClientIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(WikipediaClientIntTest.class);

    @Test
    public void concept(){

        String uri = "http://dbpedia.org/resource/Category:Night_sky";

        WikipediaClient client = new WikipediaClient();

        Double score = client.getScore(uri, Arrays.asList("sky", "night", "brightness", "model", "datum"));

        LOG.info("score: " + score);

    }

    @Test
    public void content(){
        String uri = "https://en.wikipedia.org/wiki/Sky_brightness";
        String content = new WikipediaClient().getContent(uri);
        LOG.info("Content: " + content);
    }
}
