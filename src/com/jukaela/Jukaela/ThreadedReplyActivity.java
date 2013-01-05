package com.jukaela.Jukaela;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.jukaela.Jukaela.R;

public class ThreadedReplyActivity extends SherlockActivity {

	private ListView mListView;

	public String tempArray;
	private JSONArray feedArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_threaded_reply);

		setTitle("Replies");

		Intent intent = getIntent();
		tempArray = intent.getStringExtra("threadedReplies");
		
		try {
			feedArray = new JSONArray(tempArray);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mListView = (ListView) findViewById(R.id.threaded_reply_list);
		mListView.setAdapter(new ThreadedReplyAdapter(this, feedArray));
	}
}
