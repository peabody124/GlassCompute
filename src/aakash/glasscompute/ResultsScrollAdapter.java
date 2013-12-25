package aakash.glasscompute;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Copyright 2013 Aakash Patel
 * @author AakashPatel
 *
 */
public class ResultsScrollAdapter extends CardScrollAdapter{
	private ResultObject results;
	private Context mContext;
	
	/**
	 * Basic constructor 
	 * @param mContext context of activity that wants this adapter
	 * @param results ResultObject from GlassComputeServe
	 */
	public ResultsScrollAdapter(Context mContext, ResultObject results){
		this.results = results;
		this.mContext = mContext;
	}
	
	@Override
	public int findIdPosition(Object id) {
		// Make sure object is infact the pod
		if (id instanceof HashMap) 
		{	// convert to pod
			HashMap<String, LinkedList<String>> obj = (HashMap<String, LinkedList<String>>)id;
			// Check if the title contained in the pod is contained in results
			// HashMap will contain only 1 <key, value> pair
			// The key is the pod title, the value is a LinkedList containing all result image URLs
			// Look at ResultObject.getPod() for more details
			String titleToSearch = (String)obj.keySet().toArray()[0];
			for(int i = 0; i<getCount();i++){
				String currentTitle = results.getPodTitle(i);
				if(titleToSearch.equals(currentTitle)){
					return i;
				}
			}
        }
		return AdapterView.INVALID_POSITION;
	}

	@Override
	public int findItemPosition(Object item) {
		return findIdPosition(item);
	}

	@Override
	public int getCount() {
		return results.getNumberOfPods();
	}

	@Override
	public Object getItem(int arg0) {
		return results.getPod(arg0);
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		// Get the view when we're provided a pod ID
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_results, parent);
        }
		LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.resultsLinearLayout);
		// Get and set the title of the current page
		TextView titleView = (TextView)linearLayout.findViewById(R.id.titleOfPage);
		String title = results.getPodTitle(arg0);
		titleView.setText(title);
		// Get and set the image results for current page
		for(String imageUrl:results.getPodImageUrls(arg0)){
			ImageView imageView = new ImageView(mContext);
			imageView.setPadding(0, 20, 0, 0);
			UrlImageViewHelper.setUrlDrawable(imageView, imageUrl, R.drawable.stat_notify_sync);
			linearLayout.addView(imageView);
		}
		return setItemOnCard(this, convertView);
	}

}
