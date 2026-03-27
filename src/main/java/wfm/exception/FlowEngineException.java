package wfm.exception;

public class FlowEngineException extends Exception {
    private final String errorCode;

    public FlowEngineException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
