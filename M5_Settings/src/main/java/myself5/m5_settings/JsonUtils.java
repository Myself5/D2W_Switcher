package myself5.m5_settings;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/*
* based off Kernel Manager by the mighty Grarak
* TODO: Actually use Json for device management
*/

public class JsonUtils {
    public static final String TAG = "M5 Settings";
    private static String mDevice = android.os.Build.DEVICE;
    public static class JsonDeviceArrays {

        private final String JSON;
        private JSONObject JSON_DEVICE;
        private String[] RECOVERIES;

        public JsonDeviceArrays(String json) {
            JSON = json;
        }

        public String getRecoveryPartition() {
            try {
                return getDeviceJson().getString("recovery");
            } catch (JSONException e) {
                Log.e(TAG, "unable to read recovery partition");
            }
            return null;
        }

        public String getRecoveryJson(String kernel) {
            return getRecoveryObjects(kernel, "json");
        }

        private String getRecoveryObjects(String recovery, String object) {
            for (int i = 0; i < getDeviceRecoveries().length; i++)
                if (recovery.equals( getDeviceRecoveries()[i])) {
                    try {
                        JSONArray recoveryJSON = getDeviceJson().getJSONArray("recoveries");

                        return recoveryJSON.getJSONObject(i).getString(object);
                    } catch (JSONException e) {
                        Log.e(TAG, "unable to read recoveries");
                    }
                }
            return null;
        }

        public String[] getDeviceRecoveries() {
            if (RECOVERIES == null)
                try {
                    JSONArray recoveryJSON = getDeviceJson().getJSONArray("recoveries");

                    RECOVERIES = new String[recoveryJSON.length()];
                    for (int i = 0; i < recoveryJSON.length(); i++)
                        RECOVERIES[i] = recoveryJSON.getJSONObject(i).getString("name");
                } catch (JSONException e) {
                    Log.e(TAG, "unable to read recoveries");
                }

            return RECOVERIES;
        }

        public boolean isSupported() {
            return getDeviceJson() != null;
        }

        private JSONObject getDeviceJson() {
            if (JSON_DEVICE == null) {
                if (JSON == null) return null;

                try {
                    JSONObject jsonObj = new JSONObject(JSON);
                    JSONArray devices = jsonObj.getJSONArray("devices");

                    for (int i = 0; i < devices.length(); i++) {
                        JSONObject device = devices.getJSONObject(i);

                        JSONArray codenames = device.getJSONArray("codenames");

                        for (int x = 0; x < codenames.length(); x++)
                            if (codenames.getString(x).equals(mDevice))
                                JSON_DEVICE = device;
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "unable to read devices JSON");
                }
            }
            return JSON_DEVICE;
        }

    }

    public static class JsonListArrays {

        private final String JSON;

        public JsonListArrays(String json) {
            JSON = json;
        }

        public String getName(int position) {
            return getString(position, "name");
        }

        public String getUrl(int position) {
            return getString(position, "url");
        }

        private String getString(int position, String object) {
            try {
                JSONObject download = getLists().getJSONObject(position);

                return download.getString(object);
            } catch (JSONException e) {
                Log.e(TAG, "unable to read lists");
            }
            return null;
        }

        public int getLength() {
            return getLists().length();
        }

        private JSONArray getLists() {
            try {
                JSONObject downloads = new JSONObject(JSON);

                return downloads.getJSONArray("lists");
            } catch (JSONException e) {
                Log.e(TAG, "unable to read lists");
            }
            return null;
        }

        public boolean isUseable() {
            return getLists() != null;
        }

    }

    public static class JsonDownloadArrays {

        private final String JSON;

        public JsonDownloadArrays(String json) {
            JSON = json;
        }

        public String getUrl(int position) {
            return getString(position, "url");
        }

        public String getNote(int position) {
            return getString(position, "note");
        }

        public String getVersion(int position) {
            return getString(position, "version");
        }

        private String getString(int position, String object) {
            try {
                JSONObject download = getDownloads().getJSONObject(position);

                return download.getString(object);
            } catch (JSONException e) {
                Log.e(TAG, "unable to read downloads");
            }
            return null;
        }

        public int getLength() {
            return getDownloads().length();
        }

        private JSONArray getDownloads() {
            try {
                JSONObject downloads = new JSONObject(JSON);

                return downloads.getJSONArray("downloads");
            } catch (JSONException e) {
                Log.e(TAG, "unable to read downloads");
            }
            return null;
        }

        public boolean isUseable() {
            return getDownloads() != null;
        }

    }

}