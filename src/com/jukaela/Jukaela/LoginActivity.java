package com.jukaela.Jukaela;

import org.json.JSONArray;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jukaela.Jukaela.R;

public class LoginActivity extends Activity {

	public static final String EXTRA_EMAIL = "com.jukaela.Jukaela.extra.EMAIL";

	private UserLoginTask authTask = null;

	private String emailString;
	private String passwordString;

	private CheckBox rememberMeCheckBox;
	private EditText emailTextField;
	private EditText passwordTextField;
	private View loginFormView;
	private View loginStatusView;
	private TextView loginStatusMessageView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);

		setTitle("Jukaela Social");

		emailString = getIntent().getStringExtra(EXTRA_EMAIL);
		emailTextField = (EditText) findViewById(R.id.email);
		emailTextField.setText(emailString);

		passwordTextField = (EditText) findViewById(R.id.password);
		passwordTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		loginFormView = findViewById(R.id.login_form);
		loginStatusView = findViewById(R.id.login_status);
		loginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		rememberMeCheckBox = (CheckBox)findViewById(R.id.rememberMe);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

		Boolean rememberMeBool = sharedPrefs.getBoolean("rememberMe", false);

		if (rememberMeBool == true) {
			rememberMeCheckBox.setChecked(true);

			emailTextField.setText(sharedPrefs.getString("email", null));
			passwordTextField.setText(sharedPrefs.getString("password", null));

			attemptLogin();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	public void attemptLogin() {
		if (authTask != null) {
			return;
		}

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		emailTextField.setError(null);
		passwordTextField.setError(null);

		emailString = emailTextField.getText().toString();
		passwordString = passwordTextField.getText().toString();

		boolean cancel = false;

		View focusView = null;

		if (TextUtils.isEmpty(passwordString)) {
			passwordTextField.setError(getString(R.string.error_field_required));
			focusView = passwordTextField;
			cancel = true;
		} 
		else if (passwordString.length() < 4) {
			passwordTextField.setError(getString(R.string.error_invalid_password));

			focusView = passwordTextField;

			cancel = true;
		}

		if (TextUtils.isEmpty(emailString)) {
			emailTextField.setError(getString(R.string.error_field_required));

			focusView = emailTextField;

			cancel = true;
		} 
		else if (!emailString.contains("@")) {
			emailTextField.setError(getString(R.string.error_invalid_email));

			focusView = emailTextField;

			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} 
		else {
			loginStatusMessageView.setText(R.string.login_progress_signing_in);

			showProgress(true);

			authTask = new UserLoginTask();
			authTask.execute((Void) null);

			System.out.println("Attempting login");
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			loginStatusView.setVisibility(View.VISIBLE);
			loginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					loginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			loginFormView.setVisibility(View.VISIBLE);
			loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					loginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} 
		else {
			loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				JSONObject loginObject = NetworkFactory.login(emailString, passwordString);

				JSONArray feedResponseObject = NetworkFactory.getFeedFromTo(0, 20);

				if (rememberMeCheckBox.isChecked()) {
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

					sharedPrefs.edit().putString("email", emailString).commit();
					sharedPrefs.edit().putString("password", passwordString).commit();
					sharedPrefs.edit().putBoolean("rememberMe", true).commit();
				}

				Intent i = new Intent(LoginActivity.this, MainActivity.class);
				i.putExtra("tempArray", feedResponseObject.toString());
				startActivity(i);

				showProgress(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			authTask = null;
			showProgress(false);

			if (success) {
				System.out.println("Success!");
			} 
			else {
				passwordTextField.setError(getString(R.string.error_incorrect_password));
				passwordTextField.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			authTask = null;
			showProgress(false);
		}


	}
}
