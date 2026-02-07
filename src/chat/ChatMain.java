package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatMain {

    public static void main(String[] args) {
        System.out.println("Chat started");

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Scanner scanner = null;

        try {
            String host = "localhost";
            int port = 5000;

            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(System.in);

            System.out.println("Enter connection type (CHAT/CLIENT/SHUTDOWN): ");
            String type = scanner.nextLine();
            if (type == null || type.isEmpty()) {
                type = "CHAT";
            }

            if (type.equalsIgnoreCase("CHAT")) {
                out.println("TYPE:CHAT");
            } else if (type.equalsIgnoreCase("CLIENT")) {
                out.println("TYPE:CLIENT");
                System.out.print("Login: ");
                String login = scanner.nextLine();
                System.out.print("Password: ");
                String pass = scanner.nextLine();
                out.println("LOGIN:" + login);
                out.println("PASSWORD:" + pass);
            } else if (type.equalsIgnoreCase("SHUTDOWN")) {
                out.println("TYPE:SHUTDOWN");
                out.println("SECRET:12345");
                System.out.println("Shutdown command sent from chat module");
            } else {
                System.out.println("Unknown type, defaulting to CHAT");
                out.println("TYPE:CHAT");
            }

            System.out.println("Waiting for messages from server...");
            String msg;
            boolean stop = false;
            while (!stop && (msg = in.readLine()) != null) {
                System.out.println("[SERVER] " + msg);

                if (msg.startsWith("INFO:")) {
                    if (msg.contains("shutting down")) {
                        System.out.println("Server is shutting down, closing chat...");
                        stop = true;
                    }
                }

                if (type.equalsIgnoreCase("CHAT")) {
                    System.out.print("Enter message (or 'exit' to quit): ");
                    String userMsg = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(userMsg)) {
                        stop = true;
                    } else {
                        out.println(userMsg);
                    }
                }
            }

            System.out.println("Chat finished");

        } catch (IOException e) {
            System.out.println("Chat error: " + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {}
            if (out != null) {
                out.close();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignored) {}
        }
    }
}