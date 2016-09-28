package me.tnsi.jftp;

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
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // input stream
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send Welcome Response to Client
        response(os, "Response: 220 Welcome to JFTP." + CRLF);

        // command loop
        // reference: https://en.wikipedia.org/wiki/List_of_FTP_server_return_codes
        String commandRequest;
        while(true) {
            String commandLine = br.readLine();
            StringTokenizer tokens = new StringTokenizer(commandLine);
            String clientCommand = tokens.nextToken();
            String clientArg = tokens.nextToken();
            String commandExport = clientCommand + " " + clientArg;

            if(clientCommand == "LIST") {
                ServerSocket dataSock = new ServerSocket(dataPort);
                while(true) {
                    Socket dataConn = dataSock.accept();
                    response(os, "Response: 225 Data Connection Open." + CRLF);

                    // Create Data Handler
                    DataRequest dataHandler = new DataRequest(dataConn, commandExport);

                    // Data handler Thread
                    Thread dThread = new Thread(dataHandler);

                    // run
                    dThread.run();

                }
            }

            if(clientCommand == "RETR") {
                response(os, "Response: 202 RETR not implemented." + CRLF);
            }

            if(clientCommand == "STOR") {
                response(os, "Response: 202 STOR not implemented." + CRLF);
            }

            if(clientCommand == "QUIT") {
                response(os, "Response: 221 Closing connection." + CRLF);
                break;
            }

        }

        System.out.println("---- Closing Connection ----");
        os.close();
        br.close();
        socket.close();
    }

    private void response(DataOutputStream os, String res) throws Exception {
        System.out.println(res);
        os.writeBytes(res);
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
