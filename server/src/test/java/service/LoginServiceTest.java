package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import shared.*;
import dataaccess.DataAccessException;

public class LoginServiceTest {

    @Test
    public void testLoginSuccess() throws DataAccessException {
        UserService userService = new UserService();
        userService.register(new RegisterRequest("loginUser", "password", "mail@mail.com"));
        LoginService service = new LoginService();
        LoginRequest request = new LoginRequest("loginUser", "password");
        LoginResult result = service.login(request);
        assertNotNull(result.authToken());
        assertEquals("loginUser", result.username());
    }

    @Test
    public void testLoginInvalidPassword() {
        LoginService service = new LoginService();
        LoginRequest request = new LoginRequest("nonexistentUser", "wrongpass");
        LoginResult result = service.login(request);
        assertEquals("Error: unauthorized", result.message());
    }
}
