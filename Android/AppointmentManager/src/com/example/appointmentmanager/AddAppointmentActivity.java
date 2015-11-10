package com.example.appointmentmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddAppointmentActivity extends ActionBarActivity {

	EditText appointmentid, time, id, name, address, phone, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_appointment);

		// Get the message from the intent
		Intent intent = getIntent();

		appointmentid = (EditText) findViewById(R.id.appointmentIDText);
		time = (EditText) findViewById(R.id.timeText);
		id = (EditText) findViewById(R.id.patientIDText);
		name = (EditText) findViewById(R.id.patientNameText);
		address = (EditText) findViewById(R.id.addressText);
		phone = (EditText) findViewById(R.id.phoneText);
		email = (EditText) findViewById(R.id.emailText);

		appointmentid.setText(intent.getStringExtra("AppointmentID"));
		time.setText(intent.getStringExtra("TimeID"));
		id.setText(intent.getStringExtra("PatientID"));
		name.setText(intent.getStringExtra("PatientName"));
		address.setText(intent.getStringExtra("Address"));
		phone.setText(intent.getStringExtra("Phone"));
		email.setText(intent.getStringExtra("Email"));
	}

	public void saveClick(View v) {
		// build request string
		String appointmentid="", time="", id = "", name = "", address = "", phone = "", email = "";
		appointmentid = this.appointmentid.getEditableText().toString();
		time = this.time.getEditableText().toString();
		id = this.id.getEditableText().toString();
		name = this.name.getEditableText().toString();
		address = this.address.getEditableText().toString();
		phone = this.phone.getEditableText().toString();
		email = this.email.getEditableText().toString();
		String URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=addAppointment&AppointmentID="
				+appointmentid
				+"&PatientID="
				+id
				+"&Time="
				+ time;
		

		Toast.makeText(getApplicationContext(), URL,
				Toast.LENGTH_LONG).show();
		
		// now send request and show result
		try {
			InputStream in = new PostAsyncTask().execute(URL).get();

			InputStreamReader is = new InputStreamReader(in);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();

			while (read != null) {
				sb.append(read);
				read = br.readLine();

			}
			Toast.makeText(getApplicationContext(), sb.toString(),
					Toast.LENGTH_LONG).show();
			
			URL="http://10.0.2.2:8080/Appointment/AppointmentRequest?type=addPatient&PatientID="
			+ id
			+ "&PatientName="
			+ name
			+ "&Address="
			+ address
			+ "&Phone=" + phone + "&Email=" + email;
			
			in= new PostAsyncTask().execute(URL).get();
			is = new InputStreamReader(in);
			sb = new StringBuilder();
			br = new BufferedReader(is);
			read = br.readLine();

			while (read != null) {
				sb.append(read);
				read = br.readLine();

			}
			Toast.makeText(getApplicationContext(), sb.toString(),
					Toast.LENGTH_LONG).show();
		} catch (InterruptedException | ExecutionException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_appointment, menu);
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
