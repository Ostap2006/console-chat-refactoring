package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String login;
    private String type;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line = in.readLine();
            if (line == null) return;

            if (line.equals("TYPE:CLIENT")) {
                handleClientAuth();
                if ("CLIENT".equals(type)) {
                    handleClientMessages();
                }
            } else if (line.equals("TYPE:CHAT")) {
                type = "CHAT";
                System.out.println("Chat connected");
                server.addChat(this);
                handleChat();
            } else if (line.equals("TYPE:SHUTDOWN")) {
                handleShutdown();
            }

        } catch (IOException e) {
            close();
        }
    }

    private void handleClientAuth() throws IOException {
        String loginLine = in.readLine();
        String passLine = in.readLine();

        if (loginLine == null || passLine == null) return;

        login = loginLine.substring(6);
        String password = passLine.substring(9);

        if (server.checkAuth(login, password)) {
            type = "CLIENT";
            send("INFO:AUTH_OK");
            System.out.println("Client authenticated: " + login);
            server.broadcast("INFO:" + login + " joined the chat");
            server.addClient(this);
        } else {
            send("INFO:AUTH_FAILED");
            close();
        }
    }

    private void handleClientMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MSG:")) {
                    String msg = line.substring(4);
                    server.broadcast("CHAT:" + login + ":" + msg);
                } else if (line.equals("EXIT")) {
                    server.broadcast("INFO:" + login + " left the chat");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + login);
        } finally {
            close();
        }
    }

    private void handleChat() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Chat received: " + line);
            }
        } catch (IOException e) {
            System.out.println("Chat disconnected");
        } finally {
            close();
        }
    }

    private void handleShutdown() throws IOException {
        String secret = in.readLine();
        if ("SECRET:12345".equals(secret)) {
            server.broadcast("INFO:Server is shutting down");
            server.shutdown();
        }
    }

    public void send(String msg) {
        out.println(msg);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
        server.remove(this);
    }
}