package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import shared.RegisterRequest;
import shared.RegisterResult;
import dataaccess.DataAccessException;

public class UserServiceTest {

    @Test
    public void testRegisterSuccess() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest request = new RegisterRequest("newuser", "password", "email@example.com");
        RegisterResult result = service.register(request);
        assertNotNull(result.authToken());
        assertEquals("newuser", result.username());
    }

    @Test
    public void testRegisterDuplicateUser() throws DataAccessException {
        UserService service = new UserService();
        RegisterRequest request = new RegisterRequest("user", "pass", "email@example.com");
        service.register(request);
        RegisterResult result = service.register(request);
        assertEquals("Error: already taken", result.message());
    }
}
