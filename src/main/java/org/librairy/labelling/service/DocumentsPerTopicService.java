package org.librairy.labelling.service;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.librairy.labelling.client.LibrairyClient;
import org.librairy.labelling.model.Document;
import org.librairy.labelling.model.Topic;
import org.librairy.service.modeler.facade.rest.model.Dimension;
import org.librairy.service.modeler.facade.rest.model.Inference;
import org.librairy.service.modeler.facade.rest.model.Relevance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DocumentsPerTopicService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentsPerTopicService.class);
    private final Path corpusPath;
    private final LibrairyClient librAIryClient;


    public DocumentsPerTopicService(String corpus) {

        corpusPath = Paths.get("src/main/resources",corpus);
        librAIryClient = new LibrairyClient();


    }

    public List<Document> findDocumentsPer(Topic topic){

        ExecutorService executor = Executors.newWorkStealingPool();
        ConcurrentLinkedQueue<Document> documents = new ConcurrentLinkedQueue<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(corpusPath.toFile()))));

            String line;
            AtomicInteger counter = new AtomicInteger();
            while(!Strings.isNullOrEmpty(line = reader.readLine())){

                final String text = line;

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        String[] values = text.split(";;");
                        String name     = values[0];
                        String labels   = values[1];
                        String text     = values[2].replace(">>","");

                        if (text.contains("writes:")){
                            text = StringUtils.substringAfter(text,"writes: ");
                        }

                        Inference inference = librAIryClient.inferTopicsFrom(text);
                        List<Relevance> sortedTopics = inference.getDimensions().stream().sorted((a, b) -> -a.getScore().compareTo(b.getScore())).collect(Collectors.toList());
                        Dimension topTopic = sortedTopics.get(0).getDimension();
                        if (topic.getId().contains(topTopic.getName())){
                            documents.add(new Document(name,text));
                        }

                    }
                });
                if (counter.getAndIncrement() == 1000) break;
            }
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(documents);

    }

}
