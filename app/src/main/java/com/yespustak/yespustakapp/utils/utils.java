package com.yespustak.yespustakapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.pspdfkit.annotations.AnnotationType;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.configuration.sharing.ShareFeatures;
import com.pspdfkit.forms.CheckBoxFormElement;
import com.pspdfkit.forms.ComboBoxFormElement;
import com.pspdfkit.forms.FormElement;
import com.pspdfkit.forms.FormType;
import com.pspdfkit.forms.ListBoxFormElement;
import com.pspdfkit.forms.PushButtonFormElement;
import com.pspdfkit.forms.RadioButtonFormElement;
import com.pspdfkit.forms.TextFormElement;
import com.pspdfkit.ui.PdfActivityIntentBuilder;
import com.pspdfkit.ui.special_mode.controller.AnnotationTool;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.activities.MyPdfActivity;
import com.yespustak.yespustakapp.models.PdfFormField;
import com.yespustak.yespustakapp.utils.shakeAnim.Techniques;
import com.yespustak.yespustakapp.utils.shakeAnim.YoYo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class utils {
    private static final String TAG = "utils";


    public static Context getContext() {
        return App.getAppContext();
    }

    public static String getStringResource(int id) {
        return getContext().getString(id);
    }

    public static String getStringResource(int id, Object... formatArgs) {
        return getContext().getString(id, formatArgs);
    }

    public static void getFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void gotoNextActivity(Activity activity, Class<?> aClass, Boolean finishActivity) {
        gotoNextActivity(activity, aClass, finishActivity, null);
    }

    public static void gotoNextActivity(Activity activity, Class<?> aClass, Boolean finishActivity, Bundle bundleArgs) {
        Intent intent = new Intent(activity, aClass);
        if (bundleArgs != null)
            intent.putExtras(bundleArgs);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        if (finishActivity) {
            activity.finish();
        }
    }


    public static void gotoPrevActivity(Activity activity, Class<?> aClass, Boolean finishActivity) {
        Intent intent = new Intent(activity.getApplicationContext(), aClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.getApplication().startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        if (finishActivity) {
            activity.finish();
        }
    }

    public static void gotoFragment(Activity activity, String fragment) {
        Intent intent = new Intent(getContext(), FragmentActivity.class);
        intent.putExtra("fragment", fragment);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public static void gotoNextActivityFragment(Activity activity, String fragment) {
        Intent intent = new Intent(getContext(), FragmentActivity.class);
        intent.putExtra("fragment", fragment);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public static void gotoNextFragment(@IdRes int containerViewId, Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(containerViewId, fragment)
//                .addToBackStack(null)
//                .commit();
                .commitAllowingStateLoss();//this is not recommended -> find alternative solution
    }

    public static void gotoNextFragment(@IdRes int containerViewId, Fragment fragment, FragmentManager fragmentManager, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(containerViewId, fragment);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    public static void openFragment(@IdRes int containerViewId, Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment)
//                .addToBackStack(null)
                .commit();
    }

    public static void gotoPrevFragment(@IdRes int containerViewId, Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(containerViewId, fragment)
//                .addToBackStack(null)
                .commit();
    }

    public static void clearBackStack(FragmentManager fragmentManager) {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            fragmentManager.popBackStack();
        }
    }

    public static void showToast(String text) {
        try {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(int resId) {
        showToast(getStringResource(resId));
    }

    public static void showSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }


    public static void setError(TextInputLayout textInputLayout, String msg) {
        YoYo.with(Techniques.Shake).playOn(textInputLayout);
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(msg);
        textInputLayout.getEditText().requestFocus();
    }

    //TODO: remove it in next commit
    public static void setError(EditText editText, String msg) {
        ViewParent parent = editText.getParent().getParent();
        if (parent instanceof View) {
            TextInputLayout textInputLayout = (TextInputLayout) parent;
            textInputLayout.setError(msg);
            if (msg != null) {
                textInputLayout.setErrorEnabled(true);
                YoYo.with(Techniques.Shake).playOn(textInputLayout);
            } else
                textInputLayout.setErrorEnabled(false);
            textInputLayout.getEditText().requestFocus();
        }
    }

    public static void setError(EditText editText, String msg, boolean animate) {
        ViewParent parent = editText.getParent().getParent();
        if (parent instanceof View) {
            TextInputLayout textInputLayout = (TextInputLayout) parent;
            textInputLayout.setError(msg);
            textInputLayout.setErrorEnabled(msg != null);
            if (msg != null && animate) {
                YoYo.with(Techniques.Shake).playOn(textInputLayout);
                textInputLayout.getEditText().requestFocus();
            }
        }
    }

    public static void setSpinnerError(Spinner spinner) {
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView) {
            YoYo.with(Techniques.Shake).playOn(spinner);
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
//            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
//            selectedTextView.setText(error); // actual error message
//            spinner.performClick(); // to open the spinner list if error is found.

        }
    }

    public static void removeError(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(null);
    }


    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static SweetAlertDialog showProgressBar(Context context) {
        SweetAlertDialog pDialog = getProgressDialog(context);
        pDialog.show();
        return pDialog;
    }

    public static SweetAlertDialog getProgressDialog(Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(utils.getStringResource(R.string.title_loading));
        pDialog.setCancelable(false);
        return pDialog;
    }

    public static void hideProgressBar(SweetAlertDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static void showAlert(Context context, String title, String msg, int type, SweetAlertDialog.OnSweetClickListener listener, boolean flipButtonColors) {
        SweetAlertDialog alertDialog = new SweetAlertDialog(context, type);
        String tempTitle = null;
        switch (type) {
            case SweetAlertDialog.SUCCESS_TYPE:
                alertDialog.setCanceledOnTouchOutside(false);
                tempTitle = utils.getStringResource(R.string.text_success_exclamation);
                break;
            case SweetAlertDialog.ERROR_TYPE:
                alertDialog.setCanceledOnTouchOutside(false);
                tempTitle = utils.getStringResource(R.string.text_failed_exclamation);
                break;
            case SweetAlertDialog.WARNING_TYPE:
                tempTitle = utils.getStringResource(R.string.text_are_you_sure);
                alertDialog.setCancelText(utils.getStringResource(R.string.title_no));
                alertDialog.setConfirmText(utils.getStringResource(R.string.title_yes));
                alertDialog.showCancelButton(true);
                break;
        }
        alertDialog.setTitleText(title == null ? tempTitle : title);
        alertDialog.setContentText(msg);
        if (listener != null)
            alertDialog.setConfirmClickListener(listener);

        if (flipButtonColors) {
            alertDialog.setCancelButtonBackgroundColor(context.getColor(R.color.main_green_color));
            alertDialog.setConfirmButtonBackgroundColor(context.getColor(R.color.red_btn_bg_color));
        }
        alertDialog.show();
    }

    public static void showKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }


    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEINumber() {
//        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();
        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    public static boolean isConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String dateToString(Date date, String format) {
        // create date format
        DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        // string using format() method
        return df.format(date);
    }

    public static String getInput(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static void showDatePicker(Context context, final String format, final DatePickerListener listener) {
        DatePickerDialog picker;
        final Calendar myCalendar = Calendar.getInstance();
        //set initial date to 15 year back
        myCalendar.set(Calendar.YEAR, myCalendar.get(Calendar.YEAR) - 6);
        myCalendar.set(Calendar.DAY_OF_MONTH, 1);
        myCalendar.add(Calendar.DAY_OF_MONTH, -1);

        picker = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            listener.onDateSelected(dateToString(calendar.getTime(), format));
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        picker.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());

        //set max
        myCalendar.set(Calendar.YEAR, 2000);
        myCalendar.set(Calendar.MONTH, 1);
        myCalendar.set(Calendar.DAY_OF_MONTH, 1);
        picker.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
        picker.show();
    }

    public static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }

    public static void calcProgressToView(ProgressBar progressBar, long offset, long total) {
        final float percent = (float) offset / total;
        progressBar.setProgress((int) (percent * progressBar.getMax()));
    }


    public static File getParentFile() {
        final File externalSaveDir = getContext().getExternalCacheDir();
        if (externalSaveDir == null) {
            return getContext().getCacheDir();
        } else {
            return externalSaveDir;
        }
    }

    //interface for data picker listener
    public interface DatePickerListener {
        void onDateSelected(String date);
    }

    public static List<PdfFormField> getPdfFormDataFrom(List<FormElement> formElements) {
        List<PdfFormField> fieldList = new ArrayList<>();
        fieldList.clear();
        for (FormElement formElement : formElements) {
            switch (formElement.getType()) {
                case TEXT:
                    TextFormElement text = (TextFormElement) formElement;
                    switch (text.getInputFormat()) {
                        case NORMAL:
                        case DATE:
                            fieldList.add(new PdfFormField(formElement.getType(), text.getName(), text.getText()));
//                          Log.i(TAG, "retrieveFormData: TEXT name: " + textFormElement.getName() + " value: " + textFormElement.getText());
                            break;
                    }
                    break;
                case CHECKBOX:
                    CheckBoxFormElement checkBox = (CheckBoxFormElement) formElement;
                    fieldList.add(new PdfFormField(formElement.getType(), checkBox.getName(), String.valueOf(checkBox.isSelected())));
//                                Log.i(TAG, "retrieveFormData: CHECKBOX name: " + checkBoxFormElement.getName() + " value: " + checkBoxFormElement.isSelected());
//                                ((CheckBoxFormElement) formElement).toggleSelection()
                    break;
                case RADIOBUTTON:
                    RadioButtonFormElement radioButton = (RadioButtonFormElement) formElement;
                    fieldList.add(new PdfFormField(formElement.getType(), radioButton.getName(), String.valueOf(radioButton.isSelected())));
//                                if (radioButton.isSelected()) {
//                                    String qualifiedName = radioButton.getFullyQualifiedName();
//                                    String key = qualifiedName.substring(0, qualifiedName.indexOf("."));
//                                    String selectedValue = qualifiedName.substring(qualifiedName.indexOf(".") + 1);
//
//                                    fieldList.add(new PdfFormField(formElement.getType(), qualifiedName, key, selectedValue));
//                                }
//                                Log.i(TAG, "retrieveFormData: RADIOBUTTON name: " + radioButton.getName() + " value: " + radioButton.isSelected());
//                                Log.i(TAG, "retrieveFormData: RADIOBUTTON name: " + radioButton.getName() + " value: " + radioButton.getExportValue());
                    break;

                case COMBOBOX://Dropdown
                    ComboBoxFormElement comboBox = (ComboBoxFormElement) formElement;
                    fieldList.add(new PdfFormField(formElement.getType(), comboBox.getName(), comboBox.getSelectedIndexes().toString()));
//                                Log.i(TAG, "retrieveFormData: COMBOBOX name: " + comboBox.getName() + " value: " + comboBox.getCustomText());

                    break;
                case LISTBOX://multiple selectable items
                    ListBoxFormElement listBox = (ListBoxFormElement) formElement;
                    fieldList.add(new PdfFormField(formElement.getType(), listBox.getName(), listBox.getSelectedIndexes().toString()));
//                                Log.i(TAG, "retrieveFormData: LISTBOX name: " + listBox.getName() + " value: " + listBox.getSelectedIndexes());
                    break;
                case PUSHBUTTON:
                    PushButtonFormElement pushButton = (PushButtonFormElement) formElement;
                    Log.i(TAG, "retrieveFormData: PUSHBUTTON name: " + pushButton.getName() + " value: " + pushButton.getAction());
                    break;
                case SIGNATURE:
                    Log.i(TAG, "retrieveFormData: SIGNATURE");
                    break;
                case UNDEFINED:
                    Log.i(TAG, "retrieveFormData: UNDEFINED name: " + ((FormElement) formElement).getName() + " value: " + ((FormElement) formElement).getType());
                    break;
                default:
                    // Do nothing.
            }
        }

        return fieldList;
    }

    public static void fillPdfForm(List<FormElement> formElements, HashMap<String, String> formFieldsMap) {
        if (formElements == null || formFieldsMap == null) return;

        for (FormElement formElement : formElements) {

            //data available in hashMap then proceed
            if (formFieldsMap.containsKey(formElement.getName())) {
                String value = formFieldsMap.get(formElement.getName());

                //check field and and fill data according type
                if (formElement.getType() == FormType.TEXT) {
                    TextFormElement textFormElement = (TextFormElement) formElement;
                    textFormElement.setText(value);

                } else if ((formElement.getType() == FormType.CHECKBOX)) {
                    CheckBoxFormElement checkBox = (CheckBoxFormElement) formElement;
                    if (Boolean.parseBoolean(value))
                        checkBox.select();
                    else checkBox.deselect();

                } else if ((formElement.getType() == FormType.RADIOBUTTON)) {
                    RadioButtonFormElement radioButton = (RadioButtonFormElement) formElement;
                    if (Boolean.parseBoolean(value))
                        radioButton.select();
                    else radioButton.deselect();
                }
            }
        }
    }

    //Fetch utils functions
    @NonNull
    public static String getETAString(@NonNull final Context context, final long etaInMilliSeconds) {
        if (etaInMilliSeconds < 0) {
            return "";
        }
        int seconds = (int) (etaInMilliSeconds / 1000);
        long hours = seconds / 3600;
        seconds -= hours * 3600;
        long minutes = seconds / 60;
        seconds -= minutes * 60;
        if (hours > 0) {
            return context.getString(R.string.download_eta_hrs, hours, minutes, seconds);
        } else if (minutes > 0) {
            return context.getString(R.string.download_eta_min, minutes, seconds);
        } else {
            return context.getString(R.string.download_eta_sec, seconds);
        }
    }

    @NonNull
    public static String getDownloadSpeedString(@NonNull final Context context, final long downloadedBytesPerSecond) {
        if (downloadedBytesPerSecond < 0) {
            return "";
        }
        double kb = (double) downloadedBytesPerSecond / (double) 1000;
        double mb = kb / (double) 1000;
        final DecimalFormat decimalFormat = new DecimalFormat(".##");
        if (mb >= 1) {
            return context.getString(R.string.download_speed_mb, decimalFormat.format(mb));
        } else if (kb >= 1) {
            return context.getString(R.string.download_speed_kb, decimalFormat.format(kb));
        } else {
            return context.getString(R.string.download_speed_bytes, downloadedBytesPerSecond);
        }
    }

    public static String bytesToReadableStr(long bytes) {
        double kb = (double) bytes / (double) 1000;
        double mb = kb / (double) 1000;
        final DecimalFormat decimalFormat = new DecimalFormat(".##");
        if (mb >= 1) {
            return getContext().getString(R.string.download_size_mb, decimalFormat.format(mb));
        } else if (kb >= 1) {
            return getContext().getString(R.string.download_size_kb, decimalFormat.format(kb));
        } else {
            return getContext().getString(R.string.download_size_bytes, bytes);
        }
    }

    public static String capitaliseFirstLetter(String word) {
        String firstLetter = String.valueOf(word.charAt(0)).toUpperCase();
        if (word.length() > 1)
            return firstLetter + word.substring(1);

        return firstLetter;
    }


    //this part is temporary
    public static String getRandomPdfLink(int urlIndex) {
        String[] urls = {
                "https://download.support.xerox.com/pub/docs/FlowPort2/userdocs/any-os/en/fp_dc_setup_guide.pdf",
//                "https://www.eurofound.europa.eu/sites/default/files/ef_publication/field_ef_document/ef1710en.pdf",
                "https://maxdb.sap.com/training/expert_sessions/MaxDB_LowTCO.pdf",
                "https://cartographicperspectives.org/index.php/journal/article/download/cp13-full/pdf/",
                "https://www.hq.nasa.gov/alsj/a410/AS08_CM.PDF",
                "http://ddeku.edu.in/Files/2cfa4584-5afe-43ce-aa4b-ad936cc9d3be/Custom/Sampling.pdf",
                "http://www.jnkvv.org/PDF/07042020174206Basic_Concepts_in_Sampling_Techniques.pdf",
                "http://cbseacademic.nic.in/web_material/CurriculumMain22/Sec/Science_Sec_2021-22.pdf",
                "http://cbseacademic.nic.in/web_material/SQP/ClassX_2020_21/Science-SQP.pdf",
                "http://du.ac.in/du/uploads/COVID-19/pdf/adm2020/6th%20Cut-Off%202020%20-%20Science.pdf",
                "http://www.unesco.org/new/fileadmin/MULTIMEDIA/FIELD/New_Delhi/images/contestguidelines_03.pdf",
                "http://www.ece.uah.edu/~milenka/docs/armen.dzhagaryan.dissertation.pdf"

        };
        return urls[urlIndex < urls.length ? urlIndex : 0];
    }


    //PDF related functions
    public static void openPdfActivity(Activity activity, Uri fileUri, int bookId,String bookPassword) {
        // In this example, you want to enable all annotation tools except the `IMAGE` tool.
        List<AnnotationTool> enabledAnnotationTools = new ArrayList<>();
        enabledAnnotationTools.addAll(Arrays.asList(AnnotationTool.values()));
        enabledAnnotationTools.remove(AnnotationTool.IMAGE);
        enabledAnnotationTools.remove(AnnotationTool.SIGNATURE);
        enabledAnnotationTools.remove(AnnotationTool.SQUARE);
        enabledAnnotationTools.remove(AnnotationTool.NOTE);
        enabledAnnotationTools.remove(AnnotationTool.STAMP);
        enabledAnnotationTools.remove(AnnotationTool.CAMERA);
        enabledAnnotationTools.remove(AnnotationTool.FREETEXT);
        enabledAnnotationTools.remove(AnnotationTool.FREETEXT_CALLOUT);
        enabledAnnotationTools.remove(AnnotationTool.ERASER);



        PdfActivityConfiguration config = new PdfActivityConfiguration.Builder(activity)
                .autosaveEnabled(true)
                .enableFormEditing()
                .setEnabledShareFeatures(ShareFeatures.none())
                .disablePrinting()
                .disableDocumentEditor()
//                .textSelectionPopupToolbarEnabled(false)
                .enableAnnotationEditing()
                .enabledAnnotationTools(enabledAnnotationTools)
                .enableAnnotationRotation()
                .textSelectionPopupToolbarEnabled(false)
                .animateScrollOnEdgeTaps(true)
//                .disableAnnotationEditing()
//                .disableBookmarkList()
//                .disableAnnotationList()
                .disableDocumentInfoView()
                .build();

        //Start using uri
//        Uri fileUri = getAssetFileUri(filename);
        if (bookPassword!=null && bookPassword!="" && bookPassword!="null" &&
                !bookPassword.equalsIgnoreCase("") &&
                !bookPassword.equalsIgnoreCase("null") &&
                !bookPassword.equalsIgnoreCase(null)){
            final Intent intent = PdfActivityIntentBuilder.fromUri(activity, fileUri)
                    .configuration(config)
                    .activityClass(MyPdfActivity.class)
                    .passwords(bookPassword)
                    .build();
            intent.putExtra("book_id", bookId);
            activity.startActivity(intent);
        } else {
            final Intent intent = PdfActivityIntentBuilder.fromUri(activity, fileUri)
                    .configuration(config)
                    .activityClass(MyPdfActivity.class)
                    .build();
            intent.putExtra("book_id", bookId);
            activity.startActivity(intent);
        }


        //TODO replace this with original data
//        intent.putExtra("student_id", 1);

    }

    public static void openPdfActivity2(Activity activity, Uri fileUri, int bookId,String bookPassword,String note,int pageNo) {
        // In this example, you want to enable all annotation tools except the `IMAGE` tool.
        List<AnnotationTool> enabledAnnotationTools = new ArrayList<>();
        enabledAnnotationTools.addAll(Arrays.asList(AnnotationTool.values()));
        enabledAnnotationTools.remove(AnnotationTool.IMAGE);
        enabledAnnotationTools.remove(AnnotationTool.SIGNATURE);
        enabledAnnotationTools.remove(AnnotationTool.SQUARE);
        enabledAnnotationTools.remove(AnnotationTool.NOTE);
        enabledAnnotationTools.remove(AnnotationTool.STAMP);
        enabledAnnotationTools.remove(AnnotationTool.CAMERA);
        enabledAnnotationTools.remove(AnnotationTool.FREETEXT);
        enabledAnnotationTools.remove(AnnotationTool.FREETEXT_CALLOUT);
        enabledAnnotationTools.remove(AnnotationTool.ERASER);
        PdfActivityConfiguration config;


        Log.e(TAG, "openPdfActivity2: "+bookId);

        if(pageNo==0)
        {
            config = new PdfActivityConfiguration.Builder(activity)
                    .autosaveEnabled(true)
                    .enableFormEditing()
                    .setEnabledShareFeatures(ShareFeatures.none())
                    .disablePrinting()
                    .disableDocumentEditor()
//                .textSelectionPopupToolbarEnabled(false)
                    .enableAnnotationEditing()
                    .enabledAnnotationTools(enabledAnnotationTools)
                    .enableAnnotationRotation()
                    .textSelectionPopupToolbarEnabled(false)
                    .animateScrollOnEdgeTaps(true)
//                .disableAnnotationEditing()
//                .disableBookmarkList()
//                .disableAnnotationList()
                    .disableDocumentInfoView()
                    .build();
        }
        else
        {
            config = new PdfActivityConfiguration.Builder(activity)
                .autosaveEnabled(true)
                .enableFormEditing()
                .setEnabledShareFeatures(ShareFeatures.none())
                .disablePrinting()
                .disableDocumentEditor()
//                .textSelectionPopupToolbarEnabled(false)
                .enableAnnotationEditing()
                .enabledAnnotationTools(enabledAnnotationTools)
                .enableAnnotationRotation()
                .textSelectionPopupToolbarEnabled(false)
                .animateScrollOnEdgeTaps(true)
//                .disableAnnotationEditing()
//                .disableBookmarkList()
//                .disableAnnotationList()
                .page(pageNo)
                .disableDocumentInfoView()
                .build();
        }




        //Start using uri
//        Uri fileUri = getAssetFileUri(filename);
        if (bookPassword!=null && bookPassword!="" && bookPassword!="null" &&
                !bookPassword.equalsIgnoreCase("") &&
                !bookPassword.equalsIgnoreCase("null") &&
                !bookPassword.equalsIgnoreCase(null)){
            final Intent intent = PdfActivityIntentBuilder.fromUri(activity, fileUri)
                    .configuration(config)
                    .activityClass(MyPdfActivity.class)
                    .passwords(bookPassword)
                    .build();
            intent.putExtra("book_id", bookId);
            intent.putExtra("note",note);
            activity.startActivity(intent);
        } else {
            final Intent intent = PdfActivityIntentBuilder.fromUri(activity, fileUri)
                    .configuration(config)
                    .activityClass(MyPdfActivity.class)
                    .build();
            intent.putExtra("book_id", bookId);
            intent.putExtra("note",note);
            activity.startActivity(intent);
        }


        //TODO replace this with original data
//        intent.putExtra("student_id", 1);

    }


//    private Uri getAssetFileUri(String filename) {
//        AssetManager am = getActivity().getAssets();
//        File file = null;
//
//        try {
//            InputStream inputStream = am.open(filename);
//            file = FilesUtil.createFileFromInputStream(getContext(), inputStream, filename);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (file == null) {
//            Log.e(TAG, "getAssetFile: newly created file is null");
//        }
//        return FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", file);
//    }

//    private void openPdfInAdobe(String filename) {
//        Uri fileUri = getAssetFileUri(filename);
//        Intent i = new Intent();
//        i.setAction(Intent.ACTION_VIEW);
//        i.setData(fileUri);
//        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        i.setComponent(new ComponentName("com.adobe.reader", "com.adobe.reader.AdobeReader"));
//        try {
//            startActivity(i);
//        } catch (ActivityNotFoundException e) {
//            utils.showToast("Adobe reader not found");
//            Log.i(TAG, "Exception - " + e.getMessage());
//        }
//    }


}
