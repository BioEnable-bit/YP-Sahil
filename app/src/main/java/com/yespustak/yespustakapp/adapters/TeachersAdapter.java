package com.yespustak.yespustakapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.models.FaqModel;
import com.yespustak.yespustakapp.models.TeachersModel;
import com.yespustak.yespustakapp.utils.utils;

import java.util.List;

public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.ViewHolder> {

    List<TeachersModel> arrayList;
    int expandedPosition = -1;
    int previousExpandedPosition = -1;
    Context context;


    public TeachersAdapter(List<TeachersModel> arrayList, Context context ) {
        this.arrayList = arrayList;
        this.context = context;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TeachersModel faqModel = arrayList.get(position);
        holder.tvTitle.setText(faqModel.getName());
        holder.tvDesc.setText(faqModel.getCreate_at());

        final boolean isExpanded = position == expandedPosition;
        holder.tvDesc.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);

        holder.ivExpand.setImageDrawable(AppCompatResources.getDrawable(holder.ivExpand.getContext(),
                isExpanded ? R.drawable.ic_baseline_keyboard_arrow_up_24 : R.drawable.ic_baseline_keyboard_arrow_down_24));
        holder.ivExpand.setColorFilter(holder.ivExpand.getContext().getResources()
                .getColor(isExpanded ? R.color.colorPrimary : android.R.color.darker_gray, holder.ivExpand.getContext().getTheme()));

        if (isExpanded)
            previousExpandedPosition = position;

        holder.itemView.setOnClickListener(v -> {
//            expandedPosition = isExpanded ? -1 : position;
//            notifyItemChanged(previousExpandedPosition);
//            notifyItemChanged(position);

            Intent intent = new Intent(context, FragmentActivity.class);
            intent.putExtra("fragment", "teacher_assignment");
            intent.putExtra("teacher_name", faqModel.getName());
            context.startActivity(intent);
            //activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivExpand;
        TextView tvTitle, tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivExpand = itemView.findViewById(R.id.iv_expand);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
        }
    }
}
