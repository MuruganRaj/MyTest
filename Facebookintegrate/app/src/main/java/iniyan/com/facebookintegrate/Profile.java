package iniyan.com.facebookintegrate;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class Profile extends AppCompatActivity {


    ImageView im_save, im_edit;
    CircleImageView c_profile_image;
    TextView tv_username, tv_mobileno, tv_email;
    EditText et_mobileno, et_mailid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        im_save = (ImageView) findViewById(R.id.save);
        im_edit = (ImageView) findViewById(R.id.edit);
        c_profile_image = (CircleImageView) findViewById(R.id.profile_image);

        tv_username = (TextView) findViewById(R.id.username);
        tv_mobileno = (TextView) findViewById(R.id.number);
        tv_email = (TextView) findViewById(R.id.email);


        et_mobileno = (EditText) findViewById(R.id.etnumber);
        et_mailid = (EditText) findViewById(R.id.etemail);

        c_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openWhatsApp();
                String imageUri = "drawable://" + R.drawable.ic_checkbox_selected;


                Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (intent != null)
                {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage("com.instagram.android");
                    try {
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imageUri, "I am Happy", "Share happy !")));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    shareIntent.setType("image/jpeg");

                    startActivity(shareIntent);
                }
                else
                {
                    // bring user to the market to download the app.
                    // or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
                    startActivity(intent);
                }

            }
        });


    }


    private void openWhatsApp() {
        String smsNumber = "8754137753"; //without '+'
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


}