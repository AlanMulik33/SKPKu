package com.alzen.skpku;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * MainActivity adalah halaman utama aplikasi.
 * Di sini ada dua menu sederhana, yaitu Dashboard dan SKP.
 */
public class MainActivity extends AppCompatActivity {

    private static final int TARGET_SKP = 200;

    private TextView tvGreeting, tvTotalPoin, tvTargetPoin, tvSisaPoin;
    private String userKey;
    private TextView tvReminderWajib, tvRingkasan, tvEmpty;
    private TextView navDashboard, navSkp;
    private LinearLayout layoutDashboard, layoutSkp;
    private Spinner spFilterKategori;
    private Button btnTambahDashboard, btnTambahSkp;
    private RecyclerView rvSkp;

    private SkpAdapter adapter;

    /*
     * allSkpList menyimpan semua data dari Supabase.
     * filteredSkpList menyimpan data yang sudah difilter berdasarkan kategori.
     */
    private final List<Skp> allSkpList = new ArrayList<>();
    private final List<Skp> filteredSkpList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private String selectedFilter = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("SKPKU_PREF", MODE_PRIVATE);

        initViews();
        setupRecyclerView();
        setupFilterSpinner();
        setupClickActions();

        /*
         * Nama mahasiswa hanya diminta saat pertama kali aplikasi dibuka.
         * Setelah itu nama disimpan di SharedPreferences.
         */
        checkStudentName();

        /*
         * Saat aplikasi dibuka, data langsung diambil dari Supabase.
         */
        loadSkpDataFromSupabase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * onResume dipanggil ulang saat kembali dari halaman form/detail.
         * Tujuannya agar data Dashboard dan list SKP selalu terbaru.
         */
        loadSkpDataFromSupabase();
    }

    /*
     * Menghubungkan variable Java dengan komponen di activity_main.xml.
     */
    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvTotalPoin = findViewById(R.id.tvTotalPoin);
        tvTargetPoin = findViewById(R.id.tvTargetPoin);
        tvSisaPoin = findViewById(R.id.tvSisaPoin);
        tvReminderWajib = findViewById(R.id.tvReminderWajib);
        tvRingkasan = findViewById(R.id.tvRingkasan);
        tvEmpty = findViewById(R.id.tvEmpty);

        navDashboard = findViewById(R.id.navDashboard);
        navSkp = findViewById(R.id.navSkp);

        layoutDashboard = findViewById(R.id.layoutDashboard);
        layoutSkp = findViewById(R.id.layoutSkp);

        spFilterKategori = findViewById(R.id.spFilterKategori);

        btnTambahDashboard = findViewById(R.id.btnTambahDashboard);
        btnTambahSkp = findViewById(R.id.btnTambahSkp);

        rvSkp = findViewById(R.id.rvSkp);
    }

    /*
     * Menyiapkan RecyclerView agar bisa menampilkan daftar SKP.
     */
    private void setupRecyclerView() {
        adapter = new SkpAdapter(this, filteredSkpList, skp -> {
            /*
             * Saat item list diklik, data SKP dikirim ke DetailSkpActivity.
             */
            Intent intent = new Intent(MainActivity.this, DetailSkpActivity.class);
            intent.putExtra("skp", skp);
            startActivity(intent);
        });

        rvSkp.setLayoutManager(new LinearLayoutManager(this));
        rvSkp.setAdapter(adapter);
    }

    /*
     * Menyiapkan filter kategori di menu SKP.
     */
    private void setupFilterSpinner() {
        List<String> filterList = new ArrayList<>();
        filterList.add("Semua");
        filterList.addAll(SkpRule.getKategoriList());

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterList
        );

        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterKategori.setAdapter(filterAdapter);

        spFilterKategori.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = filterList.get(position);
                applyFilter();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    /*
     * Menyiapkan semua aksi klik tombol dan menu.
     */
    private void setupClickActions() {
        navDashboard.setOnClickListener(v -> showDashboardMenu());
        navSkp.setOnClickListener(v -> showSkpMenu());

        btnTambahDashboard.setOnClickListener(v -> openFormActivity());
        btnTambahSkp.setOnClickListener(v -> openFormActivity());
    }

    /*
     * Membuka halaman Form SKP dalam mode tambah data.
     */
    private void openFormActivity() {
        Intent intent = new Intent(MainActivity.this, FormSkpActivity.class);
        intent.putExtra("mode", "create");
        intent.putExtra("user_key", userKey);
        startActivity(intent);
    }

    /*
     * Menampilkan menu Dashboard dan menyembunyikan menu SKP.
     */
    private void showDashboardMenu() {
        layoutDashboard.setVisibility(View.VISIBLE);
        layoutSkp.setVisibility(View.GONE);

        navDashboard.setTextColor(getColor(R.color.primary_blue));
        navSkp.setTextColor(getColor(R.color.text_gray));
    }

    /*
     * Menampilkan menu SKP dan menyembunyikan menu Dashboard.
     */
    private void showSkpMenu() {
        layoutDashboard.setVisibility(View.GONE);
        layoutSkp.setVisibility(View.VISIBLE);

        navDashboard.setTextColor(getColor(R.color.text_gray));
        navSkp.setTextColor(getColor(R.color.primary_blue));
    }

    /*
     * Mengecek apakah nama mahasiswa sudah tersimpan.
     * Jika belum, tampilkan dialog input nama.
     */
    private void checkStudentName() {
        String nama = sharedPreferences.getString("nama_mahasiswa", "");

        if (nama == null || nama.trim().isEmpty()) {
            showInputNameDialog();
        } else {
            tvGreeting.setText("Halo, " + nama);
        }
    }

    private String getOrCreateUserKey() {
        String key = sharedPreferences.getString("user_key", "");

        if (key == null || key.trim().isEmpty()) {
            key = UUID.randomUUID().toString();

            sharedPreferences.edit()
                    .putString("user_key", key)
                    .apply();
        }

        return key;
    }

    /*
     * Dialog input nama mahasiswa.
     * Dialog ini hanya muncul saat pertama kali aplikasi dibuka.
     */
    private void showInputNameDialog() {
        EditText inputNama = new EditText(this);
        inputNama.setHint("Nama Mahasiswa");
        inputNama.setSingleLine(true);
        inputNama.setPadding(40, 20, 40, 20);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Selamat Datang di SKPKU")
                .setMessage("Masukkan nama kamu terlebih dahulu.")
                .setView(inputNama)
                .setCancelable(false)
                .setPositiveButton("Mulai", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            button.setOnClickListener(v -> {
                String nama = inputNama.getText().toString().trim();

                if (nama.isEmpty()) {
                    inputNama.setError("Nama tidak boleh kosong");
                    return;
                }

                /*
                 * Nama disimpan secara lokal agar tidak perlu input ulang.
                 */
                sharedPreferences.edit()
                        .putString("nama_mahasiswa", nama)
                        .apply();

                tvGreeting.setText("Halo, " + nama);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    /*
     * Mengambil semua data SKP dari Supabase.
     */
    private void loadSkpDataFromSupabase() {
        SupabaseClient.getAllSkpRecords(userKey, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                try {
                    JSONArray array = new JSONArray(responseBody);
                    List<Skp> tempList = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Skp skp = Skp.fromJson(object);
                        tempList.add(skp);
                    }

                    runOnUiThread(() -> {
                        allSkpList.clear();
                        allSkpList.addAll(tempList);

                        updateDashboard();
                        applyFilter();
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Gagal membaca data JSON", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Gagal load data: " + errorMessage, Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    /*
     * Mengupdate semua informasi di Dashboard.
     */
    private void updateDashboard() {
        int totalPoin = 0;
        int totalWajib = 0;
        int totalPilihan = 0;

        for (Skp skp : allSkpList) {
            totalPoin += skp.getPoin_skp();

            if ("Wajib".equalsIgnoreCase(skp.getJenis_kegiatan())) {
                totalWajib++;
            } else {
                totalPilihan++;
            }
        }

        int sisa = TARGET_SKP - totalPoin;
        if (sisa < 0) {
            sisa = 0;
        }

        tvTotalPoin.setText(totalPoin + " Poin");
        tvTargetPoin.setText("Target: " + TARGET_SKP + " Poin");
        tvSisaPoin.setText("Sisa: " + sisa + " Poin");

        tvRingkasan.setText(
                "Total kegiatan: " + allSkpList.size()
                        + "\nKegiatan wajib: " + totalWajib
                        + "\nKegiatan pilihan: " + totalPilihan
        );

        updateReminderWajib();
    }

    /*
     * Mengecek kegiatan wajib yang sudah dicatat dan yang belum dicatat.
     */
    private void updateReminderWajib() {
        Set<String> kegiatanYangSudahAda = new HashSet<>();

        for (Skp skp : allSkpList) {
            kegiatanYangSudahAda.add(skp.getNama_kegiatan());
        }

        List<String> belumLengkap = new ArrayList<>();

        for (String wajib : SkpRule.getWajibActivities()) {
            if (!kegiatanYangSudahAda.contains(wajib)) {
                belumLengkap.add(wajib);
            }
        }

        if (belumLengkap.isEmpty()) {
            tvReminderWajib.setText("Semua SKP wajib sudah lengkap ✓");
            tvReminderWajib.setTextColor(getColor(R.color.accent_green));
        } else {
            tvReminderWajib.setText("Masih ada SKP wajib yang belum lengkap.\nBelum: " + joinList(belumLengkap));
            tvReminderWajib.setTextColor(getColor(R.color.warning_orange));
        }
    }

    /*
     * Menerapkan filter kategori pada list SKP.
     */
    private void applyFilter() {
        filteredSkpList.clear();

        if ("Semua".equals(selectedFilter)) {
            filteredSkpList.addAll(allSkpList);
        } else {
            for (Skp skp : allSkpList) {
                if (selectedFilter.equals(skp.getKategori_bidang())) {
                    filteredSkpList.add(skp);
                }
            }
        }

        adapter.setData(filteredSkpList);

        if (filteredSkpList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSkp.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvSkp.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Helper untuk menggabungkan list menjadi teks.
     * Contoh: PKKMB, Dies Natalis, Seminar Ormawa
     */
    private String joinList(List<String> list) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));

            if (i < list.size() - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}