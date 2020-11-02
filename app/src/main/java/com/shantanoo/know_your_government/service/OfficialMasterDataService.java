package com.shantanoo.know_your_government.service;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.shantanoo.know_your_government.MainActivity;
import com.shantanoo.know_your_government.model.Official;
import com.shantanoo.know_your_government.model.OfficialResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shantanoo on 10/26/2020.
 */
public class OfficialMasterDataService implements Runnable {

    public static final String GOOGLE_CIVIC_API_BASE_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    public static final String KEY_TOKEN = "?key=";
    public static final String API_KEY = "AIzaSyCNrnzXAOycCyVGE6qxhaNAJhs9ZpJdDwg";
    public static final String COMPLETE_CIVIC_API_URL = GOOGLE_CIVIC_API_BASE_URL + KEY_TOKEN + API_KEY;
    public static final String ADDRESS_TOKEN = "&address=";
    public static final String DATA_NOT_FOUND = "No Data Provided";
    private static final String TAG = "OfficialMasterDataServi";
    private final MainActivity mainActivity;
    private final String location;

    public OfficialMasterDataService(MainActivity mainActivity, String location) {
        this.mainActivity = mainActivity;
        this.location = location;
    }

    @Override
    public void run() {
        Uri uri = Uri.parse(COMPLETE_CIVIC_API_URL + ADDRESS_TOKEN + location);
        String line;
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(uri.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            Log.e(TAG, "run: Exception: ", e);
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {
        final OfficialResult jsonResult = parseJSON(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.populateOfficialData(jsonResult));
    }

    private OfficialResult parseJSON(String input) {
        Log.d(TAG, "parseJSON: Parsing JSON data");

        OfficialResult result = new OfficialResult();

        try {
            JSONObject jsonObject = new JSONObject(input);
            JSONObject normalizedInputJson = jsonObject.getJSONObject("normalizedInput");
            JSONArray officesJson = jsonObject.getJSONArray("offices");
            JSONArray officialJson = jsonObject.getJSONArray("officials");

            // location displayed in the format Chicago IL 60661
            result.setLocation(getLocationData(normalizedInputJson));

            List<Official> officialList = new ArrayList<>();
            for (int item = 0; item < officesJson.length(); item++) {

                JSONObject object = (JSONObject) officesJson.get(item);
                String officeName = object.getString("name");
                JSONArray indices = object.getJSONArray("officialIndices");

                for (int index = 0; index < indices.length(); index++) {

                    Official official = new Official();

                    // office
                    official.setOffice(officeName);

                    JSONObject officialData = (JSONObject) officialJson.get(indices.getInt(index));

                    // official name
                    official.setOfficialName(officialData.getString("name") == null ||
                            officialData.getString("name").trim().length() == 0 ? DATA_NOT_FOUND : officialData.getString("name"));

                    // official Address
                    official.setOfficialAddress(officialData.has("address") ? getAddressData(officeName, officialData) : DATA_NOT_FOUND);

                    // official party
                    official.setOfficialParty(officialData.has("party") ? officialData.getString("party") : DATA_NOT_FOUND);

                    // official phone
                    official.setOfficialPhone(officialData.has("phones") ? getPhoneData(officialData) : DATA_NOT_FOUND);

                    // official email
                    official.setOfficialEmail(officialData.has("emails") ? getEmailData(officialData) : DATA_NOT_FOUND);

                    // official photo
                    official.setOfficialPhotoURL(officialData.has("photoUrl") ? officialData.get("photoUrl").toString() : null);

                    // official web url
                    official.setOfficialWebURL(officialData.has("urls") ? getWebURLData(officialData) : DATA_NOT_FOUND);

                    // official social media channels
                    updateSocialMediaChannels(official, officialData);

                    officialList.add(official);
                }
            }

            result.setOfficialList(officialList);

        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: Failed to parse JSON", e);
        }
        return result;
    }

    private String getLocationData(JSONObject normalizedInputJson) throws JSONException {
        StringBuilder sb = new StringBuilder();
        if (normalizedInputJson.getString("city").trim().length() != 0)
            sb.append(normalizedInputJson.getString("city")).append(", ");

        if (normalizedInputJson.getString("state").trim().length() != 0)
            sb.append(normalizedInputJson.getString("state")).append(" ");

        if (normalizedInputJson.getString("zip").trim().length() != 0)
            sb.append(normalizedInputJson.getString("zip"));

        return sb.toString();
    }

    private void updateSocialMediaChannels(Official official, JSONObject officialData) throws JSONException {
        if (!officialData.has("channels")) {
            return;
        }

        JSONArray socialMedia = officialData.getJSONArray("channels");

        for (int media = 0; media < socialMedia.length(); media++) {
            JSONObject mediaObject = (JSONObject) socialMedia.get(media);
            String mediaType = (String) mediaObject.get("type");
            String mediaInfo = mediaObject.get("id").toString();

            switch (mediaType) {
                case "YouTube":
                    official.setOfficialYouTube(mediaInfo);
                    break;
                case "Facebook":
                    official.setOfficialFB(mediaInfo);
                    break;
                case "Twitter":
                    official.setOfficialTwitter(mediaInfo);
                    break;
                case "GooglePlus":
                    official.setOfficialGooglePlus(mediaInfo);
                    break;
                default:
                    Log.e(TAG, "parseJSON: Unknown Social Media Channel");
            }
        }
    }

    private String getAddressData(String officeName, JSONObject officialData) throws JSONException {
        StringBuilder sb = new StringBuilder(officeName + "\n");

        JSONArray addressJson = officialData.getJSONArray("address");
        JSONObject addressObject = (JSONObject) addressJson.get(0);

        if (addressObject.has("line1") && !TextUtils.isEmpty(addressObject.getString("line1")))
            sb.append(addressObject.getString("line1")).append('\n');
        if (addressObject.has("line2") && !TextUtils.isEmpty(addressObject.getString("line2")))
            sb.append(addressObject.getString("line2")).append('\n');
        if (addressObject.has("line3") && !TextUtils.isEmpty(addressObject.getString("line3")))
            sb.append(addressObject.getString("line3")).append('\n');

        sb.append(addressObject.getString("city")).append(", ");
        sb.append(addressObject.getString("state")).append(" ");
        sb.append(addressObject.getString("zip"));

        return sb.toString();
    }

    private String getWebURLData(JSONObject officialData) throws JSONException {
        JSONArray urlArray = officialData.getJSONArray("urls");
        return urlArray.get(0).toString();
    }

    private String getEmailData(JSONObject officialData) throws JSONException {
        JSONArray emailArray = officialData.getJSONArray("emails");
        return emailArray.get(0).toString();
    }

    private String getPhoneData(JSONObject officialData) throws JSONException {
        JSONArray phoneArray = officialData.getJSONArray("phones");
        return phoneArray.get(0).toString();
    }
}
