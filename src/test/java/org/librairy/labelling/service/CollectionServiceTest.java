package org.librairy.labelling.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class CollectionServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionServiceTest.class);

    @Test
    public void permutation(){

        List<String> words = Arrays.asList(new String[]{"w1","w2","w3","w4"});

        List<String> out = CollectionsService.permute(words);

        out.forEach(w -> LOG.info(""+w));

    }

}
