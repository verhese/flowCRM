package wfm.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wfm.config.impl.DefaultConfig;

class FlowStateConfigTest {

    @Test
    void testFlowInstanceStateProperties() {
        Assertions.assertEquals("CR", FlowInstanceState.CREATED.getCode());
        Assertions.assertFalse(FlowInstanceState.CREATED.isExecutable());
        Assertions.assertFalse(FlowInstanceState.CREATED.isError());

        Assertions.assertEquals("CL", FlowInstanceState.CLOSED.getCode());
        Assertions.assertTrue(FlowInstanceState.CLOSED.isExecutable());
        Assertions.assertFalse(FlowInstanceState.CLOSED.isError());

        Assertions.assertEquals("ERR", FlowInstanceState.ERROR.getCode());
        Assertions.assertFalse(FlowInstanceState.ERROR.isExecutable());
        Assertions.assertTrue(FlowInstanceState.ERROR.isError());
        Assertions.assertFalse(FlowInstanceState.ERROR.toString().isBlank());
    }

    @Test
    void testFlowTaskStateProperties() {
        Assertions.assertEquals("RG", FlowTaskState.REGISTERED.getCode());
        Assertions.assertTrue(FlowTaskState.REGISTERED.isExecutable());
        Assertions.assertFalse(FlowTaskState.REGISTERED.isError());
        Assertions.assertFalse(FlowTaskState.REGISTERED.isFinished());

        Assertions.assertEquals("DEX", FlowTaskState.DIRECT_EXECUTION.getCode());
        Assertions.assertTrue(FlowTaskState.DIRECT_EXECUTION.isExecutable());

        Assertions.assertEquals("REX", FlowTaskState.DELAYED_EXECUTION.getCode());
        Assertions.assertTrue(FlowTaskState.DELAYED_EXECUTION.isExecutable());

        Assertions.assertEquals("ERR", FlowTaskState.ERROR.getCode());
        Assertions.assertTrue(FlowTaskState.ERROR.isExecutable());
        Assertions.assertTrue(FlowTaskState.ERROR.isError());
        Assertions.assertFalse(FlowTaskState.ERROR.isFinished());

        Assertions.assertEquals("PND", FlowTaskState.PENDING.getCode());
        Assertions.assertFalse(FlowTaskState.PENDING.isExecutable());

        Assertions.assertEquals("EX", FlowTaskState.EXECUTING.getCode());
        Assertions.assertFalse(FlowTaskState.EXECUTING.isExecutable());

        Assertions.assertEquals("WT", FlowTaskState.WAITING_ON_REPLY.getCode());
        Assertions.assertFalse(FlowTaskState.WAITING_ON_REPLY.isExecutable());

        Assertions.assertEquals("FIN", FlowTaskState.FINISHED.getCode());
        Assertions.assertFalse(FlowTaskState.FINISHED.isExecutable());
        Assertions.assertFalse(FlowTaskState.FINISHED.isError());
        Assertions.assertTrue(FlowTaskState.FINISHED.isFinished());
        Assertions.assertFalse(FlowTaskState.FINISHED.toString().isBlank());

        Assertions.assertEquals("REJ", FlowTaskState.REJECTED.getCode());
        Assertions.assertTrue(FlowTaskState.REJECTED.isError());
        Assertions.assertTrue(FlowTaskState.REJECTED.isFinished());
    }

    @Test
    void testDefaultConfigValues() {
        DefaultConfig config = new DefaultConfig();

        Assertions.assertEquals(FlowTaskState.FINISHED, config.getSuccessState());
        Assertions.assertEquals(FlowTaskState.WAITING_ON_REPLY, config.getWaitState());
        Assertions.assertEquals(FlowTaskState.ERROR, config.getErrState());
        Assertions.assertFalse(config.getResetSateOnError());
    }
}
