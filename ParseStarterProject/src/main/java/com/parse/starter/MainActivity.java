/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  RelativeLayout layout;
  ImageView imageViewIcon;
  EditText editTextUserName;
  EditText editTextPassword;
  Button buttonAuth;
  TextView textAuth;
  Boolean signUpModeActive = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle(R.string.instagram);

    layout = findViewById(R.id.relativeLayout);
    imageViewIcon = findViewById(R.id.imageViewIcon);
    editTextUserName = findViewById(R.id.editTextUserName);
    editTextPassword = findViewById(R.id.editTextPassword);
    buttonAuth = findViewById(R.id.buttonAuth);
    textAuth = findViewById(R.id.textViewAuth);

    layout.setOnClickListener(this);
    imageViewIcon.setOnClickListener(this);
    textAuth.setOnClickListener(this);
    editTextPassword.setOnKeyListener(this);

    if (ParseUser.getCurrentUser() != null) {
//      Log.i("userName", ParseUser.getCurrentUser().getUsername())
//      showUserList();
      Log.i("current User", "LoggedIn");
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {
    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      buttonClicked(view);
    }
    return false;
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.textViewAuth){
      if (signUpModeActive) {
        signUpModeActive = false;
        buttonAuth.setText(R.string.login);
        textAuth.setText(R.string.signup_text);
      } else if (!signUpModeActive) {
        signUpModeActive = true;
        buttonAuth.setText(R.string.signup);
        textAuth.setText(R.string.signin_text);
      }
    }

    else if (view.getId() == R.id.relativeLayout || view.getId() == R.id.imageViewIcon) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void buttonClicked(View view) {
    if (editTextUserName.getText().toString().matches("") || editTextPassword.getText().toString().matches("")
            || editTextUserName.getText().toString() == null || editTextPassword.getText().toString() == null) {
      Toast.makeText(this, "UserName and Password is Required!", Toast.LENGTH_LONG).show();
    } else {
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(editTextUserName.getText().toString());
        user.setPassword(editTextPassword.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Toast.makeText(getApplicationContext(), "SignUp Done!", Toast.LENGTH_LONG).show();
              showUserList();
            } else {
              Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
          }
        });
      }
      else {
        ParseUser.logInInBackground(editTextUserName.getText().toString(), editTextPassword.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (e == null && user != null) {
              Toast.makeText(getApplicationContext(), "Login Done!", Toast.LENGTH_LONG).show();
              showUserList();
            }
            else {
              Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
          }
        });
      }
    }
  }
  
  public void showUserList() {
    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);
  }
}