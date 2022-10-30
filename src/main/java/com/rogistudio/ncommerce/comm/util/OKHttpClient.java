package com.rogistudio.ncommerce.comm.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class OKHttpClient {

	private final MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
	
	private String url;
	private String body;
	private String responseBody;
	private Request request;
	private Response response;
	
	public boolean isConnect = true;
	public String error_msg = "";
	
	public OKHttpClient(Request request){
		this.request = request;
    }
	
	public int doUsingHttp() throws Exception {

		try{

			OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(30000, TimeUnit.SECONDS)
				.readTimeout(30000, TimeUnit.SECONDS)
				.writeTimeout(30000, TimeUnit.SECONDS)
				.build();
			
			this.response = client.newCall(this.request).execute();
			if (this.response.code() == 200) {
				this.responseBody = response.body().string();
			}
			else {
				this.isConnect = false;
				this.error_msg = response.body().string();
			}
			
		} catch (Exception e) {
			this.isConnect = false;
			this.error_msg = "[ERROR] OKHttpClient.doUsingHttp() \n"+ e.toString();
		}
		
		return this.response.code();
	}
	
	 public boolean isConnect(){
		 return this.isConnect;
	 }
	 
	 public String getErrorMsg(){
		 return this.error_msg;
	 }

	 public String getResponseBody(){return this.responseBody;}

	 public Response getResponse(){return this.response;}

	 
}