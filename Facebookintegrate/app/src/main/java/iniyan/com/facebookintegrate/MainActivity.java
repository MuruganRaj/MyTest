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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
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
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.ShareMessengerMediaTemplateContent;
import com.facebook.share.model.ShareMessengerURLActionButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private static final String EMAIL = "email";
    ProfilePictureView profilePictureView;
    CallbackManager callbackManager;
    ProfileTracker profileTracker = null;
    LoginButton loginButton;
    ImageView profileImage, fbimage;
    TextView logout;
    Button fbfab;
    String TAG = MainActivity.class.getSimpleName();
    String email, firstName, lastName, userName, id;
    Uri imageUrl;
    MaterialEditText etOtp;
    VideoView videoView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView tv_Timer ;
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private int mCurrentPosition = 0;
    private Handler handler = new Handler();
    private Runnable runnable;

    private String EVENT_DATE_TIME = "2018-12-31 10:30:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Bitmap getFacebookProfilePicture(String userID) throws IOException {
        URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

        return bitmap;
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
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




//        feedback();


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        preferences = getSharedPreferences("test", Context.MODE_PRIVATE);
        editor = preferences.edit();

        fbfab = (Button) findViewById(R.id.fbfab);
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        logout = (TextView) findViewById(R.id.logout);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        fbimage = (ImageView) findViewById(R.id.fbimage);

        tv_Timer = (TextView)findViewById(R.id.timer);

        fbfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String type = "image/*";
//                String filename = "/myPhoto.jpg";
//                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//
//                String path = folder.getPath()+"/Camera/"+"IMG20181030065039.jpg";
//
//                Log.e("fileeeeee",path);
//
//                String mediaPath = Environment.getExternalStorageDirectory() + filename;
//
//                createInstagramIntent(type, path);


                URL url = null;
                try {
                    url = new URL("http://18.224.1.148:3000/images/img_1.png");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Bitmap image = null;
                try {
                     image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Drawable mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.test, null);
//                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();



                //messenger working

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Refer and Earn", null);
                Uri fileUri = Uri.parse(path);
////
////
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//
////                sendIntent.setType("text/plain");
//                sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                sendIntent.setType("image/jpeg");
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Your Promocode is Rs.232 And Checkout PayPre http:localhost/product ");
//                sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, " And Checkout PayPre http:localhost/produc");
//
//                sendIntent.setPackage("com.facebook.orca");
//
//                try {
//                    startActivity(sendIntent);
//                }
//                catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(getApplicationContext(),"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
//                }




                //face book share with content


//                ShareDialog    shareDialog = new ShareDialog(MainActivity.this);  // initialize facebook shareDialog.
//
//                ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                        .setContentTitle("Amazing Product")
//                        .setImageUrl(Uri.parse("http://18.224.1.148:3000/images/img_1.png"))
//                                        .setContentDescription(
//                                                "This product very low price hust INR.2999")
//                                                        .setContentUrl(Uri.parse("https://www.amazon.in/Sanyo-123-2-inches-XT-49S7100F-Black/dp/B01ICVLK4I/ref=gbph_img_m-2_1baa_cb589843?smid=AT95IG9ONZD7S&pf_rd_p=59bc242a-90c9-4c2b-8a7f-5efdecf61baa&pf_rd_s=merchandised-search-2&pf_rd_t=101&pf_rd_i=1389396031&pf_rd_m=A1VBAL9TL5WCBF&pf_rd_r=GWE5G8Q8R5DRTT9N3DX0"))
//                                                                        .build();
//
//
//                shareDialog.show(linkContent);  // Show facebook ShareDialog



                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent
                        .putExtra(Intent.EXTRA_TEXT,
                                "Hello.");
                sendIntent.setType("text/plain");
                sendIntent.setType("image/*");
                sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(),"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }



                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Your Title")
                        .setContentDescription("Your Description")
                        .setContentUrl(Uri.parse("URL[will open website or app]"))
                        .setImageUrl(Uri.parse("image or logo [if playstore or app store url then no need of this image url]"))
                        .build();


//                ShareMessengerURLActionButton actionButton =
//                        new ShareMessengerURLActionButton.Builder()
//                                .setTitle("Visit Facebook")
//                                .setUrl(Uri.parse("https://www.facebook.com"))
//                                .build();
//
//                ShareMessengerMediaTemplateContent mediaTemplateContent =
//                        new ShareMessengerMediaTemplateContent.Builder()
//                                .setPageId("Your page ID") // Your page ID, required
//                                .setMediaType(ShareMessengerMediaTemplateContent.MediaType.IMAGE)
//                                .setAttachmentId("Attachment Id") // AttachmentID, see media template documentation for how to upload an attachment
//                                .setButton(actionButton)
//                                .build();








//                                MessageDialog.show(mediaTemplateContent);  // Show facebook ShareDialog



//                ShareContent shareContent = new ShareMediaContent.Builder()
//                        .addMedium(sharePhoto1)
//                        .addMedium(sharePhoto2)
//                        .addMedium(shareVideo1)
//                        .addMedium(shareVideo2)
//                        .build();



//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
////                sharingIntent.setType("image/jpeg");
////                sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");
////                sharingIntent.putExtra(Intent.EXTRA_TEXT, _text);
//                sharingIntent.putExtra(Intent.EXTRA_STREAM,fileUri);  //optional//use this when you want to send an image
//                sharingIntent.setType("image/jpeg");
//                sharingIntent.setType("text/plain","image/*");
//
//                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(Intent.createChooser(sharingIntent, "Share via"));



             //facebook sharing working

//                Drawable mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.earn, null);
//                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Refer and Earn", null);
//                Uri fileUri = Uri.parse(path);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, "Your Promocode is And Checkout PayPre ");
//                intent.putExtra(Intent.EXTRA_HTML_TEXT, " And Checkout PayPre ");
//                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                intent.setType("image/*");
//                intent.setPackage("com.facebook.katana");
//                startActivity(intent);



            }
        });
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


//        getGroup();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logOut();
            }
        });

//        fbfab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList(EMAIL));
//
//
//            }
//        });




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

    private void createInstagramIntent(String type, String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
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
                "murugG4U9pJrPny3Xjio07Klfca","Your Molc otp is : "+otp,mobileNo)
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
                }else {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

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


