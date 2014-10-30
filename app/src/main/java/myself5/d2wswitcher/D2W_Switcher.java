package myself5.d2wswitcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class D2W_Switcher extends Activity {

    public static final String PREFS_NAME = "M5D2WSwitcherPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d2_w__switcher);
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean persist = settings.getBoolean("persist", true);
        ((CheckBox) findViewById(R.id.checkbox_persist)).setChecked(persist);
        if (persist) {
            boolean d2w_persisted = settings.getBoolean("d2w", true);
            ((ToggleButton) findViewById(R.id.togglebutton_d2w)).setChecked(d2w_persisted);
            try {
                d2wToggleClicked(findViewById(R.id.togglebutton_d2w));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                ((ToggleButton) findViewById(R.id.togglebutton_d2w)).setChecked(d2wValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("persist", ((CheckBox) findViewById(R.id.checkbox_persist)).isChecked());
        try {
            editor.putBoolean("d2w", d2wValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        String d2w_path = "/sys/android_touch/doubletap2wake";
        FileReader fr = new FileReader(d2w_path);
        BufferedReader D2WReader = new BufferedReader(fr);
        int D2W_VALUE = D2WReader.read();
        boolean d2w_boolean;
        if (D2W_VALUE == 49) {
            d2w_boolean = true;
        }else {
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
            Runtime.getRuntime().exec(new String[] { "su", "-c", "echo 1 > /sys/android_touch/doubletap2wake"});
        }else{
            //Disable D2W
            Runtime.getRuntime().exec(new String[] { "su", "-c", "echo 0 > /sys/android_touch/doubletap2wake"});
        }
    }
}