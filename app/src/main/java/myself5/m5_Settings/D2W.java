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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link D2W.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class D2W extends Fragment implements View.OnClickListener{

    public static final String PREFS_NAME = "M5SettingsPrefs";
    private View _myFragmentView;
    private OnFragmentInteractionListener mListener;

    public D2W() {
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
        _myFragmentView = inflater.inflate(R.layout.fragment_d2w, container, false);

        // Restore preferences
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        boolean d2w_persisted = settings.getBoolean("d2w", true);
        ((ToggleButton) _myFragmentView.findViewById(R.id.togglebutton_d2w)).setChecked(d2w_persisted);
//        try {
//            d2wToggleClicked(_myFragmentView.findViewById(R.id.togglebutton_d2w));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //Add the Buttons to the OnClick Listener
        Button d2wt = (Button) _myFragmentView.findViewById(R.id.togglebutton_d2w);
        d2wt.setOnClickListener(this);
        return _myFragmentView;
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
    public void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("d2w", ((ToggleButton) _myFragmentView.findViewById(R.id.togglebutton_d2w)).isChecked());
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
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.togglebutton_d2w:
                try {
                    d2wToggleClicked(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public void d2wToggleClicked(View view) throws IOException {
        // Is the toggle on?
        boolean d2w_on = ((ToggleButton) view).isChecked();
        if (d2w_on) {
            //Enable D2W
            Runtime.getRuntime().exec(new String[]{"su", "-c", "echo enabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
            //Toast Message to confirm D2W is enabled
            Context context = this.getActivity().getApplicationContext();
            CharSequence text = getString(R.string.d2w_enabled);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            //Disable D2W
            Runtime.getRuntime().exec(new String[]{"su", "-c", "echo disabled > /sys/devices/virtual/input/max1187x/power/wakeup"});
            //Toast Message to confirm D2W is disabled
            Context context = this.getActivity().getApplicationContext();
            CharSequence text = getString(R.string.d2w_disabled);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
