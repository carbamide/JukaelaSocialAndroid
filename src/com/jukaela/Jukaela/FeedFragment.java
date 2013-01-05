package com.jukaela.Jukaela;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.jukaela.Jukaela.R;

@SuppressLint("DefaultLocale")
public class FeedFragment extends SherlockFragment implements OnItemClickListener {

	private ListView mListView;

	public String tempArray;
	private JSONArray feedArray;

	String pluralization = null;

	public void setFeedArray(JSONArray tempJSONArray) {
		feedArray = tempJSONArray;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
		return inflater.inflate(R.layout.activity_feed, group, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mListView = (ListView) getActivity().findViewById(R.id.feed_list);
		mListView.setAdapter(new JSONAdapter(getActivity(), feedArray));
		mListView.setOnItemClickListener(this);

		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if((lastInScreen == totalItemCount)) { 
					Toast.makeText(getActivity(), "Getting Posts", Toast.LENGTH_SHORT).show();

					try {
						JSONArray result = NetworkFactory.getFeed(totalItemCount);

						for (int i = 0; i < result.length(); i++) {
							feedArray.put(result.get(i));
						}

						((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
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

	public void refreshListView() throws Exception {
		feedArray = NetworkFactory.getFeedFromTo(0, 20);
		mListView.setAdapter(new JSONAdapter(getActivity(), feedArray));
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, final int position, long id) {
		List<CharSequence> items = new LinkedList<CharSequence>();

		items.add("Reply");
		
		try {
			if (!(feedArray.getJSONObject(position).getString("user_id") == NetworkFactory.getUserID())) {
				boolean addTheLikeButton = true;

				if (feedArray.getJSONObject(position).getJSONArray("users_who_liked") != null) {
					for (int i = 0; i < feedArray.getJSONObject(position).getJSONArray("users_who_liked").length(); i++) {
						JSONObject object = feedArray.getJSONObject(position).getJSONArray("users_who_liked").getJSONObject(i);

						if(object.getString("user_id") == NetworkFactory.getUserID()) {

							System.out.println(object.getString("user_id"));
							System.out.println("addTheLikeButton is false");

							addTheLikeButton = false;
						}
					}
				}

				if (addTheLikeButton == true) {
					items.add("Like");
				}
			}

			if (feedArray.getJSONObject(position).getJSONArray("users_who_liked") != null) {
				if (feedArray.getJSONObject(position).getJSONArray("users_who_liked").length() == 1) {
					pluralization = "Like";
				}
				else if (feedArray.getJSONObject(position).getJSONArray("users_who_liked").length() > 1) {
					pluralization = "Likes";
				}

				if (feedArray.getJSONObject(position).getJSONArray("users_who_liked").length() > 0) {
					items.add(String.format("%d %s", feedArray.getJSONObject(position).getJSONArray("users_who_liked").length(), pluralization));
				}

			}

			if (feedArray.getJSONObject(position).getString("user_id") == NetworkFactory.getUserID()) {
				items.add("Delete");
			}

			if (!feedArray.getJSONObject(position).isNull("in_reply_to")) {
				items.add("Show Thread");
			}

			if (!feedArray.getJSONObject(position).isNull("image_url")) {
				items.add("View Image");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final CharSequence[] charSequenceItems = items.toArray(new CharSequence[items.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Pick an  action");
		builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				System.out.println(String.format("%d is the item", item));
				if (charSequenceItems[item].equals("Delete")) {
					try {						
						String response = NetworkFactory.deletePost(feedArray.getJSONObject(position).getInt("id"));

						if (response != null) {
							System.out.println(response);

							refreshListView();

							Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (charSequenceItems[item].equals("Reply")) {
					Intent i = new Intent(getActivity(), PostActivity.class);

					try {
						JSONObject replyTempObject = (JSONObject) feedArray.getJSONObject(position);

						String username = replyTempObject.getString("username");

						i.putExtra("replyString", String.format("@%s", username));
						i.putExtra("inReplyTo", replyTempObject.getInt("id"));

						startActivity(i);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (charSequenceItems[item].equals("Show Thread")) {
					try {
						JSONArray feedResponseObject = NetworkFactory.showThreadForMicropost(feedArray.getJSONObject(position).getInt("id"));
						
						Intent i = new Intent(getActivity(), ThreadedReplyActivity.class);
						i.putExtra("threadedReplies", feedResponseObject.toString());
						startActivity(i);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (charSequenceItems[item].equals("View Image")) {
					try {
						JSONObject tempObject = (JSONObject) feedArray.getJSONObject(position);

						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tempObject.getString("image_url")));
						startActivity(browserIntent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (charSequenceItems[item].equals("Like")) {					
					try {
						JSONObject likeResponse = NetworkFactory.likeMicropost(feedArray.getJSONObject(position).getInt("id"));

						System.out.println(likeResponse.toString());

						Toast.makeText(getActivity(), "Liked Post", Toast.LENGTH_SHORT).show();
						
						feedArray = NetworkFactory.getFeedFromTo(0, 20);
						mListView.setAdapter(new JSONAdapter(getActivity(), feedArray));
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				else
					try {
						if (charSequenceItems[item].equals(String.format("%d %s", feedArray.getJSONObject(position).getJSONArray("users_who_liked").length(), pluralization))) {							
							try {
								Intent i = new Intent(getActivity(), UsersWhoLikedActivity.class);

								i.putExtra("usersArray", feedArray.getJSONObject(position).getJSONArray("users_who_liked").toString());
								
								startActivity(i);	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});


		
		AlertDialog alert = builder.create();

		alert.show();
	}
}
