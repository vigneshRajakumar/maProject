package com.marketdatahandler.ejb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoricalInfoHelper {
	// get a stock object with a given symbol
	public ArrayList<HistoricalData> getStockHistoricalData(String symbol, int startYear,
			int startMonth, int startDay, int endYear, int endMonth, int endDay) {
		ArrayList<HistoricalData> his_info = null;
		try {
			his_info = sendGetWithSymbolAndDate(symbol, startYear,
					startMonth, startDay, endYear, endMonth, endDay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return his_info;
	}

	/*
	 * send a get request to yahoo finance to get the historical information of a stock
	 * with a given symbol within a date range 
	 * return an array of historical data object
	 */
	private ArrayList<HistoricalData> sendGetWithSymbolAndDate(String symbol, int startYear,
			int startMonth, int startDay, int endYear, int endMonth, int endDay)
			throws Exception {
		
		ArrayList<HistoricalData> result = null;
		String base = "https://query.yahooapis.com/v1/public/yql?q=";
		String query = String.format("select * from csv where url='http://ichart.finance.yahoo.com/table.csv?s=%s&c=%d&a=%d&b=%d&f=%d&d=%d&e=%d&e=.csv' and columns='date,open,high,low,close,volume,adj close'",symbol, startYear, startMonth - 1, startDay, endYear,
				endMonth - 1, endDay);
		query = URLEncoder.encode(query, "UTF-8");
		String postFix = "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
		String url = base+query+postFix;


		URL _url = new URL(String.format("%s%s%s", base, query,postFix));

		System.out.println("[MARKET] query: "+_url.toString());
		HttpURLConnection con = (HttpURLConnection) _url.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == 200) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			JSONArray array = parseResponseJson(response.toString());
			result = parseArray(array);
		}// 200
		System.out.println("[MARKET DATA HANDLER(get historical data by symbol)]: " + symbol);
		return result;
	}
	
	/*
	 * parse the response string from the get resquest
	 * return the json object with all the information of the stock
	 */
	private JSONArray parseResponseJson(String respponse){
		try {
			JSONObject jsonObj = new JSONObject(respponse);
			JSONArray result = jsonObj.getJSONObject("query").getJSONObject("results").getJSONArray("row");
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	//get an array of historical data from a start date to an end date
	private ArrayList<HistoricalData> parseArray(JSONArray jArray) throws JSONException{
		ArrayList<HistoricalData> data = new ArrayList<HistoricalData>();
		for(int i = 1; i<jArray.length(); i++){
			JSONObject obj = jArray.getJSONObject(i);
			data.add(new HistoricalData(obj.getString("date"), obj.getDouble("open"), obj.getDouble("close"), obj.getDouble("high"), obj.getDouble("low"), obj.getLong("volume"), obj.getDouble("adj_close")));
			
		}
		return data;
	}

}
