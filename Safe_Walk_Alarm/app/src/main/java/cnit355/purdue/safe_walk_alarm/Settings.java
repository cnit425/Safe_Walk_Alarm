package cnit355.purdue.safe_walk_alarm;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static cnit355.purdue.safe_walk_alarm.MainActivity.password;
import static cnit355.purdue.safe_walk_alarm.MainActivity.callNumber;
import static cnit355.purdue.safe_walk_alarm.MainActivity.messageNumber;
import static cnit355.purdue.safe_walk_alarm.MainActivity.messageContent;
import static cnit355.purdue.safe_walk_alarm.MainActivity.contact;
import static cnit355.purdue.safe_walk_alarm.MainActivity.incomingNumber;
import static cnit355.purdue.safe_walk_alarm.MainActivity.flashlight;
//import static cnit355.purdue.safe_walk_alarm.MainActivity.photo;
import static cnit355.purdue.safe_walk_alarm.MainActivity.passwordEnabled;
import static cnit355.purdue.safe_walk_alarm.MainActivity.numberEnabled;
import static cnit355.purdue.safe_walk_alarm.MainActivity.coordinate;
import static cnit355.purdue.safe_walk_alarm.MainActivity.notifyCaller;
import static cnit355.purdue.safe_walk_alarm.MainActivity.volume;
import static cnit355.purdue.safe_walk_alarm.MainActivity.volumeMax;

import static cnit355.purdue.safe_walk_alarm.Location.curPoint;

public class Settings extends AppCompatActivity {

    LinearLayout linearLayout;
    SeekBar volumeInput;
    CheckBox addFlashlight, /*addPhoto,*/ addPassword, addNumber, addCoordinate, addNotifyCaller;
    EditText passwordPrompt, passwordInput, callNumberInput, messageNumberInput, messageContentInput, contactInput, incomingNumberInput;
    String passwordEntry;
    Boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        linearLayout = (LinearLayout)findViewById(R.id.settings);

        volumeInput = (SeekBar)findViewById(R.id.volumeInput);

        addFlashlight = (CheckBox)findViewById(R.id.addFlashlight);
        //addPhoto = (CheckBox)findViewById(R.id.addPhoto);
        addPassword = (CheckBox)findViewById(R.id.addPassword);
        addNumber = (CheckBox)findViewById(R.id.addNumber);
        addCoordinate = (CheckBox)findViewById(R.id.addCoordinate);
        addNotifyCaller = (CheckBox)findViewById(R.id.addNotifyCaller);

        passwordPrompt = new EditText(this);
        passwordPrompt.setText("");
        passwordPrompt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordInput = (EditText)findViewById(R.id.passwordInput);
        callNumberInput = (EditText)findViewById(R.id.callNumberInput);
        messageNumberInput = (EditText)findViewById(R.id.messageNumberInput);
        messageContentInput = (EditText)findViewById(R.id.messageContentInput);
        contactInput = (EditText)findViewById(R.id.contactInput);
        incomingNumberInput = (EditText)findViewById(R.id.incomingNumberInput);

        start = true;

        volumeInput.setMax(volumeMax);
        volumeInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                volume = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        addFlashlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b == true)
                {
                    flashlight = true;
                }
                else
                {
                    flashlight = false;
                }
            }
        });

        /*addPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b == true)
                {
                    photo = true;
                }
                else
                {
                    photo = false;
                }
            }
        });*/

        addPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b == true)
                {
                    passwordEnabled = true;
                    passwordInput.setEnabled(true);
                    passwordInput.requestFocus();
                }
                else
                {
                    passwordEnabled = false;
                    passwordInput.setText("");
                    passwordInput.setEnabled(false);
                }
            }
        });

        passwordEntry = "";
        passwordInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                password = passwordInput.getText().toString();
                if (password.equals(""))
                {
                    addPassword.setEnabled(true);
                }
                else
                {
                    addPassword.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if (b == true)
                {
                    if (!password.equals("") && start == false)
                    {
                        AlertDialog.Builder unlock = new AlertDialog.Builder(Settings.this);
                        unlock.setTitle("Edit Password");
                        unlock.setMessage("Please enter your last password to enable editing.");
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
                                    linearLayout.requestFocus();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Exit editing this textbox to save your new input, or clear input to disable password.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        unlock.create();
                        unlock.show();
                        passwordPrompt.setText("");
                        passwordEntry = "";
                    }
                    else if (start == false)
                    {
                        Toast.makeText(getApplicationContext(), "Your password will be saved once you exit editing this textbox. An empty entry will disable password.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(password.equals(""))
                    {
                        addPassword.setChecked(false);
                    }
                    else if (start == false)
                    {
                        Toast.makeText(getApplicationContext(), "Password saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        addNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b == true)
                {
                    numberEnabled = true;
                    callNumberInput.setEnabled(true);
                }
                else
                {
                    numberEnabled = false;
                    callNumberInput.setText("911");
                    callNumberInput.setEnabled(false);
                }
            }
        });

        callNumberInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                callNumber = callNumberInput.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        messageNumberInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                messageNumber = messageNumberInput.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        messageContentInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                messageContent = messageContentInput.getText().toString();
                if (messageContent.equals("") && coordinate == false)
                {
                    addNotifyCaller.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        addCoordinate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b == true)
                {
                    if (curPoint == null && start == false)
                    {
                        Toast.makeText(getApplicationContext(), "'Include geography information' requires your location information. Please enable GPS and use 'Location' function on the main screen before enabling this feature.", Toast.LENGTH_LONG).show();
                        addCoordinate.setChecked(false);
                        coordinate = false;
                    }
                    else if (start == false)
                    {
                        Toast.makeText(getApplicationContext(), "Your last location displayed on 'Location' function will be sent.", Toast.LENGTH_LONG).show();
                        coordinate = true;
                    }
                }
                else
                {
                    coordinate = false;
                    messageContent = messageContentInput.getText().toString();
                    if (coordinate == false && messageContent.equals(""))
                    {
                        addNotifyCaller.setChecked(false);
                    }
                }
            }
        });

        contactInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                contact = contactInput.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        incomingNumberInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                incomingNumber = incomingNumberInput.getText().toString();
                if (incomingNumber.equals(""))
                {
                    addNotifyCaller.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        addNotifyCaller.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b == true)
                {
                    if (incomingNumber.equals("") || (messageContent.equals("") && coordinate == false))
                    {
                        Toast.makeText(getApplicationContext(), "'Notify the caller' feature will be enabled when you enter message content and a number to notify.", Toast.LENGTH_LONG).show();
                        addNotifyCaller.setChecked(false);
                        notifyCaller = false;
                    }
                    else
                    {
                        notifyCaller = true;
                    }
                }
                else
                {
                    notifyCaller = false;
                }
            }
        }));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (passwordEnabled == false)
        {
            passwordInput.setEnabled(false);
        }
        if (numberEnabled == false)
        {
            callNumberInput.setEnabled(false);
        }

        volumeInput.setProgress(volume);

        addFlashlight.setChecked(flashlight);
        //addPhoto.setChecked(photo);
        addPassword.setChecked(passwordEnabled);
        addNumber.setChecked(numberEnabled);
        addCoordinate.setChecked(coordinate);
        addNotifyCaller.setChecked(notifyCaller);

        passwordInput.setText(password);
        callNumberInput.setText(callNumber);
        messageNumberInput.setText(messageNumber);
        messageContentInput.setText(messageContent);
        contactInput.setText(contact);
        incomingNumberInput.setText(incomingNumber);

        linearLayout.requestFocus();

        start = false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        start = true;
    }

    private void load()
    {
        try
        {
            start = true;

            FileInputStream fileInputStream = openFileInput("settings.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            volume = Integer.valueOf(bufferedReader.readLine());
            volumeInput.setProgress(volume);

            flashlight = Boolean.valueOf(bufferedReader.readLine());
            addFlashlight.setChecked(flashlight);

            //photo = Boolean.valueOf(bufferedReader.readLine());
            //addPhoto.setChecked(photo);

            passwordEnabled = Boolean.valueOf(bufferedReader.readLine());
            addPassword.setChecked(passwordEnabled);

            password = bufferedReader.readLine();
            passwordInput.setText(password);

            numberEnabled = Boolean.valueOf(bufferedReader.readLine());
            addNumber.setChecked(numberEnabled);

            callNumber = bufferedReader.readLine();
            callNumberInput.setText(callNumber);

            messageNumber = bufferedReader.readLine();
            messageNumberInput.setText(messageNumber);

            messageContent = bufferedReader.readLine();
            messageContentInput.setText(messageContent);

            coordinate = Boolean.valueOf(bufferedReader.readLine());
            addCoordinate.setChecked(coordinate);

            contact = bufferedReader.readLine();
            contactInput.setText(contact);

            incomingNumber = bufferedReader.readLine();
            incomingNumberInput.setText(incomingNumber);

            notifyCaller = Boolean.valueOf(bufferedReader.readLine());
            addNotifyCaller.setChecked(notifyCaller);

            linearLayout.requestFocus();

            start = false;

            bufferedReader.close();
            fileInputStream.close();
            Toast.makeText(getApplicationContext(), "Saved settings loaded", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Load saved settings");
        menu.add(0, 2, 0, "Save settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                if (getBaseContext().getFileStreamPath("settings.txt").exists())
                {
                    if (!password.equals(""))
                    {
                        AlertDialog.Builder unlock = new AlertDialog.Builder(Settings.this);
                        unlock.setTitle("Load Saved Settings");
                        unlock.setMessage("Please enter your current password for alarm to load saved file.");
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
                                if (passwordEntry.equals(password))
                                {
                                    AlertDialog.Builder warning = new AlertDialog.Builder(Settings.this);
                                    warning.setTitle("Password Change Warning");
                                    warning.setMessage("The current password will be replaced by the one in saved settings file. Are you sure to continue?");
                                    warning.setPositiveButton("Continue", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            load();
                                        }
                                    });
                                    warning.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            Toast.makeText(getApplicationContext(), "Load cancelled", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    });
                                    warning.create();
                                    warning.show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                                    return;
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
                        load();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No saved settings found.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case 2:
                if (passwordInput.isFocused() && !password.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "You are currently setting a password. Please remember your input and save it before proceeding to save settings.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    try
                    {
                        FileOutputStream fileOutputStream = openFileOutput("settings.txt", Context.MODE_PRIVATE);
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                        bufferedWriter.append(String.valueOf(volume) + "\r\n");
                        bufferedWriter.append(flashlight.toString() + "\r\n");
                        //bufferedWriter.append(photo.toString() + "\r\n");
                        bufferedWriter.append(passwordEnabled.toString() + "\r\n");
                        bufferedWriter.append(password + "\r\n");
                        bufferedWriter.append(numberEnabled.toString() + "\r\n");
                        bufferedWriter.append(callNumber + "\r\n");
                        bufferedWriter.append(messageNumber + "\r\n");
                        bufferedWriter.append(messageContent + "\r\n");
                        bufferedWriter.append(coordinate.toString() + "\r\n");
                        bufferedWriter.append(contact + "\r\n");
                        bufferedWriter.append(incomingNumber + "\r\n");
                        bufferedWriter.append(notifyCaller.toString());

                        bufferedWriter.close();
                        fileOutputStream.close();
                        Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
