package com.project.fooddeliveryclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CustomerActivity extends AppCompatActivity {
    public String[] types = {"Market", "Customer", "Carrier"};
    private int[] amounts = new int[6];
    private int totalSelected = 0;

    private ImageView resultImage;
    private TextView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        MainActivity.customerActivity= this;

        TextView[] countTexts = new TextView[6];

        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
        //TextView amountText = (TextView) findViewById(R.id.amountText)

        LinearLayout selectionLayout = (LinearLayout) findViewById(R.id.selectionLayout);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        LinearLayout statusLayout = (LinearLayout) findViewById(R.id.statusLayout);

        resultImage = (ImageView) findViewById(R.id.resultImage);
        resultText = (TextView) findViewById(R.id.resultText);

        selectionLayout.setVisibility(View.INVISIBLE);

        ImageButton[] shops = new ImageButton[6];

        boolean[] isPressed = new boolean[6];

        shops[0] = (ImageButton) findViewById(R.id.shop1);
        shops[1] = (ImageButton) findViewById(R.id.shop2);
        shops[2] = (ImageButton) findViewById(R.id.shop3);
        shops[3] = (ImageButton) findViewById(R.id.shop4);
        shops[4] = (ImageButton) findViewById(R.id.shop5);
        shops[5] = (ImageButton) findViewById(R.id.shop6);

        countTexts[0] = (TextView) findViewById(R.id.countText1);
        countTexts[1] = (TextView) findViewById(R.id.countText2);
        countTexts[2] = (TextView) findViewById(R.id.countText3);
        countTexts[3] = (TextView) findViewById(R.id.countText4);
        countTexts[4] = (TextView) findViewById(R.id.countText5);
        countTexts[5] = (TextView) findViewById(R.id.countText6);

        Button removeButton = (Button) findViewById(R.id.removeButton);
        Button addButton = (Button) findViewById(R.id.addButton);
        Button purchase = (Button) findViewById(R.id.purchase);


        //addToBox.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);


        for(int i = 0;i<countTexts.length;i++){
            countTexts[i].setVisibility(View.GONE);
        }

        final int[] selected = {-1};
        for(int i = 0;i<shops.length;i++){
            int finalI = i;
            shops[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPressed[finalI] = !isPressed[finalI];
                    if (isPressed[finalI]) {
                        System.out.println(isPressed[finalI]);

                        selectionLayout.setVisibility(View.VISIBLE);

                        for(int x = 0;x<shops.length;x++) {
                            if(finalI != x){
                                shops[x].setBackgroundColor(getResources().getColor(R.color.buttonNotPressed));
                                isPressed[x] = false;
                            }
                        }
                        selected[0] = finalI;
                        shops[finalI].setBackgroundColor(getResources().getColor(R.color.purple_500));
                    } else {
                        shops[finalI].setBackgroundColor(getResources().getColor(R.color.buttonNotPressed));
                        selected[0] = -1;

                        selectionLayout.setVisibility(View.INVISIBLE);

                    }

                }
            });
        }


        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amounts[selected[0]]!=0){
                    if(amounts[selected[0]]==1) {
                        countTexts[selected[0]].setVisibility(View.GONE);
                    }

                    amounts[selected[0]]--;
                    totalSelected--;
                    if(totalSelected==0){
                        //addToBox.setClickable(false);
                        //addToBox.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    }
                }
                countTexts[selected[0]].setText(String.valueOf(amounts[selected[0]]));

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amounts[selected[0]]==0){
                    countTexts[selected[0]].setVisibility(View.VISIBLE);
                }
                amounts[selected[0]]++;
                totalSelected++;
                //addToBox.setClickable(true);
                //addToBox.getBackground().setColorFilter(null);

                countTexts[selected[0]].setText(String.valueOf(amounts[selected[0]]));
            }
        });

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalSelected==0){
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), "You should select at least one product!", duration);
                    toast.show();
                    return;
                }
                mainLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);

                String name = "Ömer";
                String adress = "Yıldız Çırağan Cd. 34349 D506 Beşiktaş/İstanbul";
                String purchase = "";
                String phoneNumber = "5434153651";
                for (int i = 0;i<amounts.length;i++) {
                    purchase += amounts[i] + "/";
                }
                try {
                    MainActivity.serverConnectionHandler.createOrder(name, adress, purchase, phoneNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("VAL" + extras.getString("clientType"));
            int value = Integer.parseInt(extras.getString("clientType"));
            welcomeText.setText("Welcome to the " + types[value] + " panel.");
        }
    }

    public void orderRefused(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultImage.setImageResource(R.drawable.orderrefused);
                resultText.setText("The market refused the order!");
            }
        });
    }

    public void orderAccepted(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultImage.setImageResource(R.drawable.orderaccepted);
                resultText.setText("The market accepted the order!");
            }
        });
    }

    public void orderCompleted(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultImage.setImageResource(R.drawable.completedicon);
                resultText.setText("Order delivered succesfully!");
            }
        });
    }

}