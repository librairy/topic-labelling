package org.librairy.labelling.model;

import org.librairy.labelling.metrics.ContentBaseRank;
import org.librairy.labelling.client.WikipediaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Candidate {

    private static final Logger LOG = LoggerFactory.getLogger(Candidate.class);

    private WikipediaClient wikipediaClient;

    String dbpediaUri;

    String wikipediaUri;

    String concept;

    String description;

    Double score = 0.0;

    public Candidate() {
        this.wikipediaClient = new WikipediaClient();
    }

    public Candidate(String dbpediaUri, String wikipediaUri, String concept, String description, Double score) {
        this.dbpediaUri = dbpediaUri;
        this.wikipediaUri = wikipediaUri;
        this.concept = concept;
        this.description = description;
        this.score = score;
        this.wikipediaClient = new WikipediaClient();
    }

    public String getDbpediaUri() {
        return dbpediaUri;
    }

    public void setDbpediaUri(String dbpediaUri) {
        this.dbpediaUri = dbpediaUri;
    }

    public String getWikipediaUri() {
        return wikipediaUri;
    }

    public void setWikipediaUri(String wikipediaUri) {
        this.wikipediaUri = wikipediaUri;
        this.description = this.wikipediaClient.getContent(wikipediaUri);
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Candidate updateScore(List<String> words){
        if (words.stream().filter(w -> w.startsWith("http")).count() > 0 ) return this;
        Double newScore = ContentBaseRank.getScore(getDescription(), words);
        if (newScore > this.score) this.score = newScore;
        return this;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "dbpediaUri='" + dbpediaUri + '\'' +
                ", wikipediaUri='" + wikipediaUri + '\'' +
                ", concept='" + concept + '\'' +
                ", score=" + score +
                '}';
    }
}
