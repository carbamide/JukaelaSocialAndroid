package com.jukaela.Jukaela;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jukaela.Jukaela.R;

public class DirectMessageComposerActivity extends SherlockActivity {

	protected static final int  SELECT_IMAGE = 0;
	protected static final int CAPTURE_IMAGE = 1;
	private EditText contentText;
	private TextView characterLabel;

	public String username;

	private Uri imageUri;

	private String imageString;

	public void setUsername (String tempString) {
		username = tempString;
	}

	public String getUsername () {
		return username;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		setTitle("Direct Message");

		contentText = (EditText) findViewById(R.id.contentText);
		characterLabel = (TextView) findViewById(R.id.post_characters);

		Intent intent = getIntent();
		username = intent.getStringExtra("username");


		final TextWatcher textEditorWatcher = new TextWatcher() {
			public void afterTextChanged(Editable s){
				int remainingCharacters = 256 - contentText.getText().length();

				if (remainingCharacters < 0) {
					contentText.setText(contentText.getText().subSequence(0, 256));
					contentText.setSelection(contentText.getText().length());
					remainingCharacters = 256 - contentText.getText().length();
				}

				if (remainingCharacters <= 0) {
					characterLabel.setTextColor(Color.RED);
				} 
				else {
					characterLabel.setTextColor(Color.BLACK);
				}

				characterLabel.setText(Integer.toString(remainingCharacters));
			}

			public void  beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void  onTextChanged (CharSequence s, int start, int before,int count) {} 
		};

		contentText.addTextChangedListener(textEditorWatcher);
		contentText.setSelection(contentText.getText().length());

		contentText.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();

		inflater.inflate(R.menu.post_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.post) {
			try {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

				JSONObject postResponseObject = NetworkFactory.directMessage(contentText.getText().toString(), username);

			}
			catch (JSONException e) {
				e.printStackTrace();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}

			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}