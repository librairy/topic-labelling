package org.librairy.labelling.service;

import org.librairy.labelling.client.DBpediaClient;
import org.librairy.labelling.client.LibrairyNLPClient;
import org.librairy.labelling.model.Relevance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBpediaBasedConceptualizationService implements ConceptualizationService {

    private static final Logger LOG = LoggerFactory.getLogger(DBpediaBasedConceptualizationService.class);
    private final Integer max;
    private final DBpediaClient dBpediaClient;

    public DBpediaBasedConceptualizationService(Integer max) {
        this.max                = max;
        this.dBpediaClient      = new DBpediaClient();
    }

    @Override
    public List<String> conceptsFrom(List<Relevance<String>> words, List<Relevance<String>> contextWords) {

        if (words.isEmpty()) return Collections.emptyList();

        List<String> wordList = CollectionsService.union(words.stream().map(r -> r.getElement()).collect(Collectors.toList()), contextWords.stream().map(r -> r.getElement()).collect(Collectors.toList()));

        List<String> resources = dBpediaClient.getResourcesBy(wordList, this.max);

        if (resources.isEmpty()) return Collections.emptyList();

        return resources.stream().flatMap(r -> dBpediaClient.getSubjectsOf(r).stream()).distinct().limit(max).collect(Collectors.toList());

    }


}
