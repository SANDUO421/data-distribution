package com.test;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author 三多
 * @Time 2019/11/23
 */
public class FileSocketServer {

    public static void main(String[] args) {
        //为了简单起见，所有的异常信息都往外抛
        int port = 9988;
        //定义一个ServerSocket监听在端口9999上
        try {
            ServerSocket server = new ServerSocket(port);
            InputStream inputStream=new FileInputStream("d:\\aaa.txt");
            byte[] data=new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = server.accept();
            while (true) {
                //每接收到一个Socket就建立两个个新的线程来处理它
                for (int i = 0; i < 500; i++) {
                    new Thread(new FileSocketServer.Task(socket,data)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来处理Socket请求的
     */
    static class Task implements Runnable {

        private Socket socket;
        private byte[] data;

        public Task(Socket socket,byte[] data) {
            this.socket = socket;
            this.data=data;
        }

        @Override
        public void run() {
            try {
                handleSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 跟客户端Socket进行通信
         *
         * @throws Exception
         */
        private void handleSocket()  {

            try {
                OutputStream outputStream = socket.getOutputStream();
                while (socket.isConnected()) {
                    outputStream.write(data);
                    outputStream.flush();
                }
                outputStream.close();
            }catch (Exception e){
                //TODO
                System.out.println("一个连接已断开");

            }

        }
    }

}
