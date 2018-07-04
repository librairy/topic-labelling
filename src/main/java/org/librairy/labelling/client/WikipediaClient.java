package org.librairy.labelling.client;

import com.google.common.base.Strings;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.librairy.labelling.metrics.ContentBaseRank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikipediaClient extends HttpClient{

    private static final Logger LOG = LoggerFactory.getLogger(WikipediaClient.class);

    public Double getScore(String wikipediaPage, List<String> words){
        Double score = 0.0;

        try{

            if (Strings.isNullOrEmpty(wikipediaPage)) return score;

            String content = wikipediaPage;
            if (wikipediaPage.startsWith("http")){
                content = getContent(wikipediaPage);
            }

            if (content.startsWith("http")) return score;

            if (Strings.isNullOrEmpty(content)){
                content = getLabel(wikipediaPage);
            }
            score = ContentBaseRank.getScore(content.replace("Category:","").replace("-Wikipedia","").replace("_"," ").replace(":"," "), words);

        }catch (Exception e){
            LOG.error("Error calculating score for '" + wikipediaPage+"'",e);
        }

        return score;

    }

    public String getContent(String uri){
        // https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&titles=Stack%20Overflow&redirects=true
        //https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=false&explaintext=true&exsectionformat=plain&titles=Stack%20Overflow&redirects=true
        String content = "";
        String title = StringUtils.substringAfterLast(uri,"/");
        try {
            String endpoint = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=true&explaintext=true&exsectionformat=plain&redirects=true&titles=";
            HttpResponse<JsonNode> response = Unirest.get(endpoint + title).asJson();
            if (response.getStatus() == 200){
                JSONObject pages = response.getBody().getObject().getJSONObject("query").getJSONObject("pages");
                String pageId = pages.keys().next();
                content= pages.getJSONObject(pageId).getString("extract");
            }else{
                LOG.error("Error getting wikipedia content " + response.getStatus() + ":" + response.getStatusText());
            }
        } catch (JSONException e) {
            LOG.debug("Wikipedia content not available for: '" + uri + "'");
        } catch (Exception e) {
            LOG.error("Unexpetec Error getting wikipedia content", e);
        }
        return content;
    }

    public String getLabel(String uri){
        if (Strings.isNullOrEmpty(uri)) return "";
        return StringUtils.substringAfterLast(uri,"/");
    }

    public String getUri(String label){

        return "https://en.wikipedia.org/wiki/" + label;
    }

}
