package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import shared.*;
import dataaccess.DataAccessException;

public class LogoutServiceTest {

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        UserService userService = new UserService();
        RegisterResult reg = userService.register(new RegisterRequest("logoutUser", "password", "mail@mail.com"));
        LogoutService service = new LogoutService();
        LogoutResult result = service.logout(reg.authToken());
        assertNull(result.message());
    }

    @Test
    public void testLogoutWithInvalidToken() {
        LogoutService service = new LogoutService();
        LogoutResult result = service.logout("invalidToken");
        assertEquals("Error: unauthorized", result.message());
    }
}
