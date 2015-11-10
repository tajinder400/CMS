package com.example.appointmentmanager;

import java.io.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;



interface AsyncResponse {
    void processFinish(String output);
}
/**
 * @author 
 * 
 * Allows easy manipulation of threads. Meaning it allows this to run in the background while the main UI is running elsewhere
 * 
 *
 */
class PostAsyncTask extends AsyncTask<String, Void, InputStream> {	
	@Override
	protected void onPreExecute() {}
	
	@Override
	protected InputStream doInBackground(String... urls) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();

		try {
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(new HttpPost(urls[0]));
			
			return response.getEntity().getContent();
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return null;
	}

	protected void onPostExecute(String result) {
	}
}
