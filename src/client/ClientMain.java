package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);

            System.out.print("Login: ");
            String login = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            out.println("TYPE:CLIENT");
            out.println("LOGIN:" + login);
            out.println("PASSWORD:" + pass);

            String response;
            boolean authenticated = false;

            while ((response = in.readLine()) != null) {
                System.out.println(response);

                if (response.equals("INFO:AUTH_OK")) {
                    authenticated = true;
                    System.out.println("You are now connected. Type your message below:");
                    break;
                }

                if (response.equals("INFO:AUTH_FAILED")) {
                    System.out.println("Authentication failed");
                    socket.close();
                    return;
                }
            }

            if (!authenticated) {
                System.out.println("No AUTH_OK received");
                socket.close();
                return;
            }

            Thread reader = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                    System.out.println("Reader stopped: server closed connection");
                } catch (IOException e) {
                    System.out.println("Reader error: " + e.getMessage());
                }
            });

            reader.start();

            while (true) {
                String text = sc.nextLine();
                if (text.equals("exit")) {
                    out.println("EXIT");
                    break;
                }
                out.println("MSG:" + text);
            }

            socket.close();

        } catch (IOException e) {
            System.out.println("Connection error");
        }
    }
}