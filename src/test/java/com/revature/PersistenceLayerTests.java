package com.revature;

import org.junit.Test;

import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.models.User;
import com.revature.repositories.PersistenceLayer;
import com.revature.util.Database;

public class PersistenceLayerTests {
	PersistenceLayer persist = new PersistenceLayer(new ConnectionFactory(new Database()));
	
    @Test
    public void test_CreateTable() {

        Metamodel mm = new Metamodel(User.class);
        persist.createTable(mm);
    }

    @Test
    public void test_AddUserToTable() {
        User u = new User(1,"John", "Doe");
        Metamodel mm = new Metamodel(User.class);
        persist.addObject(mm, u);
    }

    @Test
    public void test_AddSerialUsersToTable() {
        User u = new User("Joe", "Doe");
        User u2 = new User("Mary", "Jane");
        User u3 = new User("Karen", "Ashley");
        Metamodel mm = new Metamodel(User.class);
        persist.addObject(mm, u);
        persist.addObject(mm, u2);
        persist.addObject(mm, u3);
    }
}
