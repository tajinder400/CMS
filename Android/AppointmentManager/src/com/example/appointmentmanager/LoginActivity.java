package com.example.appointmentmanager;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class LoginActivity extends ActionBarActivity {

	private EditText username = null;
	private EditText password = null;
	private TextView attempts;
	private Button login;
	int counter = 3;
//Assigning ID's to specific textfields
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		attempts = (TextView) findViewById(R.id.textView5);
		attempts.setText(Integer.toString(counter));
		login = (Button) findViewById(R.id.button1);
	}

	public void login(View view) {
		if (username.getText().toString().equals("admin")
				&& password.getText().toString().equals("admin")) {

			startActivity(new Intent(LoginActivity.this, MainActivity.class));
		} else {
			Toast.makeText(getApplicationContext(), "Wrong Credentials",
					Toast.LENGTH_SHORT).show();
			attempts.setBackgroundColor(Color.RED);
			counter--;
			attempts.setText(Integer.toString(counter));
			if (counter == 0) {
				login.setEnabled(false);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
