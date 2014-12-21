package myself5.m5_Settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;


public class M5_Settings extends Activity {

    public static final String PREFS_NAME = "M5D2WSwitcherPrefs";
    String recovery;
    public static final String recoverypath = "/ext_card/recovery.img";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m5_settings);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean d2w_persisted = settings.getBoolean("d2w", true);
        ((ToggleButton) findViewById(R.id.togglebutton_d2w)).setChecked(d2w_persisted);
        try {
            d2wToggleClicked(findViewById(R.id.togglebutton_d2w));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean cwm_recovery = settings.getBoolean("recovery_CWM", true);
        if (cwm_recovery) {
            ((RadioButton) findViewById(R.id.radio_CWM)).setChecked(cwm_recovery);
        }
        boolean philz_recovery = settings.getBoolean("recovery_PhilZ", true);
        if (philz_recovery) {
            ((RadioButton) findViewById(R.id.radio_PhilZ)).setChecked(philz_recovery);
        }
        boolean twrp_recovery = settings.getBoolean("recovery_TWRP", true);
        if (twrp_recovery) {
            ((RadioButton) findViewById(R.id.radio_TWRP)).setChecked(twrp_recovery);
        }

    }

    protected void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("d2w", ((ToggleButton) findViewById(R.id.togglebutton_d2w)).isChecked());
        editor.putBoolean("recovery_TWRP", ((RadioButton) findViewById(R.id.radio_TWRP)).isChecked());
        editor.putBoolean("recovery_PhilZ", ((RadioButton) findViewById(R.id.radio_PhilZ)).isChecked());
        editor.putBoolean("recovery_CWM", ((RadioButton) findViewById(R.id.radio_CWM)).isChecked());
        // Commit the edits!
        editor.apply();
    }

    public void d2wToggleClicked(View view) throws IOException {
        // Is the toggle on?
        boolean d2w_on = ((ToggleButton) view).isChecked();
        if (d2w_on) {
            //Enable D2W
            Runtime.getRuntime().exec(new String[]{"su", "-c", "echo enabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
        } else {
            //Disable D2W
            Runtime.getRuntime().exec(new String[]{"su", "-c", "echo disabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
        }
    }

    public void recovery_radio(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_TWRP:
                if (checked)
                    recovery = "twrp.img";
                break;
            case R.id.radio_PhilZ:
                if (checked)
                    recovery = "philz.img";
                break;
            case R.id.radio_CWM:
                if (checked)
                    recovery = "cwm.img";
                break;
        }
    }

    public void downloadRecovery(View view) throws IOException{
        //Remove old recovery.img
        Runtime.getRuntime().exec(new String[]{"su", "-c", "rm "+recoverypath});
        //Download new recovery.img
        DownloadFileAsync.downloadFile(this, getString(R.string.DownloadDialog), recoverypath, "https://raw.githubusercontent.com/Myself5/M5_Settings/master/recovery/"+recovery);
    }

    public void flashRecovery(View view) throws IOException{
        //Flash recovery.img from SDCard
        Runtime.getRuntime().exec(new String[] { "su", "-c", "dd if="+recoverypath+" of=/dev/block/platform/msm_sdcc.1/by-name/FOTAKernel"});

        //Toast Message to confirm flash process
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.recovery_flashed);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}