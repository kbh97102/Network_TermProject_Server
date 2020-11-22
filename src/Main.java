import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

//    private static ServerSocket socket;

    public static void main(String[] args) {
        Server server = new Server();

//        try {
//             socket = new ServerSocket(10101);
//            System.out.println(socket.getLocalSocketAddress()+"  "+socket.getLocalPort());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        new Thread(() -> {
//            while(true){
//                try {
//                    Socket client = socket.accept();
//                    System.out.println(client.isConnected());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
