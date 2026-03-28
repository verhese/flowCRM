package wfm.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlowErrorCodeTest {

    @Test
    void testFromCodeWithNull() {
        Assertions.assertNull(FlowErrorCode.fromCode(null));
    }
}
