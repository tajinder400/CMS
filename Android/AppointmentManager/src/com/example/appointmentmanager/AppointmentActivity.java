package com.example.appointmentmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

public class AppointmentActivity extends ActionBarActivity {

	TextView appointmentid, time, id, name, address, phone, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);// Get the message from the intent

		setContentView(R.layout.activity_appointment);
		Intent intent = getIntent();
		String message = intent.getStringExtra("AppointmentID");

		appointmentid = (TextView) findViewById(R.id.appointmentIDText);
		time = (TextView) findViewById(R.id.timeText);
		id = (TextView) findViewById(R.id.patientIDText);
		name = (TextView) findViewById(R.id.patientNameText);
		address = (TextView) findViewById(R.id.addressText);
		phone = (TextView) findViewById(R.id.phoneText);
		email = (TextView) findViewById(R.id.emailText);

		getAppointment(message);
	}

	private void getAppointment(String message) {
		String URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=getAppointment&AppointmentID="
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

					appointmentid.setText(elem
							.getElementsByTagName("AppointmentID").item(0)
							.getChildNodes().item(0).getNodeValue());
					time.setText(elem.getElementsByTagName("Time").item(0)
							.getChildNodes().item(0).getNodeValue());
					id.setText(elem.getElementsByTagName("PatientID").item(0)
							.getChildNodes().item(0).getNodeValue());
				}
			}

		} catch (SAXException | InterruptedException | ExecutionException
				| ParserConfigurationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=getPatient&PatientID="
					+ (String) this.id.getText();
			InputStream response = new PostAsyncTask().execute(URL).get();
			if (response == null) {
				Toast.makeText(getApplicationContext(),
						"No patient matches that ID", Toast.LENGTH_LONG).show();
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

	public void editClick(View v) {
		String appointmenid, id = "", time = "", name = "", address, phone, email;
		appointmenid = (String) this.appointmentid.getText() + "1";
		id = (String) this.id.getText() + "1";
		name = (String) this.name.getText() + "1";
		time = (String) this.time.getText() + "1";
		address = (String) this.address.getText() + "1";
		phone = (String) this.phone.getText() + "1";
		email = (String) this.email.getText() + "1";
		Intent intent = new Intent(AppointmentActivity.this,
				AddAppointmentActivity.class);
		intent.putExtra("AppointmentID",appointmenid);
		intent.putExtra("Time", time);
		intent.putExtra("PatientID", id);
		intent.putExtra("PatientName", name);
		intent.putExtra("Address", address);
		intent.putExtra("Phone", phone);
		intent.putExtra("Email", email);
		startActivity(intent);

	}

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
		getMenuInflater().inflate(R.menu.appointment, menu);
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
