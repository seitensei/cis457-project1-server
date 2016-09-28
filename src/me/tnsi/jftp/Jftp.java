package me.tnsi.jftp;

import java.net.*;
import java.io.*;

public class Jftp {

    public static void main(String[] args) throws Exception {
        // Create Port
        int port = 3715;

        // if data directory does not exist, create it
        File dir = new File("./data");
        if (dir.isDirectory()) {
            // do nothing
        } else {
            dir.mkdir();
        }

        // Listen Socket
        ServerSocket listenSocket = new ServerSocket(port);

        // Code Loop
        while (true) {
            // Listen for TCP
            Socket connection = listenSocket.accept();
            System.out.println("Connected to: " + connection.getInetAddress() + ":" + connection.getPort());
            // Construct FTP Command Handler
            FtpRequest request = new FtpRequest(connection);

            // Command thread
            Thread cThread = new Thread(request);

            // Start command thread
            cThread.run();
        }

    }
}
