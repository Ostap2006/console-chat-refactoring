package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {

        System.out.println("Client started");

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket("localhost", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.print("Enter login: ");
            String login = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            out.println("TYPE:CLIENT");

            if (login != null && !login.isEmpty()) {
                out.println("LOGIN:" + login);
            } else {
                out.println("LOGIN:guest");
            }

            if (password != null && !password.isEmpty()) {
                out.println("PASSWORD:" + password);
            } else {
                out.println("PASSWORD:123");
            }

            String response;
            boolean authenticated = false;

            while ((response = in.readLine()) != null) {
                System.out.println("[SERVER] " + response);

                if (response.equals("INFO:AUTH_OK")) {
                    authenticated = true;
                    System.out.println("Authentication successful");
                    break;
                } else if (response.equals("INFO:AUTH_FAILED")) {
                    System.out.println("Authentication failed");
                    socket.close();
                    return;
                } else if (response.startsWith("INFO:")) {
                } else {
                }
            }

            if (!authenticated) {
                System.out.println("No AUTH_OK received, closing client");
                socket.close();
                return;
            }

            Thread reader = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("[CHAT] " + msg);

                        if (msg.contains("left the chat")) {
                            System.out.println("Someone left, but we don't track who");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reader error: " + e.getMessage());
                }
            });

            reader.start();

            while (true) {
                System.out.print("Enter message (exit to quit): ");
                String text = scanner.nextLine();

                if (text.equals("exit")) {
                    out.println("EXIT");
                    break;
                } else if (text.startsWith("/pm ")) {
                    out.println("MSG:" + text);
                } else if (text.startsWith("/shout ")) {
                    out.println("MSG:" + text.toUpperCase());
                } else if (text.startsWith("/help")) {
                    System.out.println("Commands: exit, /pm, /shout, /help");
                } else {
                    out.println("MSG:" + text);
                }
            }

            System.out.println("Client finished");

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException ignored) {}
            if (out != null) out.close();
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }
}