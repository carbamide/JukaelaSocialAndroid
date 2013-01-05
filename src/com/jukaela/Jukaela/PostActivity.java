package com.jukaela.Jukaela;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import com.jukaela.Jukaela.R.id;
import com.jukaela.Jukaela.R.layout;
import com.jukaela.Jukaela.R.menu;

public class PostActivity extends SherlockActivity implements OnClickListener {

	protected static final int  SELECT_IMAGE = 0;
	protected static final int CAPTURE_IMAGE = 1;
	private EditText contentText;
	private TextView characterLabel;
	private ImageButton imageButton;

	public String replyString;
	public int inReplyTo;

	private Uri imageUri;

	private String imageString;

	public void setReplyString (String tempString) {
		replyString = tempString;
	}

	public String getReplyString () {
		return replyString;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		setTitle("Post");

		contentText = (EditText) findViewById(R.id.contentText);
		characterLabel = (TextView) findViewById(R.id.post_characters);

		setImageButton((ImageButton) findViewById(R.id.imageButton));

		imageButton.setOnClickListener(this);

		Intent intent = getIntent();
		replyString = intent.getStringExtra("replyString");
		inReplyTo = intent.getIntExtra("inReplyTo", 0);

		if (replyString != null) {
			contentText.setText(replyString);

			setTitle("Reply");
		}


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

	@SuppressWarnings("unused")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.post) {
			try {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

				JSONObject postResponseObject = NetworkFactory.post(contentText.getText().toString(), imageString, inReplyTo);
						
				if (postResponseObject == null) {
					final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getApplicationContext());

					alertDialogBuilder.setTitle("Error");

					alertDialogBuilder.setMessage("There has been an error posting!").setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,final int id) {
							return;
						}
					});

					final AlertDialog alertDialog = alertDialogBuilder.create();

					alertDialog.show();
				}
				else {
					finish();
				}
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		else if (item.getItemId() == R.id.cancel) {
			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public ImageButton getImageButton() {
		return imageButton;
	}

	public void setImageButton(ImageButton imageButton) {
		this.imageButton = imageButton;
	}

	@Override
	public void onClick(View arg0) {
		List<CharSequence> items = new LinkedList<CharSequence>();

		items.add("Camera");
		items.add("Gallery");

		final CharSequence[] charSequenceItems = items.toArray(new CharSequence[items.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Pick an  action");
		builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (charSequenceItems[item] == "Camera") {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					startActivityForResult(intent, CAPTURE_IMAGE);
				}
				else if (charSequenceItems[item] == "Gallery") {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
				}
			}
		});

		AlertDialog alert = builder.create();

		alert.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE) {
			if (resultCode == RESULT_OK) {
				Bundle b = data.getExtras();

				Bitmap selectedImageBitmap =  (Bitmap) b.get("data");

				try {
					JSONObject tempObject = NetworkFactory.uploadImage(selectedImageBitmap);

					System.out.println(tempObject.toString());

					imageString = tempObject.getJSONObject("upload").getJSONObject("links").getString("original");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
			} 
			else {
				Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == SELECT_IMAGE) {
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

				String filePath = cursor.getString(columnIndex);

				cursor.close();

				Bitmap selectedImageBitmap = BitmapFactory.decodeFile(filePath);

				try {
					JSONObject tempObject = NetworkFactory.uploadImage(selectedImageBitmap);

					imageString = tempObject.getJSONObject("upload").getJSONObject("links").getString("original");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Picture was not chosen", Toast.LENGTH_SHORT).show();
			} 
			else {
				Toast.makeText(this, "Picture was not chosen", Toast.LENGTH_SHORT).show();
			}
		}
	}
}