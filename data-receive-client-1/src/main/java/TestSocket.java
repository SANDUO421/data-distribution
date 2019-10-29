import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author 三多
 * @Time 2019/10/28
 */
public class TestSocket {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.1.207", 9888);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF("tourist,tourist@#lydsj");

        String s = dataInputStream.readUTF();
        System.out.println(s);
    }
}
