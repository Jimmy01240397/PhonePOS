package com.JImmiker.go;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver
{
    private final String TAG = "BootUpService";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "BroadcastReceiver running!!");
        try
        {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要啟動的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
        catch (Exception e)
        {
            Log.i(TAG, e.toString());
        }
    }
}
