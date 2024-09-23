package com.example.baitaptuan3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        TextView textViewUrl = findViewById(R.id.textViewUrl);
        Button buttonViewImage = findViewById(R.id.buttonVisit);
        Button buttonDownload = findViewById(R.id.btnInstallImage);

        // Nhận URL từ Intent
        imageUrl = getIntent().getStringExtra("image_url");
        textViewUrl.setText(imageUrl);

        // Hành động cho nút "Xem ảnh"
        buttonViewImage.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
            startActivity(browserIntent);
        });

//        buttonDownload.setOnClickListener(v -> downloadImage(imageUrl));
    }
}