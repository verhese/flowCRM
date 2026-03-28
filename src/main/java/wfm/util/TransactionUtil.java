package wfm.util;

import wfm.config.FlowTaskState;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility methods to manage database transactions via JPA or JDBC resources.
 */
public final class TransactionUtil {

    private TransactionUtil() {
        // utility class
    }

    public static void beginTransaction(EntityManager entityManager, Connection connection) {
        if (entityManager != null) {
            EntityTransaction transaction = entityManager.getTransaction();
            if (transaction != null && !transaction.isActive()) {
                transaction.begin();
            }
            return;
        }

        if (connection != null) {
            try {
                if (connection.getAutoCommit()) {
                    connection.setAutoCommit(false);
                }
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Could not start transaction", sqlException);
            }
        }
    }

    public static void commitTransaction(EntityManager entityManager, Connection connection) {
        if (entityManager != null) {
            EntityTransaction transaction = entityManager.getTransaction();
            if (transaction != null && transaction.isActive()) {
                transaction.commit();
            }
            return;
        }

        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Could not commit transaction", sqlException);
            }
        }
    }

    public static void rollbackTransaction(EntityManager entityManager, Connection connection) {
        if (entityManager != null) {
            EntityTransaction transaction = entityManager.getTransaction();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return;
        }

        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.setAutoCommit(true);
            } catch (SQLException sqlException) {
                throw new IllegalStateException("Could not rollback transaction", sqlException);
            }
        }
    }

    public static void updateTaskState(EntityManager entityManager, Connection connection, Long taskId, FlowTaskState state) {
        if (taskId == null || state == null) {
            return;
        }

        if (entityManager != null) {
            EntityTransaction transaction = entityManager.getTransaction();
            boolean startedTransaction = transaction != null && !transaction.isActive();

            if (startedTransaction) {
                transaction.begin();
            }

            try {
                entityManager.createNativeQuery("UPDATE flow_tasks SET state = :state WHERE id = :id")
                        .setParameter("state", state.getCode())
                        .setParameter("id", taskId)
                        .executeUpdate();

                if (transaction != null && transaction.isActive()) {
                    transaction.commit();
                }
            } catch (RuntimeException runtimeException) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new IllegalStateException("Could not update task state", runtimeException);
            }
            return;
        }

        if (connection != null) {
            try {
                boolean originalAutoCommit = connection.getAutoCommit();
                if (originalAutoCommit) {
                    connection.setAutoCommit(false);
                }

                try (PreparedStatement statement = connection.prepareStatement("UPDATE flow_tasks SET state = ? WHERE id = ?")) {
                    statement.setString(1, state.getCode());
                    statement.setLong(2, taskId);
                    statement.executeUpdate();
                }

                connection.commit();

                if (originalAutoCommit) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException sqlException) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    sqlException.addSuppressed(rollbackException);
                }
                throw new IllegalStateException("Could not update task state", sqlException);
            }
        }
    }
}