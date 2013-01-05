package com.jukaela.Jukaela;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jukaela.Jukaela.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter implements ListAdapter {

	private final Activity activity;
	private final JSONArray jsonArray;

	public UserAdapter(Activity activity, JSONArray jsonArray) {
		assert activity != null;
		assert jsonArray != null;

		this.jsonArray = jsonArray;
		this.activity = activity;

		System.out.println(String.format("Inside threadedreplyadpater, the array is %s", this.jsonArray.toString()));
	}


	@Override public int getCount() {

		return jsonArray.length();
	}

	@Override public JSONObject getItem(int position) {

		return jsonArray.optJSONObject(position);
	}

	@Override public long getItemId(int position) {
		JSONObject jsonObject = getItem(position);

		return jsonObject.optLong("id");
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		ImageView thumbnail;
		TextView name;
		TextView username;

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.userrow, null);
		}

		JSONObject jsonObject = getItem(position);  

		name = (TextView)convertView.findViewById(R.id.showUserRealName);
		thumbnail = (ImageView)convertView.findViewById(R.id.showUserProfileImage);
		username = (TextView)convertView.findViewById(R.id.showUserScreenName);

		try {
			name.setText(jsonObject.getString("name"));

			String usernameString = String.format("@%s", jsonObject.getString("username"));
			
			if (!jsonObject.isNull("username")) {
				username.setText(usernameString);
			}
			else {
				username.setText("No username specified");
			}
			
			ImageLoad downloader = new ImageLoad(thumbnail, jsonObject.getString("email")); 

			String stringToDownload = GravatarHelper.getURL(jsonObject.getString("email"), 65);

			System.out.println(stringToDownload);

			downloader.execute(stringToDownload);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ImageLoad extends AsyncTask<String, Void, Bitmap> { 
		private String url; 
		private final WeakReference<ImageView> imageViewReference; 

		private String emailAddress;

		public ImageLoad(ImageView imageView, String tempEmail) { 
			emailAddress = tempEmail;

			imageViewReference = new WeakReference<ImageView>(imageView); 
		} 

		protected Bitmap doInBackground(String... params) { 
			url = params[0]; 

			try { 
				return BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream()); 
			} 
			catch (MalformedURLException e) { 
				e.printStackTrace(); 
				return null; 
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
				return null; 
			} 
		} 

		protected void onPostExecute(Bitmap result) { 
			if (imageViewReference != null) { 
				ImageView imageView = imageViewReference.get(); 
				if (imageView != null) { 
					Bitmap bitmap = result;

					try {
						FileOutputStream out = activity.getApplicationContext().openFileOutput(emailAddress, Context.MODE_PRIVATE);

						bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
					} 
					catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					catch (NullPointerException e) {
						e.printStackTrace();
					}
					imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(result, 8)); 
				} 
			} 
		} 


		protected void onPreExecute() { 
			if (imageViewReference != null) { 
				ImageView imageView = imageViewReference.get(); 
				if (imageView != null) { 
					try {
						InputStream inputStream = activity.getApplicationContext().openFileInput(emailAddress);

						Bitmap localBitmap = BitmapFactory.decodeStream(inputStream);

						if (localBitmap != null) {
							imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(localBitmap, 8)); 

							cancel(true);
						}

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			} 
		} 
	}
}
