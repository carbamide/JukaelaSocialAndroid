package com.jukaela.Jukaela;

import com.actionbarsherlock.app.SherlockFragment;
import com.jukaela.Jukaela.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
		return inflater.inflate(R.layout.activity_settings, group, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

}
