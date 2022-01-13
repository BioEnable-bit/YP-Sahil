package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.adapters.CartAdapter;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.AddToCart;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.CartList;
import com.yespustak.yespustakapp.models.CartModel;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends BaseFragment implements CartAdapter.AdapterListener, View.OnClickListener {
    String TAG = this.getClass().getSimpleName();

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvCartItems;
    Button btnCheckout, btnShopNow;
    private View clCartView, llEmpty, llCalcs, llProgress;

    TextView tvMrp, tvSaved, tvTotal;

    ArrayList<CartModel> cartItems;
    ArrayList<CartModel> searchItems;
    CartAdapter cartAdapter;

    boolean isDeletingCartItem = false;

    private SearchView searchView = null;

    List<String> bookIds = new ArrayList<>();

    MenuItem miDeleteAll;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        Checkout.preload(requireContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        getData(false);
    }

    private void setupViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvCartItems = view.findViewById(R.id.rvCart);
        tvMrp = view.findViewById(R.id.tv_mrp);
        tvSaved = view.findViewById(R.id.tv_saved);
        btnShopNow = view.findViewById(R.id.btn_shop_now);
        tvTotal = view.findViewById(R.id.tv_ypp);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        clCartView = view.findViewById(R.id.cl_cart_view);
        llProgress = view.findViewById(R.id.ll_progress);
        llEmpty = view.findViewById(R.id.ll_empty);
        llCalcs = view.findViewById(R.id.ll_calcs);

        cartItems = new ArrayList<>();
        searchItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this);

        rvCartItems.setHasFixedSize(true);
        rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvCartItems.setItemAnimator(new DefaultItemAnimator());
        rvCartItems.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvCartItems.setAdapter(cartAdapter);

        //btn click
        btnShopNow.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(() -> getData(true));

        //update initial empty view
        showHideEmptyView(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem searchViewItem = menu.findItem(R.id.mi_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchViewItem != null) {
            searchView = (SearchView) searchViewItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint("Search Cart");

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    cartAdapter.clearItem();
                    getData(false);
                    return false;
                }
            });

            //cartAdapter.filterItem(query);
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //cartAdapter.filterItem(query);
                    cartItems.clear();
                    query = query.toLowerCase();
                    if (query.length() == 0) {
                        cartItems.addAll(searchItems);
                    } else {
                        for (CartModel cartModel : searchItems) {
                            if (cartModel.getBookTitle().toLowerCase().contains(query)) {
                                cartItems.add(cartModel);
                            }
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    cartItems.clear();
                    newText = newText.toLowerCase();
                    if (newText.length() == 0) {
                        cartItems.addAll(searchItems);
                    } else {
                        for (CartModel cartModel : searchItems) {
                            if (cartModel.getBookTitle().toLowerCase().contains(newText)) {
                                cartItems.add(cartModel);
                            }
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        menu.findItem(R.id.mi_cart).setVisible(false); //hide cart action menu on cart fragment

        int cartItemsCount = SharedPref.getInt(Constants.CART_ITEMS_COUNT);
        miDeleteAll = menu.findItem(R.id.mi_delete_all);
        miDeleteAll.setVisible(cartItemsCount > 0);
        menu.findItem(R.id.mi_search).setVisible(cartItemsCount > 0);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.mi_delete_all) {
            utils.showAlert(getContext(), getString(R.string.title_delete_all, getString(R.string.title_books)),
                    getString(R.string.text_really_want_to_delete_all, getString(R.string.title_books)),
                    SweetAlertDialog.WARNING_TYPE, sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                        removeAllBooksFromCart();
                    }, true);
        }
        return super.onOptionsItemSelected(item);
    }

    public void removeAllBooksFromCart() {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().removeCartItems(utils.getIMEINumber());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                //update local cart info
                SharedVariables.cartItemIds.clear();
                SharedPref.saveInt(Constants.CART_ITEMS_COUNT, 0);
                requireActivity().invalidateOptionsMenu();
                utils.hideProgressBar(progressDialog);

                //refresh cart data
                getData(false);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void getData(boolean refresh) {
//        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        if (!refresh)
            showProgress(true);
        String deviceId = utils.getIMEINumber();
        Call<CartList> call = Retrofit2Client.getInstance().getApiService().getCartList(deviceId);
        call.enqueue(new Callback<CartList>() {
            @Override
            public void onResponse(Call<CartList> call, Response<CartList> response) {
//                utils.hideProgressBar(progressDialog);
                Log.i(TAG, "onResponse: is success: " + response.body().getCartModelList());
                cartItems.clear();
                if (response.isSuccessful()) {

                    if (response.body().getCartModelList().isEmpty()) {
                        Toast.makeText(requireContext(), "Cart is Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "onResponse: size: " + cartItems.size());
                        cartItems.addAll(response.body().getCartModelList());
                        searchItems.addAll(response.body().getCartModelList());
                        cartAdapter.notifyDataSetChanged();

                        //update cart items ids
                        SharedVariables.updateCartItemIds(cartItems);
                        requireActivity().invalidateOptionsMenu();
                        updateBottomView();
                    }
                    showHideEmptyView(false);

                } else {
                    Toast.makeText(requireActivity(), "Status: " + response.errorBody(), Toast.LENGTH_SHORT).show();
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<CartList> call, Throwable t) {
//                requestFailure(progressDialog, t);
                showProgress(false);
            }
        });
    }

    public void removeItem(int position) {
        //stop api call if previous delete operation is running
        if (isDeletingCartItem)
            return;

        isDeletingCartItem = true;

        CartModel cartModel = cartItems.get(position);
//        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        swipeRefreshLayout.setRefreshing(true);
        String deviceId = utils.getIMEINumber();
        Call<AddToCart> call = Retrofit2Client.getInstance().getApiService().removeFromCartList(deviceId, cartModel.getBookId());
        call.enqueue(new Callback<AddToCart>() {
            @Override
            public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                Log.i("CartAdapter", "onResponse: is success: " + response.isSuccessful());
//                utils.hideProgressBar(progressDialog);
                swipeRefreshLayout.setRefreshing(false);

                String message = getString(R.string.msg_something_went_wrong);
                if (response.isSuccessful()) {

                    if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                        //update shared pref. and SharedVariables
                        SharedVariables.updateCartItemIds(response.body().getResult());
                        //we can directly response.body().getResult().size() to for cart badge but kept as it is for now
                        SharedPref.saveInt(Constants.CART_ITEMS_COUNT, response.body().getResult().size());

                        /*
                         * we can use response.body().getResult() to keep our cartItems array up to date (think about it later)
                         */

                        cartAdapter.removeItem(position);
                        updateBottomView();
                        showHideEmptyView(false);

                        //update Delete all button
                        requireActivity().invalidateOptionsMenu();
                    }

                    message = response.body().getMessage();
                }

                utils.showToast(message);
                isDeletingCartItem = false;

            }

            @Override
            public void onFailure(Call<AddToCart> call, Throwable t) {
//                requestFailure(progressDialog, t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateBottomView() {
        //calculate values
        double mrp = 0, ypp = 0, saved;
        for (int i = 0; i < cartItems.size(); i++) {
            mrp += cartItems.get(i).getBookMrp();
            ypp += cartItems.get(i).getBookYpp();
        }
        saved = mrp - ypp;

        //set text
        tvMrp.setText(getString(R.string.text_price_with_rs_sign, mrp));
        tvTotal.setText(getString(R.string.text_price_with_rs_sign, ypp));
        tvSaved.setText(getString(R.string.text_saved_price_with_rs_sign, saved));
    }

    private void showHideEmptyView(boolean initialLoading) {
        boolean showEmpty = initialLoading ? SharedVariables.cartItemIds.size() == 0 : cartAdapter.getItemCount() == 0;
        llEmpty.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        llCalcs.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
        rvCartItems.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
    }

    private void showProgress(boolean show) {
        llProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        clCartView.setVisibility(show ? View.GONE : View.VISIBLE);
        if (!show)
            swipeRefreshLayout.setRefreshing(false);
    }

    public void requestFailure(SweetAlertDialog progressDialog, Throwable t) {
        isDeletingCartItem = false;
        utils.hideProgressBar(progressDialog);
        Log.e(TAG, "requestFailure: ", t);
        utils.showToast(t.getMessage());
    }

    @Override
    public void onDeleteClick(int position) {
        removeItem(position);
    }

    @Override
    public void onItemClick(int position) {
        getBookDetails(cartItems.get(position).getBookId());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shop_now:
                SharedPref.saveBoolean(TAG, Constants.OPEN_HOMEPAGE, true);
                requireActivity().finish();
                break;

            case R.id.btn_checkout:
                int amount = 0;

                for (CartModel cartItem : cartItems) {
                    amount += cartItem.getBookYpp();
                    bookIds.add(String.valueOf(cartItem.getBookId()));
                }
                Log.i(TAG, "onCheckoutClick: bookIds: " + bookIds.toString());
                Log.i(TAG, "onCheckoutClick: Amt: " + amount);

                ((FragmentActivity) requireActivity()).startPayment(amount * 100, bookIds);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}