package com.example.baitaptuan3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmailActivity extends AppCompatActivity {

    private EditText editTextSubject, editTextBody, editTextSender;
    private String recipient;
    private TextView editTextRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);

        Intent data = getIntent();
        if (data.hasExtra("nguoinhan")) {
            recipient = data.getStringExtra("nguoinhan");
            recipient = recipient.substring(7);
        } else {
            recipient = "lahuuminh678@gmail.com";
        }
        // Tham chiếu đến các trường nhập liệu

        editTextSubject = findViewById(R.id.editTextSubject);
        editTextBody = findViewById(R.id.editTextBody);
        editTextRecipient = findViewById(R.id.editTextRecipient);
        String temp = "Recipient: " + recipient;
        editTextRecipient.setText(temp);
        Button buttonSend = findViewById(R.id.buttonSend);

        // Xử lý sự kiện khi người dùng nhấn vào nút Gửi Email
        buttonSend.setOnClickListener(
                v -> {
                    // Lấy dữ liệu từ các trường nhập
                    String subject = editTextSubject.getText().toString();
                    String body = editTextBody.getText().toString();
                    // Kiểm tra xem người nhận có được nhập không
                    openEmailApp(recipient, subject, body);


                });
    }

    // Hàm để mở ứng dụng email và điền dữ liệu
    private void openEmailApp(String recipient, String subject, String body) {
        // Tạo URI "mailto:" để mở ứng dụng email với địa chỉ người nhận
        Uri uri = Uri.parse("mailto:" + recipient +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body));

        // Tạo Intent với ACTION_SENDTO và URI "mailto:"
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);

        // Thêm chủ đề và nội dung email vào intent

        try {
            // Khởi chạy Intent và mở ứng dụng email
            startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            // Thông báo nếu không có ứng dụng email trên thiết bị
            Toast.makeText(this, "Không tìm thấy ứng dụng email.", Toast.LENGTH_SHORT).show();
        }
    }
}