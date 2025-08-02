package gift.Test.integration.jpa;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
public abstract class AbstractRepositoryTest {
}
