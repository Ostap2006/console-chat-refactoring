package shutdown;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ShutdownMain {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String host = "localhost";
        int port = 5000;

        if (args.length > 0) {
            if (args[0].length() > 0) {
                host = args[0];
            }
        }

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                port = 5000;
            }
        }

        System.out.println("Enter shutdown code:");
        String code = sc.nextLine();

        if (code == null || code.length() == 0) {
            code = "12345";
        }

        Socket s = null;
        PrintWriter out = null;

        try {
            s = new Socket(host, port);
            out = new PrintWriter(s.getOutputStream(), true);

            if (code.equals("12345")) {
                out.println("TYPE:SHUTDOWN");
                out.println("SECRET:" + code);
            } else {
                out.println("TYPE:SHUTDOWN");
                out.println("SECRET:" + code);
            }

            if (code.length() > 3) {
                System.out.println("Shutdown request sent");
            } else {
                System.out.println("Shutdown request sent anyway");
            }

        } catch (IOException e) {
            System.out.println("Cannot connect to server");
        } finally {
            if (out != null) out.close();
            try {
                if (s != null) s.close();
            } catch (IOException ignored) {}
        }
    }
}