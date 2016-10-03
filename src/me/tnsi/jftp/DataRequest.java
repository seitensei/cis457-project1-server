package me.tnsi.jftp;

import java.lang.reflect.Array;
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
        DataInputStream is = new DataInputStream(socket.getInputStream());

        if(command.equals("LIST")) {
            for(String s : getDirectory()) {
                os.writeBytes(s + CRLF);
                os.flush();
            }
            os.writeBytes("EOF" + CRLF);
            os.flush();
        }

        if(command.contains("RETR")) {
            String fileName = command.substring(5);
            System.out.println("Requested File for Retrieve: " + fileName);
            if(fileExists(fileName)) {
                // File is present, execute transfer
                for (String curLine : getFile(fileName)) {
                    // Get lines from file
                    os.writeBytes(curLine + CRLF);
                    os.flush();
                }
                os.writeBytes("EOF" + CRLF);
                os.flush();
            } else {
                // File does not exist, kill connection and notify command connection
                // TODO: Notify command connection
            }
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

    private boolean fileExists(String fileName) {
        File targetFile = new File("./data/" + fileName);
        if(targetFile.exists() && !targetFile.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<String> getFile(String fileName) throws Exception {
        ArrayList<String> export = new ArrayList<String>();
        File targetFile = new File("./data/" + fileName);
        BufferedReader fileReader = new BufferedReader(new FileReader(targetFile));
        String curLine;
        while ((curLine = fileReader.readLine()) != null) {
            export.add(curLine);
        }
        fileReader.close();
        return export;
    }
}
