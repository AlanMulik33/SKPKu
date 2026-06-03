package com.alzen.skpku;

<<<<<<< HEAD
=======

>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
 * Class ini bertugas menghubungkan aplikasi Android dengan Supabase.
<<<<<<< HEAD
 * Semua proses CRUD database, upload file, dan baca file bukti lewat class ini.
=======
 * Semua proses CRUD database dan upload file akan lewat class ini.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
 */
public class SupabaseClient {

    /*
     * Data koneksi Supabase.
<<<<<<< HEAD
     * Key yang dipakai adalah publishable/anon key, bukan service role key.
=======
     * SUPABASE_ANON_KEY ini adalah publishable/anon key, bukan service role key.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static final String SUPABASE_URL = "https://iodigakwxtdyyivoxuqv.supabase.co";
    public static final String SUPABASE_ANON_KEY = "sb_publishable_CFrekMEtCf3NcHUS5Jsx0Q_VaNDSRCF";
    public static final String TABLE_NAME = "skp_records";
    public static final String BUCKET_NAME = "skp-bukti";

    private static final OkHttpClient client = new OkHttpClient();

    public interface SupabaseCallback {
        void onSuccess(String responseBody);
        void onFailure(String errorMessage);
    }

    /*
<<<<<<< HEAD
     * Callback khusus untuk mengambil file dari Supabase Storage dalam bentuk byte array.
     * Ini dipakai supaya preview dan download tidak bergantung pada bucket public.
     */
    public interface SupabaseFileCallback {
        void onSuccess(byte[] fileBytes, String contentType);
        void onFailure(String errorMessage);
    }

    /*
     * Header standar untuk request ke Supabase REST API dan Storage API.
=======
     * Header standar untuk request ke Supabase REST API.
     * apikey dan Authorization wajib dikirim agar request diterima Supabase.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    private static Request.Builder getBaseRequestBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .addHeader("Accept", "application/json");
    }

    /*
<<<<<<< HEAD
     * Membaca semua data SKP milik user tertentu.
=======
     * Method untuk membaca semua data SKP dari Supabase.
     * Data diurutkan berdasarkan timestamp terbaru.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static void getAllSkpRecords(String userKey, SupabaseCallback callback) {
        String encodedUserKey = encodePath(userKey);

        String url = SUPABASE_URL
                + "/rest/v1/"
                + TABLE_NAME
                + "?select=*&user_key=eq."
                + encodedUserKey
                + "&order=timestamp.desc";

        Request request = getBaseRequestBuilder(url)
                .get()
                .build();

        executeRequest(request, callback);
    }

    /*
<<<<<<< HEAD
     * Menyimpan data SKP baru.
=======
     * Method untuk menyimpan data SKP baru.
     * Body dikirim dalam format JSON.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static void insertSkpRecord(String jsonBody, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/" + TABLE_NAME;

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = getBaseRequestBuilder(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        executeRequest(request, callback);
    }

    /*
<<<<<<< HEAD
     * Mengupdate data SKP berdasarkan id.
=======
     * Method untuk mengupdate data SKP berdasarkan id.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static void updateSkpRecord(String id, String jsonBody, SupabaseCallback callback) {
        String url = SUPABASE_URL
                + "/rest/v1/"
                + TABLE_NAME
                + "?id=eq."
                + id;

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = getBaseRequestBuilder(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();

        executeRequest(request, callback);
    }

    /*
<<<<<<< HEAD
     * Menghapus data SKP berdasarkan id.
=======
     * Method untuk menghapus data SKP berdasarkan id.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static void deleteSkpRecord(String id, SupabaseCallback callback) {
        String url = SUPABASE_URL
                + "/rest/v1/"
                + TABLE_NAME
                + "?id=eq."
                + id;

        Request request = getBaseRequestBuilder(url)
                .delete()
                .build();

        executeRequest(request, callback);
    }

    /*
<<<<<<< HEAD
     * Upload file bukti ke Supabase Storage.
=======
     * Method untuk upload file bukti ke Supabase Storage.
     * File akan disimpan ke bucket bukti-skp.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static void uploadFile(Context context,
                                  Uri fileUri,
                                  String originalFileName,
                                  String mimeType,
                                  SupabaseCallback callback) {

        try {
            byte[] fileBytes = readBytesFromUri(context, fileUri);

            if (fileBytes == null) {
                callback.onFailure("File tidak bisa dibaca.");
                return;
            }

<<<<<<< HEAD
            String safeFileName = makeSafeFileName(originalFileName);
            String storagePath = System.currentTimeMillis() + "_" + safeFileName;
=======
            /*
             * Nama file dibuat unik memakai timestamp agar tidak bentrok
             * jika user mengupload file dengan nama yang sama.
             */
            String safeFileName = makeSafeFileName(originalFileName);
            String storagePath = System.currentTimeMillis() + "_" + safeFileName;

>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
            String encodedPath = encodePath(storagePath);

            String url = SUPABASE_URL
                    + "/storage/v1/object/"
                    + BUCKET_NAME
                    + "/"
                    + encodedPath;

            RequestBody body = RequestBody.create(
                    fileBytes,
                    MediaType.parse(mimeType)
            );

            Request request = getBaseRequestBuilder(url)
                    .addHeader("Content-Type", mimeType)
                    .addHeader("x-upsert", "true")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
<<<<<<< HEAD
=======
                        /*
                         * Response sukses kita buat sendiri dalam format sederhana:
                         * storage_path|public_url
                         * Nanti di FormSkpActivity akan dipisahkan.
                         */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
                        String publicUrl = getPublicFileUrl(storagePath);
                        callback.onSuccess(storagePath + "|" + publicUrl);
                    } else {
                        callback.onFailure("Upload gagal: " + response.code() + " - " + responseBody);
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    /*
<<<<<<< HEAD
     * Mengambil file dari Supabase Storage menggunakan storage_path.
     * Cara ini lebih aman daripada langsung membuka public URL, karena tetap mengirim apikey.
     */
    public static void downloadFileBytes(String storagePath, SupabaseFileCallback callback) {
        if (storagePath == null || storagePath.trim().isEmpty()) {
            callback.onFailure("Storage path file kosong.");
            return;
        }

        String url = getAuthenticatedFileUrl(storagePath);

        Request request = getBaseRequestBuilder(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] bytes = response.body() != null ? response.body().bytes() : new byte[0];
                String contentType = response.header("Content-Type", "application/octet-stream");

                if (response.isSuccessful()) {
                    callback.onSuccess(bytes, contentType);
                } else {
                    String errorText = new String(bytes);
                    callback.onFailure("Gagal ambil file: " + response.code() + " - " + errorText);
                }
            }
        });
    }

    /*
     * Menghapus file bukti dari Supabase Storage.
=======
     * Method untuk menghapus file bukti dari Supabase Storage.
     * storagePath diambil dari kolom storage_path pada database.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static void deleteFile(String storagePath, SupabaseCallback callback) {
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME;

        String jsonBody = "{\"prefixes\":[\"" + storagePath + "\"]}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = getBaseRequestBuilder(url)
                .addHeader("Content-Type", "application/json")
                .delete(body)
                .build();

        executeRequest(request, callback);
    }

    /*
<<<<<<< HEAD
     * URL public. Dipakai sebagai cadangan jika bucket public.
=======
     * Membuat URL publik file.
     * Karena bucket bukti-skp dibuat public, file bisa dibuka memakai URL ini.
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
     */
    public static String getPublicFileUrl(String storagePath) {
        return SUPABASE_URL
                + "/storage/v1/object/public/"
                + BUCKET_NAME
                + "/"
                + encodePath(storagePath);
    }

    /*
<<<<<<< HEAD
     * URL authenticated. Dipakai untuk preview/download internal aplikasi.
     */
    public static String getAuthenticatedFileUrl(String storagePath) {
        return SUPABASE_URL
                + "/storage/v1/object/"
                + BUCKET_NAME
                + "/"
                + encodePath(storagePath);
    }

=======
     * Method umum untuk menjalankan request OkHttp.
     * Dipakai oleh GET, POST, PATCH, DELETE database.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private static void executeRequest(Request request, SupabaseCallback callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onFailure("Error " + response.code() + ": " + responseBody);
                }
            }
        });
    }

<<<<<<< HEAD
=======
    /*
     * Membaca file dari Uri menjadi byte array.
     * Ini dibutuhkan sebelum file dikirim ke Supabase Storage.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private static byte[] readBytesFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int read;

        while ((read = inputStream.read(data)) != -1) {
            buffer.write(data, 0, read);
        }

        inputStream.close();
        return buffer.toByteArray();
    }

<<<<<<< HEAD
=======
    /*
     * Membersihkan nama file agar aman dipakai sebagai nama object di storage.
     */
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
    private static String makeSafeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "bukti_skp";
        }

        return fileName
                .replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace(":", "_");
    }

<<<<<<< HEAD
    private static String encodePath(String path) {
        try {
            String[] parts = path.split("/");
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                builder.append(URLEncoder.encode(parts[i], "UTF-8").replace("+", "%20"));

                if (i < parts.length - 1) {
                    builder.append("/");
                }
            }

            return builder.toString();
=======
    /*
     * Encode path agar aman ketika dimasukkan ke URL.
     */
    private static String encodePath(String path) {
        try {
            return URLEncoder.encode(path, "UTF-8").replace("+", "%20");
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
        } catch (Exception e) {
            return path;
        }
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 873eae3339ca4b1e7a12c0e54268467ca9642f93
