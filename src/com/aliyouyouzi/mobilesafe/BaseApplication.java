package com.aliyouyouzi.mobilesafe;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import com.aliyouyouzi.mobilesafe.R;

import android.app.Application;
import android.content.Context;


@ReportsCrashes(formUri = "http://192.168.1.50:8080/CrashBug/CrashBugServlet",
mode = ReportingInteractionMode.DIALOG,
forceCloseDialogAfterToast = false, // optional, default false
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info, 
resDialogTitle = R.string.crash_dialog_title, 
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, 
resDialogEmailPrompt = R.string.crash_user_email_label, 
resDialogOkToast = R.string.crash_dialog_ok_toast )

public class BaseApplication extends Application {
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

		ACRA.init(this);
	}
}
