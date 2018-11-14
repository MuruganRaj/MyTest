package iniyan.com.facebookintegrate.sms;

/**
 * Created by admin on 8/9/2017.
 */

public interface SmsListenerOtp {
    public void messageReceived(String provider, String messageText);
}