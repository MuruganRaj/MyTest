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

    public static void bindListener(SmsListenerOtp listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        String sms_str ="";
        SmsMessage[] smsm = null;
        String sender =null;
try {
    Object[] pdus = (Object[]) data.get("pdus");
    smsm = new SmsMessage[pdus.length];
    for (int i = 0; i < pdus.length; i++) {
        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

         sender = smsMessage.getDisplayOriginatingAddress();
        //You must check here if the sender is your provider and not another one with same text.

//        sms_str += "\r\nMessage: ";
//        sms_str += smsm[i].getMessageBody().toString();
//        sms_str+= "\r\n";
//
//        System.out.println("smmss"+sms_str);


        String messageBody = getVerificationCode(smsMessage.getMessageBody());

        System.out.println("optttttttt"+messageBody);
        mListener.messageReceived(sender, messageBody);

        //Pass on the text to our listener.
    }


}catch (NullPointerException e){
    e.printStackTrace();
}
    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(":");

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
