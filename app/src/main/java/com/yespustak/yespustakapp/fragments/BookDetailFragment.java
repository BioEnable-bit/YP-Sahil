package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.adapters.HorizontalRecyclerViewAdapter;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.AddToCart;
import com.yespustak.yespustakapp.api.response.BookDetail;
import com.yespustak.yespustakapp.api.response.RecommendationList;
import com.yespustak.yespustakapp.models.BookDetailModel;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.likeButton.LikeButtonView;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.mike5v.viewmoretextview.ViewMoreTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookDetailFragment extends BaseFragment implements View.OnClickListener, AdapterItemClickListener, LikeButtonView.ClickListener {
    String TAG = this.getClass().getSimpleName();

    //    private static final int STATUS_NONE = 1;
    private static final int STATUS_ADDED_IN_CART = 2;
    //    private static final int STATUS_PURCHASED = 3;

    View svBookDetail, llError, rlData, llProgressBookDetail, llProgressRecommendation, llRecommendationData;
    TextView tvRecommendationError;
    CarouselView carouselView;
    //    int[] sampleImages = {R.drawable.workbook1, R.drawable.workbook2};
    TextView tvBookName, tvMrp, tvISBN, tvAuthor, tvPublication, tvSubject, tvPagesCount, tvClass, tvBoard, empty;
//    ReadMoreTextView tvDescription;
    ViewMoreTextView tvDescription;
    Button btnYpp, btnAddToCart, btnAlreadyPurchased, btnRetry;
    //    ImageButton ibFavBook;
    BookDetailModel bookDetailModel;
    LikeButtonView lbvFavourite;

    RecyclerView rvRecommendation;
    HorizontalRecyclerViewAdapter adapter;
    ArrayList<BookModel> recommendationBookList;

    int[] cardBgDrawables = {R.drawable.card_gradient_bg_blue, R.drawable.card_gradient_bg_orange,
            R.drawable.card_gradient_bg_green, R.drawable.card_gradient_bg_violet,
            R.drawable.card_gradient_bg_amber,
            R.drawable.card_gradient_bg_deep_orange, R.drawable.card_gradient_bg_red50};
    int[] cardShadowColors = {R.color.dark_blue, R.color.dark_orange, R.color.dark_green,
            R.color.dark_violet, R.color.dark_amber, R.color.deep_orange, R.color.red_50};

    private final Random randomInstance = new Random();

    boolean existInCart;

    public BookDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookDetailModel = ModelSharedPref.getInstance().getModel(BookDetailModel.class);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (bookDetailModel != null) {
            setupViews(view);
            getBookDetails(bookDetailModel.getId());
        }

//        SharedVariables.downloadBookLiveData.observe(getViewLifecycleOwner(), downloadBooks -> {
//            //hide add to cart and YPP button
//            for (DownloadBook book : downloadBooks)
//                if (book.getRid() == bookDetailModel.getId()) {
//                    updateActionView();
//                    break;
//                }
//        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.mi_cart).setVisible(true);
        menu.findItem(R.id.mi_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        updateActionView();
        if (SharedPref.getBoolean(TAG, Constants.OPEN_MY_PUSTAKALAY))
            requireActivity().finish();
        super.onResume();
    }

    private void setupViews(View view) {
        svBookDetail = view.findViewById(R.id.sv_book_detail);
        llError = view.findViewById(R.id.ll_error);
        rlData = view.findViewById(R.id.rl_data);
        llProgressBookDetail = view.findViewById(R.id.ll_progress);
        llProgressRecommendation = view.findViewById(R.id.ll_progress_recommendation);
        llRecommendationData = view.findViewById(R.id.ll_recommendation_data);
        tvRecommendationError = view.findViewById(R.id.tv_recommendation_error);
        carouselView = view.findViewById(R.id.carouselViewBookDetails);
        tvBookName = view.findViewById(R.id.tvBookName);
        tvMrp = view.findViewById(R.id.tv_mrp);
//        ibFavBook = view.findViewById(R.id.ib_fav_book);
        lbvFavourite = view.findViewById(R.id.lbv_favourite);
        tvISBN = view.findViewById(R.id.tvISBN);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvPublication = view.findViewById(R.id.tv_publication);
        tvSubject = view.findViewById(R.id.tvSubject);
        tvPagesCount = view.findViewById(R.id.tvPagesCount);
        tvClass = view.findViewById(R.id.tvClass);
        tvBoard = view.findViewById(R.id.tvBoard);
        tvDescription = view.findViewById(R.id.tvDescription);
        btnYpp = view.findViewById(R.id.btn_ypp);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        btnAlreadyPurchased = view.findViewById(R.id.btn_already_purchased);
        btnRetry = view.findViewById(R.id.btn_retry);
        rvRecommendation = view.findViewById(R.id.rv_recommendation);

        setRecyclerView();


        ImageListener imageListener = (position, imageView) -> {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Picasso.get().load(Constants.DASHBOARD_URL + (position == 0 ? bookDetailModel.getImageUrl1() : bookDetailModel.getImageUrl2()))
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.img_not_available_thumb)
                    .into(imageView);
//            imageView.setImageResource(sampleImages[position]);
        };
        carouselView.setImageListener(imageListener);
        //IMP pageCount must be set after setting listener (Crash on back press after payment canceled)
        carouselView.setPageCount(2);

        btnYpp.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
        btnAlreadyPurchased.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
//        ibFavBook.setOnClickListener(this);
        tvDescription.setOnClickListener(this);
        lbvFavourite.setClickListener(this);

//        setData();
    }

    public void getBookDetails(int bookId) {
        showProgress(true);

        //also fetch recommendation books
        getRecommendedBook();
        Call<BookDetail> call = Retrofit2Client.getInstance().getApiService().getBookDetails(bookId, utils.getIMEINumber());
        call.enqueue(new Callback<BookDetail>() {
            @Override
            public void onResponse(Call<BookDetail> call, Response<BookDetail> response) {
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    bookDetailModel = response.body().getBookDetailModels();
                    setData();

                }
                showProgress(false);
                showError(false);
            }

            @Override
            public void onFailure(Call<BookDetail> call, Throwable t) {
                showProgress(false);
                showError(true);
            }
        });
    }

    public void reqToggleFavourite(boolean addToFavourite, int bookId) {
        //disable button to avoid multiple clicks

//        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        String deviceId = utils.getIMEINumber();
        Call<AddToCart> call = Retrofit2Client.getInstance().getApiService().toggleFavourite(deviceId, bookId, addToFavourite);
        call.enqueue(new Callback<AddToCart>() {
            @Override
            public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());
//                utils.hideProgressBar(progressDialog);

                String message = getString(R.string.msg_something_went_wrong);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    message = response.body().getMessage();

                    //set status and update view
                    bookDetailModel.setFavourite(addToFavourite);
                } else
                    message = "Failed to " + (addToFavourite ? "add in " : "remove from ") + "Favourite";

                //update favourite status again
                lbvFavourite.setChecked(bookDetailModel.isFavourite());
                utils.showToast(message);
            }

            @Override
            public void onFailure(Call<AddToCart> call, Throwable t) {
                lbvFavourite.setChecked(bookDetailModel.isFavourite());
                utils.showToast("Failed to " + (addToFavourite ? "add in " : "remove from ") + "Favourite");
//                requestFailure(progressDialog, t);
            }
        });
    }

    private void showError(boolean show) {
        llError.setVisibility(show ? View.VISIBLE : View.GONE);
        svBookDetail.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showProgress(boolean show) {
        rlData.setVisibility(show ? View.GONE : View.VISIBLE);
        llProgressBookDetail.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void addToCart() {
        //disable button to avoid multiple clicks
        btnAddToCart.setEnabled(false);

        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        String deviceId = utils.getIMEINumber();
        Call<AddToCart> call = Retrofit2Client.getInstance().getApiService().addToCartList(deviceId, bookDetailModel.getId());
        call.enqueue(new Callback<AddToCart>() {
            @Override
            public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());
                utils.hideProgressBar(progressDialog);
                btnAddToCart.setEnabled(true);

                String message = getString(R.string.msg_something_went_wrong);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() != Constants.STATUS_FAIL) {
                        message = response.body().getMessage();

                        if (response.body().getStatus() == STATUS_ADDED_IN_CART)
                            SharedVariables.cartItemIds.add(bookDetailModel.getId());
                        else
                            SharedVariables.updateCartItemIds(response.body().getResult());

                        SharedPref.saveInt(Constants.CART_ITEMS_COUNT, response.body().getResult().size());

                        //update action menu related view
                        requireActivity().invalidateOptionsMenu();
                        updateActionView();
                    }
                }

                utils.showToast(message);
            }

            @Override
            public void onFailure(Call<AddToCart> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void openCart() {
        Intent intent = new Intent(getContext(), FragmentActivity.class);
        intent.putExtra("fragment", "cart");
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    public void requestFailure(SweetAlertDialog progressDialog, Throwable t) {
        super.requestFailure(progressDialog, t);
        btnAddToCart.setEnabled(true);
    }

    private void setRecyclerView() {
        rvRecommendation.setHasFixedSize(true);
        rvRecommendation.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationBookList = new ArrayList<>();
        adapter = new HorizontalRecyclerViewAdapter(recommendationBookList, this);

        rvRecommendation.setAdapter(adapter);

//        getRecommendedBook();
        //setRecyclerData();
    }

    private void getRecommendedBook() {
        //final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        showRecommendationProgress(true);
        String deviceId = utils.getIMEINumber();
        Call<RecommendationList> call = Retrofit2Client.getInstance().getApiService().getRecommendation(deviceId, bookDetailModel.getId());
        call.enqueue(new Callback<RecommendationList>() {
            @Override
            public void onResponse(Call<RecommendationList> call, Response<RecommendationList> response) {
                //utils.hideProgressBar(progressDialog);
                showRecommendationProgress(false);
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());

                recommendationBookList.clear();
                if (!response.isSuccessful() || response.body() == null) {
                    showRecommendationData(true);
                    return;
                }

                boolean isSuccess = response.body().getStatus() == Constants.STATUS_SUCCESS;

                if (isSuccess) {
//                    if (response.body().getRecommendationModelList().isEmpty()) {
//                        empty.setVisibility(View.VISIBLE);
//                        rvRecommendation.setVisibility(View.GONE);
//                        Toast.makeText(requireContext(), "Recommendation is Empty", Toast.LENGTH_SHORT).show();
//                    } else {
                    //generate card background drawable and shadow color
                    for (int i = 0; i < response.body().getRecommendationModelList().size(); i++) {
                        int randomNumber = randomInstance.nextInt(cardBgDrawables.length);
                        int drawable = cardBgDrawables[randomNumber];
                        int color = cardShadowColors[randomNumber];

                        response.body().getRecommendationModelList().get(i).setCardShadowColor(color);
                        response.body().getRecommendationModelList().get(i).setCardBgDrawable(drawable);
                    }

//                        empty.setVisibility(View.GONE);
//                        rvRecommendation.setVisibility(View.VISIBLE);
                    recommendationBookList.addAll(response.body().getRecommendationModelList());
                    adapter.notifyDataSetChanged();
//                    }
                }

                showRecommendationData(!isSuccess); //isSuccess ? false : true

            }

            @Override
            public void onFailure(Call<RecommendationList> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                showRecommendationProgress(false);
                showRecommendationData(true);
            }
        });
    }

    private void showRecommendationProgress(boolean show) {
        llProgressRecommendation.setVisibility(show ? View.VISIBLE : View.GONE);
        llRecommendationData.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showRecommendationData(boolean isNetworkError) {
        boolean show = recommendationBookList.size() > 0;

        tvRecommendationError.setText(isNetworkError ? R.string.text_unable_to_load_recommendation : R.string.text_no_recommendation);

        rvRecommendation.setVisibility(show ? View.VISIBLE : View.GONE);
        tvRecommendationError.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void setData() {
        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.text_action_bar_title, bookDetailModel.getTitle()));
        tvBookName.setText(bookDetailModel.getTitle());
        tvDescription.setText(bookDetailModel.getBookDescription());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvMrp.setText(getString(R.string.text_price_with_rs_sign, Double.parseDouble(bookDetailModel.getMrp())));
            tvMrp.setPaintFlags(tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvISBN.setText(Html.fromHtml(getString(R.string.text_isbn, bookDetailModel.getIsbn()), Html.FROM_HTML_MODE_LEGACY));
            tvAuthor.setText(Html.fromHtml(getString(R.string.text_author, bookDetailModel.getAuthor()), Html.FROM_HTML_MODE_LEGACY));
            tvPublication.setText(Html.fromHtml(getString(R.string.text_publication, bookDetailModel.getPublisherName()), Html.FROM_HTML_MODE_LEGACY));
            tvSubject.setText(Html.fromHtml(getString(R.string.text_Subject, bookDetailModel.getSubject()), Html.FROM_HTML_MODE_LEGACY));
            tvPagesCount.setText(Html.fromHtml(getString(R.string.text_no_of_pages, bookDetailModel.getPageCount()), Html.FROM_HTML_MODE_LEGACY));
            tvClass.setText(Html.fromHtml(getString(R.string.text_class, bookDetailModel.getBookClass()), Html.FROM_HTML_MODE_LEGACY));
            tvBoard.setText(Html.fromHtml(getString(R.string.text_board, bookDetailModel.getBoardName()), Html.FROM_HTML_MODE_LEGACY));
            btnYpp.setText(Html.fromHtml(getString(R.string.text_ypp, Double.parseDouble(bookDetailModel.getYpp())), Html.FROM_HTML_MODE_LEGACY));
            lbvFavourite.setChecked(bookDetailModel.isFavourite());
        }

        updateActionView();

    }

    private void updateActionView() {
//        if (status == STATUS_PURCHASED) {
//            btnAddToCart.setVisibility(View.GONE);
//            btnYpp.setVisibility(View.GONE);
//            btnAlreadyPurchased.setVisibility(View.VISIBLE);
//        }
//        else if (status == STATUS_ADDED_IN_CART) {
//            btnAddToCart.setEnabled(false);
//            btnAddToCart.setText(getString(R.string.text_already_exist_in_cart));
//        }else {
//            btnAddToCart.setEnabled(true);
//            btnAddToCart.setText(getString(R.string.text_add_to_cart));
//        }

        existInCart = SharedVariables.isBookExistInCart(bookDetailModel.getId());
        boolean isPurchased = SharedVariables.isBookPurchased(bookDetailModel.getId());

        if (isPurchased) {
            btnAddToCart.setVisibility(View.GONE);
            btnYpp.setVisibility(View.GONE);
            btnAlreadyPurchased.setVisibility(View.VISIBLE);
        } else {
//            btnAddToCart.setEnabled(!existInCart);
            btnAddToCart.setText(existInCart ? getString(R.string.text_already_exist_in_cart) : getString(R.string.text_add_to_cart));
        }


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ib_fav_book:
//            case R.id.lbv_favourite:
//                // use ! to send opposite status
//                reqToggleFavourite(!bookDetailModel.isFavourite(), bookDetailModel.getId());
//                break;
            case R.id.btn_ypp:
                List<String> bookIds = new ArrayList<>();
                bookIds.add(String.valueOf(bookDetailModel.getId()));
                ((FragmentActivity) requireActivity()).startPayment((Integer.parseInt(bookDetailModel.getYpp()) * 100), bookIds);
                break;
            case R.id.btn_add_to_cart:
                if (existInCart)
                    openCart();
                else
                    addToCart();
                break;
            case R.id.btn_already_purchased:
                SharedPref.saveBoolean(TAG, Constants.OPEN_MY_PUSTAKALAY, true);
                requireActivity().finish();
                break;
            case R.id.tvDescription:
                tvDescription.toggle();
                break;
            case R.id.btn_retry:
                getBookDetails(bookDetailModel.getId());
                break;
        }
    }

    @Override
    public void onClick(Object object) {
//        BookDetailModel book = (BookDetailModel) object;
        openBookDetail(object);
//        getBookDetails(((BookModel) object).getId());
    }

    @Override
    public void onDelete(Object object) {

    }

    @Override
    public void onDownload(Object object) {

    }

    @Override
    public void onClickListener() {
        // use ! to send opposite status
        reqToggleFavourite(!bookDetailModel.isFavourite(), bookDetailModel.getId());
        SharedPref.saveBoolean(TAG, Constants.IS_FAVOURITE_STATE_CHANGE, true);
    }
}