package com.jukaela.Jukaela;

import org.json.JSONArray;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jukaela.Jukaela.R;
import com.jukaela.Jukaela.R.id;
import com.jukaela.Jukaela.R.layout;
import com.jukaela.Jukaela.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewerActivity extends SherlockActivity {

	public String imageString;

	private TouchImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);

		setImageView((TouchImageView)findViewById(R.id.imageViewer));

		setTitle("");

		Intent intent = getIntent();
		setImageString(intent.getStringExtra("imageString"));

		showImage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();

		inflater.inflate(R.menu.activity_image_viewer, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.doneButton) {
			finish();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public String getImageString () {
		return this.imageString;
	}

	public void setImageString (String tempImageResource) {
		this.imageString = tempImageResource;
	}

	private void showImage () {
		imageView.setBackgroundColor(Color.argb(0,0,0,0));

		ImageLoad downloader = new ImageLoad(imageView, imageString.substring(imageString.lastIndexOf('/') + 1), this); 

		downloader.execute(imageString);
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(TouchImageView imageView) {
		this.imageView = imageView;
	}
}
