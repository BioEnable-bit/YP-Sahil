package com.yespustak.yespustakapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.net.UrlEscapers;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.MainActivity;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.repos.DownloadBookRepo;
import com.yespustak.yespustakapp.utils.FilesUtil;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.lang.Thread.sleep;


public class DownloadService extends Service implements Observer<List<DownloadBook>> {
    private static final String TAG = "DownloadService";

    public static final String KEY_STATUS = "status";
    public static final String KEY_DOWNLOAD = "download";
    public static final String KEY_FILE_PROGRESS = "file_progress";
    public static final String KEY_PROGRESS_UPDATE = "progress_update";

    public static boolean forceDownloadAgain = false;

    IBinder iBinder = new MyBinder();
    DownloadBookRepo downloadBookRepo;
    List<DownloadBook> downloadBooks;
    Fetch fetch;

    // Create an executor that executes tasks in the main thread.
    Executor mainExecutor;

    // Create an executor that executes tasks in a background thread.
    ScheduledExecutorService backgroundExecutor;

    //Notification related
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    String channelId = "my_channel_id";
    CharSequence channelName = "My Channel";

    //TODO if this not works -> then show "remaining 3 books"
    List<Integer> addedDownloads = new ArrayList<>();
    int downloadsAdded, downloadCompleted;

    @Override
    public void onCreate() {
        super.onCreate();
        mainExecutor = ContextCompat.getMainExecutor(this);
        backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAndShowNotificationWithChannel();
        }

        //create config and init fetch
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(1)
                .setNamespace(TAG)
                .enableRetryOnNetworkGain(true)
                .setAutoRetryMaxAttempts(5)
                .enableLogging(true)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);

        //find download pending books and start downloading them
        downloadBookRepo = DownloadBookRepo.getInstance(getApplication());
        downloadBooks = downloadBookRepo.getAllDownloadBooksSync();

        for (DownloadBook book : downloadBooks) {
            if (book.getStatus() == Status.COMPLETED || book.getStatus() == Status.DELETED)
                continue;

            book.setStatus(Status.NONE);
            downloadBookRepo.update(book);
        }
        //attach listener
        fetch.addListener(fetchListener);
        createAndEnqueueRequests();

        Log.i(TAG, "onCreate: books size: " + downloadBooks.size());

        //set observer and listen for new items addition
        downloadBookRepo.getAllDownloadBooks().observeForever(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createAndShowNotificationWithChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
//        notificationChannel.enableLights(true);
//        notificationChannel.setLightColor(Color.RED);
//        notificationChannel.enableVibration(true);
//        notificationChannel.setVibrationPattern(new long[]{1000, 2000});
        notificationManager.createNotificationChannel(notificationChannel);

        notificationBuilder = new NotificationCompat.Builder(this, channelId);

//        notificationBuilder.setOngoing(true);
//        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_file_download_24);
    }

    private void postProgressNotification(DownloadBook book, String eta) {
        //TODO this would be open library fragment intent
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("fragment", "library");
        intent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, book.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentTitle(book.getTitle());
        switch (book.getStatus()) {
            case DOWNLOADING:
                notificationBuilder.setOngoing(true);
                notificationBuilder.setContentIntent(pendingIntent);
                notificationBuilder.setContentText(eta);
//                notificationBuilder.setContentText(book.getProgress() + "% " // + book.getSpeed()// +
////                        " ETA : " + Utils.getETAString(getApplicationContext(), book.getEtaInMilliSeconds())
//                );
                notificationBuilder.setProgress(100, book.getProgress(), false);
                break;

            case COMPLETED:
//                notificationBuilder.setOngoing(false);
//                notificationBuilder.setContentText("Download Complete");
                break;
        }

//        if (notificationBuilder.mActions.size() == 0)
//            notificationBuilder.addAction(R.drawable.play, "Pause", actionIntent);

        notificationManager.notify(book.getId(), notificationBuilder.build());
    }

    private void postCompleteNotification(DownloadBook book) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        Intent intent = new Intent(this, MainActivity.class);
        //add extra data to launch related fragment
        //TODO this would be open app intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("filepath", book.getFileUri());
        intent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, book.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.setContentTitle(book.getTitle());
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_baseline_file_download_done_24);
//        builder.setLargeIcon(largeIconBitmap);
//        builder.setColor(ContextCompat.getColor(getApplicationContext(), colorRes))
        builder.setContentText("Download Complete");

        notificationManager.notify(book.getId(), builder.build());
    }

    private void createAndEnqueueRequests() {
        //reset downloads added counter
        downloadsAdded = 0;
        downloadCompleted = 0;

        // Execute a task in the background thread.
        backgroundExecutor.execute(() -> {
            try {
                //loop through books and add in queue
                for (DownloadBook book : downloadBooks) {
                    if (book.getStatus() == Status.COMPLETED || book.getStatus() == Status.DELETED)
                        continue;

                    Log.i(TAG, "creating request from Thread: " + Thread.currentThread().getId());
                    Request request = createRequest(book);

                    // Update UI on the main thread
                    mainExecutor.execute(() -> {
                        Log.i(TAG, "Adding in enqueue from main Thread: " + Thread.currentThread().getId());
                        enqueue(request);
                    });

                    //sleep thread before going for next book
                    sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    private Request createRequest(DownloadBook book) {
        String fileUrl = UrlEscapers.urlFragmentEscaper().escape(Constants.DASHBOARD_URL + book.getFileUrl());
        Log.e("FileUrl",""+fileUrl);
//            String fileUrl = utils.getRandomPdfLink(urlIndex);
        Request request = new Request(fileUrl, FilesUtil.getDownloadFilePath(fileUrl, getApplicationContext()));
        request.setPriority(Priority.NORMAL);
        request.setNetworkType(NetworkType.ALL);
        request.setTag(String.valueOf(book.getId()));
//            request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");

        return request;
    }

    private void enqueue(Request request) {
        //TODO or u can create request array first then add to enqueue in batch
        fetch.enqueue(request, updatedRequest -> {
            Log.i(TAG, "request successfully enqueued : " + request);
        }, error -> {
            //An error occurred enqueuing the request.
            Log.e(TAG, "startDownload: " + error.toString());
        });
    }

    private void updateProgress(Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
        DownloadBook book = getBook(Integer.parseInt(download.getTag()));

        if (book == null) {
//            following remove call trigger purchased books api call after remove to add book entry again
            fetch.remove(download.getId());
            return;
        }

        book.setProgress(download.getProgress());
        book.setSpeed(utils.getDownloadSpeedString(getApplicationContext(), downloadedBytesPerSecond));
        book.setStatus(download.getStatus());
        book.setDownloadedBytes(download.getDownloaded());
        book.setEtaInMilliSeconds(download.getEtaInMilliSeconds());
        downloadBookRepo.update(book);
        postProgressNotification(book, utils.getETAString(getApplicationContext(), etaInMilliSeconds));

        sendMessage(Status.DOWNLOADING, book);

    }

    private DownloadBook getBook(int bookId) {
        for (DownloadBook book : downloadBooks) {
            if (bookId == book.getId())
                return book;
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        //TODO check purchased books
        downloadBookRepo.getPurchasedBooksFromApi();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    @Override
    public void onDestroy() {
//        instance = null;
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    FetchListener fetchListener = new FetchListener() {
        @Override
        public void onWaitingNetwork(@NotNull Download download) {
            Log.i(TAG, "onWaitingNetwork: ");
        }

        @Override
        public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
            Log.i(TAG, "onStarted: " + download.getId());
            DownloadBook book = getBook(Integer.parseInt(download.getTag()));
            if (book != null) {
                book.setStatus(Status.DOWNLOADING);
                book.setTotalBytes(download.getTotal());
                book.setDownloadedBytes(download.getDownloaded());
                downloadBookRepo.update(book);
                sendMessage(Status.ADDED, book);
            }
        }

        @Override
        public void onResumed(@NotNull Download download) {
            Log.i(TAG, "onResumed: ");
        }

        @Override
        public void onRemoved(@NotNull Download download) {
            Log.i(TAG, "onRemoved: " + download.getId());
            sendMessage(Status.REMOVED, null);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean b) {
            Log.i(TAG, "onQueued: Thread: " + Thread.currentThread().getId());
            Log.i(TAG, "onQueued: " + download.getId());
            DownloadBook book = getBook(Integer.parseInt(download.getTag()));
            if (book != null) {
                if (download.getProgress() > 0)
                    notificationManager.cancel(book.getId());
                    book.setStatus(Status.QUEUED);
                    downloadBookRepo.update(book);
                    sendMessage(Status.QUEUED, book);
            }
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
            Log.i(TAG, "onProgress: id: " + download.getTag() + " speed: " + utils.getDownloadSpeedString(getApplicationContext(), downloadedBytesPerSecond));
            updateProgress(download, download.getEtaInMilliSeconds(), downloadedBytesPerSecond);
//            getTotalProgress();
//            createAndPostNotification(downloadedBytesPerSecond, etaInMilliSeconds, download.getProgress());
        }

        @Override
        public void onPaused(@NotNull Download download) {
            Log.i(TAG, "onPaused: ");
            DownloadBook book = getBook(Integer.parseInt(download.getTag()));
            if (book != null) {
                book.setStatus(Status.PAUSED);
                downloadBookRepo.update(book);
            }
        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {
            Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onError: " + error.toString(), throwable);

            //TODO post something like -> retying in few minuets
//            if (error.name().equals("CONNECTION_TIMED_OUT"))
//                fetch.retry(download.getId());

            DownloadBook book = getBook(Integer.parseInt(download.getTag()));
            if (book != null) {
                book.setStatus(Status.FAILED);
                downloadBookRepo.update(book);
            }
        }

        @Override
        public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {
//            Log.i(TAG, "onDownloadBlockUpdated: ");
        }

        @Override
        public void onDeleted(@NotNull Download download) {
            Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onDeleted: ");
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            Log.i(TAG, "onCompleted: " + download.getId());
            DownloadBook book = getBook(Integer.parseInt(download.getTag()));
            if (book != null) {
                downloadCompleted++;
                addedDownloads.remove((Integer) download.getId());

                //move file
                String filepath = FilesUtil.moveFileToBooksDirectory(getApplicationContext(), download.getFileUri());
                Log.i(TAG, "onCompleted: moved to " + filepath);


                if (filepath != null) {
                    book.setStatus(Status.COMPLETED);
                    book.setFileUri(filepath);
                    downloadBookRepo.update(book);
                    sendMessage(Status.COMPLETED, book);

                    postCompleteNotification(book);
                }
                fetch.remove(download.getId());
            }
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            Log.i(TAG, "onCancelled: ");
        }

        @Override
        public void onAdded(@NotNull Download download) {
            Log.i(TAG, "onAdded: Thread: " + Thread.currentThread().getId());
            Log.i(TAG, "onAdded: " + download.getId());

            if (!addedDownloads.contains(download.getId())) {
                addedDownloads.add(download.getId());
                downloadsAdded++;
            }
        }
    };


    //item change observer
    @Override
    public void onChanged(List<DownloadBook> downloadBooks) {
        int previousCount = this.downloadBooks.size();
        this.downloadBooks = downloadBooks;
        if (previousCount < downloadBooks.size() || forceDownloadAgain) {
            createAndEnqueueRequests();
            forceDownloadAgain = false;
        }

        boolean downloadComplete = true;
        for (DownloadBook book : downloadBooks) {
            //except deleted
            if (book.getStatus() == Status.DELETED)
                continue;

            if (book.getStatus() != Status.COMPLETED) {
                downloadComplete = false;
                break;
            }
        }
        if (downloadComplete) {
            downloadsAdded = 0;
            downloadCompleted = 0;
        }
    }

//    private void getTotalProgress() {
//        fetch.getDownloads(new Func<List<Download>>() {
//            long total = 0L, downloaded = 0L;
//
//            @Override
//            public void call(@NotNull List<Download> result) {
//                for (Download download : result) {
//                    total += download.getTotal();
//                    downloaded += download.getDownloaded();
//                }
//                double percentage = ((double) downloaded / total) * 100d;
//
//                Log.i(TAG, "getTotalProgress: downloaded - " + downloaded + " total - " + total + " percentage - " + percentage + "%");
//            }
//        });
//    }

    // Send an Intent with an action named "custom-event-name". The Intent sent should
    // be received by the ReceiverActivity.
    private void sendMessage(Status status, DownloadBook downloadBook) {
//        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(KEY_PROGRESS_UPDATE);

        // You can also include some extra data.
        intent.putExtra(KEY_STATUS, status);
        intent.putExtra(KEY_DOWNLOAD, downloadBook);
        intent.putExtra(KEY_FILE_PROGRESS, "Downloading " + (downloadCompleted + 1) + " of " + downloadsAdded + " books");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class MyBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }
    }

}