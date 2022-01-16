package com.yespustak.yespustakapp.api;


import com.yespustak.yespustakapp.api.request.SavePaymentRequest;
import com.yespustak.yespustakapp.api.response.AboutUs;
import com.yespustak.yespustakapp.api.response.AddToCart;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.Boards;
import com.yespustak.yespustakapp.api.response.BookDetail;
import com.yespustak.yespustakapp.api.response.BookList;
import com.yespustak.yespustakapp.api.response.CartList;
import com.yespustak.yespustakapp.api.response.ChangePassword;
import com.yespustak.yespustakapp.api.response.Content;
import com.yespustak.yespustakapp.api.response.DictionaryItem;
import com.yespustak.yespustakapp.api.response.FavouriteBooks;
import com.yespustak.yespustakapp.api.response.ForgotPassword;
import com.yespustak.yespustakapp.api.response.GetPurchasedBooks;
import com.yespustak.yespustakapp.api.response.GetSections;
import com.yespustak.yespustakapp.api.response.GetThirdPartyApps;
import com.yespustak.yespustakapp.api.response.GetThirdPartyExtrasApps;
import com.yespustak.yespustakapp.api.response.GetThirdPartyGamesApps;
import com.yespustak.yespustakapp.api.response.GetUserPref;
import com.yespustak.yespustakapp.api.response.GetUserProfile;
import com.yespustak.yespustakapp.api.response.Login;
import com.yespustak.yespustakapp.api.response.Offers;
import com.yespustak.yespustakapp.api.response.PdfData;
import com.yespustak.yespustakapp.api.response.RecommendationList;
import com.yespustak.yespustakapp.api.response.Register;
import com.yespustak.yespustakapp.api.response.ReqBook;
import com.yespustak.yespustakapp.api.response.SaveAcademicDetails;
import com.yespustak.yespustakapp.api.response.SavePaymentResponse;
import com.yespustak.yespustakapp.api.response.Schools;
import com.yespustak.yespustakapp.api.response.Standards;
import com.yespustak.yespustakapp.api.response.VerifyOtp;
import com.yespustak.yespustakapp.models.FaqModel;
import com.yespustak.yespustakapp.models.OrdersModel;
import com.yespustak.yespustakapp.models.PaymentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    @POST("api/user/login")
    @FormUrlEncoded
    Call<Login> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("device_id") String deviceId
    );

    @POST("api/get/user/preferences")
    @FormUrlEncoded
    Call<GetUserPref> getUserPref(@Field("device_id") String deviceId);

    @POST("api/user/register")
    @FormUrlEncoded
    Call<Register> register(
            @Field("name") String name,
            @Field("gender") String gender,
            @Field("email") String email,
            @Field("password") String password,
            @Field("confirm_password") String confirmPassword,
            @Field("dob") String dob,
            @Field("mobile_no") String mobileNo,
            @Field("whatsapp_no") String whatsappNo,
            @Field("device_id") String deviceId
    );

    @POST("api/save/academic/details")
    @FormUrlEncoded
    Call<SaveAcademicDetails> saveAcademicDetails(
            @Field("uid") String uid,
            @Field("board_id") int boardId,
            @Field("school_id") int schoolId,
            @Field("standard_id") int standardId,
            @Field("section_name") String sectionName,
            @Field("academic_year") String academicYr,
            @Field("roll_no") String rollNumber,
            @Field("gr_number") String grNumber,
            @Field("device_id") String deviceId
    );

    @POST("api/forgot/password/otp")
    @FormUrlEncoded
    Call<ForgotPassword> forgotPassword(
            @Field("mobile_no") String mobileNo,
            @Field("email") String email,
            @Field("device_id") String deviceId
    );

    @POST("api/resend/otp")
    @FormUrlEncoded
    Call<BaseResponse> resendOtp(
            @Field("device_id") String deviceId,
            @Field("email") String email,
            @Field("mobile_no") String mobile_no,
            @Field("uid") String uid
    );

    @POST("api/verify/otp")
    @FormUrlEncoded
    Call<VerifyOtp> verifyOtp(
            @Field("uid") String uid,
            @Field("otp") String otp,
            @Field("device_id") String deviceId
    );

    @POST("api/reset/password")
    @FormUrlEncoded
    Call<ChangePassword> changePassword(
            @Field("uid") String uid,
            @Field("new_password") String newPassword,
            @Field("device_id") String deviceId
    );

    @POST("api/user/profile")
    @FormUrlEncoded
    Call<GetUserProfile> getUser(
            @Field("device_id") String deviceId
    );

    @POST("api/user/profile/update")
    @FormUrlEncoded
    Call<BaseResponse> saveUser(
            @Field("name") String name,
            @Field("gender") String gender,
            @Field("email") String email,
            @Field("dob") String dob,
            @Field("mobile_no") String mobileNo,
            @Field("whatsapp_no") String whatsappNo,
            @Field("section_name") String sectionName,
//            @Field("academic_year") String academicYr,
            @Field("roll_no") String rollNo,
//            @Field("gr_number") String grNo,
            @Field("device_id") String deviceId
    );

    @POST("api/update/contact/otp")
    @FormUrlEncoded
    Call<BaseResponse> sendOtpNewContact(
            @Field("uid") String uid,
            @Field("email") String email,
            @Field("mobile_no") String mobileNo,
            @Field("device_id") String deviceId
    );

    @POST("api/get/boards")
    Call<Boards> getBoards();

    @POST("api/get/schools")
    @FormUrlEncoded
    Call<Schools> getSchools(@Field("board_id") int schoolId);

    @POST("api/get/standards")
    Call<Standards> getStandards();

    @POST("api/get/section")
    @FormUrlEncoded
    Call<GetSections> getSections(
            @Field("school_id") int schoolId,
            @Field("standard_id") int standardId
    );

    @POST("api/get/content")
    @FormUrlEncoded
    Call<Content> getContent(@Field("content_type") int contentType);


    @POST("api/about/us")
    Call<AboutUs> aboutUs();

    @POST("api/books/list")
    @FormUrlEncoded
    Call<BookList> getBookList(
            @Field("device_id") String deviceId
    );

    @POST("api/books/list")
    @FormUrlEncoded
    Call<BookList> getStandardWiseBookList(
            @Field("device_id") String deviceId,
            @Field("standard_id") int standard
    );

    @POST("api/books/details")
    @FormUrlEncoded
    Call<BookDetail> getBookDetails(
            @Field("id") int id,
            @Field("device_id") String deviceId
    );

    @POST("api/books/recommendation")
    @FormUrlEncoded
    Call<RecommendationList> getRecommendation(
            @Field("device_id") String deviceId,
            @Field("book_id") int bookId
    );

    @POST("api/add/book/cart")
    @FormUrlEncoded
    Call<AddToCart> addToCartList(
            @Field("device_id") String deviceId,
            @Field("book_id") int bookId
    );

    @POST("api/save/favourite/book")
    @FormUrlEncoded
    Call<AddToCart> toggleFavourite(
            @Field("device_id") String deviceId,
            @Field("book_id") int bookId,
            @Field("favourite") boolean favourite
    );

    @POST("api/favourite/books/list")
    @FormUrlEncoded
    Call<FavouriteBooks> getFavouriteBooks(
            @Field("device_id") String deviceId
    );

    @POST("api/remove/book/cart")
    @FormUrlEncoded
    Call<AddToCart> removeFromCartList(
            @Field("device_id") String deviceId,
            @Field("book_id") int bookId
    );

    @POST("api/get/cart/details")
    @FormUrlEncoded
    Call<CartList> getCartList(
            @Field("device_id") String deviceId
    );

    @POST("students/savePdfData")
    Call<BaseResponse> saveStudentBookData(@Body PdfData pdfData);

    @POST("students/getPdfData")
    @FormUrlEncoded
    Call<PdfData> getStudentBookData(
            @Field("book_id") int bookId,
            @Field("device_id") String deviceId
    );

    @POST("api/search")
    @FormUrlEncoded
    Call<BookList> getSearchResult(
            @Field("device_id") String device_id,
            @Field("standard_id") String standardId,
            @Field("search_keyword") String keyword
    );

    @POST("orders")
    @FormUrlEncoded
    Call<OrdersModel> getOrders(
            @Header("Authorization") String credential,
            @Field("amount") int amount,
            @Field("currency") String currency
    );

    @POST("payments/{id}/capture")
    @FormUrlEncoded
    Call<PaymentModel> capturePayment(
            @Path("id") String id,
            @Header("Authorization") String credential,
            @Field("amount") int amount,
            @Field("currency") String currency
    );

    @GET("payments/{id}")
    Call<PaymentModel> fetchPayment(
            @Path("id") String id,
            @Header("Authorization") String credential
    );

    @POST("api/save/payment/details")
    Call<SavePaymentResponse> savePayment(
            @Body SavePaymentRequest savePaymentRequest
    );

    @POST("api/cart/books/remove")
    @FormUrlEncoded
    Call<BaseResponse> removeCartItems(
            @Field("device_id") String deviceId
    );

    @POST("api/purchase/books/list")
    @FormUrlEncoded
    Call<GetPurchasedBooks> getPurchasedBooks(
            @Field("device_id") String deviceId
    );

    @POST("api/get/third_party/apps")
    @FormUrlEncoded
    Call<GetThirdPartyApps> getThirdPartyApps(
            @Field("device_id") String deviceId
    );

    @POST("api/get/extra/apps/")
    @FormUrlEncoded
    Call<GetThirdPartyExtrasApps> getThirdPartyExtrasApps(
            @Field("device_id") String deviceId
    );

    @POST("api/get/extra/apps/")
    @FormUrlEncoded
    Call<GetThirdPartyGamesApps> getThirdPartyGamesApps(
            @Field("device_id") String deviceId
    );

    @POST("api/request/book")
    @FormUrlEncoded
    Call<ReqBook> requestBook(
            @Field("board_id") int boardId,
            @Field("standard_id") int standardId,
            @Field("subject") String subject,
            @Field("publication") String publication,
            @Field("book_title") String bookTitle,
            @Field("author") String author,
            @Field("edition") String edition,
            @Field("release_year") String releaseYr,
            @Field("comments") String comments,
            @Field("device_id") String deviceId
    );

    @POST("api/contact/us")
    @FormUrlEncoded
    Call<ReqBook> contactUs(
            @Field("mobile_no") String mobileNo,
            @Field("email") String email,
            @Field("subject") String subject,
            @Field("message") String message,
            @Field("communication_mode") String commMode,
            @Field("device_id") String deviceId
    );

    @POST("api/save/feedback")
    @FormUrlEncoded
    Call<ReqBook> sendFeedback(
            @Field("rating") int rating,
            @Field("message") String message,
            @Field("feedback_type") String feedbackType,
            @Field("device_id") String deviceId
    );

    @GET("{word}")
    Call<List<DictionaryItem>> getWordInfo(
            @Path("word") String word
    );

    @POST("api/get/faq")
    Call<List<FaqModel>> getFaq();

    @POST("api/get/offers")
    Call<Offers> getOffers();
}
