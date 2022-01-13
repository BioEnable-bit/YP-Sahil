package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.adapters.FavBooksAdapter;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.FavouriteBooks;
import com.yespustak.yespustakapp.api.response.RecommendationList;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.OnMenuItemClickListener;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteBooksFragment extends BaseFragment implements AdapterItemClickListener, View.OnClickListener, OnMenuItemClickListener {

    String TAG = this.getClass().getSimpleName();

    SwipeRefreshLayout swipeRefreshLayout;
    View rlFavBooksData, llProgress;
    RecyclerView rvFavBooks;
    LinearLayout llEmpty;
    FavBooksAdapter adapter;
    ArrayList<BookModel> bookList;

    SearchView searchView;
    MenuItem miSearch;

    TextView tvStateDesc;
    Button btnEmptyAction;

    int[] cardBgDrawables = {R.drawable.card_gradient_bg_blue, R.drawable.card_gradient_bg_orange,
            R.drawable.card_gradient_bg_green, R.drawable.card_gradient_bg_violet,
            R.drawable.card_gradient_bg_amber,
            R.drawable.card_gradient_bg_deep_orange, R.drawable.card_gradient_bg_red50};
    int[] cardShadowColors = {R.color.dark_blue, R.color.dark_orange, R.color.dark_green,
            R.color.dark_violet, R.color.dark_amber, R.color.deep_orange, R.color.red_50};

    private final Random randomInstance = new Random();

    public FavouriteBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        miSearch = menu.findItem(R.id.mi_search);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_cart).setVisible(true);
        menu.findItem(R.id.mi_search).setVisible(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) miSearch.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.text_search_title));

        searchView.setOnCloseListener(() -> {
            filterBooksAndUpdateView();
            return false;
        });
        searchView.setOnQueryTextListener(queryTextListener);

    }

    private void setupViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rlFavBooksData = view.findViewById(R.id.rl_fav_books_data);
        llProgress = view.findViewById(R.id.ll_progress);
        rvFavBooks = view.findViewById(R.id.rv_fav_books);
        llEmpty = view.findViewById(R.id.ll_error_empty);
        tvStateDesc = view.findViewById(R.id.tv_state_desc);
        btnEmptyAction = view.findViewById(R.id.btn_empty_action);

        rvFavBooks.setHasFixedSize(true);
        rvFavBooks.setLayoutManager(new GridLayoutManager(getContext(), 4));
        bookList = new ArrayList<>();
        adapter = new FavBooksAdapter(new ArrayList<>(), this, this);

        rvFavBooks.setAdapter(adapter);
        btnEmptyAction.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(() -> getFavBooks(true));

        getFavBooks(false);
    }

    private void filterBooksAndUpdateView() {
        adapter.setBooks(getFilteredBooks());

        boolean isNotesFound = adapter.getItemCount() > 0;
        rvFavBooks.setVisibility(isNotesFound ? View.VISIBLE : View.GONE);
        llEmpty.setVisibility(isNotesFound ? View.GONE : View.VISIBLE);

        boolean isQueryEmpty = searchView == null || searchView.getQuery().toString().trim().isEmpty();

        tvStateDesc.setVisibility(isQueryEmpty ? View.VISIBLE : View.GONE);

        btnEmptyAction.setVisibility(isQueryEmpty ? View.VISIBLE : View.GONE);
    }

    private List<BookModel> getFilteredBooks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (searchView == null)
                return bookList;

            //else
            return bookList.stream().filter(book -> utils.containsIgnoreCase(book.getTitle(), searchView.getQuery().toString())).collect(Collectors.toList());
        }

        return bookList;
    }

    private void getFavBooks(boolean refresh) {
        if (!refresh)
            showProgress(true);
        String deviceId = utils.getIMEINumber();
        Call<FavouriteBooks> call = Retrofit2Client.getInstance().getApiService().getFavouriteBooks(deviceId);
        call.enqueue(new Callback<FavouriteBooks>() {
            @Override
            public void onResponse(Call<FavouriteBooks> call, Response<FavouriteBooks> response) {
//                utils.hideProgressBar(progressDialog);
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());

                bookList.clear();
                if (!response.isSuccessful() || response.body() == null) {
//                    utils.showToast("request fail");
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    if (response.body().getResult().isEmpty()) {
                        llEmpty.setVisibility(View.VISIBLE);
                        rvFavBooks.setVisibility(View.GONE);
//                        Toast.makeText(requireContext(), "Recommendation is Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        //generate card background drawable and shadow color
                        for (int i = 0; i < response.body().getResult().size(); i++) {
                            int randomNumber = randomInstance.nextInt(cardBgDrawables.length);
                            int drawable = cardBgDrawables[randomNumber];
                            int color = cardShadowColors[randomNumber];

                            response.body().getResult().get(i).setCardShadowColor(color);
                            response.body().getResult().get(i).setCardBgDrawable(drawable);
                        }

                        llEmpty.setVisibility(View.GONE);
                        rvFavBooks.setVisibility(View.VISIBLE);

                        bookList.addAll(response.body().getResult());
//                        adapter.notifyDataSetChanged();
                        filterBooksAndUpdateView();
                    }
                } else
                    utils.showToast("Fail to get Favourite books");

                showProgress(false);

            }

            @Override
            public void onFailure(Call<FavouriteBooks> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
//                requestFailure(progressDialog, t);
                showProgress(false);

            }
        });
    }

    private void showProgress(boolean show) {
        llProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        rlFavBooksData.setVisibility(show ? View.GONE : View.VISIBLE);
        if (!show)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(Object object) {
        getBookDetails(((BookModel) object).getId());
    }

    @Override
    public void onDelete(Object object) {

    }

    @Override
    public void onDownload(Object object) {

    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            filterBooksAndUpdateView();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            filterBooksAndUpdateView();
            return true;
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_empty_action) {
            SharedPref.saveBoolean(TAG, Constants.OPEN_HOMEPAGE, true);
            requireActivity().finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        BookModel bookModel = adapter.getItemAt(position);
        if (item.getItemId() == R.id.mi_delete) {
            reqToggleFavourite(false, bookModel.getId());
            return true;
        }
        return false;
    }

    @Override
    void handleToggleFavResp(boolean addToFavourite, int bookId) {
        //remove this book
        for (int i = 0; i < bookList.size(); i++) {
            if (bookList.get(i).getId() == bookId) {
                bookList.remove(i);
                break;
            }
        }

        //and update view
        filterBooksAndUpdateView();

//        OR
//        getFavBooks();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharedPref.getBoolean(TAG, Constants.IS_FAVOURITE_STATE_CHANGE))
            getFavBooks(true);
    }
}