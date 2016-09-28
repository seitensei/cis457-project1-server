package me.tnsi.jftp;

import java.net.*;
import java.io.*;
import java.util.*;

public class DataRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
    String command;

    public DataRequest(Socket socket, String command) throws Exception {
        this.socket = socket;
        this.command = command;
    }

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

        if(command == "LIST") {
            for(String s : getDirectory()) {
                os.writeBytes(s + CRLF);
            }
            os.writeBytes("EOF" + CRLF);
        }

        os.close();
        socket.close();

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
