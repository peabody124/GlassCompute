package aakash.glasscompute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.glass.app.Card;
import com.google.gson.Gson;


/**
 * Activity that queries server for results
 * Copyright 2013 Aakash Patel
 * @author AakashPatel
 *
 */
public class ComputeActivity extends Activity {
	private static final String DEBUG_TAG = "ComputeActivity";
	public static final String DATA_TAG = "aakash.ComputeActivity.DATA";
	protected PowerManager.WakeLock mWakeLock;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compute);
		// Make progress bar white
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.getIndeterminateDrawable().setColorFilter(Color.WHITE, Mode.SRC_IN);
		// Read in Uri passed from MainActivity
		Uri uri = getIntent().getData();
		Set<String> parameters = uri.getQueryParameterNames();
		// Check if it cotains the right params
		if(!parameters.contains("parsed") || !parameters.contains("q")){
			Log.d(DEBUG_TAG, "Incorrect args provided...closing");
			finish();
		}
		// Extract params
		String query = uri.getQueryParameter("q");
		String parsed = uri.getQueryParameter("parsed");
		// Build a request Url so we can query the GlassComputeServer
		Uri.Builder serverUri = Uri.parse("http://ruxin.aaka.sh:8080").buildUpon();
		//TODO: change
		serverUri.appendQueryParameter("parsed", parsed);
		serverUri.appendQueryParameter("q", query);
		serverUri.build();
		// Start downloading results object
		new DownloadWebpageTask().execute(serverUri.toString());
		
		// Keep the screen on while downloading
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, DEBUG_TAG);
        this.mWakeLock.acquire();
	}

	protected void onResume(){

		super.onResume();


	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			try 
			{	// Get the provided URL arg
				URL url = new URL(urls[0]);
				// Read all the text returned by the server
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String str;
				String full = "";
				while ((str = in.readLine()) != null) 
				{
					full += str;
				}
				in.close();
				// Return the response string
				return full;
			} 
			// Catch any problems
			catch (MalformedURLException e) {
				Log.d(DEBUG_TAG,"MALFORMED URL: " + e);
			} catch (IOException e) {
				Log.d(DEBUG_TAG,"IOException: " + e);
			}
			// Return "error" if weird error
			return "error";
		}
		
		@Override
		protected void onPostExecute(String result) {
			// Build a ResultObject from result string 
			Gson gson = new Gson();
			ResultObject response = gson.fromJson(result, ResultObject.class);
			// Check for errors
			if(response.getStatus() == response.QUERY_NO_RESULTS){
				Card card = new Card(getBaseContext());
				card.setText("No results were found");
				View cardView = card.toView();
				setContentView(cardView);
			}
			else if(response.getStatus() == response.QUERY_UNKNOWN_ERROR || response.getStatus() == response.URL_ARGS_ERROR){
				Card card = new Card(getBaseContext());
				card.setText("Error code: " + response.getStatus() + "\nPlease try again.");
				View cardView = card.toView();
				setContentView(cardView);
			}
			
			else{
				// Do stuff with data
			
				// Send the newly formed ResultObject to ResultsActivity for displaying
				Intent ii=new Intent(ComputeActivity.this, ResultsActivity.class);
				ii.putExtra(DATA_TAG, result);
				startActivity(ii);
			}
		}
	}

	protected void onPause(){
		super.onPause();
		// We're not longer needed
		this.mWakeLock.release();
		finish();
	}




}
