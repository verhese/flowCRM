package wfm.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wfm.config.FlowTaskState;
import wfm.model.simple.SimpleInstance;
import wfm.model.simple.SimpleLog;
import wfm.model.simple.SimpleService;
import wfm.model.simple.SimpleTask;
import wfm.model.simple.SimpleTaskStateLog;
import wfm.pojo.FlowEngineResult;
import wfm.pojo.FlowLogType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

class ModelPojoTest {

    @Test
    void testSimpleModelConstructionAndTaskLists() {
        SimpleService service = new SimpleService("SVC");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);

        Assertions.assertEquals("SVC", service.getCode());
        Assertions.assertEquals(service, instance.getService());
        Assertions.assertEquals(instance, task.getInstance());
        Assertions.assertEquals(service, task.getService());
        Assertions.assertEquals(FlowTaskState.REGISTERED, task.getState());

        task.setState(FlowTaskState.EXECUTING);
        Assertions.assertEquals(FlowTaskState.EXECUTING, task.getState());

        List<AbstractLog> logs = new ArrayList<>();
        task.setLogList(logs);
        Assertions.assertSame(logs, task.getLogList());

        List<AbstractTaskStateLog> taskStateLogs = new ArrayList<>();
        task.setTaskStateLogList(taskStateLogs);
        Assertions.assertSame(taskStateLogs, task.getTaskStateLogList());
    }

    @Test
    void testSimpleLogWithTaskConstructorAndEquality() {
        SimpleService service = new SimpleService("SVC");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);

        SimpleLog log = new SimpleLog(instance, task, FlowLogType.LOG, "REF", "message");
        SimpleLog equalLog = new SimpleLog(instance, task, FlowLogType.LOG, "REF", "message");
        SimpleLog differentLog = new SimpleLog(instance, task, FlowLogType.ERR, "REF2", "other");

        Assertions.assertNull(log.getId());
        Assertions.assertNull(log.getSequence());
        Assertions.assertEquals(task, log.getTask());
        Assertions.assertEquals(instance, log.getInstance());
        Assertions.assertEquals(FlowLogType.LOG, log.getLogType());
        Assertions.assertEquals("REF", log.getReference());
        Assertions.assertEquals("message", log.getMessage());
        Assertions.assertNotNull(log.getTimestamp());

        Assertions.assertEquals(log, equalLog);
        Assertions.assertEquals(log.hashCode(), equalLog.hashCode());
        Assertions.assertNotEquals(log, differentLog);
        Assertions.assertNotEquals(log, null);
        Assertions.assertNotEquals(log, "not-a-log");
    }

    @Test
    void testSimpleLogWithoutTaskConstructor() {
        SimpleService service = new SimpleService("SVC");
        SimpleInstance instance = new SimpleInstance(service);

        SimpleLog log = new SimpleLog(instance, FlowLogType.INF, "REF", "message");

        Assertions.assertNull(log.getTask());
        Assertions.assertEquals(instance, log.getInstance());
        Assertions.assertEquals(FlowLogType.INF, log.getLogType());
        Assertions.assertEquals("REF", log.getReference());
        Assertions.assertEquals("message", log.getMessage());
        Assertions.assertNotNull(log.getTimestamp());
    }

    @Test
    void testTaskStateLogConstructorsAndGetters() {
        SimpleService service = new SimpleService("SVC");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        Timestamp updatedAt = Timestamp.valueOf("2026-03-28 12:34:56");

        SimpleTaskStateLog defaultLog = new SimpleTaskStateLog();
        Assertions.assertNull(defaultLog.getId());
        Assertions.assertNull(defaultLog.getTask());
        Assertions.assertNull(defaultLog.getNewState());
        Assertions.assertNull(defaultLog.getUpdatedAt());
        Assertions.assertNull(defaultLog.getUpdatedBy());

        SimpleTaskStateLog log = new SimpleTaskStateLog(task, "RG", updatedAt, "tester");
        Assertions.assertNull(log.getId());
        Assertions.assertEquals(task, log.getTask());
        Assertions.assertEquals("RG", log.getNewState());
        Assertions.assertEquals(updatedAt, log.getUpdatedAt());
        Assertions.assertEquals("tester", log.getUpdatedBy());
    }

    @Test
    void testFlowEngineResultCanBeInstantiated() {
        FlowEngineResult result = new FlowEngineResult();
        Assertions.assertNotNull(result);
    }
}
