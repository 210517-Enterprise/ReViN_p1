package com.revature;

import com.revature.models.Account;
import com.revature.models.User;
import com.revature.service.RevinService;
import org.junit.Before;
import org.junit.Test;

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
}
