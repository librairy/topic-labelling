package org.librairy.labelling.client;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBPediaIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(DBPediaIntTest.class);

    private DBpediaClient client;


    @Before
    public void setup(){
        this.client = new DBpediaClient();
    }


    @Test
    public void facets(){

        List<String> labels = Arrays.asList(new String[]{"datum","Diabetes"});

        List<String> entities = client.getResourcesBy(labels, 5);

        for(String entity: entities){
            LOG.info("Entity: " + entity);
            List<String> subjects = client.getSubjectsOf(entity);
            for (String subject: subjects){
                LOG.info("\t -> " + subject);
            }
        }



    }

}
