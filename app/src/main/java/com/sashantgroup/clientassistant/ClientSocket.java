package com.sashantgroup.clientassistant;

import android.util.Log;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket implements Runnable {
    String address = MainActivity.ip;
    String resultStr;
    int port = 8989;
    Thread thread;

    public ClientSocket(String resultStr) {
        this.resultStr = resultStr;
        thread = new Thread(this, "ClientSocket");
        thread.start();
    }

    @Override
    public void run() {
        try (Socket clientSocket = new Socket (address, port)) {
            Log.i("Result ", resultStr);
            clientSocket.setTcpNoDelay(false);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(resultStr));
            BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
            byte[] byteArray = new byte[4];
            while ((bis.read(byteArray)) != -1){
                bos.write(byteArray);
            }
            bis.close();
            bos.close();
        }
        catch (ConnectException e) {
            Log.e("Error ","no connection...");
        }
        catch (UnknownHostException e) {
            Log.e("Error ","server not found: " + e.getMessage());
        }
        catch (Exception e) {
            Log.e("Error ","other problem...");
            e.printStackTrace();
        }
    }
}