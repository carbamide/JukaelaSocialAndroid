package com.jukaela.Jukaela;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageView;

public class ImageLoad extends AsyncTask<String, Void, Bitmap> { 
	private String url; 
	private final WeakReference<ImageView> imageViewReference; 

	private String emailAddress;

	private final Activity activity;

	public ImageLoad(ImageView imageView, String tempEmail, Activity tempActivity) { 
		emailAddress = tempEmail;

		imageViewReference = new WeakReference<ImageView>(imageView);

		activity = tempActivity;
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
					System.out.println("Trying to save bitmap");
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();

					Date date = new Date(System.currentTimeMillis());

					editor.putLong("dateOfGravatarSave", date.getTime());
					editor.commit();

					FileOutputStream out = activity.getApplicationContext().openFileOutput(emailAddress, Context.MODE_PRIVATE);

					try {
						if (out != null) {
							bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				} 
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(result, 8)); 

			} 
		} 
	} 


	@SuppressLint("CommitPrefEdits")
	@SuppressWarnings("unused")
	protected void onPreExecute() { 
		if (imageViewReference != null) { 
			ImageView imageView = imageViewReference.get(); 
			if (imageView != null) { 
				try {
					InputStream inputStream = activity.getApplicationContext().openFileInput(emailAddress);

					Bitmap localBitmap = BitmapFactory.decodeStream(inputStream);

					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
					SharedPreferences.Editor editor = sharedPrefs.edit();

					Date currentTime = new Date(System.currentTimeMillis());

					long currentMillis = currentTime.getTime();					
					long savedTime = sharedPrefs.getLong("dateOfGravatarSave", 0);

					boolean retrieveNormally = false;

					if ((currentMillis - savedTime) > 84600000) {
						retrieveNormally = true;
					}

					if (retrieveNormally = true) {
						return;
					}
					else if (localBitmap != null) {
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
