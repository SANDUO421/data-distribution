//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class Nksocket extends Thread {
    public String ip = null;
    public Integer port = null;
    private Socket socket = null;
    private boolean close = false;
    private Integer sotimeout = 10;
    private Buffer rb = null;
    private Buffer mrb = null;

    public Nksocket(String ip, Integer port, Buffer rb, Buffer mrb) {
        this.setIp(ip);
        this.setPort(port);
        this.rb = rb;
        this.mrb = mrb;
        this.init();
    }

    public void init() {
        try {
            InetAddress address = InetAddress.getByName(this.getIp());
            this.socket = new Socket(address, this.getPort());
            System.out.println(new Date() + " 与IP=" + this.getIp() + ",Port=" + this.getPort() + " 服务器连接成功\n");
            this.socket.setKeepAlive(true);
            this.socket.setSoTimeout(this.sotimeout);
            this.close = !this.Send(this.socket, "2");
//            OutputStream outputStream = socket.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//            dataOutputStream.writeUTF("tourist,tourist@#lydsj");
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            this.close = this.isServerClose(this.socket);
            if (!this.close) {
                this.ReadAndSaveText(this.socket);
            }

            while (this.close) {
                try {
                    System.out.println(new Date() + "重新建立连接：" + this.getIp() + ":" + this.getPort());
                    InetAddress address = InetAddress.getByName(this.getIp());
                    this.socket = new Socket(address, this.getPort());
                    this.socket.setKeepAlive(true);
                    this.socket.setSoTimeout(this.sotimeout);
                    this.close = !this.Send(this.socket, "2");
//                    OutputStream outputStream = socket.getOutputStream();
//                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//                    dataOutputStream.writeUTF("tourist,tourist@#lydsj");
                    System.out.println(new Date() + "建立连接成功：" + this.getIp() + ":" + this.getPort());
                } catch (Exception var2) {
                    System.out.println(new Date() + "创建连接失败:" + this.getIp() + ":" + this.getPort());
                    this.close = true;
                }
            }
        }
    }

    public Boolean Send(Socket csocket, String message) {
        try {
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(message);
            return true;
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public String ReadText(Socket csocket) {
        try {
            csocket.setSoTimeout(this.sotimeout);
            InputStream input = csocket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            char[] sn = new char[1000];
            in.read(sn);
            String sc = new String(sn);
            return sc;
        } catch (IOException var6) {
            return null;
        }
    }

    public void ReadAndSaveText(Socket csocket) {
        String line = null;

        try {
            csocket.setSoTimeout(120000);
            System.out.println(new Date() + "服务器IP和端口为: " + csocket.getRemoteSocketAddress() + "\n");
  //          BufferedReader is = new BufferedReader(new InputStreamReader(csocket.getInputStream(), "UTF-8"));
            BufferedReader is = new BufferedReader(new InputStreamReader(csocket.getInputStream(), "iso8859-1"));

            while ((line = is.readLine()) != null) {
                System.out.println(line);
//                if (this.rb != null) {
//                    this.rb.push(Utils.filterUnNumber(line));
//                }
//                if (this.mrb != null) {
//                    this.mrb.push(Utils.filterUnNumber(line));
//                }
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(255);
            return false;
        } catch (Exception var3) {
            return true;
        }
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}