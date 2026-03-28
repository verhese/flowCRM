package wfm.exception;

import java.util.Locale;
import java.util.Optional;

import wfm.util.LocaleUtils;
import wfm.util.MessageResolver;

public class FlowEngineException extends Exception {
    private static final String DEFAULT_ERROR_CODE = "FLOW_ERR";
    private static final String BUNDLE_NAME = "messages";

    private final String errorCode;
    private final String context;
    private final FlowErrorCode flowErrorCode;

    public FlowEngineException(FlowErrorCode flowErrorCode) {
        this(flowErrorCode, Locale.ENGLISH, null);
    }

    public FlowEngineException(FlowErrorCode flowErrorCode, Locale locale, Object... messageArgs) {
        this(flowErrorCode,
                MessageResolver.getMessage(flowErrorCode.getMessageKey(), locale, messageArgs),
                MessageResolver.getMessage(flowErrorCode.getContextKey(), locale),
                null);
    }

    public FlowEngineException(FlowErrorCode flowErrorCode, String errorMessage, String context, Throwable cause) {
        super(errorMessage, cause);
        this.flowErrorCode = flowErrorCode;
        this.errorCode = flowErrorCode != null ? flowErrorCode.getCode() : DEFAULT_ERROR_CODE;
        this.context = Optional.ofNullable(context).orElse("");
    }

    public FlowEngineException(String errorCode, String errorMessage) {
        this(errorCode, errorMessage, null, null);
    }

    public FlowEngineException(String errorCode, String errorMessage, String context) {
        this(errorCode, errorMessage, context, null);
    }

    public FlowEngineException(String errorCode, String errorMessage, Throwable cause) {
        this(errorCode, errorMessage, null, cause);
    }

    public FlowEngineException(String errorCode, String errorMessage, String context, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = Optional.ofNullable(errorCode).filter(c -> !c.isBlank()).orElse(DEFAULT_ERROR_CODE);
        this.context = Optional.ofNullable(context).orElse("");
        this.flowErrorCode = FlowErrorCode.fromCode(this.errorCode);
    }

    public FlowEngineException(Throwable cause) {
        super(MessageResolver.getMessage(FlowErrorCode.FLOW_ERR.getMessageKey(), Locale.ENGLISH,
                cause != null ? cause.getMessage() : "Unexpected exception"), cause);
        this.flowErrorCode = FlowErrorCode.FLOW_ERR;
        this.errorCode = FlowErrorCode.FLOW_ERR.getCode();
        this.context = MessageResolver.getMessage(FlowErrorCode.FLOW_ERR.getContextKey(), Locale.ENGLISH);
    }

    public static FlowEngineException wrap(String errorCode, Throwable cause) {
        if (cause instanceof FlowEngineException) {
            return (FlowEngineException) cause;
        }
        FlowErrorCode code = FlowErrorCode.fromCode(errorCode);
        if (code != null) {
            return new FlowEngineException(code, Locale.getDefault(), cause != null ? cause.getMessage() : "Wrapped exception");
        }
        return new FlowEngineException(errorCode, Optional.ofNullable(cause).map(Throwable::getMessage).orElse("Wrapped exception"), null, cause);
    }

    public static FlowEngineException wrap(Throwable cause) {
        return wrap(DEFAULT_ERROR_CODE, cause);
    }

    public static FlowEngineException from(String errorCode, String message) {
        return new FlowEngineException(errorCode, message);
    }


    public String getErrorCode() {
        return errorCode;
    }

    public String getContext() {
        return context;
    }

    public String getFullMessage() {
        String base = String.format("[%s] %s", errorCode, getMessage());
        return context.isBlank() ? base : base + " (" + context + ")";
    }

    public boolean isCausedBy(Class<? extends Throwable> type) {
        if (type == null) {
            return false;
        }
        return findCause(type).isPresent();
    }

    public <T extends Throwable> Optional<T> findCause(Class<T> type) {
        if (type == null) {
            return Optional.empty();
        }

        Throwable cause = this;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return Optional.of(type.cast(cause));
            }
            cause = cause.getCause();
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("FlowEngineException{errorCode='%s', message='%s', context='%s', cause=%s}",
                errorCode, getMessage(), context, Optional.ofNullable(getCause()).map(Throwable::toString).orElse("none"));
    }
}
