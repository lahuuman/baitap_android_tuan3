package com.example.baitaptuan3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
                finish();
            } else {
                // get data lay duoc tu qr code
                String text = result.getContents();
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                handleScannedData(text);
                finish();
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
        } else if (isUrlQrCode(data)) {
            fetchFinalUrl(data);
        } else {
            Toast.makeText(this, "Dữ liệu không hợp lệ!", Toast.LENGTH_LONG).show();
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


    // url
    private boolean isUrlQrCode(String data){
        if (Patterns.WEB_URL.matcher(data).matches()){
            return true;
        }
        return false;
    }
    private void openUrl(String data) {
        Intent intent = new Intent(this, UrlActivity.class);

        if (data != null && isUrlQrCode(data)) {
            intent.putExtra("url", data);
        } else {
            String err = "URL không hợp lệ hoặc không tồn tại";
            intent.putExtra("error", err);
        }
        startActivity(intent);
    }
    // img
    private boolean isImageUrl(String url) {
        String s = ".*\\.(jpg|jpeg|png|gif|bmp|webp)$";
        return url.matches(s);
    }

    private void openImageUrl(String imageUrl) {
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putExtra("image_url", imageUrl);
        startActivity(intent);
    }

    private void fetchFinalUrl(String shortUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(shortUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode(); // Lấy mã trạng thái
                String finalUrl = connection.getURL().toString(); // Lấy URL cuối cùng
                String contentType = connection.getContentType(); // Lấy loại nội dung

                // In ra mã trạng thái và URL cuối cùng vào log
                Log.d("Response Code", "Mã trạng thái: " + responseCode);
                Log.d("Final URL", "URL cuối cùng: " + finalUrl);

                if (contentType != null && contentType.startsWith("image/")) {
                    openImageUrl(finalUrl);
                } else {
                    openUrl(finalUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi truy cập URL!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}