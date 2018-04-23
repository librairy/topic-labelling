package org.librairy.labelling;

import org.codehaus.jackson.map.ObjectMapper;
import org.librairy.labelling.model.Document;
import org.librairy.labelling.model.Topic;
import org.librairy.labelling.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TestMain {

    private static final Logger LOG = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) {

        String folder                                       = "20newsgroup_20";
        LabellingService labellingService                   = new DensityLabellingService();
        DocumentsPerTopicService documentsPerTopicService   = new DocumentsPerTopicService("20newsgroup.csv.gz");
        FilteringLinesService filteringLinesService         = new FilteringLinesService();


        Path baseDirectory = Paths.get("src/main/resources",folder);

        ObjectMapper jsonMapper = new ObjectMapper();
        try(Stream<Path> stream = Files.walk(baseDirectory)) {

            stream.filter(Files::isRegularFile).forEach(filePath -> {

                try {
                    Topic topic = jsonMapper.readValue(filePath.toFile(), Topic.class);
                    topic.setId(filePath.getFileName().toString());
                    List<String> labels = labellingService.labelsOf(topic);
                    LOG.info("Labels for " + topic + ":");
//                    labels.forEach(label -> LOG.info("\t - " + label));

                    List<Document> topDocuments = documentsPerTopicService.findDocumentsPer(topic);

                    for(String label : labels){
                        LOG.info("\t - " + label);
                        List<String> lines = new ArrayList<String>();
                        for(Document document: topDocuments){
                            String[] partialLabels = label.split("\\+");
                            List<String> relevantLines = filteringLinesService.linesContaining(document.getText(), Arrays.asList(partialLabels));
                            lines.addAll(relevantLines);
                        }
                        lines.forEach( line -> LOG.info("\t\t -> " + line));
                    }


                } catch (IOException e) {
                    LOG.warn("Parsing error on file: " + filePath,e);
                }


            });

        }catch(UncheckedIOException ex) {
            LOG.error("Unexpected error",ex);
        } catch (IOException e) {
            LOG.error("Unexpected error",e);
        }


    }

}
