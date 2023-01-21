package com.example.civiladvocacy;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;

public class Official implements Serializable {
    private int id;
    private String officeName; //from array
    private String name;
    private String address; //from address array
    private String party;
    private String phone; //from phones
    private String url; //from urls
    private String email;
    private String photoURL; //from photo url
    private String normalizedAddress; // Convrt to string
    //private ImageView iv; //from photo url

    public String getNormalizedAddress() {
        return normalizedAddress;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public String getYoutubeID() {
        return youtubeID;
    }

    public String getTwitterID() {
        return twitterID;
    }

    //private HashMap<String, String> channels;
    private String facebookID;
    private String youtubeID;
    private String twitterID;

//    public JSONObject toJSON() throws JSONException {
//        JSONObject stockJSON = new JSONObject();
//        stockJSON.put("stockSymbol",stockSymbol);
//        stockJSON.put("companyName", companyName);
//        return stockJSON;
//    }



    public static Official ToOfficial(JSONObject officialJSON) throws JSONException, ParseException {
        Log.d(TAG, "ToOfficial: " + officialJSON);
//        String officeName = officialJSON.getString("stockSymbol");
//        String name = officialJSON.getString("companyName");
//        Log.d("TAG", "ToStock: "+ stockSymbol + " "+  companyName);
//        return new Official();
        return null;
    }

    public int getId() {
        return id;
    }

    public String getOfficeName() {
        return officeName;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getParty() {
        return party;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

//    public HashMap<String, String> getChannels() {
//        return channels;
//    }

    public Official(int id, String officeName, String name, String address, String party, String phone, String url,
                    String email, String photoURL, String facebookID, String youtubeID, String twitterID, String normalizedAddress) {
        this.id = id;
        this.officeName = officeName;
        this.name = name;
        this.address = address;
        this.party = party;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photoURL = photoURL;
        //this.channels = channels;
        this.facebookID = facebookID;
        this.youtubeID = youtubeID;
        this.twitterID = twitterID;
        this.normalizedAddress = normalizedAddress;
    }
}
