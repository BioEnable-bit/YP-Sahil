package com.yespustak.yespustakapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesUtil {
    private static final String TAG = "FilesUtil";
    public static final int BOOKS_CACHE = 1;
    public static final int BOOKS = 2;

    @NonNull
    public static String getDownloadFilePath(@NonNull final String url, Context context) {
        final Uri uri = Uri.parse(url);
        final String fileName = uri.getLastPathSegment();
        final String dir = getSaveDir(context);
        return (dir + "/DownloadList/" + fileName);
    }

    @NonNull
    public static String getSaveDir(Context context) {
        return context.getCacheDir().toString() + "/fetch";
//        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/fetch";
    }

    public static String moveFileToBooksDirectory(Context context, Uri sourceFileString) {

        //check books directory. create inf not exist
        File booksDir = new File(context.getFilesDir() + "/books");
        if (!booksDir.exists())
            booksDir.mkdirs();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            try {
                //don't need to create file as Files.move() is doing this work
                File book = new File(booksDir.getPath() + "/" + System.currentTimeMillis() + sourceFileString.getLastPathSegment());

                Path sourcePath = Paths.get(new URI(sourceFileString.toString()));
                Path destinationPath = Paths.get(book.toURI());
                Path movedPath = destinationPath;

                if (!book.exists())
                    movedPath = Files.move(sourcePath, destinationPath);

                return movedPath.toUri().toString();
//                    return movedPath.toFile().getAbsolutePath();

            } catch (IOException | URISyntaxException e) {
                Log.e(TAG, "moveFileToBooksDirectory: ", e);
                e.printStackTrace();
            }
        }

        return null;
    }

    @NonNull
    public static String getMimeType(@NonNull final Context context, @NonNull final Uri uri) {
        final ContentResolver cR = context.getContentResolver();
        final MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        if (type == null) {
            type = "*/*";
        }
        return type;
    }

    public static void deleteDir(int type) {
        if (type == BOOKS_CACHE) {
            deleteFileAndContents(new File(utils.getContext().getCacheDir() + "/DownloadList/"));
        } else if (type == BOOKS)
            deleteFileAndContents(new File(utils.getContext().getFilesDir() + "/books/"));
    }

    public static void deleteFileAndContents(@NonNull final File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                final File[] contents = file.listFiles();
                if (contents != null) {
                    for (final File content : contents) {
                        deleteFileAndContents(content);
                    }
                }
            }
            file.delete();
        }
    }

    public static boolean deleteFile(Uri uri) {
        File file = new File(uri.getPath());
        if (file.exists())
            return file.delete();
        return false;
    }

    @NonNull
    public static File createFile(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            final File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File createFileFromInputStream(Context context, InputStream inputStream, String filename) {
        try {
            File f = new File(context.getCacheDir(), filename);
//            File f = File.createTempFile("tmp", ".pdf", getContext().getCacheDir());
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            Log.e(TAG, "createFileFromInputStream: ", e);
        }

        return null;
    }
}
