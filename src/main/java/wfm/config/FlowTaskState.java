package wfm.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Different task states that can be used throughout the lifecycle of a task.
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
public enum FlowTaskState {
    /**
     * The task is Registered, but not yet planned or in use.
     *
     * @since 0.0.1
     */
    REGISTERED("RG", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE),
    /**
     * The task is Registered and will be executed as soon as possible.
     *
     * @since 0.0.1
     */
    DIRECT_EXECUTION("DEX", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE),
    /**
     * The task is Registered and will be executed as soon as the timer runs out.
     *
     * @since 0.0.1
     */
    DELAYED_EXECUTION("REX", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE),
    /**
     * An error occurred during processing of the task.
     *
     * @since 0.0.1
     */
    ERROR("ERR", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE),
    /**
     * The task is Pending, meaning the task execution has started, but is not at the point where the task is actually executed.
     * For example:
     * <p>
     * The task pre-validations of the task are running.
     *
     * @since 0.0.1
     */
    PENDING("PND", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE),
    /**
     * The task is currently being executed by the Engine.
     *
     * @since 0.0.1
     */
    EXECUTING("EX", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE),
    /**
     * The task was executed, but requires feedback from an external source.
     *
     * @since 0.0.1
     */
    WAITING_ON_REPLY("WT", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE),
    /**
     * The task is finished, and has completed its lifecycle.
     *
     * @since 0.0.1
     */
    FINISHED("FIN", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE),
    /**
     * The task is finished, but with a negative feedback from an external system.
     *
     * @since 0.0.1
     */
    REJECTED("REJ", Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);

    private final String code;
    private final Boolean isActive;
    private final Boolean isUpdatable;
    private final Boolean isFinished;
    private final Boolean isError;

    FlowTaskState(String code, Boolean isActive, Boolean isUpdatable, Boolean isFinished, Boolean isError) {
        this.code = code;
        this.isActive = isActive;
        this.isUpdatable = isUpdatable;
        this.isFinished = isFinished;
        this.isError = isError;
    }

    /**
     * Retrieve the state code.
     *
     * @return Code indicating the state of the task.
     * @since 0.0.1
     */
    public String getCode() {
        return code;
    }

    /**
     * Check if this state is executable.
     *
     * @return Boolean indicating whether the task can still be executed.
     * @since 0.0.1
     */
    public Boolean isExecutable() {
        return isActive && isUpdatable;
    }

    /**
     * Check is the current state is an error.
     *
     * @return Boolean indicating whether the task encountered an error.
     * @since 0.0.1
     */
    public Boolean isError() {
        return isError;
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum class should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {

        System.out.println("errorCode = " + this.getCode());

        return new ToStringBuilder(this.getCode()).build();
    }
}
