package com.marketdatahandler.ejb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.marketdatahandle.jpa.Stock;

public class StockInfoHelper {

	// get a stock object with a given symbol
	public Stock getStock(String symbol, Stock sto) {
		try {
			System.out.println("lol!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
			JSONObject stock_info = sendGetWithSymbol(symbol);
			System.out.println("????!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
			sto = createStock(stock_info, sto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sto;
	}

	// create a stock object with all the information from yahoo
	private Stock createStock(JSONObject obj, Stock sto) {
		System.out.println(obj.toString());
		// if the stock is not in the database
		if (sto == null)
			sto = new Stock();// should query te database with the stock symbol
		try {
			sto.setSymbol(obj.getString("symbol"));
			sto.setAsk(Double.valueOf(obj.getDouble("Ask")));
			sto.setBid(obj.getDouble("Bid"));
			sto.setAverageDailyVolume(obj.getDouble("AverageDailyVolume"));
			sto.setBookValue(obj.getDouble("BookValue"));
			sto.setChangePercent(Double.valueOf(obj.getString("PercentChange").replace("%","")) / 100);
			sto.setChange(obj.getDouble("Change"));
			sto.setOpen(obj.getDouble("Open"));
			sto.setDailyHigh(obj.getDouble("DaysHigh"));
			sto.setDailyLow(obj.getDouble("DaysLow"));
			sto.setYearlyHigh(obj.getDouble("YearHigh"));
			sto.setYearlyLow(obj.getDouble("YearLow"));
			sto.setCurrency(obj.getString("Currency"));
			sto.setLastTradePrice(obj.getDouble("LastTradePriceOnly"));//price
			// might have error
//			sto.setMarketCapitalization(obj.getLong("MarketCapitalization"));
			sto.setStockExchange(obj.getString("StockExchange"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sto;
	}

	// encode a given url
	private String encodeURL(String url) {

		try {
			return URLEncoder.encode(url, "UTF-8").replace("+", "%20")
					.replace("%3D", "=").replace("%22", "\"");
//			return URLEncoder.encode(url, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * send a get request to yahoo finance to get all the information of a stock
	 * with a given symbol return an json object containing all the information
	 * of the stock
	 */
	private JSONObject sendGetWithSymbol(String symbol) throws Exception {
		String BASE_URL = "http://query.yahooapis.com/v1/public/yql?";
		JSONObject result = null;
		String query = String.format(
				"select * from yahoo.finance.quotes where symbol=\"%s\"",
				symbol);
		// query = URLEncoder.encode(query, "UTF-8");
		if ((query = encodeURL(query)) != null) {

			String postFix = "&env=store://datatables.org/alltableswithkeys&format=json";
			URL _url = new URL(String.format("%sq=%s%s", BASE_URL, query,
					postFix));
			System.out.println(_url.toString());

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
				System.out.println(response.toString());
				result = parseResponseJson(response.toString());
			}// 200

		}
		return result;
	}

	/*
	 * parse the response string from the get resquest return the json object
	 * with all the information of the stock
	 */
	private JSONObject parseResponseJson(String respponse) {
		try {
			JSONObject jsonObj = new JSONObject(respponse);
			JSONObject result = jsonObj.getJSONObject("query").getJSONObject(
					"results").getJSONObject("quote");
			System.out.println(result.toString());
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
