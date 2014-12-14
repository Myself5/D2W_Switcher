package myself5.m5_Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.io.IOException;

public class D2WApplyatBoot extends BroadcastReceiver {

    public static final String PREFS_NAME = "M5D2WSwitcherPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            boolean d2w_persisted = settings.getBoolean("d2w", true);
                if (d2w_persisted){
                    try {
                        Runtime.getRuntime().exec(new String[] { "su", "-c", "echo enabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
