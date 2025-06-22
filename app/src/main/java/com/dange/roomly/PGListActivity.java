// PGListActivity.java
package com.dange.roomly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dange.roomly.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PGListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PGAdapter adapter;
    ArrayList<PGModel> pgList = new ArrayList<>();
    String URL = "http://192.168.31.131/Roomly/get_pg.php"; // Replace with your IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg_list);

        FloatingActionButton fabAddPg;

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PGAdapter(this, pgList);
        recyclerView.setAdapter(adapter);

        fabAddPg = findViewById(R.id.fab_add_pg);


        fabAddPg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the PG upload activity
                Intent intent = new Intent(PGListActivity.this, UploadPGActivity.class); // replace with your class
                startActivity(intent);
            }
        });

        loadPGData();
    }

    void loadPGData() {
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pgList.clear();
                        try {
                            JSONArray pgArray = response.getJSONArray("pg_list");
                            for (int i = 0; i < pgArray.length(); i++) {
                                JSONObject obj = pgArray.getJSONObject(i);
                                PGModel pg = new PGModel(
                                        obj.getInt("id"),
                                        obj.getString("pg_name"),
                                        obj.getString("pg_type"),
                                        obj.getString("city"),
                                        obj.getString("description"),
                                        obj.getString("owner_name"),
                                        obj.getString("owner_phone"),
                                        obj.getDouble("latitude"),
                                        obj.getDouble("longitude"),
                                        obj.getString("image1"),
                                        obj.getString("image2"),
                                        obj.getString("image3"),
                                        obj.optString("pg_rent", "N/A") // Safe access to pg_rent

                                );
                                pgList.add(pg);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }



}

