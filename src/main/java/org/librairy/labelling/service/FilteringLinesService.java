package org.librairy.labelling.service;

import org.librairy.labelling.client.LibrairyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class FilteringLinesService {

    private static final Logger LOG = LoggerFactory.getLogger(FilteringLinesService.class);

    private final LibrairyClient librAIryClient;


    public FilteringLinesService() {
        librAIryClient = new LibrairyClient();
    }

    public List<String> linesContaining(String text, List<String> words){

        String[] lines = text.split("\\.");
        List<String> matched = new ArrayList<>();
        for(String line: lines){
            List<String> lemmas = librAIryClient.lemmasFrom(line);
            List<String> matchedLemmas = lemmas.stream().filter(l -> words.contains(l)).distinct().collect(Collectors.toList());
            if (matchedLemmas.size() == words.size()) matched.add(line);
        }
        return matched;
    }

}
