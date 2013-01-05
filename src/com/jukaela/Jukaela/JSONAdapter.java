package com.jukaela.Jukaela;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jukaela.Jukaela.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class JSONAdapter extends BaseAdapter implements ListAdapter {

	private final Activity activity;
	private final JSONArray jsonArray;

	private static final int TYPE_REGULAR = 0;
	private static final int TYPE_IMAGE = 1;

	public JSONAdapter(Activity activity, JSONArray jsonArray) {
		assert activity != null;
		assert jsonArray != null;

		this.jsonArray = jsonArray;
		this.activity = activity;
	}

	@Override
	public int getItemViewType(int position) {
		JSONObject jsonObject = getItem(position);  

		if (!jsonObject.isNull("image_url")) {
			return TYPE_IMAGE;
		}
		else {
			return TYPE_REGULAR;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
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

	@SuppressLint("SimpleDateFormat")
	@Override public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView thumbnail;
		TextView content;
		TextView name;
		TextView username;
		TextView repostString;
		TextView date;

		JSONObject jsonObject = getItem(position);  

		int type = getItemViewType(position);

		switch (type) {
		case TYPE_IMAGE:
			convertView = activity.getLayoutInflater().inflate(R.layout.row_with_image, null);

			break;
		case TYPE_REGULAR:
			convertView = activity.getLayoutInflater().inflate(R.layout.row, null);

			break;
		}
		
		content = (TextView)convertView.findViewById(R.id.micropost_content);
		name = (TextView)convertView.findViewById(R.id.name);
		thumbnail = (ImageView)convertView.findViewById(R.id.avatar);
		username = (TextView)convertView.findViewById(R.id.username);
		repostString = (TextView)convertView.findViewById(R.id.repostString);
		date = (TextView)convertView.findViewById(R.id.date);

		try {
			if (type == TYPE_IMAGE) {
				ImageView externalImage = (ImageView)convertView.findViewById(R.id.externalImage);

				externalImage.setClickable(true); 

				externalImage.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View view) {
		        		JSONObject jsonObject = getItem(position);  

		                System.out.println(String.format("ImageView clicked for the row %d", position));
		                
						try {
							Intent i = new Intent(activity.getApplicationContext(), ImageViewerActivity.class);

							i.putExtra("imageString", jsonObject.getString("image_url"));
							
							activity.startActivity(i);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        });
				String imageURLString = jsonObject.getString("image_url");

				ImageLoad downloader = new ImageLoad(externalImage, imageURLString.substring(imageURLString.lastIndexOf('/') + 1), activity); 

				String externalImageToDownload = jsonObject.getString("image_url");

				System.out.println(externalImageToDownload);

				downloader.execute(externalImageToDownload);
			}

			content.setText(jsonObject.getString("content"));
			name.setText(jsonObject.getString("name"));

			String dateString = jsonObject.getString("created_at");

			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateFormatter.setTimeZone(TimeZone.getTimeZone("CST"));

			Date dateDate = dateFormatter.parse(dateString);

			Calendar cal = Calendar.getInstance();

			date.setText(DateUtils.getRelativeTimeSpanString(dateDate.getTime(), cal.getTimeInMillis(),
					0L, DateUtils.FORMAT_ABBREV_ALL));

			String usernameString = String.format("@%s", jsonObject.getString("username"));
			username.setText(usernameString);

			if (!jsonObject.isNull("repost_username")) {
				String repostUsernameString = String.format("Reposted by @%s", jsonObject.getString("repost_username"));

				repostString.setText(repostUsernameString);
			}
			else {
				repostString.setVisibility(View.GONE);				
			}


			ImageLoad downloader = new ImageLoad(thumbnail, jsonObject.getString("email"), activity); 

			String stringToDownload = GravatarHelper.getURL(jsonObject.getString("email"), 65);

			System.out.println(stringToDownload);

			downloader.execute(stringToDownload);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertView;
	}


}