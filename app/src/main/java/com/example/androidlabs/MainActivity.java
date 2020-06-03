package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    //test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_linear);

        Button mButton = (Button) findViewById(R.id.button_show);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.toast_message),
                        Toast.LENGTH_LONG).show();
            }
        });

        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkbox_button);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                if (b) {
                Snackbar.make(getWindow().getCurrentFocus(),
                        getResources().getString(R.string.checkbox_on), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.undo), click -> {
                            cb.setChecked(false);
                        }).show();
                } else {
                    Snackbar.make(getWindow().getCurrentFocus(),
                            getResources().getString(R.string.checkbox_off), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.undo), click -> {
                                cb.setChecked(true);
                            }).show();
                }
            }
        });

        Switch mSwitch = (Switch) findViewById(R.id.switch_button);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                if (b) {
                    Snackbar.make(getWindow().getCurrentFocus(),
                            getResources().getString(R.string.switch_on), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.undo), click -> {
                                cb.setChecked(false);
                            }).show();
                } else {
                    Snackbar.make(getWindow().getCurrentFocus(),
                            getResources().getString(R.string.switch_off), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.undo), click -> {
                                cb.setChecked(true);
                            }).show();
                }
            }
        });
    }
}