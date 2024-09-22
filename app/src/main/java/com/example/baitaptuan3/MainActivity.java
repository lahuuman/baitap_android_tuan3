package com.example.baitaptuan3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_CONTACTS = 1;
    private Object ActivityResultLauncher;
    ActivityResultLauncher<Intent> luanch;
    String contactName, contactNumber;
    private Button btnContacts, btn_view_contact, btn_email, btn_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Views
        btnContacts = findViewById(R.id.bt_contact);
        btn_view_contact = findViewById(R.id.btn_view_contact);
        btn_view_contact.setEnabled(false);

        luanch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri contactUri = data.getData();
//                            addContactToPhone(contactName, contactNumber);
                            retrieveContactInfo(contactUri);
                            btn_view_contact.setEnabled(true);
                        }
                    }

                });

        btnContacts.setOnClickListener(v -> {
            // Kiểm tra quyền truy cập danh bạ
            if (ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Nếu chưa được cấp, yêu cầu quyền
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_CODE_WRITE_CONTACTS);
            } else {
                // Nếu quyền đã được cấp, thực hiện việc thêm danh bạ
                openContacts();
            }
        });

        btn_view_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Nếu chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE_WRITE_CONTACTS);
                } else {
                    // Nếu quyền đã được cấp, thực hiện việc thêm danh bạ
                    showContactDetails(contactName, contactNumber);
                }

            }
        });

        // camera
        btn_camera = findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(V -> {
            Intent i = new Intent(this, QRCodeScannerActivity.class);
            startActivity(i);
        });

        // email
        btn_email = findViewById(R.id.btn_email);
        btn_email.setOnClickListener(V -> {
            Intent i = new Intent(this, EmailActivity.class);
            startActivity(i);
        });
    }

    private void openContacts() {
        // Tạo Intent để mở danh bạ
//        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        luanch.launch(intent); // Khởi chạy danh bạ
    }

    private void retrieveContactInfo(Uri contactUri) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                contactName = cursor.getString(nameIndex);
                contactNumber = cursor.getString(numberIndex);

                // Hiển thị tên và số điện thoại đã chọn
            }
        }
    }

    private void showContactDetails(String contactName, String contactNumber) {
        Uri contactUri = null;

        // Truy vấn danh bạ để tìm liên hệ dựa trên tên và số điện thoại
        ContentResolver resolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        String[] selectionArgs = new String[]{contactName, contactNumber};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Kiểm tra nếu cột CONTACT_ID tồn tại
            int contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            if (contactIdIndex != -1) {
                // Lấy ID liên hệ từ kết quả truy vấn
                long contactId = cursor.getLong(contactIdIndex);

                // Tạo URI để mở chi tiết liên hệ
                contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
            }
            cursor.close();
        }

        if (contactUri != null) {
            // Tạo intent để mở ứng dụng danh bạ với thông tin liên hệ
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(contactUri);
            startActivity(intent);
        } else {
            // Nếu không tìm thấy liên hệ, hiển thị thông báo
            Toast.makeText(this, "Không tìm thấy liên hệ với tên: " + contactName + " và số: " + contactNumber, Toast.LENGTH_SHORT).show();
        }
    }
}
