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
package gov.nist.itl.ssd.wipp.backend.data.modelcard;

import gov.nist.itl.ssd.wipp.backend.Application;
import gov.nist.itl.ssd.wipp.backend.app.SecurityConfig;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModel;
import gov.nist.itl.ssd.wipp.backend.data.aimodel.AiModelFramework;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Collection of tests for {@link ModelCardRepository} exposed methods
 *
 * @author Antoine Meyer <antoine.meyer at nist.gov>
 */
@SpringBootTest(
        classes = { Application.class, SecurityConfig.class },
        properties = { "spring.data.mongodb.port=0", "de.flapdoodle.mongodb.embedded.version=6.0.5"}
)
public class ModelCardRepositoryTest {

    @Autowired
    ModelCardRepository modelCardRepository;

    ModelCard modelCardA;

    @BeforeEach
    public void setUp() {
        // Clear embedded database
        modelCardRepository.deleteAll();

        // Create AI Model A
        AiModel aiModelA = new AiModel("AI Model A", AiModelFramework.TENSORFLOW);

        // Create and save Model Card A
        modelCardA = new ModelCard(aiModelA);
        modelCardA = modelCardRepository.save(modelCardA);
    }

    @Test
    public void findById() throws Exception {
        modelCardRepository.findById(modelCardA.getId());
    }

    // todo

}

