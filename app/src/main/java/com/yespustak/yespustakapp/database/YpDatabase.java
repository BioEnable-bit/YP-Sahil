package com.yespustak.yespustakapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.yespustak.yespustakapp.dao.DownloadBookDao;
import com.yespustak.yespustakapp.dao.NotesDao;
import com.yespustak.yespustakapp.dao.UserDao;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.models.UserModel;

@Database(entities = {DownloadBook.class, UserModel.class, NoteModel.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class YpDatabase extends RoomDatabase {

    private static YpDatabase instance;

    public abstract DownloadBookDao downloadBookDao();
    public abstract UserDao userDao();
    public abstract NotesDao notesDao();

    public static synchronized YpDatabase getInstance(Context context) {
        if (instance ==  null) {
            instance = Room.databaseBuilder(context, YpDatabase.class, "yp_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(callback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
//            DownloadBookDao downloadBookDao = instance.downloadBookDao();
//            insertSampleData(downloadBookDao );

        }
    };
}
