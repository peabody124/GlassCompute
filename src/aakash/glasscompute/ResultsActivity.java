package aakash.glasscompute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollView;
import com.google.gson.Gson;

/**
 * Activity to show results
 * Copyright 2013 Aakash Patel
 * @author AakashPatel
 *
 */
public class ResultsActivity extends Activity implements SensorEventListener {
	private static final String DEBUG_TAG = "ResultsActivity";
	private ResultObject results;
	private CardScrollView mCardScrollView;
	private SensorManager sensorManager;
	private Sensor orientationSensor;
	private ArrayList<PagePos> pagePos;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// Get the ResultObject  that was passed to us
		Intent iin= getIntent();
		Bundle b = iin.getExtras();
		String data = "";
		if(b!=null)
		{
			data =(String) b.get(ComputeActivity.DATA_TAG);
		}
		Gson gson = new Gson();
		results = gson.fromJson(data, ResultObject.class);
		
		// Get stuff ready to hold data about scroll position
		pagePos = new ArrayList<PagePos>();
		for(int i = 0; i < results.getNumberOfPods(); i++){
			pagePos.add(new PagePos());
		}

		Log.d(DEBUG_TAG, results.toString());
		
		// Let's us scroll through results 
		mCardScrollView = new CardScrollView(this);
		ResultsScrollAdapter adapter = new ResultsScrollAdapter(this, results);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		// Show the results
		setContentView(mCardScrollView);

		// Get orientation sensor so we can scroll up and down
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	}

	protected void onResume(){
		super.onResume();
		// Register the sensor manager
		sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}	

	public void onSensorChanged(SensorEvent arg0) {
		// Convert to int between 0 (looking straight down) and 180 (looking straight up). ~90 while looking straight forward.
		int y =  180-(-1*(int)arg0.values[1]);
		// Get the ScrollView of the currently viewed card.
		View view = (View)mCardScrollView.getSelectedView();
		int currentCardIndex = mCardScrollView.getSelectedItemPosition();
		ScrollView scroller = (ScrollView)view.findViewById(R.id.resultsScrollView);
		// Get the current scroll data for the current ScrollView
		PagePos pos = pagePos.get(currentCardIndex);
		// Don't scroll unless the LinearLayout inside the ScrollView is taller than the ScrollView
		if(scroller.getChildAt(0).getHeight() > scroller.getHeight()){
			// Setup default vals
			if(pos.current == 999){
				pos.current = 0;
				pos.min = y;
				pos.max = y+scroller.getChildAt(0).getHeight()-scroller.getHeight();
				
			}
			// Case: You moved your head up, even though you were already scrolled all the way up
			// Result: make the new min scroll pos where you head is.
			else if(y < pos.min || pos.current < 0){
			
				pos.min = y;
				pos.max = y+scroller.getChildAt(0).getHeight()-scroller.getHeight();
				pos.current = 0;
			}
			else if(pos.max < (scroller.getChildAt(0).getHeight()/15)*pos.current){
				pos.min = y-(scroller.getChildAt(0).getHeight()-scroller.getHeight());
				pos.current = pos.max/(scroller.getChildAt(0).getHeight()/15);
				pos.max = y+(scroller.getChildAt(0).getHeight()-scroller.getHeight());
				
			}
			// Case: You moved your head up, and are within min/max bounds
			// Result: make the new min scroll pos where you head is.
			else{
				if(y > pos.prevY){
					pos.current = pos.current + (y - pos.prevY);
				}
				else if(y < pos.prevY){
					pos.current = pos.current - (pos.prevY - y);
				}
			}
			//Log.d(DEBUG_TAG, "Pos.min=" + pos.min);
			//Log.d(DEBUG_TAG, "Pos.max=" + pos.max);
			//Log.d(DEBUG_TAG, "Pos.current=" + pos.current);
			pos.prevY = y;
			// Scroll
			scroller.scrollTo(scroller.getScrollX(), (scroller.getChildAt(0).getHeight()/15)*pos.current);
			//Log.d(DEBUG_TAG, "Val: " + (scroller.getChildAt(0).getHeight()/15)*pos.current);
		}
		

	}

	@Override
	protected void onPause() {
		// Be sure to unregister the sensor when the activity pauses.
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// We don't really care about this

	}

	private class PagePos{
		// Holds min y position 
		public int min = 999;
		// Holds max y position
		public int max = 999;
		// Holds current scroll value
		public int current = 999;
		// Holds last y value
		public int prevY = 999;
	}
}


