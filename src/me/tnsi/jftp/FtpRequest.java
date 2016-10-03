package me.tnsi.jftp;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.*;
import java.util.*;

final class FtpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
    int dataPort = 3716;

    // constructor
    public FtpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // runnable run
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // output stream
        BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // input stream
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send Welcome Response to Client
        response(os, "Response: 220 Welcome to JFTP.");

        // command loop
        // reference: https://en.wikipedia.org/wiki/List_of_FTP_server_return_codes
        String commandRequest = "";
        String commandExport = "";
        String clientArg = "";
        while(true) {
            String commandLine = br.readLine();
            String[] clientCommand = commandLine.split(" ");

            // Check and handle variable arguments in the received command
            if(clientCommand.length == 1) {
                // Only one element in split array
                commandExport = clientCommand[0];
            } else {
                clientArg = clientCommand[1];
                commandExport = clientCommand[0] + " " + clientArg;
            }

            commandExport = clientCommand + " " + clientArg;

            if(clientCommand[0].equals("LIST")) {
                ServerSocket dataSock = new ServerSocket(dataPort);
                response(os, "Response: 225 Data Connection Open.");
                while(true) {
                    Socket dataConn = dataSock.accept();

                    // Create Data Handler
                    DataRequest dataHandler = new DataRequest(dataConn, commandExport);

                    // Data handler Thread
                    Thread dThread = new Thread(dataHandler);

                    // run
                    dThread.run();
                    dataSock.close();
                    response(os, "Response: 226 Closing Data Connection.");
                    break;
                }

            }

            if(clientCommand[0].equals("RETR")) {
                ServerSocket dataSock = new ServerSocket(dataPort);
                response(os, "Response: 225 Data Connection Open.");
                while(true) {
                    Socket dataConn = dataSock.accept();
                    DataRequest dataHandler = new DataRequest(dataConn, commandExport);
                    Thread dThread = new Thread(dataHandler);
                    dThread.run();
                    dataSock.close();
                    response(os, "Response: 226 Closing Data Connection.");
                    break;
                }
            }

            if(clientCommand[0].equals("STOR")) {
                ServerSocket dataSock = new ServerSocket(dataPort);
                response(os, "Response: 225 Data Connection Open.");
                while(true) {
                    Socket dataConn = dataSock.accept();
                    DataRequest dataHandler = new DataRequest(dataConn, commandExport);
                    Thread dThread = new Thread(dataHandler);
                    dThread.run();
                    dataSock.close();
                    response(os, "Response: 226 Closing Data Connection.");
                    break;
                }
            }

            if(clientCommand[0].equals("QUIT")) {
                response(os, "Response: 221 Closing connection.");
                break;
            }

        }

        System.out.println("---- Closing Connection ----");
        os.close();
        br.close();
        socket.close();
    }

    private void response(BufferedWriter os, String res) throws Exception {
        System.out.println(res);
        res = res + CRLF;
        os.write(res, 0, res.length());
        os.flush();
    }

    private ArrayList<String> getDirectory() {
        ArrayList<String> listContents = new ArrayList<String>();

        File dir = new File("./data");
        File[] dirList = dir.listFiles();

        for (File file : dirList) {
            listContents.add(file.getName());
        }

        return listContents;
    }
}
