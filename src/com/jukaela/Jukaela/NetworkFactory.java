package com.jukaela.Jukaela;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Base64;


@SuppressLint("DefaultLocale")
public class NetworkFactory {
	private static final String IMGUR_API_KEY = "ee66d23c163a5da80cf7a861dc2a3185";
	private static boolean PRODUCTION_SERVER = false;

	private static NetworkFactory mNetworkFactory;
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	private static String userID;
	private static String APID;

	public enum SERVER_TYPE {
		PRODUCTION, DEVELOPMENT
	}

	public NetworkFactory() {

	}

	public static NetworkFactory getNetworkFactoryInstance() {
		if (mNetworkFactory == null) {
			mNetworkFactory = new NetworkFactory();
		}
		return mNetworkFactory;
	}

	public static void setServer(SERVER_TYPE server) {
		if (server == SERVER_TYPE.PRODUCTION) {
			PRODUCTION_SERVER = true;
		}
		else {
			PRODUCTION_SERVER = false;
		}
	}

	public static void setUserID(String tempUserID) {
		userID = tempUserID;
	}

	public static String getUserID() {
		return userID;
	}

	public static String makeRequest(String path, JSONObject holder) throws Exception {		
		try {
			Class<?> strictModeClass=Class.forName("android.os.StrictMode");
			Class<?> strictModeThreadPolicyClass=Class.forName("android.os.StrictMode$ThreadPolicy");

			Object laxPolicy = strictModeThreadPolicyClass.getField("LAX").get(null);

			Method method_setThreadPolicy = strictModeClass.getMethod("setThreadPolicy", strictModeThreadPolicyClass );
			method_setThreadPolicy.invoke(null,laxPolicy);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpPost httpost = new HttpPost(path);

		StringEntity stringEntityToSend = new StringEntity(holder.toString());

		httpost.setEntity(stringEntityToSend);
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");

		HttpResponse response = httpclient.execute(httpost, httpContext);

		String responseString = EntityUtils.toString(response.getEntity());

		return responseString;
	}

	public static String deleteRequest(String path) throws Exception {
		try {
			Class<?> strictModeClass=Class.forName("android.os.StrictMode");
			Class<?> strictModeThreadPolicyClass=Class.forName("android.os.StrictMode$ThreadPolicy");

			Object laxPolicy = strictModeThreadPolicyClass.getField("LAX").get(null);

			Method method_setThreadPolicy = strictModeClass.getMethod("setThreadPolicy", strictModeThreadPolicyClass );
			method_setThreadPolicy.invoke(null,laxPolicy);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpDelete deleteRequest = new HttpDelete(path);

		deleteRequest.setHeader("Accept", "application/json");
		deleteRequest.setHeader("Content-type", "application/json");

		HttpResponse response = httpclient.execute(deleteRequest, httpContext);

		String responseString = EntityUtils.toString(response.getEntity());

		return responseString;	
	}

	public static String getRequest(String path) throws Exception {
		try {
			Class<?> strictModeClass=Class.forName("android.os.StrictMode");
			Class<?> strictModeThreadPolicyClass=Class.forName("android.os.StrictMode$ThreadPolicy");

			Object laxPolicy = strictModeThreadPolicyClass.getField("LAX").get(null);

			Method method_setThreadPolicy = strictModeClass.getMethod("setThreadPolicy", strictModeThreadPolicyClass );
			method_setThreadPolicy.invoke(null,laxPolicy);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpGet deleteRequest = new HttpGet(path);

		deleteRequest.setHeader("Accept", "application/json");
		deleteRequest.setHeader("Content-type", "application/json");

		HttpResponse response = httpclient.execute(deleteRequest, httpContext);

		String responseString = EntityUtils.toString(response.getEntity());

		return responseString;	
	}

	public static JSONObject uploadImage(Bitmap bitmap) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  

		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

		String IMGUR_POST_URI = "http://api.imgur.com/2/upload.json";

		URL url = new URL(IMGUR_POST_URI);

		String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imageEncoded, "UTF-8");
		data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(IMGUR_API_KEY, "UTF-8");

		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		InputStream is;

		if (((HttpURLConnection) conn).getResponseCode() == 400) {
			is = ((HttpURLConnection) conn).getErrorStream();
		}
		else {
			is = conn.getInputStream();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String json = reader.readLine();

		JSONTokener tokener = new JSONTokener(json);
		JSONObject responseObject = new JSONObject(tokener);

		wr.close();

		return responseObject;
	}

	public static JSONObject post(String stringToPost, String imageString, int inReplyTo) throws Exception {

		JSONObject postObject = new JSONObject();

		postObject.put("content", stringToPost);
		postObject.put("user_id", NetworkFactory.getUserID());

		if (imageString != null) {
			postObject.put("image_url", imageString);
		}

		if (inReplyTo != 0) {
			postObject.put("in_reply_to", inReplyTo);
		}

		System.out.println(postObject.toString());

		String feedResponse = NetworkFactory.makeRequest(NetworkFactory.createURLString("/microposts.json"), postObject);

		return new JSONObject(feedResponse);
	}

	public static JSONArray getFeed(int totalItemCount) throws Exception {
		JSONObject feedObject = new JSONObject();

		feedObject.put("first", totalItemCount);

		feedObject.put("last", totalItemCount + 20);

		String feedResponse = NetworkFactory.makeRequest(NetworkFactory.createURLString("/home.json"), feedObject);

		return new JSONArray(feedResponse);
	}

	public static JSONArray getFeedFromTo(int from, int to) throws Exception {
		JSONObject feedObject = new JSONObject();

		feedObject.put("first", 0);
		feedObject.put("last", 20);

		String feedResponse = NetworkFactory.makeRequest(NetworkFactory.createURLString("/home.json"), feedObject);

		return new JSONArray(feedResponse);
	}

	public static JSONArray getMentionsFromTo(int from, int to) throws Exception {
		JSONObject mentionsObject = new JSONObject();

		mentionsObject.put("first", 0);
		mentionsObject.put("last", 20);

		String mentionsResponse = NetworkFactory.makeRequest(NetworkFactory.createURLString("/pages/mentions.json"), mentionsObject);

		System.out.println(mentionsResponse.toString());

		return new JSONArray(mentionsResponse);
	}

	public static JSONArray getUsers() throws Exception {		
		String usersResponse = NetworkFactory.getRequest(NetworkFactory.createURLString("/users.json"));

		return new JSONArray(usersResponse);
	}

	public static JSONArray getDirectMessages() throws Exception {
		String dmResponse = NetworkFactory.getRequest(NetworkFactory.createURLString("/direct_messages.json"));

		return new JSONArray(dmResponse);
	}

	public static String deletePost(int micropostID) throws Exception {
		String url = String.format("%s/microposts/%d.json", server(), micropostID);

		return NetworkFactory.deleteRequest(url);
	}

	public static JSONArray showThreadForMicropost(int micropostID) throws Exception {
		String feedResponse = NetworkFactory.getRequest(String.format("%s/microposts/%d/thread_for_micropost.json", server(), micropostID));

		return new JSONArray(feedResponse);
	}

	public static JSONObject likeMicropost(int micropostID) throws Exception {
		String feedResponse = NetworkFactory.getRequest(String.format("%s/microposts/%d/like.json", server(), micropostID));

		return new JSONObject(feedResponse);
	}

	public static JSONObject login(String username, String password) throws Exception {
		JSONObject loginInformation = new JSONObject();

		loginInformation.put("email", username);
		loginInformation.put("password", password);

		if (NetworkFactory.getAPID() != null) {
			String APIDString = NetworkFactory.getAPID();

			loginInformation.put("apns", APIDString);
		}

		JSONObject parameters = new JSONObject();

		parameters.put("session", loginInformation);

		System.out.println(NetworkFactory.createURLString("/sessions.json"));

		String response = NetworkFactory.makeRequest(NetworkFactory.createURLString("/sessions.json"), parameters);

		JSONObject loginObject = new JSONObject(response);

		NetworkFactory.setUserID(loginObject.getString("id"));

		return loginObject;
	}

	public static JSONObject userInformation(int userID) throws Exception {		
		String userResponse = NetworkFactory.getRequest(String.format("%s/users/%d.json", server(), userID));

		System.out.println(userResponse);

		return new JSONObject(userResponse);
	}

	public static JSONArray currentlyFollowing() throws Exception {
		String response = NetworkFactory.getRequest(String.format("%s/users/%s/following.json", server(), NetworkFactory.getUserID()));
		
		JSONObject tempObject = new JSONObject(response);
		
		return tempObject.getJSONArray("user");
	}
	
	public static JSONArray relationships() throws Exception {
		String response = NetworkFactory.getRequest(String.format("%s/users/%s/following.json", server(), NetworkFactory.getUserID()));
		
		JSONObject tempObject = new JSONObject(response);
		
		return tempObject.getJSONArray("relationships");
	}
	
	public static int numberOfFollowers(int userID) throws Exception {
		String response = NetworkFactory.getRequest(String.format("%s/users/%d/number_of_followers", server(), userID));

		return new JSONObject(response).getInt("count");
	}

	public static int numberOfFollowing(int userID) throws Exception {
		String response = NetworkFactory.getRequest(String.format("%s/users/%d/number_of_following", server(), userID));

		return new JSONObject(response).getInt("count");
	}

	public static int numberOfPosts(int userID) throws Exception {
		String response = NetworkFactory.getRequest(String.format("%s/users/%d/number_of_posts", server(), userID));

		return new JSONObject(response).getInt("count");
	}

	public static JSONObject followRequest(int userID) throws Exception {
		JSONObject followObject = new JSONObject();
		followObject.put("commit", "Follow");

		JSONObject relationshipObject = new JSONObject();
		relationshipObject.put("followed_id", userID);

		followObject.put("relationship", relationshipObject);

		String response = NetworkFactory.makeRequest(createURLString("/relationships.json"), followObject);

		return new JSONObject(response);
	}

	public static void unfollowRequest(int unfollowID) throws Exception {
		System.out.println(String.format("%s/relationships/%d.json", server(), unfollowID));
		
		NetworkFactory.deleteRequest(String.format("%s/relationships/%d.json", server(), unfollowID));
	}
	
	public static String getAPID() {
		return APID;
	}

	public static void setAPID(String aPID) {
		APID = aPID;
	}

	public static String server() {
		if (PRODUCTION_SERVER == true) {
			return "http://cold-planet-7717.herokuapp.com";
		} 
		else {
			return "http://10.0.2.2:3000";
		}
	}

	public static String createURLString(String endPoint) {
		return String.format("%s%s", server(), endPoint);
	}

}
