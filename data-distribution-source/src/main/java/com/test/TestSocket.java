package com.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author 三多
 * @Time 2019/10/28
 */
public class TestSocket {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 5; i++) {
            new SocketThread().start();
        }

    }

    private static class SocketThread extends Thread {
        private volatile Socket socket = null;
        private volatile DataInputStream inputStream = null;

        @Override
        public void run() {

            while (true) {
                try {
                    if (socket == null || socket.isClosed()) {
                        // 连接socket
                        socket = new Socket("192.168.100.4", 2005);
                        inputStream = new DataInputStream(socket.getInputStream());
                    }
                    String result = inputStream.readUTF();
                    System.out.print(result);

                } catch (IOException e) {
                    try {
                        inputStream.close();
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }


            }
        }
    }
}
