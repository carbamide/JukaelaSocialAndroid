package com.jukaela.Jukaela;

import android.app.Application;
import android.util.Log;

import com.jukaela.Jukaela.NetworkFactory.SERVER_TYPE;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

public class JukaelaApplication extends Application {

	@Override
	public void onCreate() {

		super.onCreate();

		AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);

		UAirship.takeOff(this, options);
		Logger.logLevel = Log.VERBOSE;


		PushPreferences prefs = PushManager.shared().getPreferences();
		Logger.info("My Application onCreate - App APID: " + prefs.getPushId());
		
		NetworkFactory.setServer(SERVER_TYPE.PRODUCTION);
		
		NetworkFactory.setAPID(prefs.getPushId());
		
		CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();

		nb.statusBarIconDrawableId = R.drawable.ic_launcher;

		nb.layout = R.layout.notification;
		nb.layoutIconDrawableId = R.drawable.ic_launcher;//custom layout icon
		nb.layoutIconId = R.id.icon;
		nb.layoutSubjectId = R.id.subject;
		nb.layoutMessageId = R.id.message;


		PushManager.shared().setNotificationBuilder(nb);
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
		PushManager.enablePush();

	}
}
