package wfm.model.simple;

import wfm.model.AbstractTask;
import wfm.model.AbstractTaskStateLog;

import java.sql.Timestamp;

/**
 * A basic implementation of the AbstractTaskStateLog class.
 */
public final class SimpleTaskStateLog extends AbstractTaskStateLog {
    public SimpleTaskStateLog() {
        super();
    }

    public SimpleTaskStateLog(AbstractTask task, String newState, Timestamp updatedAt, String updatedBy) {
        super(task, newState, updatedAt, updatedBy);
    }
}
