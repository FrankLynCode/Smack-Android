package com.cainhuang.smackxmpp_demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cainhuang.smackxmpp_demo.service.SmackService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText handleInput;
    private EditText passwordInput;
    private Button actionBtn;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        res = this.getResources();

        handleInput = (EditText) this.findViewById(R.id.userNameText);
        passwordInput = (EditText) this.findViewById(R.id.passwordText);
        actionBtn = (Button) this.findViewById(R.id.actionButton);
        if(SmackService.connected()){
            actionBtn.setText(res.getString(R.string.loginActivity_disconnect_btn_text));
        }else{
            actionBtn.setText(res.getString(R.string.loginActivity_connect_btn_text));
        }

        handleInput.setText("admin");
        passwordInput.setText("1002");

        actionBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SmackService.class);

        if(SmackService.connected()){
            actionBtn.setText(res.getString(R.string.loginActivity_disconnect_btn_text));
            this.stopService(intent);
        } else {
            actionBtn.setText(res.getString(R.string.loginActivity_connect_btn_text));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit()
                    .putString("username", handleInput.getText().toString())
                    .putString("password", passwordInput.getText().toString())
                    .apply();

            this.startService(intent);
        }
    }
}