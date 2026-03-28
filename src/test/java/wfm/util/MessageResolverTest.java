package wfm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

class MessageResolverTest {

    @Test
    void testGetMessageEnglishDefault() {
        String msg = MessageResolver.getMessage("error.task.invalid", Locale.ENGLISH);
        Assertions.assertEquals("Task cannot be null", msg);
    }

    @Test
    void testGetMessageNld() {
        String msg = MessageResolver.getMessage("error.task.invalid", Locale.forLanguageTag("nl-BE"));
        Assertions.assertEquals("Taak mag niet leeg zijn", msg);
    }

    @Test
    void testGetMessageUnknownLocaleFallbackToEnglish() {
        String msg = MessageResolver.getMessage("error.task.invalid", Locale.FRENCH);
        Assertions.assertEquals("Task cannot be null", msg);
    }
}
