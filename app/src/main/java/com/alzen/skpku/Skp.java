package com.alzen.skpku;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/*
 * Model data SKP.
 * Class ini digunakan untuk menampung satu data kegiatan SKP
 * yang berasal dari tabel skp_records di Supabase.
 */
public class Skp implements Serializable {

    private String id;
    private String user_key;
    private String nama_kegiatan;
    private String jenis_kegiatan;
    private String kategori_bidang;
    private String tingkat;
    private String peran;
    private String mode_kegiatan;
    private int poin_skp;
    private String file_url;
    private String file_name;
    private String file_type;
    private String storage_path;
    private String tanggal_input;
    private long timestamp;
    private String created_at;

    public Skp() {
    }

    public Skp(String id,
               String nama_kegiatan,
               String jenis_kegiatan,
               String kategori_bidang,
               String tingkat,
               String peran,
               String mode_kegiatan,
               int poin_skp,
               String file_url,
               String file_name,
               String file_type,
               String storage_path,
               String tanggal_input,
               long timestamp,
               String created_at) {

        this.id = id;
        this.nama_kegiatan = nama_kegiatan;
        this.jenis_kegiatan = jenis_kegiatan;
        this.kategori_bidang = kategori_bidang;
        this.tingkat = tingkat;
        this.peran = peran;
        this.mode_kegiatan = mode_kegiatan;
        this.poin_skp = poin_skp;
        this.file_url = file_url;
        this.file_name = file_name;
        this.file_type = file_type;
        this.storage_path = storage_path;
        this.tanggal_input = tanggal_input;
        this.timestamp = timestamp;
        this.created_at = created_at;
    }

    /*
     * Method ini mengubah data JSON dari Supabase menjadi object Skp.
     * Dipakai saat aplikasi membaca data dari database.
     */
    public static Skp fromJson(JSONObject object) {
        Skp skp = new Skp();

        skp.id = object.optString("id", "");
        skp.user_key = object.optString("user_key", "");
        skp.nama_kegiatan = object.optString("nama_kegiatan", "");
        skp.jenis_kegiatan = object.optString("jenis_kegiatan", "");
        skp.kategori_bidang = object.optString("kategori_bidang", "");
        skp.tingkat = object.optString("tingkat", "");
        skp.peran = object.optString("peran", "");
        skp.mode_kegiatan = object.optString("mode_kegiatan", "Tidak Ada");
        skp.poin_skp = object.optInt("poin_skp", 0);
        skp.file_url = object.optString("file_url", "");
        skp.file_name = object.optString("file_name", "");
        skp.file_type = object.optString("file_type", "");
        skp.storage_path = object.optString("storage_path", "");
        skp.tanggal_input = object.optString("tanggal_input", "");
        skp.timestamp = object.optLong("timestamp", 0);
        skp.created_at = object.optString("created_at", "");

        return skp;
    }

    /*
     * Method ini mengubah object Skp menjadi JSON.
     * Dipakai saat aplikasi menyimpan atau mengupdate data ke Supabase.
     */
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();

        object.put("nama_kegiatan", nama_kegiatan);
        object.put("jenis_kegiatan", jenis_kegiatan);
        object.put("kategori_bidang", kategori_bidang);
        object.put("tingkat", tingkat);
        object.put("peran", peran);
        object.put("mode_kegiatan", mode_kegiatan);
        object.put("poin_skp", poin_skp);
        object.put("file_url", file_url);
        object.put("file_name", file_name);
        object.put("file_type", file_type);
        object.put("storage_path", storage_path);
        object.put("tanggal_input", tanggal_input);
        object.put("timestamp", timestamp);

        return object;
    }

    public String getId() {
        return id;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getNama_kegiatan() {
        return nama_kegiatan;
    }

    public String getJenis_kegiatan() {
        return jenis_kegiatan;
    }

    public String getKategori_bidang() {
        return kategori_bidang;
    }

    public String getTingkat() {
        return tingkat;
    }

    public String getPeran() {
        return peran;
    }

    public String getMode_kegiatan() {
        return mode_kegiatan;
    }

    public int getPoin_skp() {
        return poin_skp;
    }

    public String getFile_url() {
        return file_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getStorage_path() {
        return storage_path;
    }

    public String getTanggal_input() {
        return tanggal_input;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNama_kegiatan(String nama_kegiatan) {
        this.nama_kegiatan = nama_kegiatan;
    }

    public void setJenis_kegiatan(String jenis_kegiatan) {
        this.jenis_kegiatan = jenis_kegiatan;
    }

    public void setKategori_bidang(String kategori_bidang) {
        this.kategori_bidang = kategori_bidang;
    }

    public void setTingkat(String tingkat) {
        this.tingkat = tingkat;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

    public void setMode_kegiatan(String mode_kegiatan) {
        this.mode_kegiatan = mode_kegiatan;
    }

    public void setPoin_skp(int poin_skp) {
        this.poin_skp = poin_skp;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public void setStorage_path(String storage_path) {
        this.storage_path = storage_path;
    }

    public void setTanggal_input(String tanggal_input) {
        this.tanggal_input = tanggal_input;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}