package com.example.baitaptuan3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                handleScannedData(text);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleScannedData(String data) {
        // Xử lý dữ liệu quét được
        System.out.println(data);
        // Nếu dữ liệu là một email, bạn có thể gửi email
        // Nếu không, bạn có thể xử lý theo cách khác
        if (isEmailQRCode(data)) {
            if (data.startsWith("MATMSG:")) {
                // Xử lý MATMSG
                String email = getFieldFromMatMsg(data, "TO");
                String subject = getFieldFromMatMsg(data, "SUB");
                String body = getFieldFromMatMsg(data, "BODY");

                // Sử dụng thông tin email
                sendEmail(email, subject, body);
            } else if (data.trim().startsWith("MAILTO:")) {

                String temp[]=data.split(":");
                String email;
                email = temp[1];

                sendEmail(email,"","");
            }
        } else if (isSMSQrCode(data)) {
            sendSMS(data);
        } else {
            // Xử lý dữ liệu không phải email
            Toast.makeText(this, "Dữ liệu quét được: " + data, Toast.LENGTH_LONG).show();
        }
    }


    private void sendEmail(String email,String subject,String body) {
        Intent i=new Intent(this,EmailActivity.class);
        i.putExtra("nguoinhan",email);
        i.putExtra("subject",subject);
        i.putExtra("body",body);
        startActivity(i);
    }
    private String getFieldFromMatMsg(String matmsg, String field) {
        String pattern = field + ":(.*?);";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(matmsg);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void sendSMS(String data) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);

        // Data after scanned Format: "SMSTO:number:messsage"
        String[] splited_datas = data.split(":");

        // Get phone number
        String number = splited_datas[1];
        smsIntent.setData(Uri.parse("smsto:" + number));

        // Lấy message nếu có, tránh lỗi ArrayIndexOutOfBoundsException
        if (splited_datas.length > 2) {
            String message = splited_datas[2];
            smsIntent.putExtra("sms_body", message);
        }

        startActivity(smsIntent);
    }

    public boolean isEmailQRCode(String qrCodeContent) {
        // Kiểm tra xem QR code có phải là định dạng mailto hay không

        // Kiểm tra xem QR code có phải là định dạng MATMSG hay không
        if (qrCodeContent.startsWith("MATMSG:")) {
            return true;
        }
        if(qrCodeContent.startsWith("MAILTO:")){
            return true;
        }

        return false;
    }

    private boolean isSMSQrCode(String data) {
        if (data.startsWith("SMSTO:")) {
            return true;
        }

        return false;
    }
}