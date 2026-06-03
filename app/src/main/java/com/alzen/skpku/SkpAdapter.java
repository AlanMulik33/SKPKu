package com.alzen.skpku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/*
 * Adapter ini bertugas menampilkan data SKP ke RecyclerView.
 * Setiap data SKP akan ditampilkan dalam bentuk card sederhana.
 */
public class SkpAdapter extends RecyclerView.Adapter<SkpAdapter.SkpViewHolder> {

    private Context context;
    private List<Skp> skpList;
    private OnItemClickListener listener;

    /*
     * Interface ini dipakai agar MainActivity bisa menangani klik item.
     * Saat item diklik, data Skp akan dikirim ke halaman detail.
     */
    public interface OnItemClickListener {
        void onItemClick(Skp skp);
    }

    public SkpAdapter(Context context, List<Skp> skpList, OnItemClickListener listener) {
        this.context = context;
        this.skpList = new ArrayList<>();

        if (skpList != null) {
            this.skpList.addAll(skpList);
        }

        this.listener = listener;
    }

    @NonNull
    @Override
    public SkpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
         * Menghubungkan layout item_skp.xml dengan RecyclerView.
         */
        View view = LayoutInflater.from(context).inflate(R.layout.item_skp, parent, false);
        return new SkpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkpViewHolder holder, int position) {
        /*
         * Mengambil satu data SKP berdasarkan posisi item.
         */
        Skp skp = skpList.get(position);

        holder.tvItemNama.setText(skp.getNama_kegiatan());
        holder.tvItemKategori.setText(skp.getKategori_bidang());
        holder.tvItemTanggal.setText(skp.getTanggal_input());
        holder.tvItemPoin.setText(skp.getPoin_skp() + " Poin");

        /*
         * Saat item diklik, data dikirim ke listener.
         * Nanti MainActivity yang akan membuka DetailSkpActivity.
         */
        holder.itemRoot.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(skp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return skpList.size();
    }

    /*
     * Method ini dipakai untuk mengganti seluruh data RecyclerView.
     * Biasanya dipanggil setelah data baru berhasil dibaca dari Supabase.
     */
    public void setData(List<Skp> newList) {
        this.skpList.clear();

        if (newList != null) {
            this.skpList.addAll(newList);
        }

        notifyDataSetChanged();
    }

    /*
     * Method ini dipakai untuk mengambil data berdasarkan posisi.
     */
    public Skp getItem(int position) {
        return skpList.get(position);
    }

    /*
     * ViewHolder menyimpan komponen UI yang ada di item_skp.xml.
     * Tujuannya agar RecyclerView lebih hemat memori dan tidak mencari view berulang-ulang.
     */
    public static class SkpViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemRoot;
        TextView tvItemNama;
        TextView tvItemKategori;
        TextView tvItemTanggal;
        TextView tvItemPoin;

        public SkpViewHolder(@NonNull View itemView) {
            super(itemView);

            itemRoot = itemView.findViewById(R.id.itemRoot);
            tvItemNama = itemView.findViewById(R.id.tvItemNama);
            tvItemKategori = itemView.findViewById(R.id.tvItemKategori);
            tvItemTanggal = itemView.findViewById(R.id.tvItemTanggal);
            tvItemPoin = itemView.findViewById(R.id.tvItemPoin);
        }
    }
}