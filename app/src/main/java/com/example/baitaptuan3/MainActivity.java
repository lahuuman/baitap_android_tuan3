package com.example.baitaptuan3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_CONTACTS = 1;
    private Object ActivityResultLauncher;
    ActivityResultLauncher<Intent> luanch;
    String contactName,contactNumber;
    private Button btnContacts,btn_view_contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnContacts = findViewById(R.id.bt_contact);
        btn_view_contact=findViewById(R.id.btn_view_contact);
        btn_view_contact.setEnabled(false);
        luanch=registerForActivityResult(
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
//            if (ContextCompat.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_PERMISSION);
//            } else {
//                openContacts(); // Nếu có quyền, mở danh bạ
//            }
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
                    addContactToPhone(contactName, contactNumber);
                }

            }
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
//    private void showAlertDialog() {
//        // Tạo builder cho AlertDialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//        // Thiết lập tiêu đề và thông điệp cho hộp thoại
//        builder.setTitle("Thông báo");
//        builder.setMessage("Name: " + contactName + "\nPhone: " + contactNumber);
////        System.out.println("vo dialog"+ contactName + contactNumber);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
private void addContactToPhone(String contactName, String contactNumber) {
        System.out.println("Vao contact" + contactName);
    ContentValues values = new ContentValues();

    // Thêm liên hệ vào RawContacts
    Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
    long rawContactId = ContentUris.parseId(rawContactUri);

    // Thêm tên
    values.clear();
    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
    values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName);
    getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

    // Thêm số điện thoại
    values.clear();
    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber);
    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
    getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
}
}
