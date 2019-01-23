package com.example.messi_lp.touchdemo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class WindowChangeDetectingService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );
            ActivityInfo activityInfo = tryGetActivity(componentName);
            if (activityInfo!=null){
                    RxBus.getDefault().send(componentName.getPackageName());
                //...
            }
            // Log.i("younchen", "appStarted:" + componentName.flattenToShortString());
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            Log.i("younchen", "get appInfo:" + componentName.getPackageName());
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i("younchen", "onServiceConnected: "+"start");
        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }


    @Override
    public void onInterrupt() {

    }
}
