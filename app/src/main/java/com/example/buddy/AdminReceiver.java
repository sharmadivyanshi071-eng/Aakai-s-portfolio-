package com.example.buddy;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // This tells you the permission was successful
        Toast.makeText(context, "Buddy Admin: Enabled, Master", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        // This warns you if the permission is removed
        Toast.makeText(context, "Buddy Admin: Disabled", Toast.LENGTH_SHORT).show();
    }
}
