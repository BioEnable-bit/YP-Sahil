package com.yespustak.yespustakapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRecyclerViewAdapter2 extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter2.HorizontalRVViewHolder> {
    private static final String TAG = "HorizontalRecyclerViewA";

//    Context context; //there is no use of this variable. safe to remove
    ArrayList<BookModel> arrayList;
    AdapterItemClickListener clickListener;

    public HorizontalRecyclerViewAdapter2(ArrayList<BookModel> arrayList, AdapterItemClickListener clickListener) {
//        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
    }

    public void setBooks(List<BookModel> books) {
        arrayList.clear();
        arrayList.addAll(books);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorizontalRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book2, parent, false);
        return new HorizontalRVViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final HorizontalRVViewHolder holder, int position) {
        BookModel bookModel = arrayList.get(position);
        holder.textViewTitle.setText(bookModel.getTitle());
        holder.tvPublication.setText(utils.getStringResource(R.string.text_by_publication_name, bookModel.getPublication()));
        holder.tvMrp.setText(utils.getStringResource(R.string.text_mrp, bookModel.getMrp()));
        holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvYpp.setText(utils.getStringResource(R.string.text_ypp, bookModel.getYpp()));

        Log.e(TAG, "Book NCRT : "+bookModel.getNcrt_boook_flag());

        if(bookModel.getPublication().equals("NCERT"))
        {
            holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvYpp.setText("YPP: FREE");
        }
        else
        {
            holder.tvMrp.setText(utils.getStringResource(R.string.text_mrp, bookModel.getMrp()));
            holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvYpp.setText(utils.getStringResource(R.string.text_ypp, bookModel.getYpp()));
        }

        holder.clCard.setBackground(AppCompatResources.getDrawable(holder.clCard.getContext(), bookModel.getCardBgDrawable()));


        holder.itemView.setOutlineAmbientShadowColor(holder.itemView.getContext().getColor(bookModel.getCardShadowColor()));
        holder.itemView.setOutlineSpotShadowColor(holder.itemView.getContext().getColor(bookModel.getCardShadowColor()));

        holder.itemView.setOnClickListener(v -> clickListener.onClick(bookModel));


        Picasso.get().load(Constants.DASHBOARD_URL + bookModel.getImageUrl()).placeholder(R.drawable.ic_baseline_image_24).error(R.drawable.img_not_available_thumb).into(holder.imageViewThumb, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: ", e);
                holder.progressBar.setVisibility(View.GONE);
//                utils.showToast(e + "\n" + bookModel.getImageUrl());
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class HorizontalRVViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, tvPublication, tvMrp, tvYpp;
        ImageView imageViewThumb;
        ProgressBar progressBar;
        View clCard;

        public HorizontalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            clCard = itemView.findViewById(R.id.cl_card);
            textViewTitle = itemView.findViewById(R.id.tv_title);
            tvPublication = itemView.findViewById(R.id.tv_publication);
            tvMrp = itemView.findViewById(R.id.tvMrp);
            tvYpp = itemView.findViewById(R.id.tvYpp);
            imageViewThumb = itemView.findViewById(R.id.iv_thumb);
            progressBar = itemView.findViewById(R.id.pb_thumb);
        }
    }
}
