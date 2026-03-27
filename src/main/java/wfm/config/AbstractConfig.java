package wfm.config;

/**
 * Basic configuration template class.
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
public abstract class AbstractConfig {
    public abstract FlowTaskState getSuccessState();

    public abstract FlowTaskState getWaitState();

    public abstract FlowTaskState getErrState();

    public abstract Boolean getResetSateOnError();
}
