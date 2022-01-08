package com.example.clientassistant;

import android.util.Log;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket implements Runnable {
    String adress = "192.168.0.173";
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
        try (Socket clientSocket = new Socket (adress, port)) {
            Log.i("Result ", resultStr);
            clientSocket.setTcpNoDelay(false);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(resultStr));
            BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
            byte[] byteArray = new byte[8];
            while ((bis.read(byteArray)) != -1){
                bos.write(byteArray);
            }
            bis.close();
            bos.close();

            /*clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(adress, port), 5000);
            Log.i("Result socket ", clientSocket.toString());*/
            /*OutputStream os = clientSocket.getOutputStream();
            os.write(resultStr.getBytes(StandardCharsets.UTF_8));
            os.flush();*/
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