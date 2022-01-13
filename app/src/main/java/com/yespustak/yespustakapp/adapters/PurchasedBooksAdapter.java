package com.yespustak.yespustakapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tonyodev.fetch2.Status;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PurchasedBooksAdapter extends RecyclerView.Adapter<PurchasedBooksAdapter.ViewHolder> {

    private static final String TAG = "PurchasedBooksAdapter";

    AdapterItemClickListener listener;
    List<DownloadBook> downloadBooks;

    public PurchasedBooksAdapter(List<DownloadBook> downloadBooks, AdapterItemClickListener listener) {
        this.listener = listener;
        this.downloadBooks = downloadBooks;
    }

    @NotNull
    @Override
    public PurchasedBooksAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_item, parent, false);
        return new PurchasedBooksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PurchasedBooksAdapter.ViewHolder holder, int position) {
        DownloadBook book = downloadBooks.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvPublication.setText(utils.getStringResource(R.string.text_by_publication_name, book.getPublication()));

        if (book.getStatus() == Status.COMPLETED)
            holder.tvFileStatus.setText(R.string.title_downloaded);
        else if (book.getStatus() == Status.DELETED)
            holder.tvFileStatus.setText(R.string.title_deleted);
        else
            holder.tvFileStatus.setText(R.string.title_downloading);

        holder.ivDownload.setVisibility(book.getStatus() == Status.DELETED ? View.VISIBLE : View.GONE);

        Picasso.get().load(Constants.DASHBOARD_URL + book.getImgUrl()).placeholder(R.drawable.ic_baseline_image_24).error(R.drawable.img_not_available_thumb).into(holder.ivThumb, new Callback() {
            @Override
            public void onSuccess() {
                holder.pbThumb.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
                holder.pbThumb.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onClick(downloadBooks.get(position)));
        holder.ivDownload.setOnClickListener(v -> listener.onDownload(downloadBooks.get(position)));
    }

    @Override
    public int getItemCount() {
        return downloadBooks.size();
    }

    public void setBooks(List<DownloadBook> books) {
        downloadBooks = books;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvPublication, tvFileStatus;
        public ImageView ivThumb, ivDownload;
        public ProgressBar pbThumb;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPublication = itemView.findViewById(R.id.tv_publication);
            tvFileStatus = itemView.findViewById(R.id.tv_file_status);
            pbThumb = itemView.findViewById(R.id.pb_thumb);
            ivDownload = itemView.findViewById(R.id.iv_download);
            ivThumb = itemView.findViewById(R.id.iv_thumb);
        }
    }
}
