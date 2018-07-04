package org.librairy.labelling.service;

import org.junit.Test;
import org.librairy.labelling.client.LibrairyNLPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class SemanticSpaceIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(SemanticSpaceIntTest.class);

    @Test
    public void shapes(){

        SemanticSpaceService service = new SemanticSpaceService(Arrays.asList(new String[]{"light","pollution"}));

        List<String> words = Arrays.asList(new String[]{"List_of_Ace_titles_in_numeric_series"});


        for(String word : words){
            LOG.info(word + " -> " + service.getShape(word));
        }


    }

}
