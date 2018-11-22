package iniyan.com.facebookintegrate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class PaymentActivity extends AppCompatActivity {

    String orderId;
    //Test
    TextView order_id_txt;
    EditText order_res;
    EditText edt_email,edt_mobile,edt_amount;
    HashMap paramMap = new HashMap();
    String mid="MID",order_id="ORDER_ID",cust_id="CUST_ID",callback="CALLBACK",industry_type="INDUS_TYPE",txn_amount="TXN_AMOUNT",checksum="CHECKSUM",mobile="MOBILE_NO",email="EMAIL",channel_id="CHANNEL_ID";
    String website="WEBSITE";

    String url = "http://18.224.1.148:8080/generate_checksum";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

//        runtime permission
        if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PaymentActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edt_email = (EditText)findViewById(R.id.edt_email);
        edt_mobile = (EditText)findViewById(R.id.edt_mobile);
        edt_amount = (EditText)findViewById(R.id.edt_amount);


        edt_email.setText("muruga4046@gmail.com");
        edt_mobile.setText("8754137753");
        edt_amount.setText("10");

    }
    public void onStartTransaction(View view) throws InterruptedException, ExecutionException {

        if(edt_email.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Please Enter Email-id",Toast.LENGTH_LONG).show();
        }else if(edt_mobile.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Please Enter Mobile Number",Toast.LENGTH_LONG).show();
        }else if(edt_amount.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Please Enter Amount",Toast.LENGTH_LONG).show();
        }else {

            PaytmPGService Service = PaytmPGService.getStagingService();
            //PaytmPGService Service = PaytmPGService.getProductionService();

            Log.d("before request", "some");
            String edtemail = edt_email.getText().toString().trim();
            String edtmobile = edt_mobile.getText().toString().trim();
            String edtamount = edt_amount.getText().toString().trim();


            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put("orderid", orderId);
            stringHashMap.put("email", edtemail);
            stringHashMap.put("mobile", edtmobile);
            stringHashMap.put("amount", edtamount);

            SendDeviceDetails sendDeviceDetails = null;
            try {
                sendDeviceDetails = new SendDeviceDetails(url, getPostDataString(stringHashMap), Service);
                sendDeviceDetails.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        String url,data;
        PaytmPGService Service;

        public SendDeviceDetails(String url, String data, PaytmPGService Service) {
            this.url = url;
            this.data = data;
            this.Service = Service;
        }

        @Override
        protected String doInBackground(String... params) {

            String data1 = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//                wr.writeBytes("PostData=" + params[1]);
                wr.writeBytes(data);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data1 += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data1;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            //String json = (String) myAsyncTask.execute(url).get();
            JSONObject mJsonObject = null;
            try {
                mJsonObject=new JSONObject(result);
                mid = mJsonObject.getString("MID");
                order_id=mJsonObject.getString("ORDER_ID");
                cust_id = mJsonObject.getString("CUST_ID");
                callback = mJsonObject.getString("CALLBACK_URL");
                industry_type = mJsonObject.getString("INDUSTRY_TYPE_ID");
                channel_id = mJsonObject.getString("CHANNEL_ID");
                txn_amount = mJsonObject.getString("TXN_AMOUNT");
                checksum = mJsonObject.getString("CHECKSUMHASH");
                mobile = mJsonObject.getString("MOBILE_NO");
                email = mJsonObject.getString("EMAIL");
                website = mJsonObject.getString("WEBSITE");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.d("after request", "some");

            paramMap.put("MID", mid);
            paramMap.put("ORDER_ID", order_id);
            paramMap.put("CUST_ID", cust_id);
            paramMap.put("INDUSTRY_TYPE_ID",industry_type);
            paramMap.put("CHANNEL_ID",channel_id);
            paramMap.put("TXN_AMOUNT",txn_amount);
            paramMap.put("WEBSITE", website);
            paramMap.put("EMAIL",email);
            paramMap.put("MOBILE_NO",mobile);
            paramMap.put("CALLBACK_URL",callback);
            paramMap.put("CHECKSUMHASH",checksum);

            PaytmOrder Order = new PaytmOrder(paramMap);

            Service.initialize(Order, null);

            Service.startPaymentTransaction(PaymentActivity.this, true, true,
                    new PaytmPaymentTransactionCallback() {

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
// Some UI Error Occurred in Payment Gateway Activity.
// // This may be due to initialization of views in
// Payment Gateway Activity or may be due to //
// initialization of webview. // Error Message details
// the error occurred.
                            Toast.makeText(getApplicationContext(), "Payment Transaction response " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onTransactionResponse(Bundle inResponse) {
                            Log.d("LOG", "Payment Transaction :" + inResponse);
                            Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
//                            order_res = (EditText)findViewById(R.id.order_res);
//                            order_res.setText(inResponse.toString());

                            String response=inResponse.getString("RESPMSG");
                            if (response.equals("Txn Successful."))
                            {
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void networkNotAvailable() {
// If network is not
// available, then this
// method gets called.
                            Toast.makeText(getApplicationContext(), "Network" , Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
// This method gets called if client authentication
// failed. // Failure may be due to following reasons //
// 1. Server error or downtime. // 2. Server unable to
// generate checksum or checksum response is not in
// proper format. // 3. Server failed to authenticate
// that client. That is value of payt_STATUS is 2. //
// Error Message describes the reason for failure.
                            Toast.makeText(getApplicationContext(), "clientAuthenticationFailed"+inErrorMessage.toString() , Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode,
                                                          String inErrorMessage, String inFailingUrl) {

                            Toast.makeText(getApplicationContext(), "onErrorLoadingWebPage"+inErrorMessage.toString() , Toast.LENGTH_LONG).show();

                        }

                        // had to be added: NOTE
                        @Override
                        public void onBackPressedCancelTransaction() {
                            Toast.makeText(getApplicationContext(), "onBackPressedCancelTransaction", Toast.LENGTH_LONG).show();

// TODO Auto-generated method stub
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage,
                                                        Bundle inResponse) {
                            Log.d("LOG", "Payment Transaction Failed "
                                    + inErrorMessage);
                            Toast.makeText(getBaseContext(),
                                    "Payment Transaction Failed ",
                                    Toast.LENGTH_LONG).show();
                        }

                    });
        }
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
    @Override
    protected void onStart() {
        super.onStart();
        initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        order_id_txt = (TextView)findViewById(R.id.order_id);
        order_id_txt.setText(orderId);

    }
}
