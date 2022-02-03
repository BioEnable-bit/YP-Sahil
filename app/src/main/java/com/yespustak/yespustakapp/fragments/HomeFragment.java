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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.MainActivity;
import com.yespustak.yespustakapp.adapters.VerticalRecyclerViewAdapter;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.BookList;
import com.yespustak.yespustakapp.api.response.Offers;
import com.yespustak.yespustakapp.api.response.Standards;
import com.yespustak.yespustakapp.models.BookModel;
import com.yespustak.yespustakapp.models.StandardModel;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.models.VerticalModel;
import com.yespustak.yespustakapp.repos.UserRepo;
import com.yespustak.yespustakapp.utils.AdapterItemClickListener;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements AdapterItemClickListener, View.OnClickListener {
    View view;
    Context context;
    String TAG = this.getClass().getSimpleName();

    private static final int ERROR_NETWORK = 1;
    private static final int ERROR_SERVER = 2;
    private static final int ERROR_EMPTY = 3;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView verticalRecyclerView;
    View llErrorOrEmpty, rlData, llProgress, cvCarouselContainer, llProgressCarousel;
    ImageView ivStateImg;
    TextView tvStateTitle, tvStateDesc, tvCarouselError;
    Button btnRetry;

    VerticalRecyclerViewAdapter booksAdapter;
    ArrayList<VerticalModel> arrayListVertical;//contains bookList in specific format

    Spinner spnrStandard;
    List<StandardModel> standardList;
    ArrayAdapter<StandardModel> standardAdapter;

    ArrayList<BookModel> bookList;
    ArrayList<String> subjectList;

    CarouselView carouselView;
    SweetAlertDialog progressDialog;

    MenuItem miSearch;
    private SearchView searchView = null;
    private final Random randomInstance = new Random();

    List<String> carouselImages = new ArrayList<>();
    int[] sampleImages = {R.drawable.corousel1, R.drawable.corousel2, R.drawable.corousel3};
    int[] cardBgDrawables = {R.drawable.card_gradient_bg_blue, R.drawable.card_gradient_bg_orange,
            R.drawable.card_gradient_bg_green, R.drawable.card_gradient_bg_violet,
            R.drawable.card_gradient_bg_amber,
            R.drawable.card_gradient_bg_deep_orange, R.drawable.card_gradient_bg_red50};
    int[] cardShadowColors = {R.color.dark_blue, R.color.dark_orange, R.color.dark_green,
            R.color.dark_violet, R.color.dark_amber, R.color.deep_orange, R.color.red_50};

    UserRepo userRepo;
    UserModel user;
    String query = "";

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userRepo = UserRepo.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initialise();
        return view;
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
            query = "";
            getBookList(true);
            return false;
        });
        searchView.setOnQueryTextListener(queryTextListener);

//            int id = searchView.getContext()
//                    .getResources()
//                    .getIdentifier("android:id/search_src_text", null, null);
//            EditText searchEditText = searchView.findViewById(id);

//            try {
//                Field f = TextView.class.getDeclaredField("mDrawableForCursor");
//                f.setAccessible(true);
//                f.set(searchEditText, null);// set textCursorDrawable to null
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_cart_badge3));

        if (!query.isEmpty()) {
            searchView.post(() -> {
                searchView.setIconified(false);
                searchView.setQuery(query, false);
                searchView.clearFocus();
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userRepo.getUserModelLiveData().observe(getViewLifecycleOwner(), userModel -> {
            user = userModel;
            getStandards();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView != null)
            query = searchView.getQuery().toString().trim();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).updateCartBadge();
    }

    private void initialise() {
        context = this.getContext();
        progressDialog = utils.getProgressDialog(getContext());
        setupViews();
        getOffers(true);
    }

    private void setupViews() {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        verticalRecyclerView = view.findViewById(R.id.home_book_recyclerview);
        spnrStandard = view.findViewById(R.id.spnr_standard);
        cvCarouselContainer = view.findViewById(R.id.carouselView_container);
        llProgressCarousel = view.findViewById(R.id.ll_progress_carousel);
        tvCarouselError = view.findViewById(R.id.tv_carousel_error);

        llErrorOrEmpty = view.findViewById(R.id.ll_error_empty);
        rlData = view.findViewById(R.id.rl_data);
        llProgress = view.findViewById(R.id.ll_progress);
        ivStateImg = view.findViewById(R.id.iv_state_img);
        tvStateTitle = view.findViewById(R.id.tv_state_title);
        tvStateDesc = view.findViewById(R.id.tv_state_desc);
        btnRetry = view.findViewById(R.id.btn_retry);

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        arrayListVertical = new ArrayList<>();
        bookList = new ArrayList<>();
        subjectList = new ArrayList<>();
        booksAdapter = new VerticalRecyclerViewAdapter(context, arrayListVertical, this);
        verticalRecyclerView.setAdapter(booksAdapter);

        standardList = new ArrayList<>();
        standardAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, standardList);
        standardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrStandard.setAdapter(standardAdapter);

        //setData();

        carouselView = view.findViewById(R.id.carouselView);
//        ImageListener imageListener = (position, imageView) -> imageView.setImageResource(sampleImages[position]);
//        carouselView.setImageListener(imageListener);
//        carouselView.setPageCount(sampleImages.length);

        spnrStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (standardList.size() > 1)
                getBookList(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRetry.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getOffers(false);
            getBookList(false);
        });
    }

    private void updateCarouselView() {
        ImageListener imageListener = (position, imageView) -> {
//            imageView.setImageResource(sampleImages[position]);
            Picasso.get().load(Constants.DASHBOARD_URL + carouselImages.get(position)).into(imageView);
        };
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(carouselImages.size());
    }

    private  void getOffers(boolean showProgress) {
        if (showProgress)
            showCarouselProgress();
        Call<Offers> call = Retrofit2Client.getInstance().getApiService().getOffers();
        call.enqueue(new Callback<Offers>() {
            @Override
            public void onResponse(Call<Offers> call, Response<Offers> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carouselImages = response.body().getImageUrls();
                    updateCarouselView();
                    cvCarouselContainer.setVisibility(View.VISIBLE);
                }else {
                    tvCarouselError.setVisibility(View.VISIBLE);
                }
                llProgressCarousel.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Offers> call, Throwable t) {
                llProgressCarousel.setVisibility(View.GONE);
                tvCarouselError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showCarouselProgress() {
        tvCarouselError.setVisibility(View.GONE);
        llProgressCarousel.setVisibility(View.VISIBLE);
        cvCarouselContainer.setVisibility(View.INVISIBLE); //INVISIBLE because llProgressCarousel using cvCarouselContainer view's bounds
    }

    private void getBookList(boolean showLoading) {
        if (!utils.isConnectionAvailable()) {
            showErrorView(ERROR_NETWORK);
            swipeRefreshLayout.setRefreshing(false); //swipe layout already started showing loading icon, so need to disable it
            return;
        }
        if (showLoading)
            showProgress(true);
        else
            swipeRefreshLayout.setRefreshing(true);
//            progressDialog.show();

        String deviceId = utils.getIMEINumber();
        int standardId = ((StandardModel) spnrStandard.getSelectedItem()).getId();
        Call<BookList> call = Retrofit2Client.getInstance().getApiService().getSearchResult(deviceId,
                String.valueOf(standardId), searchView != null ? searchView.getQuery().toString().trim() : "");
        call.enqueue(bookListApiCallback);
    }

    private void getStandards() {
//        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        showProgress(true);
        Call<Standards> call = Retrofit2Client.getInstance().getApiService().getStandards();
        call.enqueue(new Callback<Standards>() {
            @Override
            public void onResponse(Call<Standards> call, Response<Standards> response) {
//                utils.hideProgressBar(progressDialog);
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    //add items, update view
                    standardList.clear();
                    standardList.add(new StandardModel(0, getString(R.string.title_all)));
                    if (response.body().getStandardModels() != null)
                        standardList.addAll(response.body().getStandardModels());
                    standardAdapter.notifyDataSetChanged();

                    if (user != null)
                        spnrStandard.setSelection(getStandardPosById(user.getStandardId()));
                } else
                    utils.showToast("Fail to get standards");
            }

            @Override
            public void onFailure(Call<Standards> call, Throwable t) {
                standardList.clear();
                standardList.add(new StandardModel(0, getString(R.string.title_all)));
                standardAdapter.notifyDataSetChanged();
                showProgress(false);
//                requestFailure(progressDialog, t);
            }
        });
    }

    private int getStandardPosById(int id) {
        for (int i = 0; i < standardList.size(); i++) {
            StandardModel standardModel = standardList.get(i);
            if (standardModel.getId() == id)
                return i;
        }

        return 0;
    }

    private void showErrorView(int errorType) {
        verticalRecyclerView.setVisibility(View.GONE);
        llErrorOrEmpty.setVisibility(View.VISIBLE);

        int icon;
        String btnTitle;
        String title;
        String description;

        switch (errorType) {
            case ERROR_SERVER:
                icon = R.drawable.ic_baseline_cloud_off_24;
                btnTitle = getString(R.string.title_retry);
                title = getString(R.string.text_server_error);
                description = getString(R.string.msg_server_error);
                break;
            case ERROR_EMPTY:
                icon = R.drawable.ic_empty_box;
                btnTitle = getString(R.string.title_view_all);
                title = getString(R.string.text_no_books);
                description = getString(R.string.msg_no_books);
                break;
            default:
                btnTitle = getString(R.string.title_retry);
                icon = R.drawable.ic_baseline_cloud_off_24;
                title = getString(R.string.text_no_network);
                description = getString(R.string.msg_network_error);
                break;
        }

        ivStateImg.setImageDrawable(AppCompatResources.getDrawable(requireContext(), icon));
        tvStateTitle.setText(title);
        tvStateDesc.setText(description);
        btnRetry.setTag(errorType);
        btnRetry.setText(btnTitle);
    }

    private void showProgress(boolean show) {
        llProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        rlData.setVisibility(show ? View.GONE : View.VISIBLE);

        if (!show)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(Object object) {
//        getBookDetails(((BookModel) object).getId());
        openBookDetail(object);
    }

    @Override
    public void onDelete(Object object) {

    }

    @Override
    public void onDownload(Object object) {

    }

    @Override
    public void onClick(View v) {
        int errorType = (int) v.getTag();
        if (errorType == ERROR_NETWORK || errorType == ERROR_SERVER) {
            getStandards();
//            getBookList(true);
        } else if (errorType == ERROR_EMPTY && standardList.size() > 1) {
            spnrStandard.setSelection(0);
            searchView.onActionViewCollapsed();
            getBookList(true);
        }

        getOffers(true);

    }

    @Override
    public boolean onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            return true;
        } else
            return super.onBackPressed();

    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {




            getBookList(false);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Log.i("onQueryTextChange", newText);
            query = newText;
            if (searchView.hasFocus())
                getBookList(false);
            return true;
        }
    };

    Callback<BookList> bookListApiCallback = new Callback<BookList>() {
        @SuppressLint("NotifyDataSetChanged")
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResponse(Call<BookList> call, Response<BookList> response) {
            Log.i(TAG, "onResponseStandard: is success: " + response.isSuccessful());

            if (response.isSuccessful()) {
                //Getting subjects from the response list

                if (response.body() == null || response.body().getBookListModels().isEmpty()) {
                    arrayListVertical.clear();
//                    Toast.makeText(requireActivity(), "No Book Data", Toast.LENGTH_SHORT).show();

                    showErrorView(ERROR_EMPTY);

                } else {
                    bookList.clear();
                    bookList.addAll(response.body().getBookListModels());
                    for (int i = 0; i < response.body().getBookListModels().size(); i++) {
                        String subject = response.body().getBookListModels().get(i).getSubject();
                        if (!subjectList.contains(subject)) {
                            subjectList.add(subject);
                        }
                    }

                    //clear vertical list and build again
                    arrayListVertical.clear();
                    for (String sub : subjectList) {

                        List<BookModel> subjectWiseBook = bookList.stream()
                                .filter(x -> sub.equals(x.getSubject()))
                                .collect(Collectors.toList());

                        //generate card background drawable and shadow color
                        for (int i = 0; i < subjectWiseBook.size(); i++) {
                            int randomNumber = randomInstance.nextInt(cardBgDrawables.length);
                            int drawable = cardBgDrawables[randomNumber];
                            int color = cardShadowColors[randomNumber];

                            subjectWiseBook.get(i).setCardShadowColor(color);
                            subjectWiseBook.get(i).setCardBgDrawable(drawable);
                        }

                        //generate vertical model
                        if (subjectWiseBook.size() > 0) {
                            VerticalModel verticalModel = new VerticalModel();
                            verticalModel.setTitle(sub);
                            verticalModel.setArrayList((ArrayList<BookModel>) subjectWiseBook);
                            arrayListVertical.add(verticalModel);
                        }
                    }
                    booksAdapter.notifyDataSetChanged();
                    verticalRecyclerView.scheduleLayoutAnimation();

                    verticalRecyclerView.setVisibility(View.VISIBLE);
                    llErrorOrEmpty.setVisibility(View.GONE);
                }

            } else {
                showErrorView(ERROR_SERVER);
            }

            showProgress(false);
        }

        @Override
        public void onFailure(Call<BookList> call, Throwable t) {
            showErrorView(HomeFragment.ERROR_SERVER);
            Log.e("ServerError",""+t.toString());
            showProgress(false);
        }
    };
}