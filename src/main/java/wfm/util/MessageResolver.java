package wfm.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class MessageResolver {
    private static final String BUNDLE_BASE_DEFAULT = "i18n.messages";
    private static final String BUNDLE_BASE_ERROR = "i18n.error.messages";
    private static final String BUNDLE_BASE_CONTEXT = "i18n.context.messages";

    private MessageResolver() {
        // static utility class
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        if (key == null || key.isBlank()) {
            return "";
        }

        Locale resolved = LocaleUtils.normalizeLocale(locale);

        String message = attemptResolve(key, resolved, args);

        if (message == null && !Locale.ENGLISH.equals(resolved)) {
            message = attemptResolve(key, Locale.ENGLISH, args);
        }

        return message != null ? message : key;
    }

    private static String attemptResolve(String key, Locale locale, Object... args) {
        try {
            String baseName;
            if (key.startsWith("error.")) {
                baseName = BUNDLE_BASE_ERROR;
            } else if (key.startsWith("context.")) {
                baseName = BUNDLE_BASE_CONTEXT;
            } else {
                baseName = BUNDLE_BASE_DEFAULT;
            }
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            String pattern = bundle.getString(key);
            if (args != null && args.length > 0) {
                return MessageFormat.format(pattern, args);
            }
            return pattern;
        } catch (Exception e) {
            return null;
        }
    }
}