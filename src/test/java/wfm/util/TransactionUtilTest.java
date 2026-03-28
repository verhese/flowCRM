package wfm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wfm.config.FlowTaskState;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class TransactionUtilTest {

    @Test
    void testBeginTransactionWithEntityManagerStartsInactiveTransaction() {
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        EntityTransaction transaction = Mockito.mock(EntityTransaction.class);
        Mockito.when(entityManager.getTransaction()).thenReturn(transaction);
        Mockito.when(transaction.isActive()).thenReturn(false);

        TransactionUtil.beginTransaction(entityManager, null);

        Mockito.verify(transaction).begin();
    }

    @Test
    void testBeginTransactionWithConnectionDisablesAutoCommit() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getAutoCommit()).thenReturn(true);

        TransactionUtil.beginTransaction(null, connection);

        Mockito.verify(connection).setAutoCommit(false);
    }

    @Test
    void testCommitAndRollbackWithEntityManager() {
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        EntityTransaction transaction = Mockito.mock(EntityTransaction.class);
        Mockito.when(entityManager.getTransaction()).thenReturn(transaction);
        Mockito.when(transaction.isActive()).thenReturn(true);

        TransactionUtil.commitTransaction(entityManager, null);
        TransactionUtil.rollbackTransaction(entityManager, null);

        Mockito.verify(transaction).commit();
        Mockito.verify(transaction).rollback();
    }

    @Test
    void testCommitAndRollbackWithConnection() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getAutoCommit()).thenReturn(false);

        TransactionUtil.commitTransaction(null, connection);
        TransactionUtil.rollbackTransaction(null, connection);

        Mockito.verify(connection).commit();
        Mockito.verify(connection, Mockito.atLeastOnce()).setAutoCommit(true);
        Mockito.verify(connection).rollback();
    }

    @Test
    void testUpdateTaskStateNoOpOnNullArguments() {
        TransactionUtil.updateTaskState(null, null, null, FlowTaskState.ERROR);
        TransactionUtil.updateTaskState(null, null, 1L, null);
    }

    @Test
    void testUpdateTaskStateWithEntityManagerCommitsStartedTransaction() {
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        EntityTransaction transaction = Mockito.mock(EntityTransaction.class);
        Query query = Mockito.mock(Query.class);

        Mockito.when(entityManager.getTransaction()).thenReturn(transaction);
        Mockito.when(transaction.isActive()).thenReturn(false, true);
        Mockito.when(entityManager.createNativeQuery("UPDATE flow_tasks SET state = :state WHERE id = :id")).thenReturn(query);
        Mockito.when(query.setParameter("state", FlowTaskState.FINISHED.getCode())).thenReturn(query);
        Mockito.when(query.setParameter("id", 10L)).thenReturn(query);

        TransactionUtil.updateTaskState(entityManager, null, 10L, FlowTaskState.FINISHED);

        Mockito.verify(transaction).begin();
        Mockito.verify(query).executeUpdate();
        Mockito.verify(transaction).commit();
    }

    @Test
    void testUpdateTaskStateWithEntityManagerRollsBackOnFailure() {
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        EntityTransaction transaction = Mockito.mock(EntityTransaction.class);
        Query query = Mockito.mock(Query.class);

        Mockito.when(entityManager.getTransaction()).thenReturn(transaction);
        Mockito.when(transaction.isActive()).thenReturn(false, true, true);
        Mockito.when(entityManager.createNativeQuery("UPDATE flow_tasks SET state = :state WHERE id = :id")).thenReturn(query);
        Mockito.when(query.setParameter("state", FlowTaskState.ERROR.getCode())).thenReturn(query);
        Mockito.when(query.setParameter("id", 11L)).thenReturn(query);
        Mockito.when(query.executeUpdate()).thenThrow(new RuntimeException("boom"));

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> TransactionUtil.updateTaskState(entityManager, null, 11L, FlowTaskState.ERROR));

        Assertions.assertTrue(exception.getMessage().contains("Could not update task state"));
        Mockito.verify(transaction).begin();
        Mockito.verify(transaction).rollback();
    }

    @Test
    void testUpdateTaskStateWithJdbcConnectionPersistsState() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:tx-util-success;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "");
             PreparedStatement create = connection.prepareStatement("CREATE TABLE flow_tasks(id BIGINT PRIMARY KEY, state VARCHAR(50) NOT NULL)")) {
            create.executeUpdate();

            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO flow_tasks(id, state) VALUES(?, ?)");
                 PreparedStatement select = connection.prepareStatement("SELECT state FROM flow_tasks WHERE id = ?")) {
                insert.setLong(1, 1L);
                insert.setString(2, "RG");
                insert.executeUpdate();

                TransactionUtil.updateTaskState(null, connection, 1L, FlowTaskState.FINISHED);

                select.setLong(1, 1L);
                try (var rs = select.executeQuery()) {
                    Assertions.assertTrue(rs.next());
                    Assertions.assertEquals("FIN", rs.getString("state"));
                }
            }
            Assertions.assertTrue(connection.getAutoCommit());
        }
    }

    @Test
    void testUpdateTaskStateWithJdbcConnectionWrapsSqlException() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.getAutoCommit()).thenReturn(false);
        Mockito.when(connection.prepareStatement("UPDATE flow_tasks SET state = ? WHERE id = ?"))
                .thenThrow(new SQLException("prepare failed"));

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> TransactionUtil.updateTaskState(null, connection, 99L, FlowTaskState.ERROR));

        Assertions.assertTrue(exception.getMessage().contains("Could not update task state"));
        Mockito.verify(connection).rollback();
    }

    @Test
    void testBeginCommitRollbackWrapSQLException() throws SQLException {
        Connection beginConnection = Mockito.mock(Connection.class);
        Mockito.when(beginConnection.getAutoCommit()).thenThrow(new SQLException("begin failed"));
        Assertions.assertThrows(IllegalStateException.class, () -> TransactionUtil.beginTransaction(null, beginConnection));

        Connection commitConnection = Mockito.mock(Connection.class);
        Mockito.when(commitConnection.getAutoCommit()).thenReturn(false);
        Mockito.doThrow(new SQLException("commit failed")).when(commitConnection).commit();
        Assertions.assertThrows(IllegalStateException.class, () -> TransactionUtil.commitTransaction(null, commitConnection));

        Connection rollbackConnection = Mockito.mock(Connection.class);
        Mockito.when(rollbackConnection.getAutoCommit()).thenReturn(false);
        Mockito.doThrow(new SQLException("rollback failed")).when(rollbackConnection).rollback();
        Assertions.assertThrows(IllegalStateException.class, () -> TransactionUtil.rollbackTransaction(null, rollbackConnection));
    }
}
