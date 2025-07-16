package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import shared.*;

public class ClearServiceTest {

    @Test
    public void testClearRemovesAllData() {
        ClearService service = new ClearService();
        ClearResult result = service.clearAll();
        assertEquals("Clear succeeded", result.message());
    }

    @Test
    public void testClearDoesNotThrow() {
        ClearService service = new ClearService();
        assertDoesNotThrow(service::clearAll);
    }
}
