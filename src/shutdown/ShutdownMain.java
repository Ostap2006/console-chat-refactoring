package shutdown;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ShutdownMain {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("TYPE:SHUTDOWN");
            out.println("SECRET:12345");

            socket.close();
            System.out.println("Shutdown command sent");

        } catch (IOException e) {
            System.out.println("Cannot connect to server");
        }
    }
}