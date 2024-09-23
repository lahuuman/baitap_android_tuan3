package com.example.baitaptuan3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UrlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        String url = getIntent().getStringExtra("url");
        String error = getIntent().getStringExtra("error");

        TextView textViewUrl = findViewById(R.id.textViewUrl);
        Button buttonVisit = findViewById(R.id.buttonVisit);

        if (error != null) {
            textViewUrl.setText(error);
            buttonVisit.setOnClickListener(v -> {
                Toast.makeText(this, "URL không hợp lệ", Toast.LENGTH_SHORT).show();
            });
        } else {
            textViewUrl.setText(url);
            buttonVisit.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            });
        }
    }
}