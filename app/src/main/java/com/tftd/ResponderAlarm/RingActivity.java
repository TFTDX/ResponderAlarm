package com.tftd.ResponderAlarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.tftd.ResponderAlarm.R;

import java.io.IOException;

public class RingActivity extends Activity {

    final Context context = this;
    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        Bundle extras = getIntent().getExtras();
        String num = extras.getString("num");
        String msg = extras.getString("msg");
        String alert1 = "";
        String alert2 = "";
        String alert3 = "";
        String alert4 = "";
        String location = "";

        if(num.equals("70995") || msg.contains("(ACES)")){
            //Date & Time
            String datetime = msg.substring(msg.indexOf("(ACES)") + 7, msg.indexOf(" -"));
            //Location
            location = msg.substring(msg.indexOf("to") + 3, msg.indexOf(" for"));
            //Appliances
            String appliances = msg.substring(msg.indexOf("out") + 4, msg.indexOf(" to"));

            //Incident
            String incidentSeparator = "of ";
            int incPos = msg.indexOf(incidentSeparator);
            if (incPos == -1 )
            {
                System.out.println("");
            }
            String incident = msg.substring(incPos + incidentSeparator.length());

            alert1 = "Date & Time: " + datetime;
            alert2 = "Location: " + location;
            alert3 = "Appliances: " + appliances;
            alert4 = "Incident: " + incident;

        }


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean map = ((SharedPreferences) sp).getBoolean("map", false);
//        Toast.makeText(this, map + "", Toast.LENGTH_SHORT).show();
        String music = ((SharedPreferences) sp).getString("music", "1");
        String music_string;
         if (music.equals("1")){
            music_string = "aces-fire.mp3";
        }else{
            music_string = "aces-medical.mp3";
        }
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        if(IsRingerSilent() || IsVibrate())
        {
            //  smsManager.sendTextMessage(num, null, "Device turned to ringing mode.. && It's Ringing..", null, null);
            AudioManager audioManager= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mp.setLooping(true);
            try
            {
                AssetFileDescriptor afd;
                afd = getAssets().openFd(music_string);
                mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                mp.prepare();
                mp.start();
            }
            catch (IllegalStateException | IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //  smsManager.sendTextMessage(num, null, "Device Ringing...", null, null);
            AudioManager audioManager= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mp.setLooping(true);
            try
            {
                AssetFileDescriptor afd;
                afd = getAssets().openFd(music_string);
                mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                mp.prepare();
                mp.start();
            }
            catch (IllegalStateException | IOException e)
            {
                e.printStackTrace();
            }
        }


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        // Setting Dialog Title
        alertDialogBuilder.setTitle("Alert from ACES");

        // Setting Dialog Message
        if(num.equals("70995") || msg.contains("(ACES)")) {
            alertDialogBuilder.setMessage(alert1 + "\n" + alert2 + "\n" + alert4 + "\n" + alert3);
        }else{
            alertDialogBuilder.setMessage(msg);
        }
//        final EditText input = new EditText(RingActivity.this);
//        input.setHint("Type OK in this text box.");
//        alertDialogBuilder.setView(input);


        String finalLocation = location;
        alertDialogBuilder.setNegativeButton("Acknowledge", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                if(mp.isPlaying())
                {
                    mp.setLooping(false);
                    mp.stop();
                }

                Uri gmmIntentUri = Uri.parse("geo:1.3521,103.8198?q=" + finalLocation + "&mode=1");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if(map) {
                    try {
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    } catch (NullPointerException e) {

                    }
                }

                dialog.cancel();
                finish();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        //show dialog
        alertDialog.show();
//        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//        theButton.setOnClickListener(new CustomListener(alertDialog));


    }

    private boolean IsVibrate()
    {
        AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean IsRingerSilent()
    {
        AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent ke)
    {
        if(keycode==KeyEvent.KEYCODE_BACK)
        {

            if(mp.isPlaying())
            {
                mp.setLooping(false);
                mp.stop();
            }
            finish();
        }
        return true;
    }

    public boolean validateText(EditText text)
    {
        String textInput = text.getText().toString();

        if(!textInput.isEmpty() && textInput.equals("Ok")){
            return true;
        }
        else{
            Toast.makeText(this, "Please type OK", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}