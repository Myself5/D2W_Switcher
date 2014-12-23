package myself5.m5_settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Recovery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Recovery extends Fragment implements View.OnClickListener{

    public static final String PREFS_NAME = "M5SettingsPrefs";
    private View _myFragmentView;
    private OnFragmentInteractionListener mListener;
    String _recovery;
    public static final String _recoveryPath = "/ext_card/recovery.img";

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
        // Inflate the layout for this fragment
        _myFragmentView = inflater.inflate(R.layout.fragment_recovery, container, false);

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean cwm_recovery = settings.getBoolean("recovery_CWM", true);
        if (cwm_recovery) {
            ((RadioButton) _myFragmentView.findViewById(R.id.radio_CWM)).setChecked(cwm_recovery);
            _recovery = "cwm.img";
        }
        boolean philz_recovery = settings.getBoolean("recovery_PhilZ", true);
        if (philz_recovery) {
            ((RadioButton) _myFragmentView.findViewById(R.id.radio_PhilZ)).setChecked(philz_recovery);
            _recovery = "philz.img";
        }
        boolean twrp_recovery = settings.getBoolean("recovery_TWRP", true);
        if (twrp_recovery) {
            ((RadioButton) _myFragmentView.findViewById(R.id.radio_TWRP)).setChecked(twrp_recovery);
            _recovery = "twrp.img";
        }
        //Add the Buttons to the OnClick Listener
        Button flash = (Button) _myFragmentView.findViewById(R.id.flash_button);
        flash.setOnClickListener(this);
        Button download = (Button) _myFragmentView.findViewById(R.id.download_button);
        download.setOnClickListener(this);
        Button reboot_recovery = (Button) _myFragmentView.findViewById(R.id.reboot_recovery);
        reboot_recovery.setOnClickListener(this);
        Button reboot_bootloader = (Button) _myFragmentView.findViewById(R.id.reboot_bootloader);
        reboot_bootloader.setOnClickListener(this);
        RadioButton cwm = (RadioButton) _myFragmentView.findViewById(R.id.radio_CWM);
        cwm.setOnClickListener(this);
        RadioButton philz = (RadioButton) _myFragmentView.findViewById(R.id.radio_PhilZ);
        philz.setOnClickListener(this);
        RadioButton twrp = (RadioButton) _myFragmentView.findViewById(R.id.radio_TWRP);
        twrp.setOnClickListener(this);
        return _myFragmentView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("recovery_TWRP", ((RadioButton) _myFragmentView.findViewById(R.id.radio_TWRP)).isChecked());
        editor.putBoolean("recovery_PhilZ", ((RadioButton) _myFragmentView.findViewById(R.id.radio_PhilZ)).isChecked());
        editor.putBoolean("recovery_CWM", ((RadioButton) _myFragmentView.findViewById(R.id.radio_CWM)).isChecked());
        // Commit the edits!
        editor.apply();
    }
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
                    _recovery = "twrp.img";
                break;
            case R.id.radio_PhilZ:
                    _recovery = "philz.img";
                break;
            case R.id.radio_CWM:
                    _recovery = "cwm.img";
                break;
        }
    }

    public void downloadRecovery() throws IOException {
        //Remove old recovery.img
        Runtime.getRuntime().exec(new String[]{"su", "-c", "rm "+ _recoveryPath});
        //Download new recovery.img
        DownloadFileAsync.downloadFile(getActivity(), getString(R.string.DownloadDialog), _recoveryPath, "https://raw.githubusercontent.com/Myself5/M5_Settings/master/recovery/"+ _recovery);
    }

    public void flashRecovery() throws IOException{
        //Flash recovery.img from SDCard
        Runtime.getRuntime().exec(new String[] { "su", "-c", "dd if="+ _recoveryPath +" of=/dev/block/platform/msm_sdcc.1/by-name/FOTAKernel"});

        //Toast Message to confirm flash process
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = getString(R.string.recovery_flashed);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void reboot(String modus) throws IOException{
        Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot "+ modus});

        //Toast Message to tell user it's rebooting
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = getString(R.string.rebooting);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
