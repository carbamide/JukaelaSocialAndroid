package com.jukaela.Jukaela;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

	public String tempArray;

	private FeedFragment feed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		tempArray = intent.getStringExtra("tempArray");

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab feedTab = bar.newTab();
		ActionBar.Tab mentionsTab = bar.newTab();
		ActionBar.Tab usersTab = bar.newTab();
		ActionBar.Tab dmTab = bar.newTab();

		feedTab.setText("Feed");
		feedTab.setIcon(R.drawable.feed);

		mentionsTab.setText("Mentions");
		mentionsTab.setIcon(R.drawable.mention);

		usersTab.setText("Users");
		usersTab.setIcon(R.drawable.users);

		dmTab.setText("DM");
		dmTab.setIcon(R.drawable.messages);

		feedTab.setTabListener(new JukaelaTabListener());
		mentionsTab.setTabListener(new JukaelaTabListener());
		usersTab.setTabListener(new JukaelaTabListener());
		dmTab.setTabListener(new JukaelaTabListener());

		bar.addTab(feedTab);
		bar.addTab(mentionsTab);
		bar.addTab(usersTab);
		bar.addTab(dmTab);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();

		inflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.post) {
			Intent i = new Intent(this, PostActivity.class);

			startActivity(i);

			return true;
		} 
		else if (item.getItemId() == R.id.refresh) {
			try {
				System.out.println("Refreshing the feed");

				feed.refreshListView();
				
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (item.getItemId() == R.id.settings) {
			try {
				Intent i = new Intent(this, SettingsActivity.class);

				startActivity(i);
				
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private class JukaelaTabListener implements TabListener {
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			setTitle(tab.getText());

			if (tab.getPosition() == 0) {
				try {
					feed = new FeedFragment();
					feed.setFeedArray(new JSONArray(tempArray));

					ft.replace(android.R.id.content, feed);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (tab.getPosition() == 1) {
				MentionsFragment mentionsFrag = new MentionsFragment();
				ft.replace(android.R.id.content, mentionsFrag);
			}
			else if (tab.getPosition() == 2) {
				UsersFragment usersFragment = new UsersFragment();
				ft.replace(android.R.id.content, usersFragment);
			}
			else {
				DirectMessagesFragment dmFragment = new DirectMessagesFragment();
				ft.replace(android.R.id.content, dmFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			if (tab.getPosition() == 0) {
				try {
					feed.refreshListView();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
