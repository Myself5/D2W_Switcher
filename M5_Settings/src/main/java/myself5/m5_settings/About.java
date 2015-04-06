package myself5.m5_settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;


public class About extends Activity implements View.OnClickListener {

    private View mMyFragmentView;
    private DialogFragment mDialog;
    private String DonationURL ="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AP3N6LLL9GGZ2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ImageButton donate = (ImageButton) findViewById(R.id.donate);
        donate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.donate:
                openDonatePage();
                break;
        }
    }

    void openDonatePage() {

                // Create a new DonateDialogFragment
                mDialog = DonateDialogFragment.newInstance();

                // Show DonateDialogFragment
                mDialog.show(getFragmentManager(), getString(R.string.app_name));
    }

    public void visitDonatePage(){
        Intent baseIntent = new Intent()
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(DonationURL));

        Intent chooserIntent = Intent.createChooser(baseIntent, getString(R.string.load_url));
        startActivity(chooserIntent);
    }

    // Class that creates the AlertDialog
    public static class DonateDialogFragment extends DialogFragment {

        public static DonateDialogFragment newInstance() {
            return new DonateDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.donate_dialog))

                            // User cannot dismiss dialog by hitting back button
                    .setCancelable(false)

                            // Set up Visit DonateLink Button
                    .setPositiveButton(getString(R.string.donate_now),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((About) getActivity())
                                            .visitDonatePage();
                                }
                            })

                            // Set up Not Now Button
                    .setNegativeButton(getString(R.string.not_now),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    return;
                                }
                            }).create();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_about, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
