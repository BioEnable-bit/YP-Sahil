package com.yespustak.yespustakapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.adapters.AssignmentAdapter;
import com.yespustak.yespustakapp.adapters.NoticeAdapter;
import com.yespustak.yespustakapp.models.AssignmentModel;
import com.yespustak.yespustakapp.models.NoticeModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NoticeFragment extends BaseFragment {

    View llProgressView;
    RecyclerView rvFaq;
    NoticeAdapter adapter;
    List<NoticeModel> list;

    public NoticeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        Log.e("TAG", "onCreate: I am in teaches " );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teachers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        getFaq();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void getFaq() {
        showProgress();


        //Instantiating an ArrayList object
        list.add(new NoticeModel(1,"Republic Day","123456","https://dashboard.yespustak.com/uploads/publishers/books/26/619c869b29507.pdf"));

        llProgressView.setVisibility(View.GONE);
        rvFaq.setVisibility(View.VISIBLE);




       /* Call<List<FaqModel>> call = Retrofit2Client.getInstance().getApiService().getFaq();
        call.enqueue(new Callback<List<FaqModel>>() {
            @Override
            public void onResponse(Call<List<FaqModel>> call, Response<List<FaqModel>> response) {
                if (response.isSuccessful() & response.body() != null) {
                    llProgressView.setVisibility(View.GONE);
                    rvFaq.setVisibility(View.VISIBLE);
                    faqList.addAll(response.body());


                    Log.e("TAG", "onResponse: "+response.body() );


                    adapter.notifyDataSetChanged();
                } else {
                    llProgressView.setVisibility(View.GONE);
                    utils.showAlert(getContext(), null, getString(R.string.text_fail_to_load_data), SweetAlertDialog.ERROR_TYPE, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            requireActivity().finish();
                        }
                    }, false);
                }
            }

            @Override
            public void onFailure(Call<List<FaqModel>> call, Throwable t) {
                llProgressView.setVisibility(View.GONE);
                utils.showAlert(getContext(), null, getString(R.string.text_fail_to_load_data), SweetAlertDialog.ERROR_TYPE, Dialog::dismiss, false);
            }
        });*/
    }

    private void showProgress() {
        llProgressView.setVisibility(View.VISIBLE);
        rvFaq.setVisibility(View.GONE);
    }

    private void setupViews(View view) {
        llProgressView = view.findViewById(R.id.ll_progress);
        rvFaq = view.findViewById(R.id.rv_faq);
        rvFaq.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list = new ArrayList<>();

//        faqList.add(new FaqModel("Once I have purchased my ebook, will it always be available?", "Yes, once you have purchased the ebook it is yours. The titles are stored on your YES Pustak account and you can access it at any time by logging in with your username and password. Most formats allow you to download the title as many times as you like however, we do put a limit on the downloads to prevent abuse of the system. If you need to download your ebook again and you are receiving an error message indicating that you have used all of your license."));
//        faqList.add(new FaqModel("How do I get help if I have a problem?", "You will find vast amount of advice and troubleshooting information in the YES Pustak Help Centre. Our troubleshooter page is interactive therefore, it will provide you with information that is customized to your specific problem and Reader format. If you are not able to find a solution to your problem in the Help Centre, please contact our support team which will be happy to assist you and, will always try to respond to your request within 24 hours."));
//        faqList.add(new FaqModel("What are the benefits of reading YES Pustak (e-book)?", "In most cases, ebooks are cheaper than paper books.\n" +
//                "Instantly available worldwide - just download the ebook instead of waiting for local release.\n" +
//                "Full Text Search available - Search our entire database of ebooks for a specific phrase or keyword.\n" +
//                "Quick to download - the average book takes only 10 to 15 minutes.\n" +
//                "You can build a whole library of digital books.\n" +
//                "Users can do research and create or organize content."));
//        faqList.add(new FaqModel("Will I be able to view my eBooks offline using the YES Pustak app", "Yes.You just need to sync with your bookshelf and download the ebook the first time. Once the ebook is downloaded onto your device, you can read them offline anytime on your YES Pustak device."));
//        faqList.add(new FaqModel("How to read eBooks?", "You can download a sample or purchase an eBook on YES Pustak app and You can read your eBook on your YES Pustak device."));
//        faqList.add(new FaqModel("Do I need a YES Pustak tab device to read eBooks?", "YES Yes Pustak app is only available on our customised tablet you can read ebooks only on your YES Pustak tablet."));
//        faqList.add(new FaqModel("How to download the ebooks that I have purchased recently?", "On the YES Pustak tablet device just click on the recently purchased eBook in your library and it will be downloaded. Depending upon the size of the eBook and your network speed it can take few minutes for the eBook to Appear."));
//        faqList.add(new FaqModel("What are standard formatting of the e-books?", "An ePUB, on the other hand, has reflowable text. This means that the words will magically adjust themselves to the size of the screen when the size of the page or fonts is increased or decreased. So, it is possible to increase or decrease the font size, without affecting the layout or formatting of the book. This feature makes it possible for readers to read books comfortably on devices with smaller screens –, tablets and e-Readers. In addition to this, EPUBs offer more functionality: they allow readers to change the font type, users can swipe the screen to turn the page (if the application or device has the functionality), search the book easily, and take advantage of many specific features offered by the electronic reader, or the reader app on mobiles. Other specific e-book formats also offer the same functionality as EPUB, but this format is the only one that is universally accepted."));
//        faqList.add(new FaqModel("How can we protect the copyright of the book?", "When a reader purchases or downloads a free e-book, he doesn’t get any copyright. He/she gets right to read his e-book, just as a reader has ownership of the print book that he buys. But, the reader may want to buy an e-book on read it on different devices – many people use several devices daily. The challenge is to create books in a standard format that is supported by different devices, and to make sure that one copy of e-book can be read only by one person."));
//        faqList.add(new FaqModel());

        //TODO when search result not found -> then show contact us form (Have any other query? Connect to us to get your answer.)


        adapter = new NoticeAdapter(list,getActivity());

        rvFaq.setAdapter(adapter);
    }
}