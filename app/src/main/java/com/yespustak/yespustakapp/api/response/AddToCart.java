package com.yespustak.yespustakapp.api.response;

import com.google.gson.annotations.SerializedName;
import com.yespustak.yespustakapp.models.CartModel;

import java.util.List;

public class AddToCart extends BaseResponse {

    @SerializedName("result")
    List<CartModel> result;

    @SerializedName("cart_item_count")
    int cartItemCount;

    public List<CartModel> getResult() {
        return result;
    }

    public int getCartItemCount() {
        return cartItemCount;
    }
}
