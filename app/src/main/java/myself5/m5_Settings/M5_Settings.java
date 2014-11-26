package myself5.m5_Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ToggleButton;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.http.util.ByteArrayBuffer;
import android.util.Log;


public class M5_Settings extends Activity {

    String recovery;
    public static final String PREFS_NAME = "M5D2WSwitcherPrefs";
    private final String PATH = "/data/data/myself5.m5_Settings/";  //put the downloaded file here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m5_settings);
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        boolean persist = settings.getBoolean("persist", true);
//        ((CheckBox) findViewById(R.id.checkbox_persist)).setChecked(persist);
//        if (persist) {
            boolean d2w_persisted = settings.getBoolean("d2w", true);
            ((ToggleButton) findViewById(R.id.togglebutton_d2w)).setChecked(d2w_persisted);
            try {
                d2wToggleClicked(findViewById(R.id.togglebutton_d2w));
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }else {
//            try {
//                ((ToggleButton) findViewById(R.id.togglebutton_d2w)).setChecked(d2wValue());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        boolean cwm_recovery = settings.getBoolean("recovery_CWM", true);
        if(cwm_recovery)
        {
            ((RadioButton) findViewById(R.id.radio_CWM)).setChecked(cwm_recovery);
        }
        boolean philz_recovery = settings.getBoolean("recovery_PhilZ", true);
        if(philz_recovery)
        {
            ((RadioButton) findViewById(R.id.radio_PhilZ)).setChecked(philz_recovery);
        }
        boolean twrp_recovery = settings.getBoolean("recovery_TWRP", true);
        if(twrp_recovery)
        {
            ((RadioButton) findViewById(R.id.radio_TWRP)).setChecked(twrp_recovery);
        }
    }

    protected void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean("persist", ((CheckBox) findViewById(R.id.checkbox_persist)).isChecked());
        editor.putBoolean("d2w", ((ToggleButton) findViewById(R.id.togglebutton_d2w)).isChecked());
        editor.putBoolean("recovery_TWRP", ((RadioButton) findViewById(R.id.radio_TWRP)).isChecked());
        editor.putBoolean("recovery_PhilZ", ((RadioButton) findViewById(R.id.radio_PhilZ)).isChecked());
        editor.putBoolean("recovery_CWM", ((RadioButton) findViewById(R.id.radio_CWM)).isChecked());
//        try {
//            editor.putBoolean("d2w", d2wValue());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // Commit the edits!
        editor.commit();
    }

    /**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.d2_w__switcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    **/

    public boolean d2wValue() throws IOException{
        String d2w_path = "/sys/devices/virtual/input/max1187x/power/wakeup";
        FileReader fr = new FileReader(d2w_path);
        BufferedReader D2WReader = new BufferedReader(fr);
        int D2W_VALUE = D2WReader.read();
        boolean d2w_boolean;
        if (D2W_VALUE == 101) {
            d2w_boolean = true;
        }else{
            d2w_boolean = false;
        }
        return d2w_boolean;
    }

    public void d2wToggleClicked(View view) throws IOException {
        // Is the toggle on?
        boolean d2w_on = ((ToggleButton) view).isChecked();
        if (d2w_on)
        {
            //Enable D2W
            Runtime.getRuntime().exec(new String[] { "su", "-c", "echo enabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
        }else{
            //Disable D2W
            Runtime.getRuntime().exec(new String[] { "su", "-c", "echo disabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_TWRP:
                if (checked)
                    recovery = "TWRP";
                    break;
            case R.id.radio_PhilZ:
                if (checked)
                    recovery = "PhilZ";
                    break;
            case R.id.radio_CWM:
                if (checked)
                    recovery = "CWM";
                    break;
        }
    }

    public void flashRecovery() throws IOException {
        Runtime.getRuntime().exec(new String[] {"rm /data/data/myself5.m5_Settings/recovery.img"});
        DownloadFromUrl(recovery);
        Runtime.getRuntime().exec(new String[] { "su", "-c", "dd if=/data/data/myself5.m5_Settings/recovery.img of=/dev/block/platform/msm_sdcc.1/by-name/FOTAKernel"});
    }

    public void DownloadFromUrl(String recovery) {  //this is the downloader method. Credits to http://www.helloandroid.com/tutorials/how-download-fileimage-url-your-device
        try {
            URL url = new URL("https://raw.githubusercontent.com/Myself5/M5_Settings/master/recovery/" + recovery + ".img"); //you can write here any link
                    File file = new File("recovery.img");

            long startTime = System.currentTimeMillis();
            Log.d("Recovery Downloader", "download begining");
            Log.d("Recovery Downloader", "download url:" + url);
            Log.d("Recovery Downloader", "downloaded file name: recovery.img");
                        /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

                        /*
                         * Define InputStreams to read from the URLConnection.
                         */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

                        /*
                         * Read bytes to the Buffer until there is nothing more to read(-1).
                         */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

                        /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();
            Log.d("Recovery Downloader", "download ready in"
                    + ((System.currentTimeMillis() - startTime) / 1000)
                    + " sec");

        } catch (IOException e) {
            Log.d("Recovery Downloader", "Error: " + e);
        }
    }
}