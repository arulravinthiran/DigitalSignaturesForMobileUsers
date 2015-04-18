package com.digsigmobile.util;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpConnection 
{
	private final String USER_AGENT = "Mozilla/5.0";
	private int responseCode = -1;

	public int getResponseCode() {
		return responseCode;
	}
	
	// HTTP GET request
	public void sendGet(String url) {
		HttpURLConnection con = null;

		try {
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			responseCode = con.getResponseCode();
			/*
			 * Uncomment to print response
			 *
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(con != null) {
				con.disconnect();
			}	
		}
	}

	// HTTP POST request
	public void sendPost(String url, String urlParameters) {
		HttpURLConnection con = null;

		try {
			URL obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();

			/*
			 * Uncomment to print response
			 *
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			//print result
			System.out.println(response.toString());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(con != null) {
				con.disconnect();
			}	
		}
	}

}

