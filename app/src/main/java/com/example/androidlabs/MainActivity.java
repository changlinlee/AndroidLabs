package com.example.androidlabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //cd /data/data/com.example.androidlabs/shared_prefs
    //more email.xml

    private EditText email, password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private String emailFileName = "email";
    private String emailKey = "Email_Address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email_login);
        password = (EditText) findViewById(R.id.password);
        sharedPreferences = getSharedPreferences(emailFileName, Context.MODE_PRIVATE);

        email.setText(sharedPreferences.getString(emailKey, ""));
//        Button mButton = (Button) findViewById(R.id.button_show);
//        mButton.setOnClickListener(v -> Toast.makeText(MainActivity.this,
//                getResources().getString(R.string.toast_message),
//                Toast.LENGTH_LONG).show());
//
//        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkbox_button);
//        mCheckBox.setOnCheckedChangeListener((cb, b) -> {
//            if (b) {
//            Snackbar.make(mCheckBox,
//                    getResources().getString(R.string.checkbox_on), Snackbar.LENGTH_LONG)
//                    .setAction(getResources().getString(R.string.undo), click -> {
//                        cb.setChecked(false);
//                    }).show();
//            } else {
//                Snackbar.make(mCheckBox,
//                        getResources().getString(R.string.checkbox_off), Snackbar.LENGTH_LONG)
//                        .setAction(getResources().getString(R.string.undo), click -> {
//                            cb.setChecked(true);
//                        }).show();
//            }
//        });
//
//        Switch mSwitch = (Switch) findViewById(R.id.switch_button);
//        mSwitch.setOnCheckedChangeListener((cb, b) -> {
//            if (b) {
//                Snackbar.make(mSwitch,
//                        getResources().getString(R.string.switch_on), Snackbar.LENGTH_LONG)
//                        .setAction(getResources().getString(R.string.undo), click -> {
//                            cb.setChecked(false);
//                        }).show();
//            } else {
//                Snackbar.make(mSwitch,
//                        getResources().getString(R.string.switch_off), Snackbar.LENGTH_LONG)
//                        .setAction(getResources().getString(R.string.undo), click -> {
//                            cb.setChecked(true);
//                        }).show();
//            }
//        });
    }

    public void onPause(View view) {
        editor = sharedPreferences.edit();
        editor.putString(emailKey, email.getText().toString().trim());
        editor.commit();

        Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
        goToProfile.putExtra("EMAIL", email.getText().toString().trim());
        this.startActivity(goToProfile);
    }
}