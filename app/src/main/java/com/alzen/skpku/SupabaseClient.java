package com.alzen.skpku;

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
 * Semua proses CRUD database, upload file, dan baca file bukti lewat class ini.
 */
public class SupabaseClient {

    /*
     * Data koneksi Supabase.
     * Key yang dipakai adalah publishable/anon key, bukan service role key.
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
     * Callback khusus untuk mengambil file dari Supabase Storage dalam bentuk byte array.
     * Ini dipakai supaya preview dan download tidak bergantung pada bucket public.
     */
    public interface SupabaseFileCallback {
        void onSuccess(byte[] fileBytes, String contentType);
        void onFailure(String errorMessage);
    }

    /*
     * Header standar untuk request ke Supabase REST API dan Storage API.
     * apikey dan Authorization wajib dikirim agar request diterima Supabase.
     */
    private static Request.Builder getBaseRequestBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .addHeader("Accept", "application/json");
    }

    /*
     * Membaca semua data SKP milik user tertentu.
     * Data diurutkan berdasarkan timestamp terbaru.
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
     * Menyimpan data SKP baru.
     * Body dikirim dalam format JSON.
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
     * Mengupdate data SKP berdasarkan id.
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
     * Menghapus data SKP berdasarkan id.
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
     * Upload file bukti ke Supabase Storage.
     * File akan disimpan ke bucket skp-bukti.
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

            /*
             * Nama file dibuat unik memakai timestamp agar tidak bentrok
             * jika user mengupload file dengan nama yang sama.
             */
            String safeFileName = makeSafeFileName(originalFileName);
            String storagePath = System.currentTimeMillis() + "_" + safeFileName;
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
                        /*
                         * Response sukses kita buat sendiri dalam format sederhana:
                         * storage_path|public_url
                         * Nanti di FormSkpActivity akan dipisahkan.
                         */
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
     * storagePath diambil dari kolom storage_path pada database.
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
     * Membuat URL publik file.
     * Karena bucket bukti-skp dibuat public, file bisa dibuka memakai URL ini.
     */
    public static String getPublicFileUrl(String storagePath) {
        return SUPABASE_URL
                + "/storage/v1/object/public/"
                + BUCKET_NAME
                + "/"
                + encodePath(storagePath);
    }

    /*
     * URL authenticated. Dipakai untuk preview/download internal aplikasi.
     */
    public static String getAuthenticatedFileUrl(String storagePath) {
        return SUPABASE_URL
                + "/storage/v1/object/"
                + BUCKET_NAME
                + "/"
                + encodePath(storagePath);
    }

    /*
     * Method umum untuk menjalankan request OkHttp.
     * Dipakai oleh GET, POST, PATCH, DELETE database.
     */
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

    /*
     * Membaca file dari Uri menjadi byte array.
     * Ini dibutuhkan sebelum file dikirim ke Supabase Storage.
     */
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

    /*
     * Membersihkan nama file agar aman dipakai sebagai nama object di storage.
     */
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

    /*
     * Encode path agar aman ketika dimasukkan ke URL.
     */
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
        } catch (Exception e) {
            return path;
        }
    }
}
