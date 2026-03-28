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

    @Test
    void testStringConstructorsAndFromFactory() {
        RuntimeException cause = new RuntimeException("broken");

        FlowEngineException minimal = new FlowEngineException("E001", "Message");
        Assertions.assertEquals("E001", minimal.getErrorCode());
        Assertions.assertEquals("", minimal.getContext());
        Assertions.assertEquals("[E001] Message", minimal.getFullMessage());

        FlowEngineException withContext = new FlowEngineException("E002", "Message", "Context");
        Assertions.assertEquals("Context", withContext.getContext());

        FlowEngineException withCause = new FlowEngineException("E003", "Message", cause);
        Assertions.assertSame(cause, withCause.getCause());

        FlowEngineException blankCode = new FlowEngineException("   ", "Message", "Ctx", cause);
        Assertions.assertEquals("FLOW_ERR", blankCode.getErrorCode());
        Assertions.assertEquals("Ctx", blankCode.getContext());
        Assertions.assertSame(cause, blankCode.getCause());

        FlowEngineException fromFactory = FlowEngineException.from("E004", "Created");
        Assertions.assertEquals("E004", fromFactory.getErrorCode());
        Assertions.assertEquals("Created", fromFactory.getMessage());
    }

    @Test
    void testWrapMethodsAndCauseLookupBranches() {
        IllegalArgumentException cause = new IllegalArgumentException("bad input");

        FlowEngineException wrappedKnownCode = FlowEngineException.wrap("ERR-001", cause);
        Assertions.assertEquals("ERR-001", wrappedKnownCode.getErrorCode());
        Assertions.assertFalse(wrappedKnownCode.getMessage().isBlank());

        FlowEngineException existing = new FlowEngineException("E123", "Existing", "ctx", cause);
        Assertions.assertSame(existing, FlowEngineException.wrap("OTHER", existing));

        FlowEngineException wrappedDefault = FlowEngineException.wrap(cause);
        Assertions.assertEquals("FLOW_ERR", wrappedDefault.getErrorCode());
        Assertions.assertNull(wrappedDefault.getCause());
        Assertions.assertFalse(wrappedDefault.getMessage().isBlank());

        Assertions.assertFalse(existing.isCausedBy(null));
        Assertions.assertTrue(existing.isCausedBy(IllegalArgumentException.class));
        Assertions.assertFalse(existing.isCausedBy(IllegalStateException.class));
        Assertions.assertTrue(existing.findCause(IllegalArgumentException.class).isPresent());
        Assertions.assertTrue(existing.findCause(RuntimeException.class).isPresent());
        Assertions.assertTrue(existing.findCause(FlowEngineException.class).isPresent());
        Assertions.assertTrue(existing.findCause(null).isEmpty());
    }
}
