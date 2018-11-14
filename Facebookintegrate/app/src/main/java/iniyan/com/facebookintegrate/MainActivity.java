package iniyan.com.facebookintegrate;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import iniyan.com.facebookintegrate.model.Getgroups;
import iniyan.com.facebookintegrate.model.GetgroupsResponse;
import iniyan.com.facebookintegrate.sms.SmsListenerOtp;
import iniyan.com.facebookintegrate.sms.SmsReceiverOtp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    ProfilePictureView profilePictureView;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    ProfileTracker profileTracker = null;
    LoginButton loginButton;
    ImageView profileImage, fbimage;
    TextView logout;
    Button fbfab;
    private ApiService apiService;
    String TAG = MainActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();
    String email, firstName, lastName, userName, id;
    Uri imageUrl;

    MaterialEditText etOtp;

    VideoView videoView;
    private int mCurrentPosition = 0;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    TextView tv_Timer ;


    private Handler handler = new Handler();
    private Runnable runnable;

    private String EVENT_DATE_TIME = "2018-12-31 10:30:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    protected void onStart() {
        super.onStart();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        System.out.print("ssssss" + isLoggedIn);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "iniyan.com.facebookintegrate",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // on createView method

//                        try {
//                            Bitmap mBitmap = getFacebookProfilePicture(name);
//                            fbimage.setImageBitmap(mBitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }



        preferences = getSharedPreferences("test", Context.MODE_PRIVATE);
        editor = preferences.edit();

        fbfab = (Button) findViewById(R.id.fbfab);
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        logout = (TextView) findViewById(R.id.logout);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        fbimage = (ImageView) findViewById(R.id.fbimage);

        tv_Timer = (TextView)findViewById(R.id.timer);
//
//
//        videoView = (VideoView) findViewById(R.id.videoView);
//
//
//        Uri  video = Uri.parse("https://r5---sn-h557sns7.googlevideo.com/videoplayback?ratebypass=yes&signature=499656E5EA2443A834E8ED398D8EAEF7449771F6.027F81A3528DF8A4F430B6FBB5AEC0719EE8FEF5&c=WEB&itag=18&key=cms1&lmt=1540790957196845&fvip=5&mime=video%2Fmp4&id=o-AMa7iUFO7Y9OJya-ybmBLKNuieBISR3IPSyw5g1NY-1g&pl=24&ipbits=0&ip=144.202.119.122&requiressl=yes&txp=5431432&gir=yes&dur=891.576&ei=UdziW6TDJoKlkgb97JywCA&expire=1541615793&sparams=clen,dur,ei,expire,gir,id,ip,ipbits,ipbypass,itag,lmt,mime,mip,mm,mn,ms,mv,pl,ratebypass,requiressl,source&clen=18531123&source=youtube&title=Spring+Tutorial+01+-+Understanding+Dependency+Injection&redirect_counter=1&rm=sn-a5mr776&fexp=23763603&req_id=e868799ecf02a3ee&cms_redirect=yes&ipbypass=yes&mip=103.210.141.198&mm=31&mn=sn-h557sns7&ms=au&mt=1541594133&mv=m");
//        videoView.setVideoURI(video);
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//
//                if (mCurrentPosition > 0) {
//                    videoView.seekTo(mCurrentPosition);
//                } else {
//                    videoView.seekTo(1);
//                }
//
//                mp.setLooping(true);
//                mp.start();
//            }
//        });




        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);


        getGroup();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logOut();
            }
        });

        fbfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList(EMAIL));


            }
        });



        SmsReceiverOtp.bindListener(new SmsListenerOtp() {

            @Override
            public void messageReceived(String provider, String messageText) {

                Toast.makeText(MainActivity.this, "Received", Toast.LENGTH_SHORT).show();
//                if(provider.toLowerCase().contains("paypre"))
                    if(messageText.contains("Your Molc otp is "))
                        etOtp.setText(messageText.replaceAll("[^0-9]", ""));
            }
        });


        if(checkRequestPermisstion()){

        }

//        feedback();


        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        String name = loginResult.getAccessToken().getUserId();
                        String token = loginResult.getAccessToken().getToken();
                        Date lastrefreshdate = loginResult.getAccessToken().getLastRefresh();


                        profilePictureView.setProfileId(name);
                        new DownloadImage().execute(name);


//                        Bitmap theBitmap = null;
//                        theBitmap = Glide.
//                                with(getApplicationContext()).
//                                load("your image url here").
//                                asBitmap().
//                                into(-1, -1).
//
//
//          get();


                        //    Glide.with(getApplicationContext()).asBitmap().load("https://graph.facebook.com/" + name + "/picture?type=large").into(fbimage);


//                        try {
//                            URL     imageURL = new URL("https://graph.facebook.com/" + name + "/picture?type=large");
//                            Log.e("fbimage",""+imageURL);
////                            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//                          //  fbimage.setImageBitmap(bitmap);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }


                        // on createView method

//                        try {
//                            Bitmap mBitmap = getFacebookProfilePicture(name);
//                            fbimage.setImageBitmap(mBitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }


//                   imageview
//                      Uri profilePictureUri = ImageRequest.getProfilePictureUri(Profile.getCurrentProfile().getId(), 200 , 200 );
//Log.e("fbimage",""+profilePictureUri);
//                        Glide.with(MainActivity.this).load(profilePictureUri)
//                               .into(fbimage);


                        Toast.makeText(getApplicationContext(), "Success" + name, Toast.LENGTH_SHORT).show();


                        FacebookSdk.sdkInitialize(getApplicationContext());
                        callbackManager = CallbackManager.Factory.create();

                        profileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(
                                    Profile oldProfile,
                                    Profile currentProfile) {
                                // App code
                                if (currentProfile != null) {
                                    Log.e("ddd", "sss" + currentProfile.getFirstName());
                                    Log.e("ddd", "sss" + currentProfile.getLastName());
                                    Log.e("ddd", "sss" + currentProfile.getName());
                                    Log.e("ddd", "sss" + currentProfile.getProfilePictureUri(100, 200));
                                    Log.e("ddd", "sss" + currentProfile.getId());

                                    firstName = currentProfile.getFirstName();
                                    lastName = currentProfile.getLastName();
                                    userName = currentProfile.getName();
                                    imageUrl = currentProfile.getProfilePictureUri(100, 200);
                                    id = currentProfile.getId();









//https://graph.facebook.com/1002241683260851/picture?height=200&width=100&migration_overrides=%7Boctober_2012%3Atrue%7D


                                    // profilePictureView.setProfileId(name);


                                }

                            }
                        };

                        try {
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            GraphRequest request = GraphRequest.newMeRequest(
                                    accessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.d("ddd", "dra" + object);
                                            try {
                                                String name = object.getString("name");
                                                email = object.getString("email");
//                                                String interested_in = object.getString("interested_in");
//                                                String address = object.getString("address");
//                                                String about = object.getString("about");
//                                                String relationship_status = object.getString("relationship_status");
//                                                String gender = object.getString("gender");

//                                                JSONObject ob =new JSONObject("location");
//                                                String address = ob.getString("name");
                                                String gender = response.getJSONObject().getString("gender");


                                                Profile profile = Profile.getCurrentProfile();
                                                String id = profile.getId();
                                                String link = profile.getLinkUri().toString();
                                                Log.i("Link",link);
                                                Log.e("addddd",""+gender);



//                                                String location = object.getString("location");
                                                // String link = object.getString("link");
//
//                                                String quotes = object.getString("quotes");
//                                                String languages = object.getString("languages");
//                                                String hometown = object.getString("hometown");
//
//                                                String cover = object.getString("cover");
//
//                                                String birthday = object.getString("birthday");
//                                                String age_range = object.getString("age_range");
//


//                                                Log.e("sh", "email" + interested_in+ address+about+relationship_status+gender+location+quotes
//
//
//                                                +languages+hometown);


                                                // tvName.setText(“Welcome, ” + name);

                                                Log.e("ddd", "email" + name + email);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,birthday,gender,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        if (profileTracker != null)
                            profileTracker.stopTracking();
                        Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onError(FacebookException exception) {
                        // App code

                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
//private BroadcastReceiver receiver = new BroadcastReceiver() {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if(intent.getAction().equalsIgnoreCase("otp")){
//            final String message=intent.getStringExtra("message");
//            etOtp.setText(message);
//        }
//    }
//};

    public Boolean checkRequestPermisstion(){
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int receiveSMS =ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS);
        List<String> listPermission = new ArrayList<>();
        if(receiveSMS!=PackageManager .PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.RECEIVE_SMS);

        }

        if(readSMS!=PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.READ_SMS);

        }

        if(permissionSendMessage!=PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.SEND_SMS);
        }

        if(!listPermission.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermission.toArray(new String[listPermission.size()]),1);
        return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();

        if (profileTracker != null)
            profileTracker.stopTracking();
    }

    public static Bitmap getFacebookProfilePicture(String userID) throws IOException {
        URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

        return bitmap;
    }


    // DownloadImage AsyncTask
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                //   SaveImage(bitmap);
                Log.e("bitmap", "" + bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            fbimage.setImageBitmap(result);
        }


    }


    private void getGroup(){
        disposable.add(apiService.getGroup().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Getgroups>() {
                    @Override
                    public void onSuccess(Getgroups getgroups) {
                        GetgroupsResponse[]  response = getgroups.getResponse();

                        for(int i=0;i<response.length;i++){
                            int groupid=response[i].getGroup_id();
                            String  group_leader =response[i].getGroup_leader();
                            String  group_position =response[i].getGroup_leader();
                            String  group_count =response[i].getGroup_leader();
                            String  no_multy =response[i].getGroup_leader();
                            String  payment_status =response[i].getGroup_leader();
                            String  group_status =response[i].getGroup_leader();
                            String  orderid =response[i].getGroup_leader();
                            String  customer_id =response[i].getGroup_leader();
                            String  productlink =response[i].getGroup_leader();
                            String ProfileImage = response[i].getProfileImage();
                            String created_date = response[0].getCreated_date();
                            String timecount = response[1].getTimecount();
                            String expirydate = response[1].getExpirydate();

                            Log.e("fffffff",""+timecount);

//                            gettimer(timecount);
                            countDownStart(expirydate);
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));


    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void countDownStart(final String count) {


       final  String tempDate = parseDateToddMMyyyy(count);
//     final   long counthour = Long.parseLong(count);
        Log.e("diiddd",""+tempDate);


        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(tempDate);
                    Date current_date = new Date();

                    if (!current_date.after(event_date)) {

                        long diff =  event_date.getTime() -current_date.getTime() ;

                        Log.e("diiddd",""+diff);
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //


                        tv_Timer.setText(Days+":"+Hours+":"+Minutes+":"+Seconds);
//                        tv_days.setText(String.format("%02d", Days));
//                        tv_hour.setText(String.format("%02d", Hours));
//                        tv_minute.setText(String.format("%02d", Minutes));
//                        tv_second.setText(String.format("%02d", Seconds));
                    } else {
//                        linear_layout_1.setVisibility(View.VISIBLE);
//                        linear_layout_2.setVisibility(View.GONE);
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public  void gettimer(String timer){

//        long count = Long.parseLong(timer);
//
//        new CountDownTimer(count, 1000) {
//            public void onTick(long millisUntilFinished) {
//                tv_Timer.setText("seconds remaining: " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//                tv_Timer.setText("done!");
//            }
//        }.start();



    }

    private void sendOtp(String mobileNo, final String otp){

        disposable.add(apiService.sendSMS("8754137753","admin123",
                "murugG4U9pJrPny3Xjio07Klfca","Your Molc otp is "+otp,mobileNo)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<SMSResponse>() {
            @Override
            public void onSuccess(SMSResponse smsResponse) {

                boolean status = smsResponse.getStatus();
                String message =smsResponse.getMsg();

                if(status){
                    editor.putString("otp",otp);
                    editor.commit();

                     etOtp.setVisibility(View.VISIBLE);
                }
                Log.e("errrrrrr",""+message);

            }

            @Override
            public void onError(Throwable e) {

             Log.e("err",""+e.toString());


            }
        }));


    }


     private void feedback() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final MaterialEditText etMobile = (MaterialEditText) dialogView.findViewById(R.id.mobileNo);
         etOtp = (MaterialEditText)dialogView.findViewById(R.id.otp);

         final Button btnSendOtp = (Button)dialogView.findViewById(R.id.sendOtp);

         btnSendOtp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String mobileNo = etMobile.getText().toString();
                 if (mobileNo.equalsIgnoreCase("")) {
                     Toast.makeText(MainActivity.this, "Enter Mobile No", Toast.LENGTH_SHORT).show();

                 }else if(mobileNo.length()<10){
                     Toast.makeText(MainActivity.this, "Enter 10 digit Mobile No", Toast.LENGTH_SHORT).show();


                 }   else{
                     etMobile.setEnabled(false);
                     sendOtp(mobileNo,getRandomNumberString());
                     btnSendOtp.setText("Resend Otp");

                 }
             }
         });


         etOtp.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                if(s.toString().length()==6){

                    String otpfromPref=preferences.getString("otp","");

                    Log.e("prrrrr",""+otpfromPref);
                    Log.e("prrrrr",""+s.toString());


                    if(s.toString().equalsIgnoreCase(otpfromPref)){
                        Toast.makeText(MainActivity.this, "Otp matched", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "otp not matched ", Toast.LENGTH_SHORT).show();

                    }
                }

             }
         });



        dialogBuilder.setTitle("Registration");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("otp"));
//
//    }
//
//
//    @Override
//    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//        super.onPause();
//    }
}


