package org.librairy.labelling.client;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBpediaClient extends HttpClient{

    private static final Logger LOG = LoggerFactory.getLogger(DBpediaClient.class);

    private static final String endpoint = "http://dbpedia.org/sparql";

    private final LoadingCache<String, List<String>> facetsCache;
    private final LoadingCache<String, List<String>> subjectsCache;
    private final LibrairyNLPClient librairyNLPClient;

    public DBpediaClient() {
        this.librairyNLPClient = new LibrairyNLPClient();
        this.facetsCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<String, List<String>>() {
                            public List<String> load(String words) {
                                return findResourcesBy(words,5);
                            }
                        });

        this.subjectsCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<String, List<String>>() {
                            public List<String> load(String id) {
                                return findSubjectsOf(id);
                            }
                        });

    }

    public List<String> getResourcesBy(List<String> words, Integer max){

        if (words.isEmpty() || max < 1) return Collections.emptyList();

        try{
            return this.facetsCache.get(words.stream().collect(Collectors.joining(" ")));
        } catch (ExecutionException e) {
            LOG.warn("Error getting concepts from DBpedia: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> getSubjectsOf(String id) {
        if (Strings.isNullOrEmpty(id)) return Collections.emptyList();

        try {
            return this.subjectsCache.get(id);
        } catch (ExecutionException e) {
            LOG.warn("Error getting concepts from DBpedia: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public String getURI(String subject){
        return subject.startsWith("http:")? subject : "http://dbpedia.org/resource/Category:"+ subject;
    }

    public String getLabel(String uri){
        return uri.contains("Category:")? StringUtils.substringAfterLast(uri, "Category:") : StringUtils.substringAfterLast(uri, "resource/");
    }

    private List<String> findResourcesBy(String words, Integer max){

        if (Strings.isNullOrEmpty(words)) return Collections.emptyList();

        if (words.split("\\s+").length > 10) return Collections.emptyList();

        List<String> tokens = librairyNLPClient.lemmasFrom(words.replace("_", " ").replace("+"," "));

        String condition    = tokens.stream().flatMap(w -> Arrays.stream(w.split("_"))).map(w -> w.toUpperCase()).filter(w -> !w.equalsIgnoreCase("AND")).filter(w -> !w.equalsIgnoreCase("IN")).collect(Collectors.joining(" AND "));

        String sparqlQuery =
                "prefix bif: <bif:> \n"+
                        "prefix sql: <sql:> \n"+
                        "prefix virtrdf: <http://www.openlinksw.com/schemas/virtrdf#> \n"+
                        "select ?s1 ?sc \n" +
                        "where {" +
                        "{" +
                        "{ " +
                        "select ?s1 (?sc * 3e-1) as ?sc ?o1 (sql:rnk_scale (<LONG::IRI_RANK> (?s1))) as ?getScore ?g where  \n" +
                        "  { \n" +
                        "    quad map virtrdf:DefaultQuadMap \n" +
                        "    { \n" +
                        "      graph ?g \n" +
                        "      { \n" +
                        "         ?s1 ?s1textp ?o1 .\n" +
                        "        ?o1 bif:contains  '("+condition+")'  option (score ?sc)  .\n" +
                        "        \n" +
                        "      }\n" +
                        "     }\n" +
                        "    \n" +
                        "  }\n" +
                        " order by desc (?sc * 3e-1 + sql:rnk_scale (<LONG::IRI_RANK> (?s1))) offset 0 }}} ";


        // http://vos.openlinksw.com/owiki/wiki/VOS/VirtTipsAndTricksGuideBIFContainsOptions

        List<String> entities = new ArrayList<>();
        String queryURI = null;
        try {
            //queryURI = URLEncoder.encode(sparqlQuery, "utf-8");
            queryURI = URLEncoder.encode(sparqlQuery, CharEncoding.ISO_8859_1);
            HttpResponse<JsonNode> response = Unirest.get(endpoint + "?default-graph-uri=&query=" + queryURI).asJson();
            if (response.getStatus() != 200){
                LOG.warn("No response from DBpedia");
                return Collections.emptyList();
            }


            JsonNode body = response.getBody();
            if (body == null || body.getObject() == null){
                LOG.warn("Empty response from DBpedia");
                return Collections.emptyList();
            }

            JSONObject results = body.getObject().getJSONObject("results");

            if (!results.has("bindings")){
                LOG.debug("No bindings from DBpedia: [" + words + "]");
                return Collections.emptyList();
            }

            JSONArray bindings = results.getJSONArray("bindings");
            if (bindings.length() == 0){
                LOG.debug("Empty bindings from DBpedia: [" + words + "]");
                String[] values = words.split(" ");
                if (values.length>0){
                    String newValues = Arrays.stream(values).limit(values.length - 1).collect(Collectors.joining(" "));
                    Thread.sleep(20);
                    return this.facetsCache.get(newValues);
                }
                return Collections.emptyList();
            }

            Iterator<Object> iterator = bindings.iterator();
            while(iterator.hasNext()){
                JSONObject binding = (JSONObject) iterator.next();
                String entity = binding.getJSONObject("s1").getString("value");
                entities.add(entity);
            }
        } catch (UnirestException e) {
            LOG.debug("HttpError from DBpedia: " + e.getMessage() + " from: " + words + " and condition: " + condition);
        } catch (Exception e) {
            LOG.error("Unexpected error from DBpedia",e);
        }


        return entities.stream().distinct().limit(max).collect(Collectors.toList());
    }


    // http://dbpedia.org/resource/Capstone_(cryptography)
    protected List<String> findSubjectsOf(String id){
        String sparqlQuery =
                "prefix dct: <http://purl.org/dc/terms/> \n"+
                "select ?subject \n" +
                "where {\n" +
                " <"+id+"> dct:subject ?subject\n" +
                "}";

        List<String> subjects = new ArrayList<>();
        try {
            subjects.add(StringUtils.substringAfterLast(id,"/"));
            String queryURI = URLEncoder.encode(sparqlQuery, "utf-8");
            HttpResponse<JsonNode> response = Unirest.get(endpoint + "?default-graph-uri=&query=" + queryURI).asJson();
            if (response.getStatus() != 200){
                LOG.warn("No response from DBpedia");
                return subjects;
            }


            JsonNode body = response.getBody();
            if (body == null || body.getObject() == null){
                LOG.warn("Empty response from DBpedia");
                return subjects;
            }

            JSONArray bindings = body.getObject().getJSONObject("results").getJSONArray("bindings");
            if (bindings.length() == 0){
                if (id.contains("http://dbpedia.org/resource/Category:")) {
                    subjects.add(StringUtils.substringAfter(id, "http://dbpedia.org/resource/Category:"));
                } else if (id.contains("http://dbpedia.org/resource")) {
                    subjects.add(StringUtils.substringAfter(id, "http://dbpedia.org/resource/"));
                }
                return subjects;
            }

            Iterator<Object> iterator = bindings.iterator();
            while(iterator.hasNext()){
                JSONObject binding = (JSONObject) iterator.next();
                String entity = binding.getJSONObject("subject").getString("value");
                String subject = StringUtils.substringAfter(entity,"http://dbpedia.org/resource/Category:");
                subjects.add(subject);
            }
        } catch (UnirestException e) {
            LOG.error("HttpError from DBpedia: " + e.getMessage() + " from: " + id);
            subjects.add(StringUtils.substringAfterLast(id,"/"));
        } catch (Exception e) {
            LOG.error("Unexpected error from DBpedia",e);
        }

        return subjects.stream().distinct().collect(Collectors.toList());
    }



}
