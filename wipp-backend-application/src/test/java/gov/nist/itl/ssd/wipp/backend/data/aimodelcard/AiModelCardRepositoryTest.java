/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.itl.ssd.wipp.backend.data.aimodelcard;

import gov.nist.itl.ssd.wipp.backend.Application;
import gov.nist.itl.ssd.wipp.backend.app.SecurityConfig;
import gov.nist.itl.ssd.wipp.backend.core.model.computation.Plugin;
import gov.nist.itl.ssd.wipp.backend.core.model.computation.PluginIO;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of tests for {@link AiModelCardRepository} exposed methods
 *
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@SpringBootTest(
        classes = { Application.class, SecurityConfig.class },
        properties = { "spring.data.mongodb.port=0", "de.flapdoodle.mongodb.embedded.version=6.0.5"}
)
public class AiModelCardRepositoryTest {

    @Autowired
    AiModelCardRepository aiModelCardRepository;

    AiModelCard aiModelCardA, aiModelCardB;

    @BeforeEach
    public void setUp() {
        // Clear embedded database
        aiModelCardRepository.deleteAll();

        // Setup A
        // AI Model A
        AiModel aiModelA = new AiModel("AI Model A");
        // Job A
        Job jobA = new Job();
        jobA.setName("job A");
        jobA.setOwner("user A");
        // Plugin A
        Plugin pluginA = new Plugin();
        pluginA.setName("org/test-plugin-A");
        pluginA.setVersion("1.0.0");
        // Create and save AI Model Card A
        aiModelCardA = new AiModelCard(aiModelA, jobA, pluginA);
        aiModelCardA = aiModelCardRepository.save(aiModelCardA);

        // Setup B
        // AI Model B
        AiModel aiModelB = new AiModel("AI Model B");
        // Job B
        Job jobB = new Job();
        Map<String, String> params = new HashMap<String, String>();
        params.put("param1", "paramValue1");
        jobB.setParameters(params);
        // Plugin B
        Plugin pluginB = new Plugin();
        pluginB.setCitation("example-citation");
        pluginB.setOperationType("segmentation");
        PluginIO plugin = new PluginIO();
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("architecture", "Unet");
        plugin.setOptions(options);
        List<PluginIO> list = new ArrayList<>();
        list.add(plugin);
        pluginB.setOutputs(list);
        // Create and save AI Model Card B
        aiModelCardB = new AiModelCard(aiModelB, jobB, pluginB);
        aiModelCardB = aiModelCardRepository.save(aiModelCardB);

    }

    @Test
    public void check_A() {
        Assertions.assertEquals(aiModelCardA.getName(), "AI Model A");
        Assertions.assertNull(aiModelCardA.getFramework());
        Assertions.assertNull(aiModelCardA.getOperationType());
        Assertions.assertEquals(aiModelCardA.getArchitecture(), "N/A");
    }

    @Test
    public void check_B() {
        Assertions.assertEquals(aiModelCardB.getName(), "AI Model B");
        Assertions.assertEquals(aiModelCardB.getTrainingData().get("param1"), "paramValue1");
        Assertions.assertEquals(aiModelCardB.getCitation(), "example-citation");
        Assertions.assertEquals(aiModelCardB.getOperationType(), "segmentation");
        Assertions.assertEquals(aiModelCardB.getArchitecture(), "Unet");
    }

}

