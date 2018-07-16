package cnit355.purdue.safe_walk_alarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static cnit355.purdue.safe_walk_alarm.Location.curPoint;

public class MainActivity extends AppCompatActivity {

    MediaPlayer alert;
    AudioManager audioManager;
    Camera camera;
    Camera.Parameters parameters;
    Button alarm, police, message, call, location;
    EditText passwordPrompt;
    TextView message_locked, call_locked;
    static String callNumber, password, passwordEntry, messageNumber, messageContent, contact, incomingNumber;
    static Boolean flashlight, flashlightOn, /*photo,*/ passwordEnabled, numberEnabled, instantMessage, coordinate, notifyCaller;
    static int counter, volume, volumeMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarm = (Button)findViewById(R.id.alarm);
        police = (Button)findViewById(R.id.police);
        message = (Button)findViewById(R.id.message);
        call = (Button)findViewById(R.id.call);
        location = (Button)findViewById(R.id.location);

        passwordPrompt = new EditText(this);
        passwordPrompt.setText("");
        passwordPrompt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        message_locked = (TextView)findViewById(R.id.message_locked);
        call_locked = (TextView)findViewById(R.id.call_locked);

        password = "";
        passwordEntry = "";
        callNumber = "911";
        messageNumber = "";
        messageContent = "";
        contact = "";
        incomingNumber = "";
        flashlight = false;
        flashlightOn = false;
        //photo = false;
        passwordEnabled = false;
        numberEnabled = false;
        instantMessage = false;
        coordinate = false;
        notifyCaller = false;
        counter = 1;

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        volume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC) / 2;
        volumeMax = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);

        alarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (counter == 1)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                    alert = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
                    alert.setLooping(true);
                    alert.start();

                    if (flashlight == true)
                    {
                        try
                        {
                            camera = Camera.open();
                            parameters = camera.getParameters();
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                            camera.setParameters(parameters);
                            camera.startPreview();
                            flashlightOn = true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    counter = counter * -1;
                }
                else
                {
                    if (passwordEnabled == true && !password.equals(""))
                    {
                        AlertDialog.Builder unlock = new AlertDialog.Builder(MainActivity.this);
                        unlock.setTitle("Diable Alarm");
                        unlock.setMessage("Please enter password to disable alarm.");
                        unlock.setCancelable(false);
                        if (passwordPrompt.getParent() != null)
                        {
                            ((ViewGroup)passwordPrompt.getParent()).removeView(passwordPrompt);
                        }
                        unlock.setView(passwordPrompt);
                        unlock.setPositiveButton("Enter", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                passwordEntry = passwordPrompt.getText().toString();
                                if (!passwordEntry.equals(password))
                                {
                                    Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else
                                {
                                    alert.stop();
                                    if (flashlightOn == true)
                                    {
                                        try
                                        {
                                            parameters = camera.getParameters();
                                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                            camera.setParameters(parameters);
                                            camera.stopPreview();
                                            flashlightOn = false;
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    counter = counter * -1;
                                }
                            }
                        });
                        unlock.create();
                        unlock.show();
                        passwordPrompt.setText("");
                        passwordEntry = "";
                    }
                    else
                    {
                        alert.stop();
                        if (flashlightOn == true)
                        {
                            try
                            {
                                parameters = camera.getParameters();
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(parameters);
                                camera.stopPreview();
                                flashlightOn = false;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        counter = counter * -1;
                    }
                }
            }
        });

        police.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (callNumber == "")
                {
                    callNumber = "911";
                    numberEnabled = false;
                }
                police(view);
            }
        });

        message.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                instantMessage = true;
                message(view);

            }
        });

        message.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                instantMessage = false;
                message(view);
                return true;
            }
        });

        call.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent call = new Intent(MainActivity.this, Call.class);
                startActivity(call);

                if (notifyCaller == true)
                {
                    String oldNumber = messageNumber;
                    messageNumber = incomingNumber;
                    instantMessage = true;
                    message(view);
                    messageNumber = oldNumber;
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent location = new Intent(MainActivity.this, Location.class);
                startActivity(location);
            }
        });
    }

    @Override protected void onResume()
    {
        super.onResume();

        if (messageNumber.equals("") || (messageContent.equals("") && coordinate == false))
        {
            message_locked.setVisibility(View.VISIBLE);
            message.setEnabled(false);
        }
        else
        {
            message_locked.setVisibility(View.INVISIBLE);
            message.setEnabled(true);
        }

        if (contact.equals(""))
        {
            call_locked.setVisibility(View.VISIBLE);
            call.setEnabled(false);
        }
        else
        {
            call_locked.setVisibility(View.INVISIBLE);
            call.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Settings");
        menu.add(0, 2, 0, "About");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                Intent settings = new Intent (this, Settings.class);
                startActivity(settings);
                return true;
            case 2:
                AlertDialog about = new AlertDialog.Builder(MainActivity.this).create();
                about.setTitle("App Info");
                about.setMessage("Safe Walk App for CNIT 425 Project");
                about.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void police (View view)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
            startActivity(intent);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    public void message (View view)
    {
        try
        {
            if (instantMessage == false)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + messageNumber));
                if (coordinate == true)
                {
                    intent.putExtra("sms_body", messageContent + "\nMy coordinate: \nLatitude: " + curPoint.latitude + "\nLongitude: " + curPoint.longitude);
                } else
                {
                    intent.putExtra("sms_body", messageContent);
                }
                startActivity(intent);
            }
            else
            {
                if (coordinate == true)
                {
                    SmsManager.getDefault().sendTextMessage(messageNumber, null, messageContent + "\nMy coordinate: \nLatitude: " + curPoint.latitude + "\nLongitude: " + curPoint.longitude, null, null);
                } else
                {
                    SmsManager.getDefault().sendTextMessage(messageNumber, null, messageContent, null, null);
                }
                Toast.makeText(getApplicationContext(), "Emergency message has been sent.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }
}
