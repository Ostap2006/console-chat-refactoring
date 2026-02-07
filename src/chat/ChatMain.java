package chat;

import java.io.*;
import java.net.Socket;

public class ChatMain {
    public static void main(String[] args) {
        System.out.println("Chat started");
        try {
            Socket socket = new Socket("localhost", 5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("TYPE:CHAT");

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
            }

        } catch (IOException e) {
            System.out.println("Chat error: " + e.getMessage());
        }
    }
}