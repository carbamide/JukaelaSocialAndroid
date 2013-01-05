package com.jukaela.Jukaela;

import org.json.JSONArray;

import com.actionbarsherlock.app.SherlockFragment;
import com.jukaela.Jukaela.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MentionsFragment extends SherlockFragment implements OnItemClickListener {

	private JSONArray mentionsArray;
	private ListView mentionsListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
		return inflater.inflate(R.layout.activity_mentions, group, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		getMentionsFeed();
		

	}

	private void getMentionsFeed() {
		try {
			mentionsArray = NetworkFactory.getMentionsFromTo(0, 20);
			
			System.out.println(mentionsArray);
			
			mentionsListView = (ListView) getActivity().findViewById(R.id.mentions_list);
			mentionsListView.setAdapter(new MentionsAdapter(getActivity(), mentionsArray));
			mentionsListView.setOnItemClickListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
	}
}
