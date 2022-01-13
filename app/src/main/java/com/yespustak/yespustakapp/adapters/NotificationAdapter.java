package com.yespustak.yespustakapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.NotificationModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationModel> arrayList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        NotificationModel notificationModel = arrayList.get(position);
        holder.tvTitle.setText(notificationModel.getTitle());
        holder.tvDesc.setText(notificationModel.getDesc());
        Picasso.get().load(notificationModel.getImgUrl()).placeholder(R.drawable.ic_logo).error(R.drawable.ic_launcher_background).into(holder.img
                , new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.pbNotification.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.pbNotification.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvDesc;
        ProgressBar pbNotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgNotification);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvDesc = itemView.findViewById(R.id.tvNotificationDescription);
            pbNotification = itemView.findViewById(R.id.pbNotification);
        }
    }
}
