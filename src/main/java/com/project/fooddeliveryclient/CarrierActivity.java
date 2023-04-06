package com.project.fooddeliveryclient;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;

public class CarrierActivity extends AppCompatActivity {
    public String[] types = {"Market", "Customer", "Carrier"};

    TextView[] orderDetails = new TextView[3];

    LinearLayout statusLayout, mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier);

        MainActivity.carrierActivity = this;

        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);

        orderDetails[0] = (TextView) findViewById(R.id.nameText);
        orderDetails[1] = (TextView) findViewById(R.id.adressText);
        orderDetails[2] = (TextView) findViewById(R.id.phoneNumber);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);



        AppCompatButton orderDelivered = (AppCompatButton) findViewById(R.id.orderDeliveryButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = Integer.parseInt(extras.getString("clientType"));
            welcomeText.setText("Welcome to the " + types[value] + " panel.");
        }

        orderDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.serverConnectionHandler.orderCompletedSend();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusLayout.setVisibility(View.VISIBLE);;
                            mainLayout.setVisibility(View.GONE);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showOrder(String orderName, String adress, String purchasedProducts, String phoneNumber){
        String[] separated = purchasedProducts.split("&");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderDetails[0].setText(orderName);
                orderDetails[1].setText(adress);
                orderDetails[2].setText(phoneNumber);

                statusLayout.setVisibility(View.GONE);;
                mainLayout.setVisibility(View.VISIBLE);
            }
        });
    }


}