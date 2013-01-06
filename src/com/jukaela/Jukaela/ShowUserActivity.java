package com.jukaela.Jukaela;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowUserActivity extends Activity {

	private ImageView profileImage;
	private TextView realName;
	private TextView screenName;
	private Button follow;
	private TextView description;
	private TextView stats;
	private Button following;
	private Button followers;
	private Button microposts;

	private JSONObject userDict;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_user);

		profileImage = (ImageView)findViewById(R.id.showUserProfileImage);
		realName = (TextView)findViewById(R.id.showUserRealName);
		screenName = (TextView)findViewById(R.id.showUserScreenName);
		follow = (Button)findViewById(R.id.showUserFollow);
		description = (TextView)findViewById(R.id.showUserDescription);
		following = (Button)findViewById(R.id.showUserFollowingButton);
		followers = (Button)findViewById(R.id.showUserFollowersButton);
		microposts = (Button)findViewById(R.id.showUserMicropostsButton);

		Intent intent = getIntent();
		String tempDict = intent.getStringExtra("userDict");

		try {
			userDict = new JSONObject(tempDict);

			fillInformation();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					JSONObject response = NetworkFactory.followRequest(userDict.getInt("id"));
					
					System.out.println(response.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		
		});
	}

	private void fillInformation() throws Exception {
		ImageLoad downloader = new ImageLoad(profileImage, userDict.getString("email"), this); 
		String stringToDownload = GravatarHelper.getURL(userDict.getString("email"), 65);
		downloader.execute(stringToDownload);

		realName.setText(userDict.getString("name"));

		if (!userDict.isNull("username")) {
			screenName.setText(userDict.getString("username"));
		}
		else {
			screenName.setText("No username specified");
		}

		if (!userDict.isNull("profile")) {
			description.setText(userDict.getString("profile"));
		}
		else {
			description.setText("No profile specified");
		}	
		
		microposts.setText(String.format("%d\nPosts", NetworkFactory.numberOfPosts(userDict.getInt("id"))));
		following.setText(String.format("%d\nFollowing", NetworkFactory.numberOfFollowing(userDict.getInt("id"))));
		followers.setText(String.format("%d\nFollowers", NetworkFactory.numberOfFollowers(userDict.getInt("id"))));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_user, menu);
		return true;
	}

}
