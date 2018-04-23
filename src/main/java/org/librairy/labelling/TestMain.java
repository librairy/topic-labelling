package org.librairy.labelling;

import org.codehaus.jackson.map.ObjectMapper;
import org.librairy.labelling.model.Topic;
import org.librairy.labelling.service.DensityLabellingService;
import org.librairy.labelling.service.TopLabellingService;
import org.librairy.labelling.service.LabellingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TestMain {

    private static final Logger LOG = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) {

        String folder                       = "20newsgroup_6";
        LabellingService labellingService   = new DensityLabellingService();


        Path baseDirectory = Paths.get("src/main/resources",folder);

        ObjectMapper jsonMapper = new ObjectMapper();
        try(Stream<Path> stream = Files.walk(baseDirectory)) {

            stream.filter(Files::isRegularFile).forEach(filePath -> {

                try {
                    Topic topic = jsonMapper.readValue(filePath.toFile(), Topic.class);
                    topic.setId(filePath.getFileName().toString());
                    List<String> labels = labellingService.labelsOf(topic);
                    LOG.info("Labels for " + topic + ":");
                    labels.forEach(label -> LOG.info("\t - " + label));

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
