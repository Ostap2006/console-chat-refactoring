package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Server server;
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

            String first = in.readLine();
            if (first == null) return;

            if (first.startsWith("TYPE:")) {
                if (first.equals("TYPE:CLIENT")) {
                    String l = in.readLine();
                    String p = in.readLine();

                    if (l != null && l.startsWith("LOGIN:")) {
                        login = l.substring(6);
                    } else {
                        login = "unknown";
                    }

                    String pass = "";
                    if (p != null && p.startsWith("PASSWORD:")) {
                        pass = p.substring(9);
                    }

                    if (server.checkAuth(login, pass)) {
                        type = "CLIENT";
                        out.println("INFO:AUTH_OK");
                        server.broadcast("INFO:" + login + " joined the chat");
                        server.addClient(this);
                    } else {
                        out.println("INFO:AUTH_FAILED");
                        close();
                        return;
                    }

                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("MSG:")) {
                            String msg = line.substring(4);
                            server.broadcast("CHAT:" + login + ":" + msg);
                        } else if (line.equals("EXIT")) {
                            server.broadcast("INFO:" + login + " left the chat");
                            break;
                        } else if (line.startsWith("LOGIN:")) {
                            login = line.substring(6);
                        } else if (line.startsWith("PASSWORD:")) {
                            String pass2 = line.substring(9);
                        } else if (line.startsWith("TYPE:")) {
                        } else {
                        }
                    }

                } else if (first.equals("TYPE:CHAT")) {
                    type = "CHAT";
                    server.addChat(this);

                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.length() > 0) {
                            server.broadcast("CHAT:CHAT_CLIENT:" + line);
                        }
                    }

                } else if (first.equals("TYPE:SHUTDOWN")) {
                    String secret = in.readLine();
                    if (secret != null && secret.equals("SECRET:12345")) {
                        server.broadcast("INFO:Server is shutting down");
                        server.shutdown();
                    }
                }
            }

        } catch (IOException e) {
        } finally {
            close();
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