package wfm.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Different instance states that can be used throughout the lifecycle of an instance.
 *
 * @author Sean Verheyen
 * @since 0.0.1
 */
public enum FlowInstanceState {
    /**
     * The instance is created, but nothing more is done.
     *
     * @since 0.0.1
     */
    CREATED("CR", Boolean.TRUE, Boolean.FALSE),
    /**
     * The instance is finished, and not more tasks can be executed.
     *
     * @since 0.0.1
     */
    CLOSED("CL", Boolean.FALSE, Boolean.FALSE),
    /**
     * The instance has gone into error, tasks can still be executed, but this should be seen as an alert for ths user.
     *
     * @since 0.0.1
     */
    ERROR("ERR", Boolean.TRUE, Boolean.TRUE);

    private final String code;
    private final Boolean isReadOnly;
    private final Boolean isError;

    FlowInstanceState(String code, Boolean isReadOnly, Boolean isError) {
        this.code = code;
        this.isReadOnly = isReadOnly;
        this.isError = isError;
    }

    /**
     * Retrieve the state code.
     *
     * @return Code used to indicate the state.
     * @since 0.0.1
     */
    public String getCode() {
        return code;
    }

    /**
     * Check if this state is executable.
     *
     * @return Boolean indicating whether the Instance can still be executed.
     * @since 0.0.1
     */
    public Boolean isExecutable() {
        return !isReadOnly;
    }

    /**
     * Check is the current state is an error.
     *
     * @return Boolean indicating whether the Instance encountered an error.
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
        return new ToStringBuilder(this).build();
    }
}
