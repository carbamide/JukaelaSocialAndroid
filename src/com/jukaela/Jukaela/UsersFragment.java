package com.jukaela.Jukaela;

import org.json.JSONArray;

import com.actionbarsherlock.app.SherlockFragment;
import com.jukaela.Jukaela.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class UsersFragment extends SherlockFragment implements OnItemClickListener {

	private JSONArray usersArray;
	private ListView usersListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
		return inflater.inflate(R.layout.activity_users, group, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		getUsers();
		

	}

	private void getUsers() {
		try {
			usersArray = NetworkFactory.getUsers();
			
			usersListView = (ListView) getActivity().findViewById(R.id.users_list);
			usersListView.setAdapter(new UserAdapter(getActivity(), usersArray));
			usersListView.setOnItemClickListener(this);
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
