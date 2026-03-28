package wfm.engine.impl;

import wfm.engine.AbstractEngine;
import wfm.exception.FlowEngineException;

public class DefaultNoLogicEngine extends AbstractEngine {
    @Override
    protected void validate() throws FlowEngineException {
        // no-op validation for testing default execute behavior
    }

    @Override
    public void initialize() {
    }

    @Override
    public void handleError() {
    }
}