package com.yespustak.yespustakapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.models.VerticalModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;

import java.util.ArrayList;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.VerticalRVViewHolder> {

    Context context;//not using. safe to remove
    ArrayList<VerticalModel> arrayList;
    AdapterItemClickListener listener;

    public VerticalRecyclerViewAdapter(Context context, ArrayList<VerticalModel> arrayList, AdapterItemClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VerticalRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical, parent, false);
        return new VerticalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRVViewHolder holder, int position) {
        final VerticalModel verticalModel = arrayList.get(position);
        String title = verticalModel.getTitle();
        ArrayList<BookModel> singleItem = verticalModel.getArrayList();
        holder.textViewTitle.setText(title);

//        HorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(context, singleItem, activity);
        HorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(singleItem, listener);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(horizontalRecyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void clearItem(){
        arrayList.clear();
        notifyDataSetChanged();
    }

    public static class VerticalRVViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textViewTitle;
        Button btnMore;

        public VerticalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView1);
            textViewTitle = itemView.findViewById(R.id.txtTitle1);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
