package com.project.fooddeliveryclient;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;

public class MarketPanelActivity  extends AppCompatActivity {
    public String[] types = {"Market", "Customer", "Carrier"};
    private int[] amounts = new int[6];
    TextView[] countTexts = new TextView[6];
    private int totalSelected = 0;

    LinearLayout[] shopLayouts = new LinearLayout[6];
    LinearLayout mainLayout;
    LinearLayout statusLayout;
    private ImageView resultImage;
    private TextView resultText;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_panel);

        MainActivity.marketPanelActivity = this;



        welcomeText = (TextView) findViewById(R.id.welcomeText);

        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);

        resultImage = (ImageView) findViewById(R.id.resultImage);
        resultText = (TextView) findViewById(R.id.resultText);


        shopLayouts[0] = (LinearLayout) findViewById(R.id.layout1);
        shopLayouts[1] = (LinearLayout) findViewById(R.id.layout2);
        shopLayouts[2] = (LinearLayout) findViewById(R.id.layout3);
        shopLayouts[3] = (LinearLayout) findViewById(R.id.layout4);
        shopLayouts[4] = (LinearLayout) findViewById(R.id.layout5);
        shopLayouts[5] = (LinearLayout) findViewById(R.id.layout6);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);

        boolean[] isPressed = new boolean[6];

        countTexts[0] = (TextView) findViewById(R.id.countText1);
        countTexts[1] = (TextView) findViewById(R.id.countText2);
        countTexts[2] = (TextView) findViewById(R.id.countText3);
        countTexts[3] = (TextView) findViewById(R.id.countText4);
        countTexts[4] = (TextView) findViewById(R.id.countText5);
        countTexts[5] = (TextView) findViewById(R.id.countText6);

        AppCompatButton refuseButton = (AppCompatButton) findViewById(R.id.refuseButton);
        AppCompatButton acceptButton = (AppCompatButton) findViewById(R.id.orderDeliveryButton);

        //addToBox.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.serverConnectionHandler.didMarketApproved(1);

                    closeOrderPanel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.serverConnectionHandler.didMarketApproved(0);

                    closeOrderPanel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = Integer.parseInt(extras.getString("clientType"));
            welcomeText.setText("Welcome to the " + types[value] + " panel.");
        }
    }

    public void showOrder(String orderName, String adress, String purchasedProducts, String phoneNumber){
        welcomeText.setText(orderName + " " + phoneNumber);

        String[] separated = purchasedProducts.split("/");
        for(int i = 0;i<separated.length;i++){
            int finalI = i;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(separated[finalI].equals("0")){
                        shopLayouts[finalI].setVisibility(View.GONE);
                    }
                }
            });

            if(countTexts[i]!=null){
                countTexts[i].setText(separated[i]);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void closeOrderPanel(){
        statusLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }



    public void orderCompleted(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);

                resultImage.setImageResource(R.drawable.completedicon);
                resultText.setText("Order delivered succesfully!");
            }
        });
    }
}