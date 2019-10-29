import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
/**
 * @description:    数据源
 * @author:         sanduo
 * @date:           2019/11/1 19:30
 * @version:        1.0
 */
public class SocketServer {

    public static Date now = new Date();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

    public static void main(String[] args) throws IOException {
        //为了简单起见，所有的异常信息都往外抛
        int port = 9999;
        //定义一个ServerSocket监听在端口9999上
        ServerSocket server = new ServerSocket(port);
        while (true) {
            //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = server.accept();
            //每接收到一个Socket就建立两个个新的线程来处理它
            new Thread(new Task(socket)).start();
            new Thread(new Task(socket)).start();
        }
    }

    /**
     * 用来处理Socket请求的
     */
    static class Task implements Runnable {

        private Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
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
//               Writer writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
               DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

               String str = "adasdasdada";
               while (socket.isConnected()) {
                   str = getData();
                   System.out.println(str);
                   dataOutputStream.writeUTF(str);
//                   writer.write(str);
                   dataOutputStream.flush();
//                   writer.flush();
//                   System.out.println(str);
//                   System.out.println("发送消息中");
//                   Thread.sleep(500L);
               }
               dataOutputStream.close();
//               writer.close();
               socket.close();

           }catch (Exception e){
//               System.out.println("一个连接已断开");
               e.printStackTrace();
           }

        }

        public static String getData() {
            return String.valueOf((new Random()).nextInt(27)) + "," + dateFormat.format(now) + "," + getRandom(5) + "," +
                    "" + getRandom(20) + ",1" + getRandom(10) + ",000000000000000" + "" +
                    ",4600" + getRandom(11) + ",1" + getRandom(4) + ",1" + getRandom(4)+"\n";
        }

        public static String getRandom(int a) {
            String res = "";
            for (int i = 0; i < a; i++) {
                res += String.valueOf((new Random()).nextInt(10));
            }

            return res;
        }
    }

}
