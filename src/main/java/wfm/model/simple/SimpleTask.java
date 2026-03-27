package wfm.model.simple;

import wfm.config.FlowTaskState;
import wfm.model.AbstractInstance;
import wfm.model.AbstractTask;

public final class SimpleTask extends AbstractTask {
    /**
     * The default constructor for the creation of a task.
     * The state of the task is defaulted to FlowTaskState.REGISTERED.
     *
     * @param instance AbstractInstance implementation for which the task is created.
     * @see FlowTaskState
     * @since 0.0.1
     */
    public SimpleTask(AbstractInstance instance) {
        super(instance);
    }
}
