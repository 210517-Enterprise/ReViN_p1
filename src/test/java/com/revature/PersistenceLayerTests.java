package com.revature;

import com.revature.models.Account;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.models.User;
import com.revature.repositories.PersistenceLayer;
import com.revature.util.Database;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceLayerTests {
	PersistenceLayer persist = new PersistenceLayer(new ConnectionFactory(new Database()));
	Metamodel mm = new Metamodel(User.class);
	
    @Test
    public void test_0CreateTable() {
        persist.createTable(mm);
    }

    @Test
    public void test_CreateTableWithForeignKey() {
        Metamodel mm = new Metamodel(User.class);
        Metamodel mm2 = new Metamodel(Account.class);
        persist.createTable(mm);
        persist.createTable(mm2);

        User u = new User("John", "Doe");
        Account a = new Account(1, 1_000_000.00);
        persist.addObject(mm, u);
        persist.addObject(mm2, a);
    }

    public void test_1AddUserToTable() {
        User u = new User(1,"John", "Doe");
        persist.addObject(mm, u);
    }

    @Test
    public void test_2AddSerialUsersToTable() {
        User u = new User("Joe", "Doe");
        User u2 = new User("Mary", "Jane");
        User u3 = new User("Karen", "Ashley");
        persist.addObject(mm, u);
        persist.addObject(mm, u2);
        persist.addObject(mm, u3);
    }
    
    @Test
    public void test_3DeleteUser() {
    	persist.deleteObject(mm, new User(4, "Karen", "Ashley"));
    }
    
    @Test
    public void test_4readAllUser() {
    	persist.readAllObject(mm);
    }
    
    @Test
    public void test_5readUser() {
    	persist.readObject(mm, 3);
    }
}
