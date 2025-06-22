package com.dange.roomly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class PGAdapter extends RecyclerView.Adapter<PGAdapter.PGViewHolder> {

    Context context;
    ArrayList<PGModel> list;

    PGAdapter(Context context, ArrayList<PGModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PGViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pg, parent, false);
        return new PGViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PGViewHolder holder, int position) {
        PGModel pg = list.get(position);
        holder.pgName.setText(pg.pg_name);
        holder.pgCity.setText(pg.city);
        holder.pgRent.setText(pg.pg_rent);
        String imageUrl = "http://192.168.1.5/Roomly/uploads/" + pg.image1;
        Glide.with(context).load(imageUrl).into(holder.pgImage);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, PGDetailActivity.class);
            i.putExtra("pg", pg);
            context.startActivity(i);
        });

        String rentText = "Rent: " +
                (pg.pg_rent != null && !pg.pg_rent.trim().isEmpty() && !pg.pg_rent.equalsIgnoreCase("N/A")
                        ? "₹" + pg.pg_rent
                        : "Contact for price");
        holder.pgRent.setText(rentText);

        holder.pgName.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + pg.owner_phone));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PGViewHolder extends RecyclerView.ViewHolder {
        TextView pgName, pgCity, pgRent;
        ImageView pgImage;
        public PGViewHolder(@NonNull View itemView) {
            super(itemView);
            pgName = itemView.findViewById(R.id.pgName);
            pgCity = itemView.findViewById(R.id.pgCity);
            pgRent = itemView.findViewById(R.id.pgRent);
            pgImage = itemView.findViewById(R.id.pgImage);
        }
    }
}