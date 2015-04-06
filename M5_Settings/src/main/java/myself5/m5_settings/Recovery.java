package myself5.m5_settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

import eu.chainfire.libsuperuser.Shell;

import static java.lang.System.getProperty;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Recovery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Recovery extends Fragment implements View.OnClickListener{

    public static final String PREFS_NAME = "M5SettingsPrefs";
    private View mMyFragmentView;
    private OnFragmentInteractionListener mListener;
    private String mRecovery;
    private String mDevice;
    private static final String mRecoveryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    public Recovery() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Which device is running this app
        mDevice = android.os.Build.DEVICE;
        if(mDevice.equals("sirius") || mDevice.equals("D6503") || mDevice.equals("D6502") || mDevice.equals("D6506") || mDevice.equals("D6543")) {
            mDevice = "sirius";
        }else if(mDevice.equals("z3") || mDevice.equals("leo") || mDevice.equals("D6603") || mDevice.equals("D6602") || mDevice.equals("D6606") || mDevice.equals("D6643")){
            mDevice = "z3";
        }else{
            mDevice = "not_supported";
        }

        // Inflate the layout for this fragment
        mMyFragmentView = inflater.inflate(R.layout.fragment_recovery, container, false);

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean cwm_recovery = settings.getBoolean("recovery_CWM", true);
        if (cwm_recovery) {
            ((RadioButton) mMyFragmentView.findViewById(R.id.radio_CWM)).setChecked(cwm_recovery);
            mRecovery = "CWM.img";
        }
        boolean philz_recovery = settings.getBoolean("recovery_PhilZ", true);
        if (philz_recovery) {
            ((RadioButton) mMyFragmentView.findViewById(R.id.radio_PhilZ)).setChecked(philz_recovery);
            mRecovery = "PhilZ.img";
        }
        boolean twrp_recovery = settings.getBoolean("recovery_TWRP", true);
        if (twrp_recovery) {
            ((RadioButton) mMyFragmentView.findViewById(R.id.radio_TWRP)).setChecked(twrp_recovery);
            mRecovery = "TWRP.img";
        }
        //Add the Buttons to the OnClick Listener
        Button flash = (Button) mMyFragmentView.findViewById(R.id.flash_button);
        flash.setOnClickListener(this);
        Button download = (Button) mMyFragmentView.findViewById(R.id.download_button);
        download.setOnClickListener(this);
        Button reboot_recovery = (Button) mMyFragmentView.findViewById(R.id.reboot_recovery);
        reboot_recovery.setOnClickListener(this);
        Button reboot_bootloader = (Button) mMyFragmentView.findViewById(R.id.reboot_bootloader);
        reboot_bootloader.setOnClickListener(this);
        RadioButton cwm = (RadioButton) mMyFragmentView.findViewById(R.id.radio_CWM);
        cwm.setOnClickListener(this);
        RadioButton philz = (RadioButton) mMyFragmentView.findViewById(R.id.radio_PhilZ);
        philz.setOnClickListener(this);
        RadioButton twrp = (RadioButton) mMyFragmentView.findViewById(R.id.radio_TWRP);
        twrp.setOnClickListener(this);
        return mMyFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause(){
        super.onPause();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("recovery_TWRP", ((RadioButton) mMyFragmentView.findViewById(R.id.radio_TWRP)).isChecked());
        editor.putBoolean("recovery_PhilZ", ((RadioButton) mMyFragmentView.findViewById(R.id.radio_PhilZ)).isChecked());
        editor.putBoolean("recovery_CWM", ((RadioButton) mMyFragmentView.findViewById(R.id.radio_CWM)).isChecked());
        // Commit the edits!
        editor.apply();
    }
//    @Override
//    public void onStop(){
//        super.onStop();
//    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.flash_button:
                try {
                    flashRecovery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.download_button:
                try {
                    downloadRecovery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.reboot_recovery:
                try {
                    reboot("recovery");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.reboot_bootloader:
                try {
                    reboot("bootloader");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.radio_TWRP:
                    mRecovery = "TWRP.img";
                break;
            case R.id.radio_PhilZ:
                    mRecovery = "PhilZ.img";
                break;
            case R.id.radio_CWM:
                    mRecovery = "CWM.img";
                break;
        }
    }

    public void downloadRecovery() throws IOException {
        if(mDevice.equals("not_supported")) {
            //Toast Message to tell user it's rebooting
            Context context = this.getActivity().getApplicationContext();
            CharSequence text = getString(R.string.not_supported);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else{
            //Remove old recovery.img
            Shell.SU.run("rm -f" + mRecoveryPath + mRecovery);
            //Wait
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Download new recovery.img
            DownloadFileAsync.downloadFile(getActivity(), getString(R.string.DownloadDialog), mRecoveryPath + mRecovery, "http://dl.myself5.de/" + mDevice + "/Recovery/" + mRecovery);

        }
    }

    public void flashRecovery() throws IOException {
        //Flash recovery.img from SDCard
        if (mDevice.equals("not_supported")) {
            //Toast Message to tell user the device is not supported
            Context context = this.getActivity().getApplicationContext();
            CharSequence text = getString(R.string.not_supported);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            //Flash the Recovery
            Shell.SU.run("dd if=" + mRecoveryPath + mRecovery + " of=/dev/block/platform/msm_sdcc.1/by-name/FOTAKernel");

            //Toast Message to confirm flash process
            Context context = this.getActivity().getApplicationContext();
            CharSequence text = getString(R.string.recovery_flashed);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void reboot(String mode) throws IOException {
        //Toast Message to tell user it's rebooting
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = getString(R.string.rebooting);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Shell.SH.run("reboot" + mode);
    }
}
