package com.jukaela.Jukaela;

import org.json.JSONArray;
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
	private JSONArray currentlyFollowing;
	private JSONArray relationships;

	private boolean followingStatus = false;
	private int unfollowID = 0;

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

			currentlyFollowing = NetworkFactory.currentlyFollowing();
			relationships = NetworkFactory.relationships();

			System.out.println(relationships.toString());
			
			fillInformation();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (followingStatus == true) {
					try {
						NetworkFactory.unfollowRequest(unfollowID);

						follow.setText("Follow");

						unfollowID = 0;
						followingStatus = false;

						currentlyFollowing = NetworkFactory.currentlyFollowing();
						relationships = NetworkFactory.relationships();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					try {
						JSONObject response = NetworkFactory.followRequest(userDict.getInt("id"));

						System.out.println(response.toString());

						follow.setText("Unfollow");

						currentlyFollowing = NetworkFactory.currentlyFollowing();
						relationships = NetworkFactory.relationships();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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

		checkFollowingStatus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_user, menu);
		return true;
	}
	public void checkFollowingStatus() throws JSONException {
		for (int i = 0; i < currentlyFollowing.length(); i++) {
			System.out.println("inside first loop");
			JSONObject tempObject = currentlyFollowing.getJSONObject(i);

			if (tempObject.getInt("id") == userDict.getInt("id")) {
				follow.setText("Unfollow");

				followingStatus = true;
			}
		}

		for (int i = 0; i < relationships.length(); i++) {
			System.out.println("inside second loop");

			JSONObject tempObject = relationships.getJSONObject(i);

			if (!tempObject.isNull("followed_id")) {
				if (tempObject.getInt("followed_id") == userDict.getInt("id")) {				
					unfollowID = tempObject.getInt("id");
				}
			}
		}
	}
}
