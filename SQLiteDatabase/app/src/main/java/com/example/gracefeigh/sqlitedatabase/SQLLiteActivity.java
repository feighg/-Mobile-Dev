package com.example.gracefeigh.sqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SQLLiteActivity extends AppCompatActivity {
    SQLiteExample mSQLiteExample;
    Button mSQLSubmitButton;
    Cursor mSQLCursor;
    SimpleCursorAdapter mSQLCursorAdapter;
    private static final String TAG = "SQLActivity";
    SQLiteDatabase mSQLDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqllite);

        mSQLiteExample = new SQLiteExample(this);
        mSQLDB = mSQLiteExample.getWritableDatabase();

        mSQLSubmitButton = (Button) findViewById(R.id.button);
        mSQLSubmitButton.setOnClickListener((v) -> {
            if (mSQLDB != null) {
                ContentValues vals = new ContentValues();
                vals.put(DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, ((EditText) findViewById(R.id.sql_string_input)).getText().toString());
                vals.put(DBContract.DemoTable.COLUMN_NAME_DEMO_INT, ((EditText) findViewById(R.id.sql_int_input)).getText().toString());
                mSQLDB.insert(DBContract.DemoTable.TABLE_NAME, null, vals);
                populateTable();
            }else{
                Log.d(TAG, "Unable to access database");
            }
        });
    }

    final class DBContract {
        private DBContract(){};

        public final class DemoTable implements BaseColumns {
            public static final String DB_NAME = "demo_db";
            public static final String TABLE_NAME = "demo";
            public static final String COLUMN_NAME_DEMO_STRING = "demo_string";
            public static final String COLUMN_NAME_DEMO_INT = "demo_int";
            public static final int DB_VERSION = 5;


            public static final String SQL_CREATE_DEMO_TABLE = "CREATE TABLE " +
                    DemoTable.TABLE_NAME + "(" + DemoTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    DemoTable.COLUMN_NAME_DEMO_STRING + " VARCHAR(255)," +
                    DemoTable.COLUMN_NAME_DEMO_INT + " INTEGER);";

            public  static final String SQL_DROP_DEMO_TABLE = "DROP TABLE IF EXISTS " + DemoTable.TABLE_NAME;
        }
    }

    private void populateTable(){
        if (mSQLDB != null){
            try{
                if(mSQLCursorAdapter != null && mSQLCursorAdapter.getCursor() != null){
                    if (!mSQLCursorAdapter.getCursor().isClosed()){
                        mSQLCursorAdapter.getCursor().close();
                    }
                }
                mSQLCursor = mSQLDB.query(DBContract.DemoTable.TABLE_NAME,
                                new String[]{DBContract.DemoTable._ID, DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, DBContract.DemoTable.COLUMN_NAME_DEMO_INT},
                        DBContract.DemoTable.COLUMN_NAME_DEMO_INT + " > ?",
                        new String[]{"100"},
                        null,
                        null,
                        null);

                ListView SQLListView = (ListView) findViewById(R.id.sql_listview);
                mSQLCursorAdapter = new SimpleCursorAdapter(this,
                        R.layout.sql_item,
                        mSQLCursor,
                        new String[]{DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, DBContract.DemoTable.COLUMN_NAME_DEMO_INT},
                        new int[]{R.id.sql_listview_string, R.id.sql_listview_int},
                        0);
                SQLListView.setAdapter(mSQLCursorAdapter);
            } catch(Exception e){
                Log.d(TAG, "Error loading data from database");
            }

        }
    }

    class SQLiteExample extends SQLiteOpenHelper {

        public SQLiteExample(Context context) {
            super(context, DBContract.DemoTable.DB_NAME, null, DBContract.DemoTable.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBContract.DemoTable.SQL_CREATE_DEMO_TABLE);

            ContentValues testValues = new ContentValues();
            testValues.put(DBContract.DemoTable.COLUMN_NAME_DEMO_INT, 42);
            testValues.put(DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, "Hello SQLite");
            db.insert(DBContract.DemoTable.TABLE_NAME,null,testValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DBContract.DemoTable.SQL_DROP_DEMO_TABLE);
            onCreate(db);
        }
    }
}