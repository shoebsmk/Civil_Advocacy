package com.example.civiladvocacy;

import static android.content.ContentValues.TAG;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class OfficeAdapter extends RecyclerView.Adapter<OfficeViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Official> officialArrayList;

    public OfficeAdapter(MainActivity mainActivity, ArrayList<Official> officialArrayList) {
        this.mainActivity = mainActivity;
        this.officialArrayList = officialArrayList;
    }

    @NonNull
    @Override
    public OfficeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.office_entry, parent, false);
        itemView.setOnClickListener(mainActivity);
        return new OfficeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficeViewHolder holder, int position) {
        Official official = officialArrayList.get(position);
        holder.officeNameTV.setText(official.getOfficeName());
        holder.officialNameTV.setText(official.getName());
        holder.officialPartyTV.setText("(" + official.getParty() + ")");
        if (official.getPhotoURL() != null) {

            Glide.with(mainActivity)
                    .load(official.getPhotoURL())
                    //.load("https://cdn.britannica.com/33/194733-050-4CF75F31/Girl-with-a-Pearl-Earring-canvas-Johannes-1665.jpg")
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .error(R.drawable.brokenimage)
                    .into(holder.officialImageIV);

        } else {
            holder.officialImageIV.setImageResource(R.drawable.missing);
        }

//        Log.e(TAG, "onBindViewHolder: " + official.getPhotoURL());
    }

    @Override
    public int getItemCount() {
        return officialArrayList.size();
    }
}
