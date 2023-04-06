package com.project.fooddeliveryclient;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnectionHandler extends Thread {
    private final MainActivity mainActivity;
    private Exception exception;

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private String whichClient = "0";
    private String orderData = "";

    public ServerConnectionHandler(String whichClient, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.whichClient = whichClient;
    }

    public static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        Log.d(TAG, "model=" + model);
        String product = Build.PRODUCT;
        Log.d(TAG, "product=" + product);
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }
        Log.d(TAG, "isEmulator=" + isEmulator);
        return isEmulator;
    }

    @Override
    public void run(){
        try{
            try {
                if(isAndroidEmulator()) {
                    System.out.println("456");
                    socket = new Socket("10.0.2.2",4545);
                }
                else {
                    System.out.println("123");
                    socket = new Socket("192.168.203.31",4545);
                }


                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));

                sendDirectly(whichClient);

                while (true) {
                    String line = bufferedReader.readLine();
                    InputData(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //SEND
    private void sendCommand(String data) throws IOException {
        new Thread(() -> {
            try {
                bufferedWriter.write("1," + data + "\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void sendMessage(String data) throws IOException {
        bufferedWriter.write("0,"+data+"\n");
        bufferedWriter.flush();
    }

    private void sendDirectly(String data) throws IOException {
        bufferedWriter.write(data+"\n");
        bufferedWriter.flush();
    }

    private void InputData(String utf8string) throws IOException {
        String[] separated = utf8string.split(",");
        if(separated[0].equals("0")){
            processMessage(separated[1]);
        } else if(separated[0].equals("1")){
            processCommand(separated[1]);
        }
    }

    //COMMANDS

    private void clientConnected(String type){
        mainActivity.transitView(type);
    }
    //PROCESS
    private void processCommand(String data) throws IOException {
        System.out.println("DATA " + data);
        String[] separated = data.split(":");
        if(separated[0].equals("clientConnected")){
            clientConnected(separated[1]);
        } else if(separated[0].equals("processOrder")){
            processOrder(separated[1]);
        } else if(separated[0].equals("processCarrier")){
            processCarrier(separated[1]);
        } else if(separated[0].equals("marketRefused")){
            marketRefused();
        } else if(separated[0].equals("marketAccepted")){
            marketAccepted();
        } else if(separated[0].equals("orderCompletedReceive")){
            orderCompletedReceive();
        }

    }

    private void processMessage(String data) throws IOException {
        String string = bufferedReader.readLine();
        System.out.println(string);
    }

    public void closeConnection() throws IOException {
        bufferedWriter.close();
        bufferedReader.close();
        socket.close();
    }

    //CUSTOMER SYSTEM
    public void createOrder(String orderName, String adress, String purchasedProducts, String phoneNumber) throws IOException {
        sendCommand("createOrder:"+orderName+"&"+adress+"&"+purchasedProducts+"&"+phoneNumber);
    }

    public void marketRefused() throws IOException {
        MainActivity.customerActivity.orderRefused();
    }

    public void marketAccepted() throws IOException {
        MainActivity.customerActivity.orderAccepted();
    }



    //MARKET SYSTEM
    public void processOrder(String data) throws IOException {
        String[] separated = data.split("&");

        orderData = data;

                //if(separated.length>2){
        MainActivity.marketPanelActivity.showOrder(separated[0], separated[1], separated[2], separated[3]);
        //}
    }

    //CARRIER SYSTEM
    public void processCarrier(String data) throws IOException {
        System.out.println(data);
        String[] separated = data.split("&");
        MainActivity.carrierActivity.showOrder(separated[0], separated[1], separated[2], separated[3]);
    }

    //MARKET AND CUSTOMER SYSTEM
    public void orderCompletedReceive() throws IOException {
        if(MainActivity.customerActivity!=null){
            MainActivity.customerActivity.orderCompleted();
        } else if(MainActivity.marketPanelActivity!=null){
            MainActivity.marketPanelActivity.orderCompleted();
        }
    }

    //MARKET AND CUSTOMER SYSTEM
    public void orderCompletedSend() throws IOException {
        sendCommand("orderCompleted:1");
    }
    //Market Kabul Butonu Bunu Çağırıyor
    public void didMarketApproved(int isAccepted) throws IOException {
        sendCommand("orderResponse:"+isAccepted+"&"+orderData);
    }
}