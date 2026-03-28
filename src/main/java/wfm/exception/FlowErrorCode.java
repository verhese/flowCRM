package wfm.exception;

public enum FlowErrorCode {
    INVALID_INSTANCE("INVALID_INSTANCE", "error.instance.invalid", "context.instance.invalid"),
    INVALID_TASK("INVALID_TASK", "error.task.invalid", "context.task.invalid"),
    INVALID_CONFIG("INVALID_CONFIG", "error.config.invalid", "context.config.invalid"),
    NO_EXECUTION_LOGIC("ERR-001", "error.no_execution", "context.no_execution"),
    NO_VALIDATION_LOGIC("ERR-002", "error.no_validation", "context.no_validation"),
    NO_ERROR_LOGIC("ERR-003", "error.no_error", "context.no_error"),
    FLOW_ERR("FLOW_ERR", "error.flow_err", "context.flow_err");

    private final String code;
    private final String messageKey;
    private final String contextKey;

    FlowErrorCode(String code, String messageKey, String contextKey) {
        this.code = code;
        this.messageKey = messageKey;
        this.contextKey = contextKey;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getContextKey() {
        return contextKey;
    }

    public static FlowErrorCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (FlowErrorCode value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}