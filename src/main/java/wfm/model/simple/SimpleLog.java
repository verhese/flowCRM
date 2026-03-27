package wfm.model.simple;

import wfm.model.AbstractInstance;
import wfm.model.AbstractLog;
import wfm.model.AbstractTask;
import wfm.pojo.FlowLogType;

/**
 * A basic implementation of the AbstractLog class.
 */
public final class SimpleLog extends AbstractLog {
    /**
     * Default constructor for when adding logging to a specific task.
     *
     * @param instance  The AbstractInstance which is linked to the logging.
     * @param task      The AbstractTask which is linked to the logging.
     * @param logType   The actual log level.
     * @param reference A reference of the logging, to be used for easier filtering and accessing of relevant logging.
     * @param message   The actual message which is logged.
     * @since 0.0.1
     */
    public SimpleLog(AbstractInstance instance, AbstractTask task, FlowLogType logType, String reference, String message) {
        super(instance, task, logType, reference, message);
    }

    /**
     * Default constructor for when adding logging to an instance itself, without linking it to a task.
     *
     * @param instance  The AbstractInstance which is linked to the logging.
     * @param logType   The actual log level.
     * @param reference A reference of the logging, to be used for easier filtering and accessing of relevant logging.* @param message The actual message which is logged.
     * @param message   The actual message which is logged.
     * @since 0.0.1
     */
    public SimpleLog(AbstractInstance instance, FlowLogType logType, String reference, String message) {
        super(instance, logType, reference, message);
    }
}
