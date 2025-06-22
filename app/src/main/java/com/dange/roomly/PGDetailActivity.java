package com.dange.roomly;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PGDetailActivity extends AppCompatActivity {

    ImageView img1, img2, img3;
    TextView pgName, pgType, city, description, ownerName, ownerPhone, pgRent;
    Button btnCall, btnNavigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg_detail);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        pgName = findViewById(R.id.pgName);
        pgRent = findViewById(R.id.pgRent);
        pgType = findViewById(R.id.pgType);
        city = findViewById(R.id.city);
        description = findViewById(R.id.description);
        ownerName = findViewById(R.id.ownerName);
        ownerPhone = findViewById(R.id.ownerPhone);
        btnCall = findViewById(R.id.btnCall);
        btnNavigate = findViewById(R.id.btnNavigate);

        PGModel pg = (PGModel) getIntent().getSerializableExtra("pg");

        pgName.setText(pg.pg_name);
        pgRent.setText(pg.pg_rent);
        pgType.setText(pg.pg_type);
        city.setText(pg.city);
        description.setText(pg.description);
        ownerName.setText(pg.owner_name);
        ownerPhone.setText(pg.owner_phone);

        String rentText = pg.pg_rent != null && !pg.pg_rent.trim().isEmpty() && !pg.pg_rent.equalsIgnoreCase("N/A")
                ? "₹" + pg.pg_rent
                : "Contact for price";
        pgRent.setText(rentText);


        Glide.with(this).load("http://192.168.1.5/Roomly/uploads/" + pg.image1).into(img1);
        Glide.with(this).load("http://192.168.1.5/Roomly/uploads/" + pg.image2).into(img2);
        Glide.with(this).load("http://192.168.1.5/Roomly/uploads/" + pg.image3).into(img3);

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + pg.owner_phone));
            startActivity(intent);
        });

        btnNavigate.setOnClickListener(v -> {
            String geo = "geo:" + pg.latitude + "," + pg.longitude + "?q=" + pg.pg_name;
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
    }
}