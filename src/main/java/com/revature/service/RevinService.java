package com.revature.service;

public class RevinService {
    final private static RevinService rserv = new RevinService();

    private RevinService() {

    }

    public static RevinService getInstance() {
        return rserv;
    }

    public void addClass(Class<?> clazz) {
        //to do
    }

    public void getList() {
        //to do
    }

    public void updateObj(Object o, String features) {
        //to do
    }

    public void removeObj(Object o) {
        //to do
    }

}
