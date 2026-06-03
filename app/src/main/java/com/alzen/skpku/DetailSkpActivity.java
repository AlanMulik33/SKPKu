package com.alzen.skpku;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

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

    /*
     * Menghubungkan variable Java dengan komponen XML.
     */
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

    /*
     * Mengambil data SKP yang dikirim dari MainActivity.
     */
    private void getDataFromIntent() {
        skp = (Skp) getIntent().getSerializableExtra("skp");

        if (skp == null) {
            Toast.makeText(this, "Data SKP tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showDetailData();
    }

    /*
     * Menampilkan data SKP ke halaman detail.
     */
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

    /*
     * Menyiapkan aksi tombol di halaman detail.
     */
    private void setupClickActions() {
        btnLihatBukti.setOnClickListener(v -> openProofFile());
        btnDownloadBukti.setOnClickListener(v -> downloadProofFile());
        btnEdit.setOnClickListener(v -> openEditForm());
        btnHapus.setOnClickListener(v -> showDeleteConfirmation());
        btnKembali.setOnClickListener(v -> finish());
    }

    /*
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

    /*
     * Membuka FormSkpActivity dalam mode edit.
     * Data SKP dikirim agar form bisa menampilkan data lama.
     */
    private void openEditForm() {
        Intent intent = new Intent(DetailSkpActivity.this, FormSkpActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("skp", skp);
        startActivity(intent);
        finish();
    }

    /*
     * Dialog konfirmasi sebelum menghapus data.
     * Ini penting agar user tidak menghapus data secara tidak sengaja.
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Data SKP")
                .setMessage("Apakah kamu yakin ingin menghapus data ini? File bukti juga akan dihapus dari storage.")
                .setPositiveButton("Hapus", (dialog, which) -> deleteData())
                .setNegativeButton("Batal", null)
                .show();
    }

    /*
     * Proses hapus data.
     * Jika ada file bukti, hapus file dulu dari Storage.
     * Setelah itu hapus record dari tabel skp_records.
     */
    private void deleteData() {
        if (skp == null || skp.getId() == null || skp.getId().trim().isEmpty()) {
            Toast.makeText(this, "ID data tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        setActionButtonsEnabled(false);

        String storagePath = skp.getStorage_path();

        if (storagePath != null && !storagePath.trim().isEmpty()) {
            SupabaseClient.deleteFile(storagePath, new SupabaseClient.SupabaseCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    deleteRecordFromDatabase();
                }

                @Override
                public void onFailure(String errorMessage) {
                    deleteRecordFromDatabase();
                }
            });
        } else {
            deleteRecordFromDatabase();
        }
    }

    /*
     * Menghapus record data dari tabel Supabase.
     */
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
                    setActionButtonsEnabled(true);
                    Toast.makeText(DetailSkpActivity.this, "Gagal hapus data: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

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

    /*
     * Helper agar tampilan tidak menampilkan null.
     */
    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }
}
