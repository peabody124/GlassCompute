package aakash.glasscompute;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.widget.ProgressBar;

/**
 * Builds intent URI from voice input and launches ComputeActivity
 * Copyright 2013 Aakash Patel
 * @author AakashPatel
 * 
 */
public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the loader
		setContentView(R.layout.activity_compute);
		// Make the progress bar white
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.getIndeterminateDrawable().setColorFilter(Color.WHITE, Mode.SRC_IN);
		
	}

	protected void onResume(){
		super.onResume();
		// Get voice input results
		ArrayList<String> voiceResults = getIntent().getExtras()
				.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		String voiceString = "";
		for(String str:voiceResults){
			voiceString = voiceString + " " + str;
		}
		voiceString = voiceString.trim();
		
		// Build a search URL
		// Format would be: compute://aaka.sh.glasscompute/?parsed=<false if raw voice input>&q=<query>
		Uri uri = new Uri.Builder()
		.scheme("compute")
		.authority("aakash.glasscompute")
		.appendQueryParameter("parsed", "false")
		.appendQueryParameter("q", voiceString)
		.build();
		// Build an intent with the uri and launch it
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(uri);
		startActivity(i);
	}
	
	protected void onPause(){
		super.onPause();
		// We're no longer needed
		finish();
	}

}
