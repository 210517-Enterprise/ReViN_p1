package com.revature.service;

import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.repositories.PersistenceLayer;
import com.revature.util.Database;

import java.sql.Connection;
import java.util.List;

public class RevinService {
    final private static RevinService rserv = new RevinService();
    List<Metamodel<?>> currClasses;
    PersistenceLayer persist;

    private RevinService() {
        persist = new PersistenceLayer(new ConnectionFactory(new Database()));
    }

    public static RevinService getInstance() {
        return rserv;
    }

    public void addClass(Class<?> clazz) {
        for (Metamodel mm : currClasses) {
            if (mm.getClassName().equals(clazz.getName())) {
                throw new RuntimeException("Already existing class");
            }
        }

        Metamodel mm = new Metamodel(clazz);
        persist.createTable(mm);
        currClasses.add(mm);
    }

    public void addObject(Class<?> clazz, Object o) {
        if (!o.getClass().equals(clazz)) {
            throw new RuntimeException("Wrong class and Object");
        }

        for (Metamodel mm : currClasses) {
            if (mm.getClassName().equals(clazz.getName())) {
                persist.addObject(mm, o);
            }
        }
    }

    public void getList(Class<?> clazz) {
        //to do
    }

    public void updateObj(Object o, String features) {
        //to do
    }

    public void removeObj(Object o) {
        //to do
    }

}
