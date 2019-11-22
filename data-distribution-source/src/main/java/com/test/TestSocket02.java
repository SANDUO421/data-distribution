package com.test;

/**
 * @author 三多
 * @Time 2019/11/20
 */
public class TestSocket02 {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            //new Nksocket("192.168.100.4",2005,new Buffer(20000),new Buffer(20000)).start();
            new Nksocket("192.168.20.155",9888,new Buffer(20000),new Buffer(20000)).start();
        }
    }
}
