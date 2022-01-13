package com.yespustak.yespustakapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.utils.SharedPref;

public class PaymentStatusFragment extends Fragment {

    private static final String TAG = "PaymentStatusFragment";

    public static final int STATUS_FAIL = 0;
    public static final int STATUS_SUCCESS = 1;
    private static final String ARG_STATUS = "ARG_STATUS";
    private static final String ARG_AMOUNT = "ARG_AMOUNT";
    private static final String ARG_FROM = "ARG_FROM";
    private static final String ARG_TO = "ARG_TO";
    private static final String ARG_REASON = "ARG_REASON";

    private int status;
    private double amt;
    private String from, to, reason;

    private View flRootLayout;
    private ImageView ivImage;
    private TextView tvStatus, tvDesc, tvAmt, tvFrom, tvTo, tvReasonTitle, tvReason;
    private Button btnDone;

    public static PaymentStatusFragment newInstance(int status, int amount, String from, String to, String reason) {
        PaymentStatusFragment fragment = new PaymentStatusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STATUS, status);
        args.putInt(ARG_AMOUNT, amount);
        args.putString(ARG_FROM, from);
        args.putString(ARG_TO, to);
        args.putString(ARG_REASON, reason);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle getBundleArgs(int status, int amount, String from, String to, String reason) {
        Bundle args = new Bundle();
        //set args to fragment
        args.putInt(ARG_STATUS, status);
        args.putInt(ARG_AMOUNT, amount);
        args.putString(ARG_FROM, from);
        args.putString(ARG_TO, to);
        args.putString(ARG_REASON, reason);

        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getInt(ARG_STATUS);
            amt = getArguments().getInt(ARG_AMOUNT) / 100d;
            from = getArguments().getString(ARG_FROM);
            to = getArguments().getString(ARG_TO);
            reason = getArguments().getString(ARG_REASON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_status2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

    }

    private void setupViews(View view) {
        flRootLayout = view.findViewById(R.id.fl_root_layout);
        ivImage = view.findViewById(R.id.iv_image);
//        tvStatus = view.findViewById(R.id.tv_status);
        tvDesc = view.findViewById(R.id.tvDescription);
        tvAmt = view.findViewById(R.id.tv_amount);
        tvFrom = view.findViewById(R.id.tv_from);
        tvTo = view.findViewById(R.id.tv_to);
        tvReason = view.findViewById(R.id.tv_reason);
        tvReasonTitle = view.findViewById(R.id.tv_reason_title);
        btnDone = view.findViewById(R.id.btn_done);

        boolean isSuccess = status == STATUS_SUCCESS;

        //set title -> this is not working
//        ((FragmentActivity) requireActivity()).setTitle("YesPustak | " + ( isSuccess ? "Thank you!" : "We are sorry"));

        //set data
//        flRootLayout.setBackgroundColor(getResources().getColor(isSuccess ? R.color.bg_payment_success : R.color.bg_payment_fail));
        ivImage.setImageResource(isSuccess ? R.drawable.ic_baseline_check_circle_24 : R.drawable.ic_baseline_error_24);
        ivImage.setColorFilter(isSuccess ? requireContext().getColor(R.color.bg_payment_success) : requireContext().getColor(R.color.bg_payment_fail));
//        tvStatus.setText(getString(isSuccess ? R.string.title_success : R.string.title_failed));
        tvDesc.setText(getString(isSuccess ? R.string.text_payment_success_description : R.string.text_payment_fail_description, amt));
//        tvDesc.setText(getString(isSuccess ? R.string.text_payment_success_description : R.string.text_payment_fail_description, amt));
        tvFrom.setText(from);
        tvTo.setText(to);
        tvAmt.setText(getString(R.string.text_price_with_rs_sign, amt));
        tvReasonTitle.setText(isSuccess ? getString(R.string.text_order_id) : getString(R.string.title_reason));
        tvReason.setText(reason);

        btnDone.setOnClickListener(v -> {
            if (status == STATUS_FAIL)
                requireActivity().onBackPressed();
            else {
                SharedPref.saveBoolean(TAG, Constants.OPEN_MY_PUSTAKALAY, true);
                requireActivity().finish();
            }
        });

    }
}