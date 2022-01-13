package com.yespustak.yespustakapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.CartModel;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;

public class CartAdapter extends RecyclerSwipeAdapter<CartAdapter.ViewHolder> {
    private static final String TAG = "CartAdapter";

    ArrayList<CartModel> arrayList;
    ArrayList<CartModel> filteredList;
    AdapterListener listener;
    SwipeItemRecyclerMangerImpl swipeItemManger;

    public CartAdapter(ArrayList<CartModel> cartList, AdapterListener listener) {
        this.arrayList = cartList;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(cartList);
        this.listener = listener;
        swipeItemManger = new SwipeItemRecyclerMangerImpl(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CartModel cartModel = arrayList.get(position);

        double saved = cartModel.getBookMrp() - cartModel.getBookYpp();

        holder.tvTitle.setText(cartModel.getBookTitle());
        holder.tvPrice.setText(utils.getStringResource(R.string.text_price_with_rs_sign, cartModel.getBookYpp()));
        holder.tvMrp.setText(utils.getStringResource(R.string.text_price_with_rs_sign, cartModel.getBookMrp()));
        holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//TODO check how much resources it use
        holder.tvSaved.setText(utils.getStringResource(R.string.text_saved_price_with_rs_sign, saved));
        holder.tvSubject.setText(cartModel.getBookSubject());
        holder.tvStandard.setText(cartModel.getBookStandard());

        holder.swipeLayout.getSurfaceView().setOnClickListener(v -> listener.onItemClick(position));
        holder.ivDelete.setOnClickListener(v -> listener.onDeleteClick(position));
        holder.rvSwipeDelete.setOnClickListener(v -> listener.onDeleteClick(position));

        Picasso.get().load(Constants.DASHBOARD_URL + cartModel.getBookFrontImageUrl())
                .placeholder(R.drawable.ic_baseline_image_24).error(R.drawable.img_not_available_thumb)
                .into(holder.ivThumb, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.pbThumb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError: ", e);
                        holder.pbThumb.setVisibility(View.GONE);
                        utils.showToast(e + "\n" + cartModel.getBookFrontImageUrl());
                    }
                });

        swipeItemManger.bindView(holder.itemView, position);
        holder.swipeLayout.addSwipeListener(swipeListener);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    public void removeItem(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);

        //this line below updates the list items after the deleted item
        notifyItemRangeChanged(position, getItemCount());

        //close this item
        swipeItemManger.closeItem(position);
    }

    public void clearItem() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void filterItem(String string) {
        arrayList.clear();
        if (string.length() == 0) {
            arrayList.addAll(filteredList);
        } else {
            for (CartModel cartModel : filteredList) {
                if (cartModel.getBookTitle().equals(string)) {
                    arrayList.add(cartModel);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice, tvStandard, tvSubject, tvMrp, tvSaved;
        ImageView ivThumb, ivDelete;
        RelativeLayout rvSwipeDelete, cardView;
        SwipeLayout swipeLayout;
        ProgressBar pbThumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.book_title);
            tvPrice = itemView.findViewById(R.id.book_price);
            tvStandard = itemView.findViewById(R.id.text_standard);
            tvSubject = itemView.findViewById(R.id.text_subject);
            ivThumb = itemView.findViewById(R.id.book_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            pbThumb = itemView.findViewById(R.id.pb_thumb);
            cardView = itemView.findViewById(R.id.cardView);
            tvMrp = itemView.findViewById(R.id.book_mrp);
            tvSaved = itemView.findViewById(R.id.book_saving);
            rvSwipeDelete = itemView.findViewById(R.id.rv_swipe_actions);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);

        }
    }


    SwipeLayout.SwipeListener swipeListener = new SwipeLayout.SwipeListener() {
        @Override
        public void onStartOpen(SwipeLayout layout) {
            swipeItemManger.closeAllExcept(layout);
        }

        @Override
        public void onOpen(SwipeLayout layout) {

        }

        @Override
        public void onStartClose(SwipeLayout layout) {

        }

        @Override
        public void onClose(SwipeLayout layout) {

        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

        }
    };

    //create listener interface
    public interface AdapterListener {

        //we can pass CartModel object also instead of position
        void onDeleteClick(int position);
        void onItemClick(int position);
    }
}
