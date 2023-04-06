package com.project.fooddeliveryclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    static ServerConnectionHandler serverConnectionHandler;
    public static CustomerActivity customerActivity;
    public static MarketPanelActivity marketPanelActivity;
    public static CarrierActivity carrierActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity mainActivity = this;


        //LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver,new IntentFilter("data"));

        Button marketButton = (Button) findViewById(R.id.marketButton);
        Button customerButton = (Button) findViewById(R.id.customerButton);
        Button carrierButton = (Button) findViewById(R.id.carrierButton);

        marketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnectionHandler = new ServerConnectionHandler("0", mainActivity);
                serverConnectionHandler.start();
            }
        });

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnectionHandler = new ServerConnectionHandler("1", mainActivity);
                serverConnectionHandler.start();
            }
        });

        carrierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnectionHandler = new ServerConnectionHandler("2", mainActivity);
                serverConnectionHandler.start();
            }
        });

    }

    public void transitView(String Type){
        Intent intent = null;

        if(Type.equals("0")){
            System.out.println("market");
            intent = new Intent(getApplicationContext(), MarketPanelActivity.class);
        } else if(Type.equals("1")){
            System.out.println("customer");
            intent = new Intent(getApplicationContext(), CustomerActivity.class);
        } else {
            System.out.println("carrier");
            intent = new Intent(getApplicationContext(), CarrierActivity.class);
        }
        intent.putExtra("clientType",Type);
        startActivity(intent);

    }

}