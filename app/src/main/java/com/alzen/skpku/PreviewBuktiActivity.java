package com.alzen.skpku;

import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
 * Activity ini digunakan untuk melihat bukti kegiatan di dalam aplikasi.
 * File gambar diambil langsung dari Supabase Storage dalam bentuk byte array,
 * lalu ditampilkan di WebView sebagai base64. Dengan cara ini preview tidak bergantung
 * pada bucket public dan tidak membuka Chrome.
 */
public class PreviewBuktiActivity extends AppCompatActivity {

    private TextView tvPreviewTitle;
    private WebView webViewBukti;
    private Button btnKembaliPreview;

    private String storagePath;
    private String fileName;
    private String fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_bukti);

        tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        webViewBukti = findViewById(R.id.webViewBukti);
        btnKembaliPreview = findViewById(R.id.btnKembaliPreview);

        storagePath = getIntent().getStringExtra("storage_path");
        fileName = getIntent().getStringExtra("file_name");
        fileType = getIntent().getStringExtra("file_type");

        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "Bukti SKP";
        }

        tvPreviewTitle.setText(fileName);
        setupWebView();

        if (storagePath == null || storagePath.trim().isEmpty()) {
            Toast.makeText(this, "Storage path file tidak tersedia", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadFileFromSupabase();
        btnKembaliPreview.setOnClickListener(v -> finish());
    }

    private void setupWebView() {
        WebSettings settings = webViewBukti.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webViewBukti.setWebViewClient(new WebViewClient());
    }

    private void loadFileFromSupabase() {
        showLoadingHtml();

        SupabaseClient.downloadFileBytes(storagePath, new SupabaseClient.SupabaseFileCallback() {
            @Override
            public void onSuccess(byte[] fileBytes, String contentType) {
                runOnUiThread(() -> showFile(fileBytes, contentType));
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    showErrorHtml(errorMessage);
                    Toast.makeText(PreviewBuktiActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showFile(byte[] fileBytes, String contentType) {
        if (fileBytes == null || fileBytes.length == 0) {
            showErrorHtml("File kosong atau tidak bisa dibaca.");
            return;
        }

        String mimeType = getMimeType(contentType);
        String base64 = Base64.encodeToString(fileBytes, Base64.NO_WRAP);

        if ("application/pdf".equals(mimeType)) {
            /*
             * Beberapa Android WebView tidak bisa menampilkan PDF base64.
             * Karena itu, PDF diberi pesan sederhana dan tetap bisa diunduh dari Detail.
             */
            String html = "<html><body style='font-family:sans-serif;background:#f3f4f6;padding:24px;'>"
                    + "<h3>File PDF berhasil ditemukan</h3>"
                    + "<p>Preview PDF tidak selalu didukung oleh WebView Android.</p>"
                    + "<p>Gunakan tombol <b>Download Bukti</b> di halaman detail untuk membuka file PDF.</p>"
                    + "<p>Nama file: " + escapeHtml(fileName) + "</p>"
                    + "</body></html>";
            webViewBukti.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            return;
        }

        String html = "<html>"
                + "<head><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"
                + "<body style='margin:0;padding:12px;background:#f3f4f6;'>"
                + "<img src='data:" + mimeType + ";base64," + base64 + "' "
                + "style='display:block;width:100%;height:auto;border-radius:12px;' />"
                + "</body></html>";

        webViewBukti.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private String getMimeType(String contentType) {
        if (contentType != null) {
            if (contentType.contains("png")) return "image/png";
            if (contentType.contains("pdf")) return "application/pdf";
            if (contentType.contains("jpeg") || contentType.contains("jpg")) return "image/jpeg";
        }

        String lowerName = fileName == null ? "" : fileName.toLowerCase();
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".pdf")) return "application/pdf";
        return "image/jpeg";
    }

    private void showLoadingHtml() {
        String html = "<html><body style='font-family:sans-serif;background:#f3f4f6;padding:24px;'>"
                + "<p>Memuat bukti...</p>"
                + "</body></html>";
        webViewBukti.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private void showErrorHtml(String message) {
        String html = "<html><body style='font-family:sans-serif;background:#f3f4f6;padding:24px;'>"
                + "<h3>Gagal menampilkan bukti</h3>"
                + "<p>" + escapeHtml(message) + "</p>"
                + "</body></html>";
        webViewBukti.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
