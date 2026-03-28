package wfm.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wfm.config.FlowTaskState;
import wfm.engine.impl.DefaultNoLogicEngine;
import wfm.engine.impl.ErrorNoLogicEngine;
import wfm.engine.impl.SuccessNoLogicEngine;
import wfm.exception.FlowEngineException;
import wfm.exception.FlowErrorCode;
import wfm.model.simple.SimpleInstance;
import wfm.model.simple.SimpleService;
import wfm.model.simple.SimpleTask;
import wfm.pojo.FlowLogType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class AbstractEngineTest {
    @Test
    void testCatchExecutionError() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        ErrorNoLogicEngine engine = new ErrorNoLogicEngine();

        engine = (ErrorNoLogicEngine) engine.setTask(task);

        engine.run();

        Assertions.assertEquals(FlowTaskState.ERROR, engine.getTask().getState(), "Expected the task to have status error, but this was not the case.");
        Assertions.assertTrue(engine.getLogList().stream().anyMatch(log -> FlowLogType.ERR.equals(log.getLogType()) && log.getMessage().contains("No execution logic provided")));
    }

    @Test
    void testChangeTaskStatus() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        SuccessNoLogicEngine engine = new SuccessNoLogicEngine();

        engine = (SuccessNoLogicEngine) engine.setTask(task);

        engine.run();

        Assertions.assertEquals(FlowTaskState.FINISHED, engine.getTask().getState(), "Expected the task to have status finished, but this was not the case.");
        Assertions.assertTrue(engine.getTask().getLogList().stream().filter(abstractLog -> FlowLogType.ERR.equals(abstractLog.getLogType())).findAny().isEmpty());
    }

    @Test
    void testDefaultExecuteNoLogic() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        DefaultNoLogicEngine engine = new DefaultNoLogicEngine();

        engine = (DefaultNoLogicEngine) engine.setTask(task);

        engine.run();

        Assertions.assertEquals(FlowTaskState.ERROR, engine.getTask().getState());
        Assertions.assertTrue(engine.getTask().getLogList().stream().anyMatch(abstractLog -> FlowLogType.ERR.equals(abstractLog.getLogType())));
        Assertions.assertTrue(engine.getTask().getLogList().stream().filter(abstractLog -> FlowLogType.ERR.equals(abstractLog.getLogType())).findFirst().get().getMessage().contains("No execution logic provided"));
    }

    static class NoValidationEngine extends AbstractEngine {
        @Override
        public void initialize() {
        }

        @Override
        public void handleError() {
            // do nothing
        }
    }

    @Test
    void testDefaultValidationNoLogic() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        NoValidationEngine engine = new NoValidationEngine();

        engine = (NoValidationEngine) engine.setTask(task);

        engine.run();
        Assertions.assertEquals(FlowTaskState.ERROR, engine.getTask().getState());
        Assertions.assertTrue(engine.getTask().getLogList().stream().anyMatch(log -> FlowLogType.ERR.equals(log.getLogType())));
    }

    static class NoErrorLogicEngine extends AbstractEngine {
        @Override
        protected void validate() throws FlowEngineException {
            // skip validation
        }

        @Override
        public void execute() throws FlowEngineException {
            throw new FlowEngineException(FlowErrorCode.NO_EXECUTION_LOGIC);
        }

        @Override
        public void initialize() {
        }
    }

    @Test
    void testDefaultHandleErrorNoLogic() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        NoErrorLogicEngine engine = new NoErrorLogicEngine();

        engine = (NoErrorLogicEngine) engine.setTask(task);

        engine.run();
        Assertions.assertEquals(FlowTaskState.ERROR, engine.getTask().getState());
        Assertions.assertTrue(engine.getTask().getLogList().stream().anyMatch(log -> FlowLogType.ERR.equals(log.getLogType())));
        Assertions.assertTrue(engine.getTask().getLogList().stream().filter(log -> FlowLogType.ERR.equals(log.getLogType())).findFirst().get().getMessage().contains("No error handling logic provided"));
    }

    static class JdbcTxEngine extends AbstractEngine {
        private final Connection connection;
        private final long taskId;
        private final boolean failExecution;

        JdbcTxEngine(Connection connection, long taskId, boolean failExecution) {
            this.connection = connection;
            this.taskId = taskId;
            this.failExecution = failExecution;
        }

        @Override
        public void initialize() {
        }

        @Override
        protected void validate() {
            // no validation needed for this test
        }

        @Override
        public void execute() throws FlowEngineException {
            if (failExecution) {
                throw new FlowEngineException(FlowErrorCode.NO_EXECUTION_LOGIC);
            }
        }

        @Override
        public void handleError() {
            // no-op
        }

        @Override
        protected Connection getJdbcConnection() {
            return connection;
        }

        @Override
        protected Long getPersistentTaskId() {
            return taskId;
        }
    }

    private void prepareTaskRecords(Statement statement, String serviceCode, long instanceId, long taskId) throws Exception {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS flow_services(code VARCHAR(100) PRIMARY KEY)");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS flow_instances(id BIGINT PRIMARY KEY, service_id VARCHAR(100) NOT NULL)");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS flow_tasks(id BIGINT PRIMARY KEY, instance_id BIGINT NOT NULL, service_code VARCHAR(100) NOT NULL, state VARCHAR(50) NOT NULL)");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS flow_task_state_logs(id BIGINT AUTO_INCREMENT PRIMARY KEY, task_id BIGINT NOT NULL, new_state VARCHAR(50) NOT NULL, updated_at TIMESTAMP NOT NULL, updated_by VARCHAR(255) NOT NULL)");
        statement.executeUpdate("CREATE TRIGGER IF NOT EXISTS trg_flow_task_state_logs_ins AFTER INSERT ON flow_tasks FOR EACH ROW CALL \"wfm.util.TaskStateLogH2Trigger\"");
        statement.executeUpdate("CREATE TRIGGER IF NOT EXISTS trg_flow_task_state_logs AFTER UPDATE ON flow_tasks FOR EACH ROW CALL \"wfm.util.TaskStateLogH2Trigger\"");

        statement.executeUpdate("DELETE FROM flow_task_state_logs");
        statement.executeUpdate("DELETE FROM flow_tasks");
        statement.executeUpdate("DELETE FROM flow_instances");
        statement.executeUpdate("DELETE FROM flow_services");

        statement.executeUpdate("INSERT INTO flow_services(code) VALUES('" + serviceCode + "')");
        statement.executeUpdate("INSERT INTO flow_instances(id, service_id) VALUES(" + instanceId + ", '" + serviceCode + "')");
        statement.executeUpdate("INSERT INTO flow_tasks(id, instance_id, service_code, state) VALUES(" + taskId + ", " + instanceId + ", '" + serviceCode + "', 'RG')");
    }

    @Test
    void testTransactionCommitOnSuccess() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:engine-tx-success;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "");
             Statement statement = connection.createStatement()) {
            prepareTaskRecords(statement, "TEST", 1L, 100L);

            SimpleService service = new SimpleService("TEST");
            SimpleInstance instance = new SimpleInstance(service);
            SimpleTask task = new SimpleTask(instance);
            JdbcTxEngine engine = new JdbcTxEngine(connection, 100L, false);

            engine = (JdbcTxEngine) engine.setTask(task);

            engine.run();
            Assertions.assertEquals(FlowTaskState.FINISHED, engine.getTask().getState());

            ResultSet rs = statement.executeQuery("SELECT state FROM flow_tasks WHERE id=100");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("FIN", rs.getString("state"));

            // Validate exact state transition sequence: RG → PND → EX → FIN
            ResultSet logRs = statement.executeQuery("SELECT new_state, updated_at, updated_by FROM flow_task_state_logs WHERE task_id=100 ORDER BY id");
            String[] expectedSequence = {"RG", "PND", "EX", "FIN"};
            int index = 0;
            
            while (logRs.next() && index < expectedSequence.length) {
                String newState = logRs.getString("new_state");
                Assertions.assertEquals(expectedSequence[index], newState, 
                    "State transition " + (index + 1) + ": expected " + expectedSequence[index] + " but got " + newState);
                Assertions.assertNotNull(logRs.getTimestamp("updated_at"), 
                    "State log for " + newState + ": updated_at should not be null");
                Assertions.assertFalse(logRs.getString("updated_by").isBlank(), 
                    "State log for " + newState + ": updated_by should not be blank");
                index++;
            }
            
            Assertions.assertEquals(expectedSequence.length, index, 
                "Expected " + expectedSequence.length + " state transitions but found " + index);
        }
    }

    @Test
    void testTransactionRollbackOnError() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:engine-tx-rollback;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "");
             Statement statement = connection.createStatement()) {
            prepareTaskRecords(statement, "TEST", 2L, 200L);

            SimpleService service = new SimpleService("TEST");
            SimpleInstance instance = new SimpleInstance(service);
            SimpleTask task = new SimpleTask(instance);
            JdbcTxEngine engine = new JdbcTxEngine(connection, 200L, true);

            engine = (JdbcTxEngine) engine.setTask(task);

            engine.run();
            Assertions.assertEquals(FlowTaskState.ERROR, engine.getTask().getState());

            ResultSet rs = statement.executeQuery("SELECT state FROM flow_tasks WHERE id=200");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("ERR", rs.getString("state"));

            // Validate exact state transition sequence: RG → PND → EX → ERR
            ResultSet logRs = statement.executeQuery("SELECT new_state, updated_at, updated_by FROM flow_task_state_logs WHERE task_id=200 ORDER BY id");
            String[] expectedSequence = {"RG", "PND", "EX", "ERR"};
            int index = 0;
            
            while (logRs.next() && index < expectedSequence.length) {
                String newState = logRs.getString("new_state");
                Assertions.assertEquals(expectedSequence[index], newState, 
                    "State transition " + (index + 1) + ": expected " + expectedSequence[index] + " but got " + newState);
                Assertions.assertNotNull(logRs.getTimestamp("updated_at"), 
                    "State log for " + newState + ": updated_at should not be null");
                Assertions.assertFalse(logRs.getString("updated_by").isBlank(), 
                    "State log for " + newState + ": updated_by should not be blank");
                index++;
            }
            
            Assertions.assertEquals(expectedSequence.length, index, 
                "Expected " + expectedSequence.length + " state transitions but found " + index);
        }
    }

    @Test
    void testEngineHasErrorReturnsFalseOnEmptyLogList() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        SuccessNoLogicEngine engine = new SuccessNoLogicEngine();
        engine.setTask(task);
        // logList is empty before run() — CollectionUtils.isNotEmpty returns false, engineHasError bypasses the stream and returns false
        Assertions.assertFalse(engine.engineHasError());
    }

    @Test
    void testEngineHasErrorReturnsTrueWhenErrorLogPresent() throws FlowEngineException {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        ErrorNoLogicEngine engine = new ErrorNoLogicEngine();
        engine.setTask(task);
        engine.run();
        // After a failed run the engine's logList retains the ERR entry — engineHasError must return true
        Assertions.assertTrue(engine.engineHasError());
    }
}
