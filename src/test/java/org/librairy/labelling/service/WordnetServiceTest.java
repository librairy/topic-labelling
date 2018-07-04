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

public class WordnetServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetServiceTest.class);

    @Test
    public void semDistance(){

        WordnetService service = new WordnetService();
        LibrairyNLPClient librairyNLPClient = new LibrairyNLPClient();

        String w1 = "Light";
        String w2 = "Pollution";




        List<String> words = Arrays.asList(new String[]{"List_of_Ace_titles_in_numeric_series"});


        for(String word : words){
            String text = word.replace("_"," ").replace("+"," ");
            Double x = librairyNLPClient.lemmasFrom(text).stream().map(w -> service.distance(w, w1, DictionaryService.Relation.HYPERNYM)).reduce((a, b) -> (a + b) / 2.0).get();
            Double y = librairyNLPClient.lemmasFrom(text).stream().map(w -> service.distance(w, w2, DictionaryService.Relation.HYPERNYM)).reduce((a, b) -> (a + b) / 2.0).get();

            LOG.info(word + " [" + x + "," + y + "]");

        }


    }

}
