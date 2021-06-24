package com.revature.service;

import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.repositories.PersistenceLayer;
import com.revature.util.Column;
import com.revature.util.Database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RevinService {
    final private static RevinService rserv = new RevinService();
    List<Metamodel<?>> currClasses;
    Map<Metamodel<?>, List<Object>> cache;
    PersistenceLayer persist;
    boolean dbChanged;

    private RevinService() {
        persist = new PersistenceLayer(new ConnectionFactory(new Database()));
        currClasses = new ArrayList<>();
        cache = new HashMap<>();
        dbChanged = false;
    }

    public static RevinService getInstance() {
        return rserv;
    }

    private boolean classIsPersisted(Class<?> clazz) {
        for (Metamodel mm : currClasses) {
            if (mm.getClassName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean addClass(Class<?> clazz) {
        if (classIsPersisted(clazz)) {
            return false;
        }

        Metamodel mm = new Metamodel(clazz);
        persist.createTable(mm);
        currClasses.add(mm);
        return true;
    }

    public boolean addObject(Class<?> clazz, Object o) {
        if (!classIsPersisted(clazz) || !o.getClass().equals(clazz)) {
            return false;
        }

        for (Metamodel mm : currClasses) {
            if (mm.getClassName().equals(clazz.getName())) {
                int id = persist.addObject(mm, o);
                mm.setPrimaryKey(clazz, o, id);
                dbChanged = true;
            }
        }

        return true;
    }

    public <T> List<T> getList(Class<T> clazz) {
        if (!classIsPersisted(clazz)) {
            return null;
        }

        if (cached(clazz) && !dbChanged) {
            return (List<T>)cache.get(clazz);
        }

        Metamodel mm = new Metamodel(clazz);
        dbChanged = false;
        return (List<T>)persist.readAllObject(mm);
    }

    public <T> T get(Class<T> clazz, int primaryKey) {
        if (!classIsPersisted(clazz)) {
            return null;
        }

        Metamodel mm = new Metamodel(clazz);
        dbChanged = false;
        return (T)persist.readObject(mm, primaryKey);
    }

    private boolean cached(Class<?> clazz) {
        if (cache.containsKey(clazz)) {
            return true;
        }

        return false;
    }

    public boolean updateObj(Class<?> clazz, Object o) {
        if (!classIsPersisted(clazz) || !o.getClass().equals(clazz)) {
            return false;
        }

        for (Metamodel mm : currClasses) {
            if (mm.getClassName().equals(clazz.getName())) {
                //persist.updateObject(mm, o);
                dbChanged = true;
            }
        }

        return true;
    }

    public boolean removeObj(Class<?> clazz, Object o) {
        if (!classIsPersisted(clazz) || !o.getClass().equals(clazz)) {
            return false;
        }

        for (Metamodel mm : currClasses) {
            if (mm.getClassName().equals(clazz.getName())) {
                persist.deleteObject(mm, o);
                dbChanged = true;
            }
        }

        return true;
    }

}
