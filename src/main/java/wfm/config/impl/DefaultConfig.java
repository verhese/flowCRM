package wfm.config.impl;

import wfm.config.AbstractConfig;
import wfm.config.FlowTaskState;

public final class DefaultConfig extends AbstractConfig {
    private static final FlowTaskState SUCCESS_STATE = FlowTaskState.FINISHED;
    private static final FlowTaskState WAIT_STATE = FlowTaskState.WAITING_ON_REPLY;
    private static final FlowTaskState ERR_STATE = FlowTaskState.ERROR;
    private static final Boolean RESET_STATE_ON_ERROR = Boolean.FALSE;

    public FlowTaskState getSuccessState() {
        return SUCCESS_STATE;
    }

    public FlowTaskState getWaitState() {
        return WAIT_STATE;
    }

    public FlowTaskState getErrState() {
        return ERR_STATE;
    }

    public Boolean getResetSateOnError() {
        return RESET_STATE_ON_ERROR;
    }
}
