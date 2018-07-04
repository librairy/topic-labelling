package org.librairy.labelling.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ConceptPruneServiceIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConceptPruneServiceIntTest.class);

    @Test
    public void clusters(){


        Map<String,Double> topicWords = new HashMap<>();

        topicWords.put("Night_Sky", 0.40457524768887565);
        topicWords.put("Dillingham_Airfield", 0.3306440461704014);
        topicWords.put("Blue_in_Judaism", 0.29883059860251693);
        topicWords.put("Hipparcos_objects", 0.2987959038573344);
        topicWords.put("Shades_of_black",0.29345387054516664);
        topicWords.put("China–Russia_relations",0.23716090204701815);
        topicWords.put("Shades_of_blue", 0.23315057139628567);
        topicWords.put("Night_Sky_of_Krynn",0.2188594553192773);
        topicWords.put("Crucifixion_with_the_Virgin_and_St_John",0.1974245869252814);
        topicWords.put("Orion_(constellation)",0.19032604800263556);
        topicWords.put("Night",0.1870486363898214);
        topicWords.put("Observational_astronomy",0.17877728585977806);
        topicWords.put("Bellatrix",0.14192292367570736);
        topicWords.put("Midnight_blue",0.13096277663522932);
        topicWords.put("CAIC_Z-10",0.12806902474399642);
        topicWords.put("Military_helicopters",0.11721165027622679);
        topicWords.put("Night_Sky_Network",0.09347325861499967);

        ConceptPruneService service = new ConceptPruneService();
        List<String> concepts = service.prune(topicWords, Arrays.asList(new String[]{"Light", "Pollution"}));

        LOG.info("Topic Labels: " + concepts);

    }

}
