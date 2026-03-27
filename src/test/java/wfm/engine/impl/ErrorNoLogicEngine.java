package wfm.engine.impl;

import wfm.engine.AbstractEngine;
import wfm.exception.FlowEngineException;

public class ErrorNoLogicEngine extends AbstractEngine {
    /**
     * Abstract method execute which needs to be implemented in the actual Engine implementation.
     * <br/>
     * This method should be seen as the start point of the task execution where the actual implementation of the task is called.
     *
     * @throws FlowEngineException
     * @since 0.0.1
     */
    @Override
    public void execute() throws FlowEngineException {
        throw new FlowEngineException("ERR-001", "Error");
    }

    /**
     * Abstract method initialize which can be used to add extra logic to the initialization of the Engine.
     *
     * @since 0.0.1
     */
    @Override
    public void initialize() {

    }

    /**
     * Abstract method handleError which can be used to add specific logic to the logging process of te Engine.
     *
     * @since 0.0.1
     */
    @Override
    public void handleError() {

    }
}
