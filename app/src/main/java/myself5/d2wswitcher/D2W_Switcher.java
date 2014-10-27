package myself5.d2wswitcher;

import android.app.Activity;
import android.view.View;
import android.widget.ToggleButton;
import java.io.IOException;


public class D2W_Switcher extends Activity {

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
