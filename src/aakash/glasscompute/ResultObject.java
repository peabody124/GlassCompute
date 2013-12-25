package aakash.glasscompute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Holds results from GlassComputeServer
 * Copyright 2013 Aakash Patel
 * @author AakashPatel
 *
 */


public class ResultObject {
	// Data storage
	private ArrayList<HashMap<String, LinkedList<String>>> results;
	// Status codes
	public final static int QUERY_NO_RESULTS = 100;
	public final static int QUERY_UNKNOWN_ERROR = 101;
	public final static int URL_ARGS_ERROR = 102;
	private int status;
	/** 
	 *	Default constructor. Only used in GlassComputeServer
	 *	@params results
	 *	@params status
	 */
	public ResultObject(ArrayList<HashMap<String, LinkedList<String>>> results, int status){
		this.results = results;
		this.status = status;

	}
	/** Get data associated with a pod index
	 * 	HashMap will contain only 1 key, value pair
	 * 	The key is the pod title, the value is a LinkedList containing all result image URLs
	 * @param podIndex index of pod
	 * @return HashMap of results.
	 */
	public HashMap<String, LinkedList<String>> getPod(int podIndex){
		return results.get(podIndex);
	}
	
	
	/**
	 * Get status of query result
	 * @return integer result code.
	 */
	public int getStatus(){
		return status;
	}
	
	
	/** 
	 * Get number of image results
	 * @return integer num images
	 */
	public int getResultCount(){
		int resultCount = 0;
		for(int i = 0; i<getNumberOfPods();i++){
			for(String url:getPodImageUrls(i)){
				resultCount++;
			}
		}
		return resultCount;
	}
	
	
	public String toString(){
		String temp = "Status: " + status + "\n" +
				"podCount: " + getNumberOfPods() + "\n" +
				"resultCount: " + getResultCount() + "\n" +
				"results: ";
		for(int i = 0; i<results.size(); i++){
			temp = temp+ i +": " + results.get(i).toString() + " \n";
		}
		return temp;
		
	}
	
	/**
	 * Get number of pods (pages) in result
	 * @return int number of pods
	 */
	public int getNumberOfPods(){
		return results.size();
	}
	
	/**
	 * Get title associated with a pod index
	 * @param pod index
	 * @return String title
	 */
	public String getPodTitle(int pod){
		return (String)results.get(pod).keySet().toArray()[0];
	}
	/**
	 * Get URLs associated with result images of a pod index (page)
	 * @param pod index
	 * @return LinkedList<String> of urls
	 */
	public LinkedList<String> getPodImageUrls(int pod){
		return results.get(pod).get(getPodTitle(pod));
	}
	

}
