package co.elastic.logging.log4j;

import co.elastic.logging.AbstractEcsLoggingTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class Log4jEcsLayoutTest extends AbstractEcsLoggingTest {

    private Logger logger;
    private ListAppender appender;
    private EcsLayout ecsLayout;

    @BeforeEach
    void setUp() {
        logger = LogManager.getLogger(getClass());
        logger.removeAllAppenders();
        appender = new ListAppender();
        logger.addAppender(appender);
        ecsLayout = new EcsLayout();
        ecsLayout.setServiceName("test");
    }

    @BeforeEach
    @AfterEach
    void tearDown() {
        MDC.clear();
        NDC.clear();
    }

    @Override
    public void putMdc(String key, String value) {
        MDC.put(key, value);
        assertThat(MDC.get(key)).isEqualTo(value);
    }

    @Override
    public boolean putNdc(String message) {
        NDC.push(message);
        return true;
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    @Override
    public JsonNode getLastLogLine() throws IOException {
        return objectMapper.readTree(ecsLayout.format(appender.getLogEvents().get(0)));
    }

}