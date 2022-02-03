package com.yespustak.yespustakapp.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.HighlightAnnotation;
import com.pspdfkit.annotations.LinkAnnotation;
import com.pspdfkit.annotations.SquigglyAnnotation;
import com.pspdfkit.annotations.StrikeOutAnnotation;
import com.pspdfkit.annotations.TextMarkupAnnotation;
import com.pspdfkit.annotations.UnderlineAnnotation;
import com.pspdfkit.annotations.actions.Action;
import com.pspdfkit.annotations.actions.UriAction;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.document.PdfDocument;
import com.pspdfkit.document.search.SearchResult;
import com.pspdfkit.document.search.TextSearch;
import com.pspdfkit.ui.PdfActivity;
import com.pspdfkit.ui.special_mode.controller.AnnotationTool;
import com.pspdfkit.ui.special_mode.manager.TextSelectionManager;
import com.pspdfkit.ui.toolbar.AnnotationCreationToolbar;
import com.pspdfkit.ui.toolbar.AnnotationEditingToolbar;
import com.pspdfkit.ui.toolbar.ContextualToolbar;
import com.pspdfkit.ui.toolbar.ContextualToolbarMenuItem;
import com.pspdfkit.ui.toolbar.TextSelectionToolbar;
import com.pspdfkit.ui.toolbar.ToolbarCoordinatorLayout;
import com.yespustak.yespustakapp.DemoActivity;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.PdfData;
import com.yespustak.yespustakapp.fragments.EditNoteFragment;
import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.repos.NotesRepo;
import com.yespustak.yespustakapp.services.SendPdfDataService;
import com.yespustak.yespustakapp.utils.App;
import com.yespustak.yespustakapp.utils.CustomAnnotationCreationToolbarGroupingRule;
import com.yespustak.yespustakapp.utils.PopUpDialog;
import com.yespustak.yespustakapp.utils.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPdfActivity extends PdfActivity implements ToolbarCoordinatorLayout.OnContextualToolbarLifecycleListener,
        EditNoteFragment.OnDismissListener {
    private static final String TAG = "MyPdfActivity";

    private static final int MENU_SAVE = 1;
    private static final int MENU_RESET = 2;

    private ContextualToolbar toolbar;
    private ContextualToolbarMenuItem cmiCreateNote, cmiEditNote, cmiSearchDictionary, cmiTranslate, cmiUnderline, cmiStrikeout, cmiSquiggle;

    boolean isBackingUp = false;
    int bookId,book_page_no;
    String note;
    HashMap<String, String> formFieldsMap;

    NotesRepo notesRepo;
    //    NotesFragmentViewModel notesViewModel;
    List<NoteModel> notes;
    WebView webView;
    Timer  timer;
    PdfDocument pdfDocument;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            bookId = intent.getIntExtra("book_id", 0);
            note = intent.getStringExtra("note");


            Log.e(TAG, "onCreate: "+bookId );

            if(!note.equals("null"))
            {

            }
        }





        // Register the activity as a callback for contextual toolbar changes.
        // It will be called once the `TextSelectionToolbar` is going to be presented.
        super.onCreate(savedInstanceState);
        setOnContextualToolbarLifecycleListener(this);

        notesRepo = NotesRepo.getInstance(this);
        notesRepo.getNotesByBookIdLiveData(bookId).observe(this, noteModels -> notes = noteModels);

        //get pdf form data
//        getBookData();

//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
////                    Log.e(TAG, "Sahil" );
////                    Log.e(TAG, "run: "+getSelectedURL() );
//                    String url = getSelectedURL();
//                    url = url.replaceAll("\\s+","");
//                    if(url!=null && isValidURL(url)) {
//                        Intent intent = new Intent(MyPdfActivity.this, DemoActivity.class);
//                        intent.putExtra("LinkUrl", url);
//                        startActivity(intent);
//                        timer.cancel();
//                    }
//                }
//                catch (Exception e)
//                {
//                    Log.e(TAG, "run: "+e );
//                }
//
//            }
//        }, 0, 1000);
    }

    public boolean isValidURL(String url) {

        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }

        return true;
    }


//    @Override
//    public void onResume(){
//        super.onResume();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    Log.e(TAG, "Sahil" );
//                    Log.e(TAG, "run: "+getSelectedURL() );
//                    String url = getSelectedURL();
//                    url = url.replaceAll("\\s+","");
//                    if(url!=null && isValidURL(url)) {
//                        Intent intent = new Intent(MyPdfActivity.this, DemoActivity.class);
//                        intent.putExtra("LinkUrl", url);
//                        startActivity(intent);
//                        timer.cancel();
//                    }
//                }
//                catch (Exception e)
//                {
//                    Log.e(TAG, "run: "+e );
//                }
//
//            }
//        }, 0, 1000);
//
//    }
    @Override
    public boolean onPageClick(@NonNull PdfDocument document, int pageIndex, @Nullable MotionEvent event, @Nullable PointF pagePosition, @Nullable Annotation clickedAnnotation) {
//        Log.i(TAG, "onPageClick: " + clickedAnnotation.toString());
        boolean handled = false;
        // If no annotation was clicked, `clickedAnnotation` may be null.
        // Otherwise, check if the clicked annotation is a link annotation.
        if (clickedAnnotation instanceof LinkAnnotation) {
            LinkAnnotation linkAnnotation = (LinkAnnotation) clickedAnnotation;

            // Every link annotation may have an action defined.
            final Action action = linkAnnotation.getAction();


            Log.e(TAG, "pageIndex: "+pageIndex);


            // Check if an action exists and if it is a `UriAction`.
            if (action instanceof UriAction) {
                // Extract the URI of the annotation action.
//                Uri uri = Uri.parse(((UriAction) action).getUri());

                // You can now check if you would like to handle the URI yourself, e.g. by using
                // a `UriMatcher` or some custom logic.
//                if (isCustomUri(uri)) {
                String uriStr = ((UriAction) action).getUri();
                if (uriStr != null && uriStr.contains("youtube.com") || uriStr.contains("youtu.be") || uriStr.contains("youtube")  ) {
                    String videoId = null;
                    if(uriStr.contains("youtu.be"))
                    {
                        videoId = uriStr.substring(uriStr.indexOf("be/") + 3);
                    }
                    else {
                        videoId = uriStr.substring(uriStr.indexOf("=") + 1);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("videoId", videoId);

                    Log.e(TAG, "onPageClick: "+videoId );


                    utils.gotoNextActivity(MyPdfActivity.this, FullscreenDemoActivity.class, false, bundle);
                    handled = true;
                   // timer.cancel();
//                } else {
//                    Intent intent = new Intent(MyPdfActivity.this, DemoActivity.class);
//                    intent.putExtra("LinkUrl",uriStr);
//                    startActivity(intent);
//                }
                    Log.e(TAG, "onPageClick: "+uriStr );


                } else {

                    Log.e(TAG, "onPageClick: DEMO "+uriStr);
                    try {
                        Uri uri = Uri.parse(uriStr);
                        Intent intent = new Intent(MyPdfActivity.this, DemoActivity.class);
                        intent.putExtra("LinkUrl", uriStr);
                        intent.setDataAndType(uri, "application/pdf");
                        startActivity(intent);
                        timer.cancel();
                    }
                    catch(ActivityNotFoundException e)
                    {
                        Log.e(TAG, "onPageClick: "+e );
                    }
                }
            }
        }
        return handled;
    }



    @Override
    public void onPageChanged(@NonNull PdfDocument document, int pageIndex) {
        super.onPageChanged(document, pageIndex);

        book_page_no = pageIndex;

        Log.e(TAG, "onPageChanged: "+pageIndex );
    }

    private void createCmi() {
        // Create a custom menu item that will be shown inside the text selection toolbar.
        cmiCreateNote = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_create_note,
                AppCompatResources.getDrawable(this, R.drawable.ic_notes_add_24),
                getString(R.string.title_create_note),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );



        // Create a custom menu item that will be shown inside the text selection toolbar.
        cmiEditNote = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_edit_note,
                AppCompatResources.getDrawable(this, R.drawable.ic_notes_edit_24),
                getString(R.string.title_edit_note),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );

        cmiSearchDictionary = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_search_dictionary,
                AppCompatResources.getDrawable(this, R.drawable.ic_baseline_import_contacts_24),
                getString(R.string.text_search_in_dictionary),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );

        cmiTranslate = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_translate,
                AppCompatResources.getDrawable(this, R.drawable.ic_baseline_g_translate_24),
                getString(R.string.title_translate),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );

        cmiUnderline = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_underline,
                AppCompatResources.getDrawable(this, R.drawable.pspdf__ic_underline),
                getString(R.string.title_underline),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );

        cmiStrikeout = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_strikeout,
                AppCompatResources.getDrawable(this, R.drawable.pspdf__ic_strikeout),
                getString(R.string.title_strikeout),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );

        cmiSquiggle = ContextualToolbarMenuItem.createSingleItem(
                this,
                R.id.mi_squiggle,
                AppCompatResources.getDrawable(this, R.drawable.pspdf__ic_squiggly),
                getString(R.string.title_squiggle),
                Color.WHITE,
                Color.RED,
                ContextualToolbarMenuItem.Position.END,
                false
        );
    }

    private Intent createProcessTextIntent() {
        return new Intent()
                .setAction(Intent.ACTION_PROCESS_TEXT)
                .setType("text/plain");
    }

    private List<ResolveInfo> getSupportedActivities() {
        PackageManager packageManager = getPackageManager();
        return packageManager.queryIntentActivities(createProcessTextIntent(), 0);
    }

    public void onInitializeMenu(Menu menu) {
        // Start with a menu Item order value that is high enough
        // so that your "PROCESS_TEXT" menu items appear after the
        // standard selection menu items like Cut, Copy, Paste.
        int menuItemOrder = 100;
        for (ResolveInfo resolveInfo : getSupportedActivities()) {
            String label = String.valueOf(resolveInfo.loadLabel(getPackageManager()));
            menu.add(Menu.NONE, Menu.NONE, menuItemOrder++, label);
//                    .setIntent(createProcessTextIntentForResolveInfo(resolveInfo))
//                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    private void openTranslator(String selectedText) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_PROCESS_TEXT)
//                .addCategory(Intent.CATEGORY_DEFAULT)
//                .setPackage("com.google.android.apps.translate")
                .setType("text/plain")
                .putExtra(Intent.EXTRA_PROCESS_TEXT, selectedText)
                .putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
//                .setClassName("gaurav.lookup", "gaurav.lookup.activities.PopupWord");
//                .setClassName("mumayank.copydictionary", "mumayank.copydictionary.CopyDictionary");
                .setClassName("com.google.android.apps.translate", "com.google.android.apps.translate.copydrop.CopyDropContextMenuActivity");
//                .setClassName(info.activityInfo.packageName, info.activityInfo.name);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            utils.showToast("App not installed");
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        createCmi();
    }

    @Override
    protected void onPause() {
        App.getExecutorService().execute(new RetrieveFormDataTask());
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onSetActivityTitle(@NonNull PdfActivityConfiguration configuration, @Nullable PdfDocument document) {
        // Set the required title in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_SAVE, 0, "Save");
//        menu.add(0, MENU_RESET, 0, "Reset");
//        menu.add(0, FILL_BY_FIELD_NAME, 0, "Fetch and fill");
//        menu.add(0, OPEN_DOCUMENT, 0, "Open document");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Call the super implementation so PSPDFKit can add its own icons to the menu. This has
        // to be called first or your changes will be overwritten.
        super.onPrepareOptionsMenu(menu);

        // Finds the search action by its ID and hides it.
//        menu.findItem(PdfActivity.MENU_OPTION_EDIT_ANNOTATIONS).setVisible(false);
        menu.findItem(PdfActivity.MENU_OPTION_OUTLINE).setVisible(false);
//        menu.findItem(PdfActivity.MENU_OPTION_READER_VIEW).setVisible(false);

        // Make sure to return true to apply your changes.
        return true;
    }

    /**
     * Let's the user tap the activity icon to go 'home'.
     * Requires setHomeButtonEnabled() in onCreate().
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case MENU_SAVE:
                retrieveAndSaveFormData(false);
                return true;
            case MENU_RESET:
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @SuppressLint("CheckResult")
    private void retrieveAndSaveFormData(boolean runInBackground) {
        if (getDocument() == null) return;
        if (isBackingUp) return;
        getDocument().getFormProvider()
                .getFormElementsAsync()
                .subscribe(formElements -> {

                    isBackingUp = true;
//                    List<PdfFormField> pdfFormFieldsData = utils.getPdfFormDataFrom(formElements);
                    PdfData pdfData = new PdfData(utils.getIMEINumber(), bookId, utils.getPdfFormDataFrom(formElements));
                    //call service from her

                    if (runInBackground) {
                        Intent intent = new Intent(this, SendPdfDataService.class);
                        intent.putExtra("pdf_data", pdfData);
                        startService(intent);
                        isBackingUp = false;
                    } else //currently we are on non-UI thread. so following code on UI thread
                        runOnUiThread(() -> saveBookData(pdfData));
//                    for (PdfFormField formField : pdfFormFieldsData) {
//                        Log.i(TAG, "retrieveFormData: " + formField.toString());
//                    }

                });
    }

//    @SuppressLint("CheckResult")
//    private void fillData() {
//        if (getDocument() == null || formFieldsMap == null) return;
//        // Querying form elements by name can be slow. If you need to fill many form
//        // elements at once, retrieve list of all form fields/elements first and iterate through it.
//        getDocument().getFormProvider()
//                .getFormElementsAsync()
//                .subscribe(formElements -> {
//                    utils.fillPdfForm(formElements, formFieldsMap);
//                    formFieldsMap = null;
//                });
//    }

    @SuppressLint("CheckResult")
    @Override
    public void onDocumentLoaded(@NonNull PdfDocument document) {
        super.onDocumentLoaded(document);
        //sometimes data fetched but document not loaded, so filling data again
//        fillData();
    }

    private void saveBookData(PdfData pdfData) {
        SweetAlertDialog progressDialog = utils.showProgressBar(this);
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().saveStudentBookData(pdfData);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                response.toString();
                if (response.isSuccessful())
                    Log.i(TAG, "onResponse: data saved successfully");
                isBackingUp = false;
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                isBackingUp = false;
                progressDialog.dismiss();
            }
        });
    }

//    private void getBookData() {
//        SweetAlertDialog progressDialog = utils.showProgressBar(this);
//        Call<PdfData> call = Retrofit2Client.getInstance().getApiService()
//                .getStudentBookData(bookId, utils.getIMEINumber());
//        call.enqueue(new Callback<PdfData>() {
//            @Override
//            public void onResponse(Call<PdfData> call, Response<PdfData> response) {
//                Log.i(TAG, "onResponse: " + response.toString());
//                if (response.isSuccessful()) {
//
//                    if (response.body() != null && response.body().getFieldList().size() > 0) {
//                        formFieldsMap = new HashMap<>();
//                        for (PdfFormField field : response.body().getFieldList())
//                            formFieldsMap.put(field.getKey(), field.getValue());
//
//                        if (formFieldsMap.size() > 0)
//                            fillData();
//                    }
//                }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<PdfData> call, Throwable t) {
//                progressDialog.dismiss();
//                Log.e(TAG, "onFailure: ", t);
//            }
//        });
//    }

//    private HashMap<String, String> getHashMap(List<PdfFormField> fieldList) {
//        HashMap<String, String> hashMap = new HashMap<>();
//        for (PdfFormField field : fieldList) {
//            hashMap.put(field.getKey(), field.getValue());
//        }
//        return hashMap;
//    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: pressed back");
        App.getExecutorService().execute(new RetrieveFormDataTask());
        super.onBackPressed();
    }

    @Override
    public void onPrepareContextualToolbar(@NonNull ContextualToolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setPosition(ToolbarCoordinatorLayout.LayoutParams.Position.TOP);
        Log.i(TAG, "onPrepareContextualToolbar: ");
        // Add the custom action once the selection toolbar is being prepared.
        // At this point, you could also remove undesired actions from the toolbar.
        if (toolbar instanceof TextSelectionToolbar) {
            boolean addMenuItems = false;
            final List<ContextualToolbarMenuItem> menuItems = toolbar.getMenuItems();
//            ContextualToolbarMenuItem cmiLink = menuItems.get(menuItems.size() - 1);

            toolbar.setMenuItemVisibility(R.id.pspdf__text_selection_toolbar_item_link, View.GONE);
//            toolbar.setMenuItemVisibility(R.id.pspdf__text_selection_toolbar_item_highlight, View.GONE);

            if (!menuItems.contains(cmiUnderline)) {
                addMenuItems = true;
                menuItems.add(2, cmiUnderline);
            }

            if (!menuItems.contains(cmiStrikeout)) {
                addMenuItems = true;
                menuItems.add(2, cmiStrikeout);
            }

            if (!menuItems.contains(cmiSquiggle)) {
                addMenuItems = true;
                menuItems.add(2, cmiSquiggle);
            }

            if (!menuItems.contains(cmiCreateNote)) {
                addMenuItems = true;
                menuItems.add(cmiCreateNote);
            }

            //TODO refactor it
            if (notes.size() > 1) {
                if (!menuItems.contains(cmiEditNote)) {
                    addMenuItems = true;
                    menuItems.add(cmiEditNote);
                }
            }

            if (!menuItems.contains(cmiSearchDictionary)) {
                addMenuItems = true;
                menuItems.add(cmiSearchDictionary);
            }

            if (!menuItems.contains(cmiTranslate)) {
                addMenuItems = true;
                menuItems.add(cmiTranslate);
            }

            if (addMenuItems)
                toolbar.setMenuItems(menuItems);

        } else if (toolbar instanceof AnnotationEditingToolbar) {
            // This shows how to hide annotation note button for all annotations.
            toolbar.setMenuItemVisibility(R.id.pspdf__annotation_editing_toolbar_item_annotation_note, View.GONE);
            ;

        } else if (toolbar instanceof AnnotationCreationToolbar) {
            toolbar.setDraggable(false);
            toolbar.setUseBackButtonForCloseWhenHorizontal(true);
            toolbar.setMenuItemGroupingRule(new CustomAnnotationCreationToolbarGroupingRule(this));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onDisplayContextualToolbar(@NonNull ContextualToolbar toolbar) {
        Log.i(TAG, "onDisplayContextualToolbar: ");
        // Register a click listener to handle taps on the custom text selection action.
        toolbar.setOnMenuItemClickListener((contextualToolbar, menuItem) -> {
            boolean handled = false;

            switch (menuItem.getId()) {
                case R.id.mi_create_note: {
                    handled = true;

                    Log.e(TAG, "onDisplayContextualToolbar: "+bookId);
                    NoteModel note = new NoteModel();
                    note.setBookId(bookId);
                    note.setBook_page_no(book_page_no);
                    note.setDescription(getSelectedText());
                    heightLight();
                    EditNoteFragment editNoteDialogFragment = EditNoteFragment.newInstance(note);
                    editNoteDialogFragment.setOnDismissListener(this);
                    editNoteDialogFragment.show(getSupportFragmentManager(), "editNoteDialogFragment");
                    break;
                }
                case R.id.mi_edit_note: {
                    handled = true;
                    NoteModel note = notes.get(0);
                    note.setDescription(note.getDescription() + "\n \n" + getSelectedText());
                    EditNoteFragment editNoteDialogFragment = EditNoteFragment.newInstance(note);
                    editNoteDialogFragment.setOnDismissListener(this);
                    editNoteDialogFragment.show(getSupportFragmentManager(), "editNoteDialogFragment");

                    break;
                }
                case R.id.mi_search_dictionary: {
                    PopUpDialog dialog = new PopUpDialog(this, getSelectedText());
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setOnDismissListener(dialog1 -> getPdfFragment().exitCurrentlyActiveMode());
                    dialog.show();

                    Log.e(TAG, "search ..." );
                    break;
                }
                case R.id.mi_translate: {
                    openTranslator(getSelectedText());
                    break;
                }


                case R.id.mi_underline:
//                    // Define the places you want to apply annotation
//                    List<RectF> textRects = getPdfFragment().getTextSelection().textBlocks;
//                    // Create the highlight on the first page.
//                    @SuppressLint("Range") final UnderlineAnnotation underlineAnnotation = new UnderlineAnnotation(getPdfFragment().getPageIndex(), textRects);
////                    underlineAnnotation.setColor(Color.RED);
//                    underlineAnnotation.setColor(getPdfFragment().getAnnotationPreferences().getColor(AnnotationTool.UNDERLINE));
//                    // Add it to the page.
//                    getPdfFragment().addAnnotationToPage(underlineAnnotation, false, () -> getPdfFragment().exitCurrentlyActiveMode());
                    handled = underLine();
                    break;

                case R.id.mi_strikeout:
                    @SuppressLint("Range") final StrikeOutAnnotation strikeOutAnnotation = new StrikeOutAnnotation(getPdfFragment().getPageIndex(), getPdfFragment().getTextSelection().textBlocks);
//                    strikeOutAnnotation.setColor(Color.RED);
                    strikeOutAnnotation.setColor(getPdfFragment().getAnnotationPreferences().getColor(AnnotationTool.STRIKEOUT));
                    // Add it to the page.
                    getPdfFragment().addAnnotationToPage(strikeOutAnnotation, false, () -> getPdfFragment().exitCurrentlyActiveMode());
                    handled = true;
                    break;

                case R.id.mi_squiggle:
                    @SuppressLint("Range") final SquigglyAnnotation squigglyAnnotation = new SquigglyAnnotation(getPdfFragment().getPageIndex(), getPdfFragment().getTextSelection().textBlocks);
//                    squigglyAnnotation.setColor(Color.RED);
                    squigglyAnnotation.setColor(getPdfFragment().getAnnotationPreferences().getColor(AnnotationTool.SQUIGGLY));
                    // Add it to the page.
                    getPdfFragment().addAnnotationToPage(squigglyAnnotation, false, () -> getPdfFragment().exitCurrentlyActiveMode());
                    handled = true;
                    break;




            }

            return handled;
        });
    }

    private boolean underLine()
    {



        List<RectF> textRects = getPdfFragment().getTextSelection().textBlocks;
        // Create the highlight on the first page.
        @SuppressLint("Range") final UnderlineAnnotation underlineAnnotation = new UnderlineAnnotation(getPdfFragment().getPageIndex(), textRects);
//                    underlineAnnotation.setColor(Color.RED);
        underlineAnnotation.setColor(getPdfFragment().getAnnotationPreferences().getColor(AnnotationTool.UNDERLINE));
        // Add it to the page.
        getPdfFragment().addAnnotationToPage(underlineAnnotation, false, () -> getPdfFragment().exitCurrentlyActiveMode());
        return true;
    }


    private boolean heightLight()
    {



        List<RectF> textRects = getPdfFragment().getTextSelection().textBlocks;
        // Create the highlight on the first page.
        @SuppressLint("Range") final HighlightAnnotation underlineAnnotation = new HighlightAnnotation(getPdfFragment().getPageIndex(), textRects);
//                    underlineAnnotation.setColor(Color.RED);
        underlineAnnotation.setColor(getPdfFragment().getAnnotationPreferences().getColor(AnnotationTool.HIGHLIGHT));
        // Add it to the page.
        getPdfFragment().addAnnotationToPage(underlineAnnotation, false, () -> getPdfFragment().exitCurrentlyActiveMode());
        return true;
    }


    private void search(String text)
    {
        PopUpDialog dialog = new PopUpDialog(this, text);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(dialog1 -> getDocument());
        dialog.show();

        Log.e(TAG, "search ..." );
    }

    @Override
    public void startSearch(@Nullable String initialQuery, boolean selectInitialQuery, @Nullable Bundle appSearchData, boolean globalSearch) {
        super.startSearch(initialQuery, selectInitialQuery, appSearchData, globalSearch);

    }

    private String getSelectedText( ) {

        if(note.equals("null")) {
            String selectedText = getPdfFragment().getTextSelection().text;
            if (selectedText != null)
                return selectedText.replaceAll("[^A-Za-z\\s]", "").trim();
        }

        return  null;
    }

    private String getSelectedURL() {
        String selectedText = getPdfFragment().getTextSelection().text;
        return selectedText;
    }

    @Override
    public void onRemoveContextualToolbar(@NonNull ContextualToolbar toolbar) {
        toolbar.setOnMenuItemClickListener(null);
    }

    @Override
    public void onDismiss(EditNoteFragment dialogFragment) {
        if (toolbar != null)
            toolbar.onBackPressed();
    }

    class RetrieveFormDataTask implements Runnable {

        @Override
        public void run() {
            retrieveAndSaveFormData(true);
        }
    }
}
