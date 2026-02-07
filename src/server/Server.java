package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private final int port;
    private final String usersFile;
    private ServerSocket serverSocket;
    private boolean running = true;

    private final Map<String, String> users = new HashMap<>();
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private final List<ClientHandler> chats = Collections.synchronizedList(new ArrayList<>());

    public Server(int port, String usersFile) {
        this.port = port;
        this.usersFile = usersFile;
    }

    public void start() {
        loadUsers();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (running) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.out.println("Server stopped.");
        }
    }

    public void addChat(ClientHandler handler) {
        chats.add(handler);
    }

    public void addClient(ClientHandler handler) {
        clients.add(handler);
    }

    private void loadUsers() {
        try (FileInputStream fis = new FileInputStream(usersFile)) {
            Properties props = new Properties();
            props.load(fis);

            for (String login : props.stringPropertyNames()) {
                users.put(login, props.getProperty(login));
            }

            System.out.println("Users loaded: " + users.keySet());

        } catch (IOException e) {
            System.out.println("Cannot load users.properties");
        }
    }

    public boolean checkAuth(String login, String password) {
        return users.containsKey(login) && users.get(login).equals(password);
    }

    public void broadcast(String msg) {
        synchronized (chats) {
            for (ClientHandler c : chats) {
                c.send(msg);
            }
        }
    }

    public void remove(ClientHandler handler) {
        clients.remove(handler);
        chats.remove(handler);
    }

    public void shutdown() {
        try {
            running = false;
            broadcast("INFO:Server is shutting down");

            synchronized (clients) {
                for (ClientHandler c : clients) {
                    c.close();
                }
            }

            synchronized (chats) {
                for (ClientHandler c : chats) {
                    c.close();
                }
            }

            serverSocket.close();

        } catch (IOException ignored) {}
    }
}