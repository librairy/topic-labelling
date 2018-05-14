package org.librairy.labelling.index;

import org.junit.Test;
import org.librairy.labelling.Config;
import org.librairy.labelling.model.LemmaText;
import org.librairy.labelling.model.WikiArticle;
import org.librairy.labelling.reader.JsonlReader;
import org.librairy.labelling.service.ParallelService;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikiReadIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(WikiReadIntTest.class);

    @Test
    public void read(){

        WikiIndex wikiIndex = new WikiIndex(Config.get("index.path"));

        wikiIndex.get("house buddha", 10).forEach(article -> LOG.info("Article: " + article));



    }

}
