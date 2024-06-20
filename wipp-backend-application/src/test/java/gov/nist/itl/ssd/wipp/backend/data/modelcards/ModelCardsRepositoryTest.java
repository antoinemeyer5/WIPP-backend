package gov.nist.itl.ssd.wipp.backend.data.modelcards;

import gov.nist.itl.ssd.wipp.backend.Application;
import gov.nist.itl.ssd.wipp.backend.app.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * todo: add comments
 */
@SpringBootTest(
        classes = { Application.class, SecurityConfig.class },
        properties = { "spring.data.mongodb.port=0", "de.flapdoodle.mongodb.embedded.version=6.0.5"}
)
public class ModelCardsRepositoryTest {

    @Autowired
    //ModelCardsRepository modelCardsRepository;

    ModelCards publicModelCardA;

    @BeforeEach
    public void setUp() {
        // todo: create setup
    }

    @Test
    public void test_1() throws Exception {
        // todo: add test 1 !
    }
}
