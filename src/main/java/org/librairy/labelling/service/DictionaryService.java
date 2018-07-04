package org.librairy.labelling.service;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface DictionaryService {

    Double distance(String w1, String w2, Relation relation);

    List<String> hypernymsOf(String w);

    List<String> hyponymsOf(String w);

    public enum Relation {
        SIMILAR_TO, HYPERNYM, HYPONYM, ANTONYM, ATTRIBUTE, CATEGORY, CATEGORY_MEMBER, CAUSE, DERIVATION, USAGE, MEMBER_ALL;
    }
}
