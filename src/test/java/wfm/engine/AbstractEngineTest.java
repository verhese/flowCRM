package wfm.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wfm.config.FlowTaskState;
import wfm.engine.impl.ErrorNoLogicEngine;
import wfm.engine.impl.SuccessNoLogicEngine;
import wfm.exception.FlowEngineException;
import wfm.model.simple.SimpleInstance;
import wfm.model.simple.SimpleLog;
import wfm.model.simple.SimpleService;
import wfm.model.simple.SimpleTask;
import wfm.pojo.FlowLogType;

class AbstractEngineTest {
    @Test
    void testCatchExecutionError() {
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        ErrorNoLogicEngine engine = new ErrorNoLogicEngine();

        try {
            engine = (ErrorNoLogicEngine) engine.setTask(task);
        } catch (FlowEngineException e) {
            engine.handleError();
        }

        engine.run();

        Assertions.assertEquals(FlowTaskState.ERROR, engine.getTask().getState(), "Expected the task to have status error, but this was not the case.");
        Assertions.assertTrue(engine.getLogList().contains(new SimpleLog(instance, FlowLogType.ERR, service.getCode(), "Error")));
    }

    @Test
    void testChangeTaskStatus(){
        SimpleService service = new SimpleService("TEST");
        SimpleInstance instance = new SimpleInstance(service);
        SimpleTask task = new SimpleTask(instance);
        SuccessNoLogicEngine engine = new SuccessNoLogicEngine();

        try {
            engine = (SuccessNoLogicEngine) engine.setTask(task);
        } catch (FlowEngineException e) {
            engine.handleError();
        }

        engine.run();

        Assertions.assertEquals(FlowTaskState.FINISHED, engine.getTask().getState(), "Expected the task to have status error, but this was not the case.");
        Assertions.assertTrue(engine.getTask().getLogList().stream().filter(abstractLog -> FlowLogType.ERR.equals(abstractLog.getLogType())).findAny().isEmpty());
    }
}
