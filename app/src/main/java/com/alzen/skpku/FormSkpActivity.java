package com.alzen.skpku;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * FormSkpActivity digunakan untuk tambah dan edit data SKP.
 * Halaman ini memakai spinner bertingkat agar user tidak mengisi data secara bebas.
 */
public class FormSkpActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_FILE = 101;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private TextView tvFormTitle, tvPoinOtomatis, tvFileName, tvLabelMode;
    private Spinner spKategori, spKegiatan, spTingkat, spPeran, spMode;
    private Button btnPilihFile, btnSimpan, btnBatal;
    private ProgressBar progressUpload;
    private String userKey = "";

    private String selectedKategori = "";
    private String selectedKegiatan = "";
    private String selectedTingkat = "";
    private String selectedPeran = "";
    private String selectedMode = SkpRule.MODE_TIDAK_ADA;
    private int selectedPoin = 0;

    private Uri selectedFileUri = null;
    private String selectedFileName = "";
    private String selectedMimeType = "";

    private boolean isEditMode = false;
    private Skp editSkp = null;

    /*
     * Data file lama dipakai saat mode edit.
     * Jika user tidak memilih file baru, file lama tetap dipakai.
     */
    private String existingFileUrl = "";
    private String existingFileName = "";
    private String existingFileType = "";
    private String existingStoragePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_skp);
        userKey = getOrCreateUserKey();

        initViews();
        checkMode();
        setupKategoriSpinner();
        setupClickActions();
    }
    private String getOrCreateUserKey() {
        SharedPreferences preferences = getSharedPreferences("SKPKU_PREF", MODE_PRIVATE);
        String key = preferences.getString("user_key", "");

        if (key == null || key.trim().isEmpty()) {
            key = UUID.randomUUID().toString();

            preferences.edit()
                    .putString("user_key", key)
                    .apply();
        }

        return key;
    }

    /*
     * Menghubungkan variable Java dengan komponen XML.
     */
    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        tvPoinOtomatis = findViewById(R.id.tvPoinOtomatis);
        tvFileName = findViewById(R.id.tvFileName);
        tvLabelMode = findViewById(R.id.tvLabelMode);

        spKategori = findViewById(R.id.spKategori);
        spKegiatan = findViewById(R.id.spKegiatan);
        spTingkat = findViewById(R.id.spTingkat);
        spPeran = findViewById(R.id.spPeran);
        spMode = findViewById(R.id.spMode);

        btnPilihFile = findViewById(R.id.btnPilihFile);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnBatal = findViewById(R.id.btnBatal);

        progressUpload = findViewById(R.id.progressUpload);
    }

    /*
     * Mengecek apakah halaman dibuka untuk tambah data atau edit data.
     */
    private void checkMode() {
        String mode = getIntent().getStringExtra("mode");

        if ("edit".equalsIgnoreCase(mode)) {
            isEditMode = true;
            editSkp = (Skp) getIntent().getSerializableExtra("skp");

            tvFormTitle.setText("Edit Data SKP");

            if (editSkp != null) {
                existingFileUrl = editSkp.getFile_url();
                existingFileName = editSkp.getFile_name();
                existingFileType = editSkp.getFile_type();
                existingStoragePath = editSkp.getStorage_path();

                if (existingFileName != null && !existingFileName.isEmpty()) {
                    tvFileName.setText("File saat ini: " + existingFileName);
                }
            }
        } else {
            isEditMode = false;
            tvFormTitle.setText("Tambah Data SKP");
        }
    }

    /*
     * Spinner pertama: kategori bidang.
     */
    private void setupKategoriSpinner() {
        List<String> kategoriList = SkpRule.getKategoriList();

        setSpinnerData(spKategori, kategoriList);

        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKategori = kategoriList.get(position);
                setupKegiatanSpinner();

                /*
                 * Saat mode edit, spinner akan diarahkan ke data lama.
                 * Ini hanya dijalankan setelah spinner pertama selesai dibuat.
                 */
                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(spKategori, editSkp.getKategori_bidang());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (isEditMode && editSkp != null) {
            setSpinnerSelection(spKategori, editSkp.getKategori_bidang());
        }
    }

    /*
     * Spinner kedua: nama kegiatan.
     * Isi spinner ini tergantung kategori yang dipilih.
     */
    private void setupKegiatanSpinner() {
        List<String> kegiatanList = SkpRule.getKegiatanList(selectedKategori);
        setSpinnerData(spKegiatan, kegiatanList);

        spKegiatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKegiatan = kegiatanList.get(position);
                setupTingkatSpinner();

                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(spKegiatan, editSkp.getNama_kegiatan());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (isEditMode && editSkp != null) {
            setSpinnerSelection(spKegiatan, editSkp.getNama_kegiatan());
        }
    }

    /*
     * Spinner ketiga: tingkat kegiatan.
     */
    private void setupTingkatSpinner() {
        List<String> tingkatList = SkpRule.getTingkatList(selectedKategori, selectedKegiatan);
        setSpinnerData(spTingkat, tingkatList);

        spTingkat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTingkat = tingkatList.get(position);
                setupPeranSpinner();

                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(spTingkat, editSkp.getTingkat());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (tingkatList.size() == 1) {
            spTingkat.setEnabled(false);
        } else {
            spTingkat.setEnabled(true);
        }

        if (isEditMode && editSkp != null) {
            setSpinnerSelection(spTingkat, editSkp.getTingkat());
        }
    }

    /*
     * Spinner keempat: peran / partisipasi / jabatan.
     */
    private void setupPeranSpinner() {
        List<String> peranList = SkpRule.getPeranList(selectedKategori, selectedKegiatan, selectedTingkat);
        setSpinnerData(spPeran, peranList);

        spPeran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPeran = peranList.get(position);
                setupModeSpinner();

                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(spPeran, editSkp.getPeran());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (peranList.size() == 1) {
            spPeran.setEnabled(false);
        } else {
            spPeran.setEnabled(true);
        }

        if (isEditMode && editSkp != null) {
            setSpinnerSelection(spPeran, editSkp.getPeran());
        }
    }

    /*
     * Spinner kelima: mode kegiatan.
     * Mode hanya aktif untuk kegiatan tertentu, misalnya Forum Ilmiah sebagai peserta.
     */
    private void setupModeSpinner() {
        List<String> modeList = SkpRule.getModeList(
                selectedKategori,
                selectedKegiatan,
                selectedTingkat,
                selectedPeran
        );

        setSpinnerData(spMode, modeList);

        spMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMode = modeList.get(position);
                calculatePointNow();

                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(spMode, editSkp.getMode_kegiatan());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (modeList.size() == 1 && SkpRule.MODE_TIDAK_ADA.equals(modeList.get(0))) {
            spMode.setEnabled(false);
            tvLabelMode.setText("Mode Kegiatan");
        } else {
            spMode.setEnabled(true);
            tvLabelMode.setText("Mode Kegiatan");
        }

        if (isEditMode && editSkp != null) {
            setSpinnerSelection(spMode, editSkp.getMode_kegiatan());
        }

        calculatePointNow();
    }

    /*
     * Menghitung poin otomatis berdasarkan pilihan spinner saat ini.
     */
    private void calculatePointNow() {
        selectedPoin = SkpRule.calculatePoint(
                selectedKategori,
                selectedKegiatan,
                selectedTingkat,
                selectedPeran,
                selectedMode
        );

        tvPoinOtomatis.setText(selectedPoin + " Poin");
    }

    /*
     * Aksi tombol pilih file, simpan, dan batal.
     */
    private void setupClickActions() {
        btnPilihFile.setOnClickListener(v -> openFilePicker());

        btnSimpan.setOnClickListener(v -> validateBeforeSave());

        btnBatal.setOnClickListener(v -> finish());
    }

    /*
     * Membuka file picker Android.
     * User hanya bisa memilih gambar atau PDF.
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "image/jpeg",
                "image/png",
                "application/pdf"
        });

        startActivityForResult(intent, REQUEST_PICK_FILE);
    }

    /*
     * Menerima hasil file yang dipilih user.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            if (selectedFileUri == null) {
                Toast.makeText(this, "File tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedFileName = getFileName(selectedFileUri);
            selectedMimeType = getContentResolver().getType(selectedFileUri);

            if (selectedMimeType == null || selectedMimeType.trim().isEmpty()) {
                selectedMimeType = getMimeTypeFromFileName(selectedFileName);
            }

            long fileSize = getFileSize(selectedFileUri);

            if (!isAllowedMimeType(selectedMimeType)) {
                selectedFileUri = null;
                Toast.makeText(this, "Format file harus JPG, PNG, atau PDF", Toast.LENGTH_LONG).show();
                return;
            }

            if (fileSize > MAX_FILE_SIZE) {
                selectedFileUri = null;
                Toast.makeText(this, "Ukuran file maksimal 5 MB", Toast.LENGTH_LONG).show();
                return;
            }

            tvFileName.setText("File dipilih: " + selectedFileName);
        }
    }

    /*
     * Validasi sebelum menyimpan data.
     */
    private void validateBeforeSave() {
        calculatePointNow();

        if (selectedKategori.isEmpty()
                || selectedKegiatan.isEmpty()
                || selectedTingkat.isEmpty()
                || selectedPeran.isEmpty()
                || selectedMode.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua pilihan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPoin <= 0) {
            Toast.makeText(this, "Poin belum valid. Cek pilihan kegiatan.", Toast.LENGTH_SHORT).show();
            return;
        }

        /*
         * Saat tambah data baru, file bukti wajib dipilih.
         * Saat edit, file boleh tidak dipilih karena bisa memakai file lama.
         */
        if (!isEditMode && selectedFileUri == null) {
            Toast.makeText(this, "Pilih file bukti terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        checkQuotaThenSave();
    }

    /*
     * Mengecek batas maksimal kegiatan sebelum simpan.
     */
    private void checkQuotaThenSave() {
        int quotaLimit = SkpRule.getQuotaLimit(selectedKegiatan, selectedTingkat);

        if (quotaLimit <= 0) {
            saveData();
            return;
        }

        SupabaseClient.getAllSkpRecords(userKey, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                try {
                    JSONArray array = new JSONArray(responseBody);
                    int count = 0;

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Skp skp = Skp.fromJson(object);

                        /*
                         * Saat edit, data yang sedang diedit tidak dihitung,
                         * agar user tetap bisa menyimpan perubahan pada data yang sama.
                         */
                        if (isEditMode && editSkp != null && skp.getId().equals(editSkp.getId())) {
                            continue;
                        }

                        boolean sameKegiatan = selectedKegiatan.equals(skp.getNama_kegiatan());

                        boolean sameTingkat = true;
                        if ("Panitia dalam Kegiatan Kemahasiswaan".equals(selectedKegiatan)) {
                            sameTingkat = selectedTingkat.equals(skp.getTingkat());
                        }

                        if (sameKegiatan && sameTingkat) {
                            count++;
                        }
                    }

                    int finalCount = count;

                    runOnUiThread(() -> {
                        if (finalCount >= quotaLimit) {
                            Toast.makeText(
                                    FormSkpActivity.this,
                                    "Kegiatan ini sudah mencapai batas maksimal: " + quotaLimit,
                                    Toast.LENGTH_LONG
                            ).show();
                        } else {
                            saveData();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(FormSkpActivity.this, "Gagal mengecek kuota kegiatan", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() ->
                        Toast.makeText(FormSkpActivity.this, "Gagal cek kuota: " + errorMessage, Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    /*
     * Menyimpan data.
     * Jika ada file baru, upload file dulu.
     * Jika edit tanpa file baru, langsung update data memakai file lama.
     */
    private void saveData() {
        setLoading(true);

        if (selectedFileUri != null) {
            uploadFileThenSave();
        } else {
            saveRecordToDatabase(
                    existingFileUrl,
                    existingFileName,
                    existingFileType,
                    existingStoragePath
            );
        }
    }

    /*
     * Upload file bukti ke Supabase Storage.
     */
    private void uploadFileThenSave() {
        SupabaseClient.uploadFile(
                this,
                selectedFileUri,
                selectedFileName,
                selectedMimeType,
                new SupabaseClient.SupabaseCallback() {
                    @Override
                    public void onSuccess(String responseBody) {
                        /*
                         * Response dari SupabaseClient berbentuk:
                         * storage_path|public_url
                         */
                        String[] parts = responseBody.split("\\|", 2);

                        String storagePath = parts.length > 0 ? parts[0] : "";
                        String publicUrl = parts.length > 1 ? parts[1] : "";

                        String fileType = getSimpleFileType(selectedMimeType);

                        saveRecordToDatabase(
                                publicUrl,
                                selectedFileName,
                                fileType,
                                storagePath
                        );
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            setLoading(false);
                            Toast.makeText(FormSkpActivity.this, "Upload gagal: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }

    /*
     * Menyimpan record ke tabel skp_records.
     */
    private void saveRecordToDatabase(String fileUrl,
                                      String fileName,
                                      String fileType,
                                      String storagePath) {
        try {
            Skp skp = new Skp();

            skp.setNama_kegiatan(selectedKegiatan);
            skp.setJenis_kegiatan(SkpRule.getJenisKegiatan(selectedKategori));
            skp.setKategori_bidang(selectedKategori);
            skp.setTingkat(selectedTingkat);
            skp.setPeran(selectedPeran);
            skp.setMode_kegiatan(selectedMode);
            skp.setPoin_skp(selectedPoin);
            skp.setFile_url(fileUrl);
            skp.setFile_name(fileName);
            skp.setFile_type(fileType);
            skp.setStorage_path(storagePath);
            skp.setTanggal_input(getTodayDate());
            skp.setUser_key(userKey);

            /*
             * Untuk edit, timestamp lama tetap dipakai agar urutan data tidak berubah drastis.
             * Untuk tambah baru, timestamp dibuat dari waktu saat ini.
             */
            if (isEditMode && editSkp != null) {
                skp.setTimestamp(editSkp.getTimestamp());
            } else {
                skp.setTimestamp(System.currentTimeMillis());
            }

            String jsonBody = skp.toJson().toString();

            if (isEditMode && editSkp != null) {
                updateRecord(jsonBody);
            } else {
                insertRecord(jsonBody);
            }

        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(this, "Gagal membuat data JSON", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Insert data baru ke Supabase.
     */
    private void insertRecord(String jsonBody) {
        SupabaseClient.insertSkpRecord(jsonBody, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(FormSkpActivity.this, "Data SKP berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(FormSkpActivity.this, "Gagal simpan: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /*
     * Update data lama di Supabase.
     */
    private void updateRecord(String jsonBody) {
        SupabaseClient.updateSkpRecord(editSkp.getId(), jsonBody, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(FormSkpActivity.this, "Data SKP berhasil diupdate", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(FormSkpActivity.this, "Gagal update: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /*
     * Mengatur loading saat proses upload atau simpan berlangsung.
     */
    private void setLoading(boolean loading) {
        if (loading) {
            progressUpload.setVisibility(View.VISIBLE);
            btnSimpan.setEnabled(false);
            btnPilihFile.setEnabled(false);
            btnBatal.setEnabled(false);
        } else {
            progressUpload.setVisibility(View.GONE);
            btnSimpan.setEnabled(true);
            btnPilihFile.setEnabled(true);
            btnBatal.setEnabled(true);
        }
    }

    /*
     * Helper untuk mengisi spinner.
     */
    private void setSpinnerData(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                data
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /*
     * Helper untuk memilih item spinner berdasarkan teks.
     * Dipakai saat mode edit.
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            Object item = spinner.getItemAtPosition(i);

            if (item != null && value.equals(item.toString())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    /*
     * Mengambil nama file dari Uri.
     */
    private String getFileName(Uri uri) {
        String result = "bukti_skp";

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex);
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    /*
     * Mengambil ukuran file dari Uri.
     */
    private long getFileSize(Uri uri) {
        long size = 0;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            try {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                if (sizeIndex >= 0 && cursor.moveToFirst()) {
                    size = cursor.getLong(sizeIndex);
                }
            } finally {
                cursor.close();
            }
        }

        return size;
    }

    /*
     * Mengecek tipe file yang diizinkan.
     */
    private boolean isAllowedMimeType(String mimeType) {
        return "image/jpeg".equals(mimeType)
                || "image/png".equals(mimeType)
                || "application/pdf".equals(mimeType);
    }

    /*
     * Mengambil MIME type dari nama file jika ContentResolver tidak memberi MIME.
     */
    private String getMimeTypeFromFileName(String fileName) {
        String extension = "";

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex + 1);
        }

        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());

        if (mime == null) {
            return "";
        }

        return mime;
    }

    /*
     * Mengubah MIME type menjadi tipe sederhana untuk disimpan di database.
     */
    private String getSimpleFileType(String mimeType) {
        if ("application/pdf".equals(mimeType)) {
            return "pdf";
        }

        if ("image/jpeg".equals(mimeType) || "image/png".equals(mimeType)) {
            return "image";
        }

        return "file";
    }

    /*
     * Format tanggal input yang mudah dibaca.
     */
    private String getTodayDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        return format.format(new Date());
    }
}