package com.tftd.ResponderAlarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class ReceiveSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String msg_from;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String alarm_source = ((SharedPreferences) sp).getString("alarm", "70995");
            if(bundle != null){
                try{
                    Object [] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i = 0; i< msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if(msg_from.equals("70995") && msgBody.contains("Turn out")&& msg_from.equals(alarm_source)){
                            this.abortBroadcast();
                            Intent i1 = new Intent(context, RingActivity.class);
                            i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i1.putExtra("num", msg_from);
                            i1.putExtra("msg", msgBody);
                            context.startActivity(i1);
                        }else if(!msg_from.equals("70995") && msg_from.equals(alarm_source)){
                            this.abortBroadcast();
                            Intent i1 = new Intent(context, RingActivity.class);
                            i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i1.putExtra("num", msg_from);
                            i1.putExtra("msg", msgBody);
                            context.startActivity(i1);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
}