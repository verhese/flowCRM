package wfm.util;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * H2 trigger implementation to audit task state changes.
 */
public final class TaskStateLogH2Trigger implements Trigger {
    private static final int TASK_ID_COLUMN_INDEX = 0;
    private static final int STATE_COLUMN_INDEX = 3;

    @Override
    public void init(Connection connection, String schemaName, String triggerName, String tableName, boolean before, int type) {
        // no-op
    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        if (newRow == null) {
            return;
        }

        Object previousState = oldRow == null ? null : oldRow[STATE_COLUMN_INDEX];
        Object nextState = newRow[STATE_COLUMN_INDEX];
        if (nextState == null || (previousState != null && previousState.equals(nextState))) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO flow_task_state_logs(task_id, new_state, updated_at, updated_by) VALUES (?, ?, CURRENT_TIMESTAMP, ?)")) {
            statement.setLong(1, ((Number) newRow[TASK_ID_COLUMN_INDEX]).longValue());
            statement.setString(2, String.valueOf(nextState));
            statement.setString(3, connection.getMetaData().getUserName());
            statement.executeUpdate();
        }
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void remove() {
        // no-op
    }
}
