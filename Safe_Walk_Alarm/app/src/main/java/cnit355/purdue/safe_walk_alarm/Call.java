package cnit355.purdue.safe_walk_alarm;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static cnit355.purdue.safe_walk_alarm.MainActivity.contact;
import static cnit355.purdue.safe_walk_alarm.MainActivity.incomingNumber;

public class Call extends AppCompatActivity {

    ImageView imageView;
    TextView contactName;
    TextView contactNumber;
    MediaPlayer ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        contactName = (TextView)findViewById(R.id.name);
        contactNumber = (TextView)findViewById(R.id.number);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = MediaPlayer.create(getApplicationContext(), notification);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        contactName.setText(contact);
        contactNumber.setText(incomingNumber);
        try
        {
            ringtone.start();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "There is a problem in playing ringtone", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        try
        {
            ringtone.stop();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
}
