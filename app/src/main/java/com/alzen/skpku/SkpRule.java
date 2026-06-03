package com.alzen.skpku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Class ini berisi aturan poin SKP.
 * Semua poin dihitung otomatis berdasarkan Buku Pedoman SKP.
 * Tujuannya agar mahasiswa tidak menginput poin secara manual.
 */
public class SkpRule {

    public static final String KATEGORI_WAJIB = "Kegiatan Wajib Institusi";
    public static final String KATEGORI_ORGANISASI = "Bidang Organisasi dan Kepemimpinan";
    public static final String KATEGORI_PENALARAN = "Bidang Penalaran dan Keilmuan";
    public static final String KATEGORI_MINAT = "Bidang Minat dan Bakat";
    public static final String KATEGORI_PENGABDIAN = "Bidang Pengabdian Kepada Masyarakat";

    public static final String MODE_TIDAK_ADA = "Tidak Ada";
    public static final String MODE_ONLINE = "Online";
    public static final String MODE_OFFLINE = "Offline";

    /*
     * Daftar kategori utama SKP.
     * Data ini akan dipakai untuk Spinner kategori di form.
     */
    public static List<String> getKategoriList() {
        return list(
                KATEGORI_WAJIB,
                KATEGORI_ORGANISASI,
                KATEGORI_PENALARAN,
                KATEGORI_MINAT,
                KATEGORI_PENGABDIAN
        );
    }

    /*
     * Menentukan jenis kegiatan.
     * Kategori wajib akan disimpan sebagai "Wajib",
     * sedangkan kategori lain disimpan sebagai "Pilihan".
     */
    public static String getJenisKegiatan(String kategori) {
        if (eq(kategori, KATEGORI_WAJIB)) {
            return "Wajib";
        }
        return "Pilihan";
    }

    /*
     * Daftar kegiatan akan berubah sesuai kategori yang dipilih.
     * Ini dipakai untuk Spinner nama kegiatan.
     */
    public static List<String> getKegiatanList(String kategori) {
        if (eq(kategori, KATEGORI_WAJIB)) {
            return list(
                    "PKKMB",
                    "Dies Natalis",
                    "Upacara Bendera 17 Agustus",
                    "Alumni Pulang Campus",
                    "Seminar Institut Bisnis dan Teknologi Indonesia",
                    "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia"
            );
        }

        if (eq(kategori, KATEGORI_ORGANISASI)) {
            return list(
                    "Pengurus Organisasi",
                    "Panitia dalam Kegiatan Kemahasiswaan",
                    "Pendukung / Pengisi / Partisipan Kegiatan"
            );
        }

        if (eq(kategori, KATEGORI_PENALARAN)) {
            return list(
                    "Prestasi Lomba Karya Tulis / Inovasi",
                    "Forum Ilmiah",
                    "Karya Ilmiah Jurnal",
                    "Karya Ilmiah Prosiding",
                    "Karya di Majalah / Surat Kabar",
                    "Menghadiri Seminar Proposal / Skripsi",
                    "Asisten Lab 1 Semester",
                    "Workshop Institusi",
                    "SDM Projek Inovasi"
            );
        }

        if (eq(kategori, KATEGORI_MINAT)) {
            return list(
                    "Prestasi Akademik / Non Akademik",
                    "Latihan Rutin Ormawa",
                    "Anggota Insidental Kegiatan Institut",
                    "Model Foto / Video Publikasi",
                    "Pengembangan Perangkat Pembelajaran"
            );
        }

        if (eq(kategori, KATEGORI_PENGABDIAN)) {
            return list(
                    "Pengabdian Kepada Masyarakat",
                    "Penanganan Bencana",
                    "Studi Banding",
                    "Magang Kerja Bukan PKL"
            );
        }

        return list("Tidak Ada");
    }

    /*
     * Daftar tingkat akan berubah sesuai kategori dan kegiatan.
     */
    public static List<String> getTingkatList(String kategori, String kegiatan) {
        if (eq(kategori, KATEGORI_WAJIB)) {
            return list("Institusi");
        }

        if (eq(kategori, KATEGORI_ORGANISASI)) {
            if (eq(kegiatan, "Pengurus Organisasi")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "BEM",
                        "Himaprodi / UKM / FPK"
                );
            }

            if (eq(kegiatan, "Panitia dalam Kegiatan Kemahasiswaan")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi (PKKMB / Dies Natalis / Wisuda)",
                        "Ormawa"
                );
            }

            if (eq(kegiatan, "Pendukung / Pengisi / Partisipan Kegiatan")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi",
                        "Ormawa"
                );
            }
        }

        if (eq(kategori, KATEGORI_PENALARAN)) {
            if (eq(kegiatan, "Prestasi Lomba Karya Tulis / Inovasi")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi",
                        "Ormawa"
                );
            }

            if (eq(kegiatan, "Forum Ilmiah")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi"
                );
            }

            if (eq(kegiatan, "Karya Ilmiah Jurnal")) {
                return list(
                        "Internasional Grade A",
                        "Internasional Grade B",
                        "Internasional Grade C",
                        "Nasional SINTA 1",
                        "Nasional SINTA 2",
                        "Nasional SINTA 3",
                        "Nasional SINTA 4-6",
                        "Nasional Tidak Terakreditasi"
                );
            }

            if (eq(kegiatan, "Karya Ilmiah Prosiding")) {
                return list(
                        "Internasional Terindeks Scopus",
                        "Internasional Terindeks",
                        "Internasional Tidak Terindeks ber-ISBN",
                        "Nasional Terindeks",
                        "Nasional Tidak Terindeks"
                );
            }

            if (eq(kegiatan, "Karya di Majalah / Surat Kabar")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi"
                );
            }

            if (eq(kegiatan, "SDM Projek Inovasi")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi"
                );
            }

            return list("Institusi");
        }

        if (eq(kategori, KATEGORI_MINAT)) {
            if (eq(kegiatan, "Prestasi Akademik / Non Akademik")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi",
                        "Ormawa"
                );
            }

            return list("Institusi");
        }

        if (eq(kategori, KATEGORI_PENGABDIAN)) {
            if (eq(kegiatan, "Pengabdian Kepada Masyarakat")) {
                return list(
                        "Internasional",
                        "Nasional",
                        "Regional",
                        "Institusi",
                        "Ormawa"
                );
            }

            return list("Institusi");
        }

        return list("Tidak Ada");
    }

    /*
     * Daftar peran akan berubah sesuai kegiatan dan tingkat.
     */
    public static List<String> getPeranList(String kategori, String kegiatan, String tingkat) {
        if (eq(kategori, KATEGORI_WAJIB)) {
            return list("Peserta");
        }

        if (eq(kategori, KATEGORI_ORGANISASI)) {
            if (eq(kegiatan, "Pengurus Organisasi")) {
                return list("Pengurus Inti", "Anggota");
            }

            if (eq(kegiatan, "Panitia dalam Kegiatan Kemahasiswaan")) {
                return list("Pengurus Inti", "Anggota");
            }

            if (eq(kegiatan, "Pendukung / Pengisi / Partisipan Kegiatan")) {
                return list("Partisipan");
            }
        }

        if (eq(kategori, KATEGORI_PENALARAN)) {
            if (eq(kegiatan, "Prestasi Lomba Karya Tulis / Inovasi")) {
                if (eq(tingkat, "Ormawa")) {
                    return list("Juara");
                }
                return list("Juara", "Peserta");
            }

            if (eq(kegiatan, "Forum Ilmiah")) {
                return list("Pembicara", "Peserta");
            }

            if (eq(kegiatan, "Karya Ilmiah Jurnal")
                    || eq(kegiatan, "Karya Ilmiah Prosiding")) {
                return list("Ketua", "Anggota");
            }

            if (eq(kegiatan, "Karya di Majalah / Surat Kabar")) {
                return list("Penulis");
            }

            if (eq(kegiatan, "Menghadiri Seminar Proposal / Skripsi")) {
                return list("Peserta");
            }

            if (eq(kegiatan, "Asisten Lab 1 Semester")) {
                return list("Asisten Lab");
            }

            if (eq(kegiatan, "Workshop Institusi")) {
                return list("Peserta");
            }

            if (eq(kegiatan, "SDM Projek Inovasi")) {
                return list("Peserta");
            }
        }

        if (eq(kategori, KATEGORI_MINAT)) {
            if (eq(kegiatan, "Prestasi Akademik / Non Akademik")) {
                return list("Juara", "Peserta");
            }

            if (eq(kegiatan, "Latihan Rutin Ormawa")) {
                return list("Peserta");
            }

            if (eq(kegiatan, "Anggota Insidental Kegiatan Institut")) {
                return list("Anggota");
            }

            if (eq(kegiatan, "Model Foto / Video Publikasi")) {
                return list("Model");
            }

            if (eq(kegiatan, "Pengembangan Perangkat Pembelajaran")) {
                return list("Peserta");
            }
        }

        if (eq(kategori, KATEGORI_PENGABDIAN)) {
            if (eq(kegiatan, "Penanganan Bencana")) {
                return list("Relawan");
            }

            if (eq(kegiatan, "Magang Kerja Bukan PKL")) {
                return list("Mahasiswa Magang");
            }

            return list("Peserta");
        }

        return list("Tidak Ada");
    }

    /*
     * Mode Online/Offline hanya dibutuhkan pada kegiatan tertentu.
     * Untuk aplikasi sederhana ini, mode hanya aktif pada Forum Ilmiah jika perannya Peserta.
     */
    public static List<String> getModeList(String kategori, String kegiatan, String tingkat, String peran) {
        if (eq(kategori, KATEGORI_PENALARAN)
                && eq(kegiatan, "Forum Ilmiah")
                && eq(peran, "Peserta")) {
            return list(MODE_ONLINE, MODE_OFFLINE);
        }

        return list(MODE_TIDAK_ADA);
    }

    /*
     * Method utama untuk menghitung poin SKP.
     * Method ini akan dipanggil setiap kali user mengubah pilihan spinner.
     */
    public static int calculatePoint(String kategori,
                                     String kegiatan,
                                     String tingkat,
                                     String peran,
                                     String mode) {

        if (eq(kategori, KATEGORI_WAJIB)) {
            return calculateWajib(kegiatan);
        }

        if (eq(kategori, KATEGORI_ORGANISASI)) {
            return calculateOrganisasi(kegiatan, tingkat, peran);
        }

        if (eq(kategori, KATEGORI_PENALARAN)) {
            return calculatePenalaran(kegiatan, tingkat, peran, mode);
        }

        if (eq(kategori, KATEGORI_MINAT)) {
            return calculateMinat(kegiatan, tingkat, peran);
        }

        if (eq(kategori, KATEGORI_PENGABDIAN)) {
            return calculatePengabdian(kegiatan, tingkat);
        }

        return 0;
    }

    private static int calculateWajib(String kegiatan) {
        if (eq(kegiatan, "PKKMB")) return 25;
        if (eq(kegiatan, "Dies Natalis")) return 15;
        if (eq(kegiatan, "Upacara Bendera 17 Agustus")) return 10;
        if (eq(kegiatan, "Alumni Pulang Campus")) return 20;
        if (eq(kegiatan, "Seminar Institut Bisnis dan Teknologi Indonesia")) return 20;
        if (eq(kegiatan, "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia")) return 10;

        return 0;
    }

    private static int calculateOrganisasi(String kegiatan, String tingkat, String peran) {
        if (eq(kegiatan, "Pengurus Organisasi")) {
            if (eq(tingkat, "Internasional") && eq(peran, "Pengurus Inti")) return 50;
            if (eq(tingkat, "Internasional") && eq(peran, "Anggota")) return 30;

            if (eq(tingkat, "Nasional") && eq(peran, "Pengurus Inti")) return 40;
            if (eq(tingkat, "Nasional") && eq(peran, "Anggota")) return 20;

            if (eq(tingkat, "Regional") && eq(peran, "Pengurus Inti")) return 30;
            if (eq(tingkat, "Regional") && eq(peran, "Anggota")) return 10;

            if (eq(tingkat, "BEM") && eq(peran, "Pengurus Inti")) return 25;
            if (eq(tingkat, "BEM") && eq(peran, "Anggota")) return 15;

            if (eq(tingkat, "Himaprodi / UKM / FPK") && eq(peran, "Pengurus Inti")) return 20;
            if (eq(tingkat, "Himaprodi / UKM / FPK") && eq(peran, "Anggota")) return 10;
        }

        if (eq(kegiatan, "Panitia dalam Kegiatan Kemahasiswaan")) {
            if (eq(tingkat, "Internasional") && eq(peran, "Pengurus Inti")) return 30;
            if (eq(tingkat, "Internasional") && eq(peran, "Anggota")) return 25;

            if (eq(tingkat, "Nasional") && eq(peran, "Pengurus Inti")) return 25;
            if (eq(tingkat, "Nasional") && eq(peran, "Anggota")) return 20;

            if (eq(tingkat, "Regional") && eq(peran, "Pengurus Inti")) return 20;
            if (eq(tingkat, "Regional") && eq(peran, "Anggota")) return 15;

            if (eq(tingkat, "Institusi (PKKMB / Dies Natalis / Wisuda)") && eq(peran, "Pengurus Inti")) return 20;
            if (eq(tingkat, "Institusi (PKKMB / Dies Natalis / Wisuda)") && eq(peran, "Anggota")) return 15;

            if (eq(tingkat, "Ormawa") && eq(peran, "Pengurus Inti")) return 10;
            if (eq(tingkat, "Ormawa") && eq(peran, "Anggota")) return 5;
        }

        if (eq(kegiatan, "Pendukung / Pengisi / Partisipan Kegiatan")) {
            if (eq(tingkat, "Internasional")) return 25;
            if (eq(tingkat, "Nasional")) return 20;
            if (eq(tingkat, "Regional")) return 15;
            if (eq(tingkat, "Institusi")) return 10;
            if (eq(tingkat, "Ormawa")) return 10;
        }

        return 0;
    }

    private static int calculatePenalaran(String kegiatan, String tingkat, String peran, String mode) {
        if (eq(kegiatan, "Prestasi Lomba Karya Tulis / Inovasi")) {
            if (eq(tingkat, "Internasional") && eq(peran, "Juara")) return 50;
            if (eq(tingkat, "Internasional") && eq(peran, "Peserta")) return 40;

            if (eq(tingkat, "Nasional") && eq(peran, "Juara")) return 35;
            if (eq(tingkat, "Nasional") && eq(peran, "Peserta")) return 25;

            if (eq(tingkat, "Regional") && eq(peran, "Juara")) return 30;
            if (eq(tingkat, "Regional") && eq(peran, "Peserta")) return 20;

            if (eq(tingkat, "Institusi") && eq(peran, "Juara")) return 25;
            if (eq(tingkat, "Institusi") && eq(peran, "Peserta")) return 15;

            if (eq(tingkat, "Ormawa") && eq(peran, "Juara")) return 20;
        }

        if (eq(kegiatan, "Forum Ilmiah")) {
            if (eq(peran, "Pembicara")) {
                if (eq(tingkat, "Internasional")) return 100;
                if (eq(tingkat, "Nasional")) return 75;
                if (eq(tingkat, "Regional")) return 50;
                if (eq(tingkat, "Institusi")) return 25;
            }

            if (eq(peran, "Peserta")) {
                if (eq(tingkat, "Internasional") && eq(mode, MODE_ONLINE)) return 20;
                if (eq(tingkat, "Internasional") && eq(mode, MODE_OFFLINE)) return 30;

                if (eq(tingkat, "Nasional") && eq(mode, MODE_ONLINE)) return 10;
                if (eq(tingkat, "Nasional") && eq(mode, MODE_OFFLINE)) return 20;

                if (eq(tingkat, "Regional") && eq(mode, MODE_ONLINE)) return 5;
                if (eq(tingkat, "Regional") && eq(mode, MODE_OFFLINE)) return 15;

                if (eq(tingkat, "Institusi") && eq(mode, MODE_ONLINE)) return 5;
                if (eq(tingkat, "Institusi") && eq(mode, MODE_OFFLINE)) return 10;
            }
        }

        if (eq(kegiatan, "Karya Ilmiah Jurnal")) {
            if (eq(tingkat, "Internasional Grade A") && eq(peran, "Ketua")) return 100;
            if (eq(tingkat, "Internasional Grade A") && eq(peran, "Anggota")) return 80;

            if (eq(tingkat, "Internasional Grade B") && eq(peran, "Ketua")) return 85;
            if (eq(tingkat, "Internasional Grade B") && eq(peran, "Anggota")) return 60;

            if (eq(tingkat, "Internasional Grade C") && eq(peran, "Ketua")) return 45;
            if (eq(tingkat, "Internasional Grade C") && eq(peran, "Anggota")) return 20;

            if (eq(tingkat, "Nasional SINTA 1") && eq(peran, "Ketua")) return 65;
            if (eq(tingkat, "Nasional SINTA 1") && eq(peran, "Anggota")) return 40;

            if (eq(tingkat, "Nasional SINTA 2") && eq(peran, "Ketua")) return 45;
            if (eq(tingkat, "Nasional SINTA 2") && eq(peran, "Anggota")) return 20;

            if (eq(tingkat, "Nasional SINTA 3") && eq(peran, "Ketua")) return 35;
            if (eq(tingkat, "Nasional SINTA 3") && eq(peran, "Anggota")) return 25;

            if (eq(tingkat, "Nasional SINTA 4-6") && eq(peran, "Ketua")) return 25;
            if (eq(tingkat, "Nasional SINTA 4-6") && eq(peran, "Anggota")) return 15;

            if (eq(tingkat, "Nasional Tidak Terakreditasi") && eq(peran, "Ketua")) return 20;
            if (eq(tingkat, "Nasional Tidak Terakreditasi") && eq(peran, "Anggota")) return 10;
        }

        if (eq(kegiatan, "Karya Ilmiah Prosiding")) {
            if (eq(tingkat, "Internasional Terindeks Scopus") && eq(peran, "Ketua")) return 45;
            if (eq(tingkat, "Internasional Terindeks Scopus") && eq(peran, "Anggota")) return 20;

            if (eq(tingkat, "Internasional Terindeks") && eq(peran, "Ketua")) return 35;
            if (eq(tingkat, "Internasional Terindeks") && eq(peran, "Anggota")) return 25;

            if (eq(tingkat, "Internasional Tidak Terindeks ber-ISBN") && eq(peran, "Ketua")) return 25;
            if (eq(tingkat, "Internasional Tidak Terindeks ber-ISBN") && eq(peran, "Anggota")) return 15;

            if (eq(tingkat, "Nasional Terindeks") && eq(peran, "Ketua")) return 25;
            if (eq(tingkat, "Nasional Terindeks") && eq(peran, "Anggota")) return 15;

            if (eq(tingkat, "Nasional Tidak Terindeks") && eq(peran, "Ketua")) return 20;
            if (eq(tingkat, "Nasional Tidak Terindeks") && eq(peran, "Anggota")) return 10;
        }

        if (eq(kegiatan, "Karya di Majalah / Surat Kabar")) {
            if (eq(tingkat, "Internasional")) return 50;
            if (eq(tingkat, "Nasional")) return 35;
            if (eq(tingkat, "Regional")) return 25;
            if (eq(tingkat, "Institusi")) return 10;
        }

        if (eq(kegiatan, "Menghadiri Seminar Proposal / Skripsi")) {
            return 5;
        }

        if (eq(kegiatan, "Asisten Lab 1 Semester")) {
            return 30;
        }

        if (eq(kegiatan, "Workshop Institusi")) {
            return 20;
        }

        if (eq(kegiatan, "SDM Projek Inovasi")) {
            if (eq(tingkat, "Internasional")) return 100;
            if (eq(tingkat, "Nasional")) return 75;
            if (eq(tingkat, "Regional")) return 50;
            if (eq(tingkat, "Institusi")) return 25;
        }

        return 0;
    }

    private static int calculateMinat(String kegiatan, String tingkat, String peran) {
        if (eq(kegiatan, "Prestasi Akademik / Non Akademik")) {
            if (eq(tingkat, "Internasional") && eq(peran, "Juara")) return 50;
            if (eq(tingkat, "Internasional") && eq(peran, "Peserta")) return 40;

            if (eq(tingkat, "Nasional") && eq(peran, "Juara")) return 35;
            if (eq(tingkat, "Nasional") && eq(peran, "Peserta")) return 25;

            if (eq(tingkat, "Regional") && eq(peran, "Juara")) return 30;
            if (eq(tingkat, "Regional") && eq(peran, "Peserta")) return 20;

            if (eq(tingkat, "Institusi") && eq(peran, "Juara")) return 25;
            if (eq(tingkat, "Institusi") && eq(peran, "Peserta")) return 15;

            if (eq(tingkat, "Ormawa") && eq(peran, "Juara")) return 20;
            if (eq(tingkat, "Ormawa") && eq(peran, "Peserta")) return 5;
        }

        if (eq(kegiatan, "Latihan Rutin Ormawa")) return 25;
        if (eq(kegiatan, "Anggota Insidental Kegiatan Institut")) return 15;
        if (eq(kegiatan, "Model Foto / Video Publikasi")) return 10;
        if (eq(kegiatan, "Pengembangan Perangkat Pembelajaran")) return 35;

        return 0;
    }

    private static int calculatePengabdian(String kegiatan, String tingkat) {
        if (eq(kegiatan, "Pengabdian Kepada Masyarakat")) {
            if (eq(tingkat, "Internasional")) return 60;
            if (eq(tingkat, "Nasional")) return 50;
            if (eq(tingkat, "Regional")) return 30;
            if (eq(tingkat, "Institusi")) return 20;
            if (eq(tingkat, "Ormawa")) return 10;
        }

        if (eq(kegiatan, "Penanganan Bencana")) return 50;
        if (eq(kegiatan, "Studi Banding")) return 25;
        if (eq(kegiatan, "Magang Kerja Bukan PKL")) return 35;

        return 0;
    }

    /*
     * Method ini dipakai untuk mengecek batas maksimal kegiatan tertentu.
     * Jika return -1 artinya kegiatan tidak punya batas maksimal.
     */
    public static int getQuotaLimit(String kegiatan, String tingkat) {
        if (eq(kegiatan, "Seminar Institut Bisnis dan Teknologi Indonesia")) {
            return 3;
        }

        if (eq(kegiatan, "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia")) {
            return 2;
        }

        if (eq(kegiatan, "Panitia dalam Kegiatan Kemahasiswaan") && eq(tingkat, "Ormawa")) {
            return 6;
        }

        if (eq(kegiatan, "Forum Ilmiah")) {
            return 3;
        }

        return -1;
    }

    public static boolean hasQuotaLimit(String kegiatan, String tingkat) {
        return getQuotaLimit(kegiatan, tingkat) > 0;
    }

    /*
     * Daftar SKP wajib institusi.
     * Data ini dipakai Dashboard untuk menampilkan pengingat SKP wajib yang belum dicatat.
     */
    public static List<String> getWajibActivities() {
        return list(
                "PKKMB",
                "Dies Natalis",
                "Upacara Bendera 17 Agustus",
                "Alumni Pulang Campus",
                "Seminar Institut Bisnis dan Teknologi Indonesia",
                "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia"
        );
    }

    /*
     * Helper untuk membuat list agar kode lebih pendek dan rapi.
     */
    private static List<String> list(String... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

    /*
     * Helper untuk membandingkan string agar tidak error jika ada nilai null.
     */
    private static boolean eq(String a, String b) {
        return a != null && a.equals(b);
    }
}