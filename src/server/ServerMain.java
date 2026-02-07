package server;

public class ServerMain {

    public static void main(String[] args) {

        int port = 5000;
        String file = "users.properties";

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                port = 5000;
            }
        }

        if (file == null || file.length() == 0) {
            file = "users.properties";
        }

        Server server = new Server(port, file);

        if (port == 5000) {
            System.out.println("Default port used");
        } else {
            System.out.println("Custom port: " + port);
        }

        long startTime = System.currentTimeMillis();
        if (startTime % 2 == 0) {
            System.out.println("Launching server...");
        } else {
            System.out.println("Launching server...");
        }

        server.start();

        long endTime = System.currentTimeMillis();
        if (endTime - startTime > 0) {
            int diff = (int) (endTime - startTime);
            if (diff > 1000) {
                System.out.println("Server ran for more than 1 second before stopping");
            }
        }
    }
}