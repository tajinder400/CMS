package com.example.appointmentmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author
 * 
 *         show info about the patient you picked at main activity 1. send
 *         request to server with patientid 2. parse xml response and show it
 * 
 */
public class PatientActivity extends ActionBarActivity {

	TextView name, address, phone, email;
	String id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		// Get the message from the intent
		Intent intent = getIntent();
		String message = intent.getStringExtra("PatientID");

		name = (TextView) findViewById(R.id.patientNameText);
		address = (TextView) findViewById(R.id.addressText);
		phone = (TextView) findViewById(R.id.phoneText);
		email = (TextView) findViewById(R.id.emailText);

		getPatient(message);
	}

	// this is kinda same as requestes in main activity, the diffrences are URL
	// (type, and other parameters)
	private void getPatient(String message) {
		String URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=getPatient&PatientID="
				+ message;

		try {
			InputStream response = new PostAsyncTask().execute(URL).get();
			if (response == null) {
				Toast.makeText(getApplicationContext(),
						"No result match your query", Toast.LENGTH_LONG).show();
				return;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(response);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;

					id = elem.getElementsByTagName("PatientID").item(0)
							.getChildNodes().item(0).getNodeValue();
					name.setText(elem.getElementsByTagName("PatientName")
							.item(0).getChildNodes().item(0).getNodeValue());
					address.setText(elem.getElementsByTagName("Address")
							.item(0).getChildNodes().item(0).getNodeValue());
					phone.setText(elem.getElementsByTagName("Phone").item(0)
							.getChildNodes().item(0).getNodeValue());
					email.setText(elem.getElementsByTagName("Email").item(0)
							.getChildNodes().item(0).getNodeValue());

				}
			}

		} catch (SAXException | InterruptedException | ExecutionException
				| ParserConfigurationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Attemps to send data to the server
	public void editClick(View v) {

		// build request string
		String id = "", name = "", address = "", phone = "", email = "";
		id = (String) this.id + "1";
		name = (String) this.name.getText() + "1";
		address = (String) this.address.getText() + "1";
		phone = (String) this.phone.getText() + "1";
		email = (String) this.email.getText() + "1";
		
		Intent intent = new Intent(PatientActivity.this, AddPatientActivity.class);
		intent.putExtra("PatientID", id);
		intent.putExtra("PatientName", name);
		intent.putExtra("Address", address);
		intent.putExtra("Phone", phone);
		intent.putExtra("Email", email);
		startActivity(intent);
	}

	// Starts a call intent (make a phone call happen)then sends the
	// number to it
	public void callClick(View v) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + (String) phone.getText()));
		startActivity(callIntent);
	}

	public void emailClick(View v) {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto", (String) email.getText(), null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient, menu);
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
