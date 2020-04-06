package com.example.releasingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReleasingAdapter extends RecyclerView.Adapter<ReleasingAdapter.ViewHolder> {

    private static final String TAG = "ReleasingAdapter";
    private Context context;
    public ArrayList<Seed> mSeed;

    public ReleasingAdapter(Context context, ArrayList<Seed> mSeed) {
        this.context = context;
        this.mSeed = mSeed;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textVariety, textPalletName, textQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            textVariety = itemView.findViewById(R.id.varietyId);
            textPalletName = itemView.findViewById(R.id.palletName);
            textQuantity = itemView.findViewById(R.id.testView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_released, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ReleasingAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Seed seed = mSeed.get(position);

        viewHolder.textQuantity.setText(seed.getQuantity());
        viewHolder.textVariety.setText(seed.getVariety());
        viewHolder.textPalletName.setText(String.valueOf(seed.getpalletName()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mSeed.size();
    }
}
