package wfm.engine.impl;

import wfm.engine.AbstractEngine;
import wfm.exception.FlowEngineException;
import wfm.exception.FlowErrorCode;

public class ErrorNoLogicEngine extends AbstractEngine {

    @Override
    protected void validate() throws FlowEngineException {
        // no-op validation for test engine
    }

    @Override
    public void execute() throws FlowEngineException {
        throw new FlowEngineException(FlowErrorCode.NO_EXECUTION_LOGIC);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void handleError() {

    }
}
