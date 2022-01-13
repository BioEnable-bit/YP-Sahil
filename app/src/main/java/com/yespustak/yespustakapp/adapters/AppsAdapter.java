package com.yespustak.yespustakapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.AppModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    Context context;
    ArrayList<AppModel> arrayList;
    AdapterItemClickListener listener;
    int myViewType = 0;

    public AppsAdapter(Context context, ArrayList<AppModel> arrayList, AdapterItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    public AppsAdapter(Context context, ArrayList<AppModel> arrayList, AdapterItemClickListener listener, int viewType) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
        this.myViewType = viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (myViewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel appModel = arrayList.get(position);
        holder.tvName.setText(appModel.getName());
        holder.ivIcon.setImageDrawable(appModel.getIcon());

        if (myViewType == 0)
            holder.itemView.setOnClickListener(v -> listener.onClick(appModel));


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
