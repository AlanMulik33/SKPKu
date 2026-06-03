package com.alzen.skpku;

import android.app.AlertDialog;
<<<<<<< HEAD
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
=======
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

=======
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
/*
 * DetailSkpActivity digunakan untuk menampilkan detail lengkap satu data SKP.
 * Dari halaman ini user bisa melihat bukti, download bukti, edit data, dan hapus data.
 */
public class DetailSkpActivity extends AppCompatActivity {

    private TextView tvDetailNama, tvDetailPoin, tvDetailInfo, tvDetailFile;
    private Button btnLihatBukti, btnDownloadBukti, btnEdit, btnHapus, btnKembali;

    private Skp skp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_skp);

        initViews();
        getDataFromIntent();
        setupClickActions();
    }

<<<<<<< HEAD
=======
    /*
     * Menghubungkan variable Java dengan komponen XML.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void initViews() {
        tvDetailNama = findViewById(R.id.tvDetailNama);
        tvDetailPoin = findViewById(R.id.tvDetailPoin);
        tvDetailInfo = findViewById(R.id.tvDetailInfo);
        tvDetailFile = findViewById(R.id.tvDetailFile);

        btnLihatBukti = findViewById(R.id.btnLihatBukti);
        btnDownloadBukti = findViewById(R.id.btnDownloadBukti);
        btnEdit = findViewById(R.id.btnEdit);
        btnHapus = findViewById(R.id.btnHapus);
        btnKembali = findViewById(R.id.btnKembali);
    }

<<<<<<< HEAD
=======
    /*
     * Mengambil data SKP yang dikirim dari MainActivity.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void getDataFromIntent() {
        skp = (Skp) getIntent().getSerializableExtra("skp");

        if (skp == null) {
            Toast.makeText(this, "Data SKP tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showDetailData();
    }

<<<<<<< HEAD
=======
    /*
     * Menampilkan data SKP ke halaman detail.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void showDetailData() {
        tvDetailNama.setText(skp.getNama_kegiatan());
        tvDetailPoin.setText(skp.getPoin_skp() + " Poin");

        String info =
                "Kategori: " + safeText(skp.getKategori_bidang()) +
                        "\nJenis: " + safeText(skp.getJenis_kegiatan()) +
                        "\nTingkat: " + safeText(skp.getTingkat()) +
                        "\nPeran: " + safeText(skp.getPeran()) +
                        "\nMode: " + safeText(skp.getMode_kegiatan()) +
                        "\nTanggal: " + safeText(skp.getTanggal_input());

        tvDetailInfo.setText(info);

        if (skp.getFile_name() == null || skp.getFile_name().trim().isEmpty()) {
            tvDetailFile.setText("Tidak ada file bukti");
            btnLihatBukti.setEnabled(false);
            btnDownloadBukti.setEnabled(false);
        } else {
            tvDetailFile.setText(skp.getFile_name());
            btnLihatBukti.setEnabled(true);
            btnDownloadBukti.setEnabled(true);
        }
    }

<<<<<<< HEAD
    private void setupClickActions() {
        btnLihatBukti.setOnClickListener(v -> openProofFile());
        btnDownloadBukti.setOnClickListener(v -> downloadProofFile());
        btnEdit.setOnClickListener(v -> openEditForm());
        btnHapus.setOnClickListener(v -> showDeleteConfirmation());
=======
    /*
     * Menyiapkan aksi tombol di halaman detail.
     */
    private void setupClickActions() {
        btnLihatBukti.setOnClickListener(v -> openProofFile());

        btnDownloadBukti.setOnClickListener(v -> downloadProofFile());

        btnEdit.setOnClickListener(v -> openEditForm());

        btnHapus.setOnClickListener(v -> showDeleteConfirmation());

>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
        btnKembali.setOnClickListener(v -> finish());
    }

    /*
<<<<<<< HEAD
     * Preview bukti dibuka di dalam aplikasi.
     * Yang dikirim adalah storage_path, bukan file_url, agar tidak bermasalah jika URL lama salah.
     */
    private void openProofFile() {
        if (!hasStoragePath()) {
            Toast.makeText(this, "Storage path file bukti tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DetailSkpActivity.this, PreviewBuktiActivity.class);
        intent.putExtra("storage_path", skp.getStorage_path());
        intent.putExtra("file_name", skp.getFile_name());
        intent.putExtra("file_type", skp.getFile_type());
        startActivity(intent);
    }

    /*
     * Download bukti tidak memakai DownloadManager lagi.
     * File diambil lewat OkHttp + auth header, lalu disimpan manual ke folder Downloads.
     */
    private void downloadProofFile() {
        if (!hasStoragePath()) {
            Toast.makeText(this, "Storage path file bukti tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        setActionButtonsEnabled(false);
        Toast.makeText(this, "Mengunduh bukti...", Toast.LENGTH_SHORT).show();

        SupabaseClient.downloadFileBytes(skp.getStorage_path(), new SupabaseClient.SupabaseFileCallback() {
            @Override
            public void onSuccess(byte[] fileBytes, String contentType) {
                runOnUiThread(() -> {
                    try {
                        saveFileToDownloads(fileBytes, getSafeFileName(), getMimeType(contentType));
                        Toast.makeText(DetailSkpActivity.this, "Bukti berhasil disimpan di folder Downloads", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(DetailSkpActivity.this, "Gagal simpan file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        setActionButtonsEnabled(true);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    setActionButtonsEnabled(true);
                    Toast.makeText(DetailSkpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void saveFileToDownloads(byte[] fileBytes, String fileName, String mimeType) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);

            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                throw new Exception("Gagal membuat file di Downloads");
            }

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                throw new Exception("Gagal membuka output file");
            }

            outputStream.write(fileBytes);
            outputStream.flush();
            outputStream.close();

            values.clear();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            getContentResolver().update(uri, values, null, null);
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File outputFile = new File(downloadsDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(fileBytes);
            outputStream.flush();
            outputStream.close();
        }
    }

=======
     * Membuka file bukti menggunakan aplikasi bawaan HP.
     * Jika file PDF, akan dibuka di PDF viewer/browser.
     * Jika gambar, akan dibuka di browser/gallery yang mendukung URL.
     */
    private void openProofFile() {
        if (skp == null || skp.getFile_url() == null || skp.getFile_url().trim().isEmpty()) {
            Toast.makeText(this, "URL file bukti tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(skp.getFile_url()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak bisa membuka file bukti", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Download file bukti ke folder Downloads HP menggunakan DownloadManager.
     */
    private void downloadProofFile() {
        if (skp == null || skp.getFile_url() == null || skp.getFile_url().trim().isEmpty()) {
            Toast.makeText(this, "URL file bukti tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = skp.getFile_name();

            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "bukti_skp";
            }

            /*
             * DownloadManager adalah fitur bawaan Android.
             * Sistem akan menampilkan notifikasi saat download selesai.
             */
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(skp.getFile_url()));
            request.setTitle("Download Bukti SKP");
            request.setDescription(fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            if (downloadManager != null) {
                downloadManager.enqueue(request);
                Toast.makeText(this, "Download dimulai. Cek folder Downloads.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "DownloadManager tidak tersedia", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Gagal download: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Membuka FormSkpActivity dalam mode edit.
     * Data SKP dikirim agar form bisa menampilkan data lama.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void openEditForm() {
        Intent intent = new Intent(DetailSkpActivity.this, FormSkpActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("skp", skp);
        startActivity(intent);
<<<<<<< HEAD

        finish();
    }

=======
    }

    /*
     * Dialog konfirmasi sebelum menghapus data.
     * Ini penting agar user tidak menghapus data secara tidak sengaja.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Data SKP")
                .setMessage("Apakah kamu yakin ingin menghapus data ini? File bukti juga akan dihapus dari storage.")
                .setPositiveButton("Hapus", (dialog, which) -> deleteData())
                .setNegativeButton("Batal", null)
                .show();
    }

<<<<<<< HEAD
=======
    /*
     * Proses hapus data.
     * Jika ada file bukti, hapus file dulu dari Storage.
     * Setelah itu hapus record dari tabel skp_records.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void deleteData() {
        if (skp == null || skp.getId() == null || skp.getId().trim().isEmpty()) {
            Toast.makeText(this, "ID data tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

<<<<<<< HEAD
        setActionButtonsEnabled(false);
=======
        btnHapus.setEnabled(false);
        btnEdit.setEnabled(false);
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93

        String storagePath = skp.getStorage_path();

        if (storagePath != null && !storagePath.trim().isEmpty()) {
<<<<<<< HEAD
=======
            /*
             * Hapus file dari Supabase Storage terlebih dahulu.
             * Jika hapus file gagal, record database tetap akan dihapus agar data tidak menggantung di aplikasi.
             */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
            SupabaseClient.deleteFile(storagePath, new SupabaseClient.SupabaseCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    deleteRecordFromDatabase();
                }

                @Override
                public void onFailure(String errorMessage) {
<<<<<<< HEAD
=======
                    /*
                     * Jika file gagal dihapus, kita tetap lanjut hapus database.
                     * Ini dibuat agar fitur Delete tetap berjalan saat demo.
                     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
                    deleteRecordFromDatabase();
                }
            });
        } else {
            deleteRecordFromDatabase();
        }
    }

<<<<<<< HEAD
=======
    /*
     * Menghapus record data dari tabel Supabase.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private void deleteRecordFromDatabase() {
        SupabaseClient.deleteSkpRecord(skp.getId(), new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                runOnUiThread(() -> {
                    Toast.makeText(DetailSkpActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
<<<<<<< HEAD
                    setActionButtonsEnabled(true);
=======
                    btnHapus.setEnabled(true);
                    btnEdit.setEnabled(true);
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
                    Toast.makeText(DetailSkpActivity.this, "Gagal hapus data: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

<<<<<<< HEAD
    private boolean hasStoragePath() {
        return skp != null && skp.getStorage_path() != null && !skp.getStorage_path().trim().isEmpty();
    }

    private String getSafeFileName() {
        String fileName = skp.getFile_name();

        if (fileName == null || fileName.trim().isEmpty()) {
            if ("pdf".equalsIgnoreCase(skp.getFile_type())) {
                return "bukti_skp.pdf";
            }
            return "bukti_skp.jpg";
        }

        return fileName;
    }

    private String getMimeType(String contentType) {
        if (contentType != null) {
            if (contentType.contains("png")) return "image/png";
            if (contentType.contains("pdf")) return "application/pdf";
            if (contentType.contains("jpeg") || contentType.contains("jpg")) return "image/jpeg";
        }

        String name = getSafeFileName().toLowerCase();
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".pdf")) return "application/pdf";
        return "image/jpeg";
    }

    private void setActionButtonsEnabled(boolean enabled) {
        btnLihatBukti.setEnabled(enabled);
        btnDownloadBukti.setEnabled(enabled);
        btnEdit.setEnabled(enabled);
        btnHapus.setEnabled(enabled);
        btnKembali.setEnabled(enabled);
    }

=======
    /*
     * Helper agar tampilan tidak menampilkan null.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
<<<<<<< HEAD
        return value;
    }
}
=======

        return value;
    }
}
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
