package com.alzen.skpku;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private void openEditForm() {
        Intent intent = new Intent(DetailSkpActivity.this, FormSkpActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("skp", skp);
        startActivity(intent);
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

        btnHapus.setEnabled(false);
        btnEdit.setEnabled(false);

        String storagePath = skp.getStorage_path();

        if (storagePath != null && !storagePath.trim().isEmpty()) {
            /*
             * Hapus file dari Supabase Storage terlebih dahulu.
             * Jika hapus file gagal, record database tetap akan dihapus agar data tidak menggantung di aplikasi.
             */
            SupabaseClient.deleteFile(storagePath, new SupabaseClient.SupabaseCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    deleteRecordFromDatabase();
                }

                @Override
                public void onFailure(String errorMessage) {
                    /*
                     * Jika file gagal dihapus, kita tetap lanjut hapus database.
                     * Ini dibuat agar fitur Delete tetap berjalan saat demo.
                     */
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
                    btnHapus.setEnabled(true);
                    btnEdit.setEnabled(true);
                    Toast.makeText(DetailSkpActivity.this, "Gagal hapus data: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
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