package wfm.util;

import java.util.Locale;

public final class LocaleUtils {
    private LocaleUtils() {
        // static utility class
    }

    public static Locale normalizeLocale(Locale locale) {
        if (locale == null) {
            return Locale.ENGLISH;
        }

        String tag = locale.toLanguageTag().toLowerCase();

        if (tag.equals("en") || tag.equals("en-us") || tag.equals("en-gb")) {
            return Locale.ENGLISH;
        }

        if (tag.equals("nl") || tag.equals("nl-nl")) {
            return Locale.forLanguageTag("nl-NL");
        }

        if (tag.equals("nl-be")) {
            return Locale.forLanguageTag("nl-BE");
        }

        return Locale.ENGLISH;
    }
}
