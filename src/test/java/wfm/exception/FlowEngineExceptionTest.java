package wfm.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wfm.exception.FlowErrorCode;

import java.util.Locale;

class FlowEngineExceptionTest {

    @Test
    void testConstructorAndGetters() {
        FlowEngineException ex = new FlowEngineException(FlowErrorCode.INVALID_TASK);

        Assertions.assertEquals("INVALID_TASK", ex.getErrorCode());
        Assertions.assertEquals("setTask failed", ex.getContext());
        Assertions.assertEquals("[INVALID_TASK] Task cannot be null (setTask failed)", ex.getFullMessage());
        Assertions.assertTrue(ex.toString().contains("errorCode='INVALID_TASK'"));
    }

    @Test
    void testWrapThrowablePreservesCause() {
        IllegalStateException inner = new IllegalStateException("state violation");
        FlowEngineException ex = FlowEngineException.wrap("E002", inner);

        Assertions.assertEquals("E002", ex.getErrorCode());
        Assertions.assertSame(inner, ex.getCause());
        Assertions.assertTrue(ex.isCausedBy(IllegalStateException.class));
        Assertions.assertTrue(ex.findCause(IllegalStateException.class).isPresent());
    }

    @Test
    void testI18nEnGbFallback() {
        FlowEngineException ex = new FlowEngineException(FlowErrorCode.INVALID_TASK, Locale.UK);

        Assertions.assertEquals("INVALID_TASK", ex.getErrorCode());
        Assertions.assertTrue(ex.getMessage().contains("Task cannot be null"));
        Assertions.assertTrue(ex.getContext().contains("setTask failed"));
    }

    @Test
    void testI18nNlBe() {
        FlowEngineException ex = new FlowEngineException(FlowErrorCode.INVALID_TASK, Locale.forLanguageTag("nl-BE"));

        Assertions.assertEquals("INVALID_TASK", ex.getErrorCode());
        Assertions.assertTrue(ex.getMessage().contains("Taak mag niet leeg zijn"));
        Assertions.assertTrue(ex.getContext().contains("setTask mislukt"));
    }

    @Test
    void testI18nUnknownDefaultEn() {
        FlowEngineException ex = new FlowEngineException(FlowErrorCode.INVALID_TASK, Locale.FRENCH);

        Assertions.assertEquals("INVALID_TASK", ex.getErrorCode());
        Assertions.assertTrue(ex.getMessage().contains("Task cannot be null"));
        Assertions.assertTrue(ex.getContext().contains("setTask failed"));
    }

    @Test
    void testFromThrowableDefaultErrorCode() {
        RuntimeException inner = new RuntimeException("database down");
        FlowEngineException ex = new FlowEngineException(inner);

        Assertions.assertEquals("FLOW_ERR", ex.getErrorCode());
        Assertions.assertSame(inner, ex.getCause());
    }
}
