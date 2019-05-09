package com.expresspaygh.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.expresspaygh.api.ExpressPayApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ExpressPayApi.ExpressPayPaymentCompletionListener{

    static ExpressPayApi expressPayApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /** 
         *    Initialize a expressPayApi instance to communicate with expressPay SDK.  *  *
         * @param context 
         * @param yourServerURL the full path url to the location on your servers where you implement our server side sdk. 
         * *                      if null it defaults to https://sandbox.expresspaygh.com/api/sdk/php/server.php
         * */
        expressPayApi= new ExpressPayApi(this,"https://sandbox.expresspaygh.com/api/sdk/php/server.php");

        /**
         * Set the developnment env
         * Please ensure you set this value to false in your production code
         */
        expressPayApi.setDebugMode(true);
        Button payBtn= (Button)findViewById(R.id.payBtn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null)
         expressPayApi.onActivityResult(this, requestCode, resultCode, data);
    }

  
    public void pay(){
        /**
         * Make a request to your server to get a token
         * For this demo we have a sample server which we make the request to.
         * url: https://sandbox.expresspaygh.com/api/sdk/php/server.php
         * In Dev: Use amount 1.00 to simulate a failed transaction and greater than or equals 2.00 for a successful transaction
         */

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("request","submit");
        params.put("order_id", "82373");
        params.put("currency", "GHS");
        params.put("amount", "5.00");
        params.put("order_desc", "Daily Plan");
        params.put("user_name","testapi@expresspaygh.com");
        params.put("first_name","Test");
        params.put("last_name","Api");
        params.put("email","testapi@expresspaygh.com");
        params.put("phone_number","233244123123");
        params.put("account_number","233244123123");




        expressPayApi.submit(params, MainActivity.this, new ExpressPayApi.ExpressPaySubmitCompletionListener() {
            @Override
            public void onExpressPaySubmitFinished(JSONObject jsonObject, String message) {
                /**
                 * Once the request is completed this listener is called with the response
                 * if the jsonObject is null then there was an error
                 */


                if (jsonObject!=null){
                    //You can access the returned token
                    try {
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            String token=expressPayApi.getToken();
                            checkout();
                        }else {
                            Log.d("expressPayDemo",message);
                            showDialog(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("expressPayDemo", message);
                        showDialog(message);

                    }

                }else {
                    Log.d("expressPayDemo",message);
                    showDialog(message);
                }
            }
        });

    }

    public void checkout(){
        /**
         * Displays the payment page to accept the payment method from the user
         *
         * When the payment is complete the ExpressPayPaymentCompletionListener is called
         */

        expressPayApi.checkout(this);
    }


    public  void queryPayment(String token){
        /**
         * After the payment has been completed we query our servers to find out
         * the status of the transaction
         * url: https://sandbox.expresspaygh.com/api/server.php
         */
        expressPayApi.query(token, new ExpressPayApi.ExpressPayQueryCompletionListener() {
            @Override
            public void onExpressPayQueryFinished(Boolean paymentSuccessful, JSONObject jsonObject, String message) {
                if (paymentSuccessful) {
                    showDialog(message);
                } else {
                    //There was an error
                    Log.d("expressPayDemo", message);
                    showDialog(message);
                }
            }
        });
    }


    @Override
    public void onExpressPayPaymentFinished(boolean paymentCompleted, String message) {
        if (paymentCompleted){
            //Payment was completed
            String token=expressPayApi.getToken();
            queryPayment(token);
        }
        else{
            //There was an error
            Log.d("expressPayDemo",message);
            showDialog(message);
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
