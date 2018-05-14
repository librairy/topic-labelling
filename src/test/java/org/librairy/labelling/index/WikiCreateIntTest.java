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

public class WikiCreateIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(WikiCreateIntTest.class);

    @Test
    public void create(){
        WikiIndex wikiIndex = new WikiIndex(Config.get("index.path"),true);

        ParallelService executor = new ParallelService();
        try {
            LOG.info("Adding wikipedia articles to the index ..");
            Path wikiDump = Paths.get(Config.get("corpus.path"));
            JsonlReader<WikiArticle> readerTrain = new JsonlReader(wikiDump.toFile(), WikiArticle.class);
            Optional<WikiArticle> article;
            AtomicInteger counter = new AtomicInteger();
            Integer numArticles = Integer.valueOf(Config.get("max.articles"));
            while((numArticles<0 || counter.get()<numArticles) && (article = readerTrain.next()).isPresent()){
                if (counter.incrementAndGet() % 100 == 0) {
                    LOG.info(counter.get() + " articles added");
                    Thread.currentThread().sleep(20);
                }
                final WikiArticle currentArticle = article.get();
                executor.execute(() -> {

                    // lemmatize
                    String text = LemmaText.from(currentArticle.getText(), Arrays.asList( new PoS[]{PoS.ADVERB, PoS.VERB, PoS.NOUN, PoS.ADJECTIVE}), "en");

                    WikiArticle customWikiArticle = new WikiArticle();
                    customWikiArticle.setTitle(currentArticle.getTitle());
                    customWikiArticle.setId(currentArticle.getId());
                    customWikiArticle.setUrl(currentArticle.getUrl());
                    customWikiArticle.setText(text);

                    // index
                    wikiIndex.add(customWikiArticle);

                });
            }
            executor.pause();
            wikiIndex.close();
            LOG.info("corpus indexed!");

        } catch (Exception e) {
            LOG.error("Unexpected error in evaluation",e);
        }
    }

}
