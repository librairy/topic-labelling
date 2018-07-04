package org.librairy.labelling.service;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WordnetService implements DictionaryService {

    private static final Logger LOG = LoggerFactory.getLogger(WordnetService.class);

    private final Dictionary dictionary;


    public WordnetService() {

        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Double distance(String w1, String w2, Relation relation) {


        String[] w1parts = w1.replace("_", " ").replace("+", " ").split(" ");
        String[] w2parts = w2.replace("_", " ").replace("+", " ").split(" ");

        Double distance = 0.0;

        for(String x : w1parts){
            for(String y: w2parts){
              distance += unigramDistance(x,y,relation);
            }
        }

        return distance / w1parts.length;

    }

    private Double unigramDistance(String w1, String w2, Relation relation) {

        Optional<IndexWord> iword1 = getWord(w1);
        Optional<IndexWord> iword2 = getWord(w2);

        if (!iword1.isPresent() || !iword2.isPresent()) return Double.MAX_VALUE;

        try {
            RelationshipList list = RelationshipFinder.findRelationships(iword1.get().getSenses().get(0), iword2.get().getSenses().get(0), pointerFrom(relation));

            if (list.isEmpty()) return Double.MAX_VALUE;


            Integer min = list.stream().map(rel -> rel.getDepth()).reduce((a, b) -> (a < b) ? a : b).get();
            return Double.valueOf(min);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        return Double.MAX_VALUE;

    }

    @Override
    public List<String> hypernymsOf(String w) {
        return null;
    }

    @Override
    public List<String> hyponymsOf(String w) {
        return null;
    }

    private Optional<IndexWord> getWord(String word){
        List<POS> posList = POS.getAllPOS();

        for(POS pos : posList){
            try {
                IndexWord indexWord = dictionary.getIndexWord(pos, word);
                if (indexWord == null) continue;
                return Optional.of(indexWord);
            } catch (JWNLException e) {
                LOG.warn("Word not found: '" + word + "'");
            }
        }

        return Optional.empty();
    }

    private PointerType pointerFrom(Relation relation){

        switch (relation){
            case HYPERNYM: return PointerType.HYPERNYM;
            case HYPONYM: return PointerType.HYPONYM;
            case SIMILAR_TO: return PointerType.SIMILAR_TO;
            case ANTONYM: return PointerType.ANTONYM;
            case ATTRIBUTE: return PointerType.ATTRIBUTE;
            case CATEGORY: return PointerType.CATEGORY;
            case CATEGORY_MEMBER: return PointerType.CATEGORY_MEMBER;
            case CAUSE: return PointerType.CAUSE;
            case DERIVATION: return PointerType.DERIVATION;
            case USAGE: return PointerType.USAGE;
            case MEMBER_ALL: return PointerType.MEMBER_ALL;
            default: return PointerType.HYPERNYM;
        }
    }
}
