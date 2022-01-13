package com.yespustak.yespustakapp.adapters;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tonyodev.fetch2.Status;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.utils.ActionListener;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.OnMenuItemClickListener;
import com.yespustak.yespustakapp.utils.utils;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> implements OnMenuItemClickListener {
    private static final String TAG = "DownloadAdapter";

    @NonNull
    List<DownloadBook> downloadBooks;

    private ActionListener actionListener;
    private final AdapterItemClickListener listener;
    private OnMenuItemClickListener menuItemClickListener;

//    public DownloadAdapter(@NonNull List<DownloadBook> downloadBooks, @NonNull ActionListener actionListener) {
//        this.downloadBooks = downloadBooks;
//        this.actionListener = actionListener;
//    }

    public DownloadAdapter(@NonNull List<DownloadBook> downloadBooks, @NonNull AdapterItemClickListener listener) {
        this.downloadBooks = downloadBooks;
        this.listener = listener;
    }

    public void setDownloadBooks(List<DownloadBook> books) {
        downloadBooks = books;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false);
        return new ViewHolder(view, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        DownloadBook downloadBook = downloadBooks.get(position);
        holder.tvTitle.setText(downloadBook.getTitle());
        holder.tvPublication.setText(utils.getStringResource(R.string.text_by_publication_name, downloadBook.getPublication()));

        holder.ibMore.setVisibility(downloadBook.getStatus() == Status.COMPLETED ? View.VISIBLE : View.GONE);

        Picasso.get().load(Constants.DASHBOARD_URL + downloadBook.getImgUrl()).placeholder(R.drawable.ic_baseline_image_24).error(R.drawable.img_not_available_thumb).into(holder.ivThumb, new Callback() {
            @Override
            public void onSuccess() {
                holder.isImageLoaded = true;
                holder.pbThumb.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
                holder.pbThumb.setVisibility(View.GONE);
            }
        });

        holder.llDownloadInfo.setVisibility(View.VISIBLE);

        if (downloadBook.getStatus() != null) {
            switch (downloadBook.getStatus()) {
                case NONE:
                case COMPLETED:
                    holder.llDownloadInfo.setVisibility(View.GONE);
//                holder.pbDownload.setVisibility(View.GONE);
                    break;
                case DOWNLOADING:
                    holder.tvStatus.setText(utils.getStringResource(R.string.percent_progress, downloadBook.getProgress()));
                    holder.pbDownload.setVisibility(View.VISIBLE);
                    holder.llDownloadInfo.setVisibility(View.VISIBLE);
                    holder.pbDownload.setIndeterminate(false);
                    holder.pbDownload.setProgress(downloadBook.getProgress());
                    break;
                case QUEUED:
                    holder.llDownloadInfo.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText(utils.isConnectionAvailable() ? "Waiting in queue" : "Waiting for network");
                    holder.pbDownload.setVisibility(View.VISIBLE);
                    if (downloadBook.getProgress() > 0) {
                        holder.pbDownload.setIndeterminate(false);
                        holder.pbDownload.setProgress(downloadBook.getProgress());
                    } else
                        holder.pbDownload.setIndeterminate(true);

                case FAILED:
//                    holder.llDownloadInfo.setVisibility(View.VISIBLE);
//                    holder.tvStatus.setText("Download fail");
//                    if (downloadBook.getProgress() > 0) {
//                        holder.pbDownload.setIndeterminate(false);
//                        holder.pbDownload.setProgress(downloadBook.getProgress());
//                    } else
//                        holder.pbDownload.setVisibility(View.GONE);
//                    break;
                default:
                    break;

            }
        }
//        if (idDownloading) {
//            holder.pbDownload.setIndeterminate(false);
//            holder.pbDownload.setProgress(downloadBook.getProgress());
//        } else {
//            holder.pbDownload.setIndeterminate(true);
//        }

//        holder.llDownloadInfo.setVisibility(downloadBook.getStatus() == Status.COMPLETED ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
//            listener.onClick(downloadBook);
            if (downloadBook.getStatus() == Status.COMPLETED)
                listener.onClick(downloadBook);
            else
                utils.showToast("Book download in progress");
        });
        //TODO for QUEUED show waiting test progressbar

    }


    @Override
    public int getItemCount() {
        return downloadBooks.size();
    }

//    private String getStatusString(Status status) {
//        switch (status) {
//            case COMPLETED:
//                return "Done";
//            case DOWNLOADING:
//                return "Downloading";
//            case FAILED:
//                return "Error";
//            case PAUSED:
//                return "Paused";
//            case QUEUED:
//                return "Waiting in Queue";
//            case REMOVED:
//                return "Removed";
//            case NONE:
//                return "Not Queued";
//            default:
//                return "Unknown";
//        }
//    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        if (item.getItemId() == R.id.mi_delete)
            listener.onDelete(downloadBooks.get(position));
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        //TODO add final
        public TextView tvTitle, tvPublication, tvProgress, tvSpeed, tvStatus;//, tvEta;
        public ImageView ivThumb;
        public ImageButton ibMore;
        public final ProgressBar pbThumb, pbDownload;
        private final LinearLayout llDownloadInfo;
        boolean isImageLoaded = false;
        OnMenuItemClickListener menuItemClickListener;
//        public final Button btnAction;

        ViewHolder(View itemView, OnMenuItemClickListener menuItemClickListener) {
            super(itemView);
            this.menuItemClickListener = menuItemClickListener;
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPublication = itemView.findViewById(R.id.tv_publication);
            ibMore = itemView.findViewById(R.id.ib_more);
            pbThumb = itemView.findViewById(R.id.pb_thumb);
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            tvStatus = itemView.findViewById(R.id.tv_status);
            pbDownload = itemView.findViewById(R.id.pb_download);
            llDownloadInfo = itemView.findViewById(R.id.ll_progress);
//            btnAction = itemView.findViewById(R.id.btn_action);
//            tvProgress = itemView.findViewById(R.id.tv_progress);
//            tvEta = itemView.findViewById(R.id.remaining_TextView);
//            tvSpeed = itemView.findViewById(R.id.tv_speed);

            //set click listeners
            ibMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: " + getAbsoluteAdapterPosition());
            showPopupMenu(v);
        }


        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.downloaded_book_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return menuItemClickListener.onMenuItemClick(item, getAbsoluteAdapterPosition());
        }
    }
}
