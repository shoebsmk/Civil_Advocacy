package com.example.civiladvocacy;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class PhotoActivity extends AppCompatActivity {

    private TextView PAAddressTV2;
    private ImageView officialIV2;
    private TextView officialTVP2;
    private TextView officeTVP2;
    ImageView partyLogoIV2;

    private View person;
    private String partyURL;

    private Official official;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        PAAddressTV2 = findViewById(R.id.PAAddressTV2);
        officialIV2 = findViewById(R.id.officialIV2);
        officialTVP2 = findViewById(R.id.officialTVP2);
        officeTVP2 = findViewById(R.id.officeTVP2);
        partyLogoIV2 = findViewById(R.id.partyLogoIV2);
        person = findViewById(R.id.personCL2);

        if (getIntent().hasExtra("OFFICIAL")) {
            official = (Official) getIntent().getSerializableExtra("OFFICIAL");
            populateApiData();
        }
    }

    private void populateApiData() {
        PAAddressTV2.setText(official.getNormalizedAddress());
        setOfficialIV(official);
        officialTVP2.setText(official.getName());
        officeTVP2.setText(official.getOfficeName());
        if (official.getParty().contains("Demo")) {
            person.setBackgroundColor(getColor(R.color.democract_color));
            partyLogoIV2.setImageResource(R.drawable.dem_logo);
            partyURL = "https://democrats.org";
        }
        if (official.getParty().contains("Repub")) {
            person.setBackgroundColor(getColor(R.color.republic_color));
            partyLogoIV2.setImageResource(R.drawable.rep_logo);
            partyURL = "https://www.gop.com";
        }
    }

    public void clickLogo(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(partyURL));

        // Check if there is an app that can handle https intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (https) intents");
        }
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setOfficialIV(Official official) {
        if (official.getPhotoURL() != null) {
            Glide.with(this)
                    .load(official.getPhotoURL())
                    //.load("https://cdn.britannica.com/33/194733-050-4CF75F31/Girl-with-a-Pearl-Earring-canvas-Johannes-1665.jpg")
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //Log.d(TAG, "onLoadFailed: " + e);
                            //holder.officialImageIV.setImageResource(R.drawable.brokenimage);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .error(R.drawable.brokenimage)
                    .into(officialIV2);
        } else {
            officialIV2.setImageResource(R.drawable.missing);
        }


    }
}