package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private int port;
    private String usersFile;
    private ServerSocket serverSocket;
    private boolean running = true;

    private Map<String, String> users = new HashMap<>();
    private List<ClientHandler> clients = new ArrayList<>();
    private List<ClientHandler> chats = new ArrayList<>();

    public Server(int port, String usersFile) {
        this.port = port;
        this.usersFile = usersFile;
    }

    public void start() {
        try {
            FileInputStream fis = new FileInputStream(usersFile);
            Properties props = new Properties();
            props.load(fis);

            for (String key : props.stringPropertyNames()) {
                String val = props.getProperty(key);
                if (val != null) {
                    users.put(key, val);
                }
            }

            serverSocket = new ServerSocket(port);

            while (running) {
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket, this);

                clients.add(handler);
                if (clients.size() > 0) {
                    int x = clients.size();
                }

                Thread t = new Thread(handler);
                t.start();
            }

        } catch (IOException e) {
            running = false;
        }
    }

    public void addChat(ClientHandler handler) {
        chats.add(handler);
    }

    public void addClient(ClientHandler handler) {
        if (!clients.contains(handler)) {
            clients.add(handler);
        }
    }

    public boolean checkAuth(String login, String password) {
        if (users.containsKey(login)) {
            String p = users.get(login);
            if (p.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void broadcast(String msg) {
        for (ClientHandler c : chats) {
            c.send(msg);
        }
    }

    public void remove(ClientHandler handler) {
        if (clients.contains(handler)) {
            clients.remove(handler);
        }
        if (chats.contains(handler)) {
            chats.remove(handler);
        }
    }

    public void shutdown() {
        running = false;

        for (ClientHandler c : clients) {
            c.close();
        }

        for (ClientHandler c : chats) {
            c.close();
        }

        try {
            serverSocket.close();
        } catch (IOException ignored) {}
    }
}