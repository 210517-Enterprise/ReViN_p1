package com.revature.tests.services;

import com.revature.models.Account;
import com.revature.models.User;
import com.revature.service.RevinService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ServiceTests {
    private RevinService service;
    @Before
    public void setup() {
        service = RevinService.getInstance();
    }

    @Test
    public void test_AddingClass() {
        assertEquals(true, service.addClass(User.class));
        assertEquals(true, service.addClass(Account.class));
        assertEquals(false, service.addClass(User.class));
    }

    @Test
    public void test_AddingWrongObject() {
        service.addClass(User.class);
        service.addClass(Account.class);
        User u = new User("Joey", "Wheeler");
        assertEquals(false, service.addObject(Account.class, u));
    }

    @Test
    public void test_AddingCorrectObject() {
        service.addClass(User.class);
        service.addClass(Account.class);
        User u = new User("Joey", "Wheeler");
        assertEquals(true, service.addObject(User.class, u));
    }

    @Test
    public void test_GetAllList() {
        service.addClass(User.class);
        User u = new User("Joe", "Doe");
        User u2 = new User("Mary", "Jane");
        User u3 = new User("Karen", "Ashley");
        service.addObject(User.class, u);
        service.addObject(User.class, u2);
        service.addObject(User.class, u3);
        List<User> list = service.getList(User.class);

        for (User user : list) {
            System.out.println(user);
        }
    }

    @Test
    public void test_GetOneObject() {
        service.addClass(User.class);
        User u = new User("Joe", "Doe");
        User u2 = new User("Mary", "Jane");
        User u3 = new User("Karen", "Ashley");
        service.addObject(User.class, u);
        service.addObject(User.class, u2);
        service.addObject(User.class, u3);

        assertEquals("Joe", service.get(User.class, 1).getUsername());
        assertEquals("Mary", service.get(User.class, 2).getUsername());
        assertEquals("Karen", service.get(User.class, 3).getUsername());
    }

    @Test
    public void test_AddAndSetCorrectPK() {
        service.addClass(User.class);
        User u = new User("Joe", "Doe");
        User u2 = new User("Mary", "Jane");
        User u3 = new User("Karen", "Ashley");
        service.addObject(User.class, u);
        service.addObject(User.class, u2);
        service.addObject(User.class, u3);

        assertEquals(1, u.getId());
        assertEquals(2, u2.getId());
        assertEquals(3, u3.getId());
    }
}
