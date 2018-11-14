package iniyan.com.facebookintegrate.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by admin on 8/9/2017.
 */

public class SmsReceiverOtp extends BroadcastReceiver {

    private static SmsListenerOtp mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
try {
    Object[] pdus = (Object[]) data.get("pdus");

    for (int i = 0; i < pdus.length; i++) {
        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

        String sender = smsMessage.getDisplayOriginatingAddress();
        //You must check here if the sender is your provider and not another one with same text.

        String messageBody = smsMessage.getMessageBody();

        //Pass on the text to our listener.
        mListener.messageReceived(sender, messageBody);
    }
}catch (NullPointerException e){
    e.printStackTrace();
}
    }

    public static void bindListener(SmsListenerOtp listener) {
        mListener = listener;
    }
}
