package com.example.baitaptuan3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanQRCode();
    }

    private void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code with email");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // get data lay duoc tu qr code
                String text = result.getContents();
                handleScannedData(text);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void handleScannedData(String data) {
        // Xử lý dữ liệu quét được
        // Nếu dữ liệu là một email, bạn có thể gửi email
        // Nếu không, bạn có thể xử lý theo cách khác
        if (data.toLowerCase().startsWith("mailto:")) {
            // Gọi hàm gửi email
            sendEmail(data);
        } else {
            // Xử lý dữ liệu không phải email
            Toast.makeText(this, "Dữ liệu quét được: " + data, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidEmail(String email) {
        // Kiểm tra tính hợp lệ của email (có thể sử dụng regex hoặc cách khác)
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendEmail(String email) {
        Intent i=new Intent(this,EmailActivity.class);
        i.putExtra("nguoinhan",email);
        startActivity(i);
    }
}