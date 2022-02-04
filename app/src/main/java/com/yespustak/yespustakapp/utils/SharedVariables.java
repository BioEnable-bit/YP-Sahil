package com.yespustak.yespustakapp.utils;

import android.content.Context;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.CartModel;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.repos.DownloadBookRepo;
import com.yespustak.yespustakapp.repos.UserRepo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;

public class SharedVariables {
    private static final String TAG = "SharedVariables";

    public static String DF_NOTES = "dd MMM yyyy hh:mm a";

//    public static List<CartModel> cartItems;
    public static List<Integer> cartItemIds = new ArrayList<>();

    public static UserModel user;
    public static List<DownloadBook> downloadBooks;

    public static int[] cardShadowColors = {R.color.dark_blue, R.color.dark_orange, R.color.dark_green,
            R.color.dark_violet, R.color.dark_amber, R.color.deep_orange, R.color.red_50};

    public static void init(Context context) {
        UserRepo userRepo = UserRepo.getInstance(context);
        DownloadBookRepo downloadBookRepo = DownloadBookRepo.getInstance(context);
        userRepo.getUserModelLiveData().observeForever(userModel -> user = userModel);
        downloadBooks = downloadBookRepo.getAllDownloadBooksSync();
        downloadBookRepo.getAllDownloadBooks().observeForever(downloadBooks -> SharedVariables.downloadBooks = downloadBooks);
//        downloadBookLiveData = downloadBookRepo.getAllDownloadBooks();
    }

    public static void updateCartItemIds(List<CartModel> cartItems) {
        cartItemIds.clear();
        for (CartModel cartItem : cartItems)
            cartItemIds.add(cartItem.getBookId());
    }

    public static boolean isBookExistInCart(int bookId) {
        return cartItemIds.contains(bookId);
//        for (CartModel cartItem : cartItems) {
//            if (cartItem.getBookId() == bookId)
//                return true;
//        }
//        return false;
    }

    public static boolean isBookPurchased(int bookId) {
        //Sometimes its showing null and causing crash
        if (downloadBooks == null)
            return false;

        for (DownloadBook book : downloadBooks)
            if (book.getRid() == bookId)
                return true;
            else if(book.getPublication() == "NCERT")
                return true;

        return false;
    }

    public static String getRazorpayCredentials() {
        boolean enableLivePayment = SharedPref.getBoolean(TAG, "enable_live_payment");
        return Credentials.basic(enableLivePayment ? Constants.LIVE_RAZORPAY_KEY : Constants.DEMO_RAZORPAY_KEY,
                enableLivePayment ? Constants.LIVE_RAZORPAY_SECRET : Constants.DEMO_RAZORPAY_SECRET);
    }

    public static String getRazorpayKey() {
        boolean enableLivePayment = SharedPref.getBoolean(TAG, "enable_live_payment");
        return enableLivePayment ? Constants.LIVE_RAZORPAY_KEY : Constants.DEMO_RAZORPAY_KEY;
    }
}
