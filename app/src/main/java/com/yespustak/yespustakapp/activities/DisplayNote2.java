package com.yespustak.yespustakapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.utils.NDb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class DisplayNote2 extends AppCompatActivity {
    EditText name;
    EditText content;
    CoordinatorLayout coordinatorLayout;
    String dateString;
    Bundle extras;
    int id_To_Update = 0;
    Snackbar snackbar;
    FloatingActionButton fab, fab2;
    Toolbar toolbar;
    private NDb mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Yespustak | Notes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = findViewById(R.id.txtname);
        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab2);
        content = findViewById(R.id.txtcontent);
        coordinatorLayout = findViewById(R.id
                .coordinatorLayout);
        mydb = new NDb(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                snackbar = Snackbar
                        .make(coordinatorLayout, "Note Id : " + String.valueOf(Value), Snackbar.LENGTH_LONG);
                snackbar.show();
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                String nam = rs.getString(rs.getColumnIndex(NDb.name));
                String contents = rs.getString(rs.getColumnIndex(NDb.remark));
                if (!rs.isClosed()) {
                    rs.close();
                }
                name.setText((CharSequence) nam);
                content.setText((CharSequence) contents);
            }
        }
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydb.deleteNotes(id_To_Update);
                Toast.makeText(DisplayNote2.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
                finish();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setMessage(R.string.DeleteNote)
//                        .setPositiveButton("YES",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int id) {
//                                        mydb.deleteNotes(id_To_Update);
//                                        Toast.makeText(DisplayNote.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(
//                                                getApplicationContext(),
//                                                MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                })
//                        .setNegativeButton("NO",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int id) {
//                                    }
//                                });
//                AlertDialog d = builder.create();
//                d.setTitle("Are you sure");
//                d.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = getIntent().getExtras();
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                dateString = formattedDate;
                if (extras != null) {
                    int Value = extras.getInt("id");
                    if (Value > 0) {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, "Please fill in name of the note", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            if (mydb.updateNotes(id_To_Update, name.getText()
                                    .toString(), dateString, content.getText()
                                    .toString())) {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Your note Updated Successfully!!!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                onBackPressed();
                            } else {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "There's an error. That's all I can tell. Sorry!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, "Please fill in name of the note", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            if (mydb.insertNotes(name.getText().toString(), dateString,
                                    content.getText().toString())) {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Added Successfully.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                onBackPressed();
                            } else {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Unfortunately Task Failed.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }
                }
                finish();
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.Delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.DeleteNote)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mydb.deleteNotes(id_To_Update);
                                        Toast.makeText(DisplayNote2.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(
                                                getApplicationContext(),
                                                MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                    }
                                });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
                return true;
            case R.id.Save:
                if (name.getText().toString().trim().equals("")) {
                    name.setError("Please fill title");
                }

                Bundle extras = getIntent().getExtras();
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                dateString = formattedDate;
                if (extras != null) {
                    int Value = extras.getInt("id");
                    if (Value > 0) {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, "Please fill in name of the note", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            if (mydb.updateNotes(id_To_Update, name.getText()
                                    .toString(), dateString, content.getText()
                                    .toString())) {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Your note Updated Successfully!!!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                onBackPressed();
                            } else {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "There's an error. That's all I can tell. Sorry!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, "Please fill in name of the note", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            if (mydb.insertNotes(name.getText().toString(), dateString,
                                    content.getText().toString())) {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Added Successfully.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                onBackPressed();
                            } else {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Unfortunately Task Failed.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(
                getApplicationContext(),
                MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}