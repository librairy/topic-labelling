package org.librairy.labelling.metrics;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ContentBaseRank {

    private static final Logger LOG = LoggerFactory.getLogger(ContentBaseRank.class);


    public static Double getScore(String text, List<String> words){

        List<String> refWords = words.stream().map(w -> w.toLowerCase()).collect(Collectors.toList());

        if (Strings.isNullOrEmpty(text)) return 0.0;

        String[] wordList = text.split(" ");

        List<String> containedWords = Arrays.stream(wordList).filter(word -> refWords.contains(word.toLowerCase())).collect(Collectors.toList());

        Double presenceRatio    = Double.valueOf(containedWords.size()) / Double.valueOf(wordList.length) ;

        Double scopeRatio       = Double.valueOf(containedWords.stream().distinct().count()) / Double.valueOf(refWords.size()) ;

        if (scopeRatio < 0.20) return 0.0;

        Double alpha = 0.2;

        return alpha*presenceRatio + ((1-alpha) * scopeRatio);
    }
}
