package com.example.civiladvocacy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficeViewHolder extends RecyclerView.ViewHolder {

    public TextView officeNameTV;
    public TextView officialNameTV;
    public TextView officialPartyTV;
    public ImageView officialImageIV;


    public OfficeViewHolder(@NonNull View itemView) {
        super(itemView);
        officeNameTV = itemView.findViewById(R.id.officeNameTV);
        officialNameTV = itemView.findViewById(R.id.officialNameTV);
        officialPartyTV = itemView.findViewById(R.id.officialPartyTV);
        officialImageIV = itemView.findViewById(R.id.officiaImageIV);

    }
}
