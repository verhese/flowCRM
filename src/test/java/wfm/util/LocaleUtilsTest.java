package wfm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

class LocaleUtilsTest {

    @Test
    void testNormalizeLocaleEnGb() {
        Assertions.assertEquals(Locale.ENGLISH, LocaleUtils.normalizeLocale(Locale.UK));
    }

    @Test
    void testNormalizeLocaleNlBe() {
        Assertions.assertEquals(Locale.forLanguageTag("nl-BE"), LocaleUtils.normalizeLocale(Locale.forLanguageTag("nl-BE")));
    }

    @Test
    void testNormalizeLocaleUnknownFallsBackEn() {
        Assertions.assertEquals(Locale.ENGLISH, LocaleUtils.normalizeLocale(Locale.FRENCH));
    }
}
