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

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceLayerTests {
	PersistenceLayer persist = new PersistenceLayer(new ConnectionFactory(new Database()));
	Metamodel mm = new Metamodel(User.class);
	
    @Test
    public void test_0CreateTable() {
        persist.createTable(mm);
    }

    @Test
    public void test_1CreateTableWithForeignKey() {
        Metamodel mm = new Metamodel(User.class);
        Metamodel mm2 = new Metamodel(Account.class);
        persist.createTable(mm);
        persist.createTable(mm2);

        User u = new User("John", "Doe");
        Account a = new Account(1, 1_000_000.00);
        int user_id = persist.addObject(mm, u);
        int acc_id = persist.addObject(mm2, a);

        assertEquals(1, user_id);
        assertEquals(1, acc_id);
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
    
    public static void printUser(User u) {
    	System.out.println("[id = " + u.getId() 
		 + ", username = " + u.getUsername() 
		 + ", password = " + u.getPassword()
		 + ", isCitizen = " + u.isCitizen()
		 + ", accounts = " + u.getAccounts()
		 + ", netWorth = " + u.getNetWorth() +"]");
    }
    
    @Test
    public void test_4readAllUser() {
    	for (Object o : persist.readAllObject(mm))
    		printUser((User) o);
    }
    
    @Test
    public void test_5readUser() {
    	printUser((User) persist.readObject(mm, 3));
    }
}