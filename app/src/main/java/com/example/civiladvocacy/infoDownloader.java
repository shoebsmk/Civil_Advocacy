package com.example.civiladvocacy;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.annotation.GlideModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class infoDownloader {
    private static final String apiKey = "AIzaSyBb2-LJzxy_p3rfT3lNa2mXsG7HdrjR2Ro";
    //https://www.googleapis.com/civicinfo/v2/representatives?key=ABC123xyz&address=60605
    private static final String DATA_URL_BASE = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private static final String DATA_URL_ADDR_BASE = "&address=";
    private static final String DATA_URL_ADDR_VAL = "60659";
    //private static ArrayList<Official> officialArrayList = new ArrayList<>();

    public static void getAPIData(String inputAddress, MainActivity mainActivity){
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        String urlToUse = getUrlToUse(inputAddress);

        Response.Listener<JSONObject> listener =
                response -> {
                    try {
                        handleResults(mainActivity,response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Log.e(TAG, "getFinDataAsStock: " + parseJSONandReturnStock(response.toString()));
                };

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getAPIData: Failed");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getApiData: " + jsonObject);
                handleResults(mainActivity, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, JSONObject responseOBJ) throws JSONException {
        if (responseOBJ == null) {
            Log.d(TAG, "handleResults: " + "Failed to download data");
            mainActivity.downloadFailed();
            return;
        }
        mainActivity.updateData(parseJSON(responseOBJ,mainActivity));
        //Log.e(TAG, "handleResults: Official " + parseJSON(responseOBJ));

    }

    private static ArrayList<Official> parseJSON(JSONObject responseOBJ, MainActivity mainActivity) throws JSONException {
        String normalizedAddress = null;
        if(responseOBJ.has("normalizedInput")){
            String nICity = responseOBJ.getJSONObject("normalizedInput").getString("city");
            String nIState = responseOBJ.getJSONObject("normalizedInput").getString("state");
            String nIZip = responseOBJ.getJSONObject("normalizedInput").getString("zip");
            normalizedAddress = String.format("%s, %s %s", nICity, nIState, nIZip);
            mainActivity.updateMainActivityAddress(nICity, nIState, nIZip);
            Log.e(TAG, "parseJSON: Normalized Input: " + nICity + ", " + nIState + " " + nIZip);
        }
        JSONArray officesJ = responseOBJ.getJSONArray("offices");
        JSONArray officialsJ = responseOBJ.getJSONArray("officials");
        String officialName = null;
        String party = null;
        JSONArray officialAddressArray = null;
        String officialAddressL1 = null;
        String officialAddressL2 = null;
        String officialAddressL3 = null;
        String officialAddressFinal = null;

        String officialCity = null;
        String officialZip = null;
        String phone = null;
        String url = null;
        String email = null;
        String photoUrl = null;
        HashMap<String, String> channels = new HashMap<>();
        String facebookID = null;
        String youtubeID = null;
        String twitterID = null;
        ArrayList<Official> officialArrayList = new ArrayList<>();
        try{
            for(int i = 0; i< officesJ.length(); i++){
                JSONObject office = (JSONObject) officesJ.get(i);
                String officeName = office.getString("name");
                JSONArray officialIndices = office.getJSONArray("officialIndices");
                for(int j = 0; j< officialIndices.length(); j++){
                    int officialIndex = (int) officialIndices.get(j);
                    JSONObject officialJ = (JSONObject) officialsJ.get(officialIndex);

                    if(officialJ.has("name")){
                        officialName = officialJ.getString("name");
                    }
                    if(officialJ.has("party")){
                        party = officialJ.getString("party");
                    }


                    if(officialJ.has("phones")){
                        phone = (String) ((JSONArray) officialJ.getJSONArray("phones")).get(0);
                    }
                    if(officialJ.has("urls")){
                        url = (String) ((JSONArray) officialJ.getJSONArray("urls")).get(0);
                    }
                    if(officialJ.has("emails")){
                        email = (String) ((JSONArray) officialJ.getJSONArray("emails")).get(0);
                    }

                    if(officialJ.has("photoUrl")){
                        photoUrl = officialJ.getString("photoUrl");
                    }

                    if(officialJ.has("channels")){
                        for(int x = 0; x < officialJ.getJSONArray("channels").length();x++){
                            //Store when the loop ends
                            channels.put(((JSONObject) officialJ.getJSONArray("channels").get(x))
                                            .getString("type"),
                                    ((JSONObject) officialJ.getJSONArray("channels").get(x))
                                            .getString("id"));
                        }

                        if(((JSONObject) officialJ.getJSONArray("channels").get(0))
                                .has("type")){
                                twitterID = ((JSONObject) officialJ.getJSONArray("channels")
                                    .get(0)).getString("id");
                                Log.d(TAG, "parseJSON: twitter ID (0): " + twitterID );
                        }
                        if(officialJ.getJSONArray("channels").length() == 2 &&
                                ((JSONObject) officialJ.getJSONArray("channels").get(1))
                                    .has("type")){
                                facebookID = ((JSONObject) officialJ.getJSONArray("channels")
                                        .get(1)).getString("id");
                                Log.d(TAG, "parseJSON: facebook ID (1): " + facebookID );

                        }
                        if(officialJ.getJSONArray("channels").length() == 3 &&
                                ((JSONObject) officialJ.getJSONArray("channels").get(2))
                                    .has("type")){
                                youtubeID = ((JSONObject) officialJ.getJSONArray("channels")
                                        .get(2)).getString("id");
                                Log.d(TAG, "parseJSON: youtube ID (1): " + youtubeID );

                        }

                    }


                    if(officialJ.has("address")){
                        officialAddressArray = officialJ.getJSONArray("address");
                        if(officialAddressArray.getJSONObject(0).has("line1")){
                            officialAddressL1 = officialAddressArray.getJSONObject(0).getString("line1");
                            officialAddressFinal = officialAddressL1;
                        }
                        if(officialAddressArray.getJSONObject(0).has("line2")){
                            officialAddressL2 = officialAddressArray.getJSONObject(0).getString("line2");
                            officialAddressFinal = officialAddressFinal + ", " + officialAddressL2;
                        }

                        if(officialAddressArray.getJSONObject(0).has("city")){
                            officialCity = officialAddressArray.getJSONObject(0).getString("city");
                            officialAddressFinal = officialAddressFinal + ", " + officialCity;
                        }

                        if(officialAddressArray.getJSONObject(0).has("zip")){
                            officialZip = officialAddressArray.getJSONObject(0).getString("zip");
                            officialAddressFinal = officialAddressFinal + ", " + officialZip;
                        }
                        //officialAddressFinal +=

                    }


                    Log.d(TAG, "parseJSON: " + officialIndex);
                    Log.d(TAG, "parseJSON: " + officeName);
                    Log.d(TAG, "parseJSON: " + officialName);
                    Log.d(TAG, "parseJSON: ADDR ARR " + officialAddressArray);
                    Log.d(TAG, "parseJSON: Address Lines[1,2,3]: " + officialAddressL1 + ", "+ officialAddressL2 + ", " + officialCity + " " + officialZip);
                    Log.d(TAG, "parseJSON: Address Final: " + officialAddressFinal);
                    Log.d(TAG, "parseJSON: Party: " + party);
                    Log.d(TAG, "parseJSON: Phone: " + phone);
                    Log.d(TAG, "parseJSON: url: " + url);
                    Log.d(TAG, "parseJSON: email: " + email);
                    Log.d(TAG, "parseJSON: photoUrl: " + photoUrl);
                    Log.d(TAG, "parseJSON: Channels: " + channels);
                    Log.d(TAG, "parseJSON: " + "--------------------");

                    officialArrayList.add(
                     new Official(officialIndex,
                            officeName,
                            officialName,
                           // officialAddressL1 + ", " + officialAddressL2 + ", " + officialCity + " " + officialZip,
                             officialAddressFinal,
                            party,
                            phone,
                            url,
                            email,
                            photoUrl,
                             facebookID,
                             youtubeID,
                             twitterID,
                             normalizedAddress
                    )
                    );
                    photoUrl = null;
                    officialName = null;
                    party = null;
                    officialAddressArray = null;
                    officialAddressL1 = null;
                    officialAddressL2 = null;
                    officialAddressL3 = null;
                    officialAddressFinal = null;
                    officialCity = null;
                    officialZip = null;
                    phone = null;
                }
                //Log.e(TAG, "parseJSON: " + officialIndices);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        Log.e(TAG, "parseJSON: " + officesJ);
//        Log.e(TAG, "parseJSON: " + officialsJ);

//        return new Official(0,
//                "President of the United States",
//                "Joseph R. Biden",
//                "Address",
//                "Demo",
//                "123456789",
//                "wefghj.com",
//                "a@b.com",
//                "https://www.whitehouse.gov/sites/whitehouse.gov/files/images/45/PE%20Color.jpg",
//                stringStringHashMap
//        );
        return officialArrayList;
    }

    @NonNull
    public static String getUrlToUse(String input) {
        Uri.Builder buildURL = Uri.parse(DATA_URL_BASE).buildUpon();
        String urlToUse = buildURL.build().toString();
        urlToUse = urlToUse + apiKey + DATA_URL_ADDR_BASE + input;
        return urlToUse;
    }




}
