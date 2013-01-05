package com.jukaela.Jukaela;

import org.json.JSONArray;
import org.json.JSONException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.jukaela.Jukaela.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;

public class UsersWhoLikedActivity extends SherlockActivity implements OnItemClickListener {

	private ListView listView;

	private JSONArray usersArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_who_liked);
		

		setTitle("Likes");
		
		Intent intent = getIntent();
		String usersArrayString = intent.getStringExtra("usersArray");
				
		try {
			usersArray = new JSONArray(usersArrayString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("users array is " + usersArray.toString());

		listView = (ListView) findViewById(R.id.users_who_liked_list);
		listView.setAdapter(new UserAdapter(this, usersArray));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//
//		inflater.inflate(R.menu.feed_menu, menu);

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
