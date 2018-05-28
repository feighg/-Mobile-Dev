package com.example.gracefeigh.sqlitedatabase;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SqlLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    SqlLocation.SQLiteExample mSQLiteExample;
    Button mSQLSubmitButton;
    Cursor mSQLCursor;
    SimpleCursorAdapter mSQLCursorAdapter;
    private static final String TAG = "SqlLocation";
    SQLiteDatabase mSQLDB;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLatText;
    private String mLonText;
    private Location mLastLocation;
    private LocationListener mLocationListener;
    private static final int LOCATION_REQUEST_RESULT = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_location);

        if (mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        //mLatText = (TextView) findViewById(R.id.sql_lat);
        //mLonText = (TextView) findViewById(R.id.sql_lon);
        //mLatText.setText("Activity Created");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);

        mLocationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location){
                if (location != null){
                    mLonText = String.valueOf(location.getLongitude());
                    mLatText = String.valueOf(location.getLatitude());
                }
                else{
                    mLonText = "No Location Available";
                }
            }
        };

        mSQLiteExample = new SqlLocation.SQLiteExample(this);
        mSQLDB = mSQLiteExample.getWritableDatabase();

        mSQLSubmitButton = (Button) findViewById(R.id.string_button);
        mSQLSubmitButton.setOnClickListener((v) -> {

            updateLocation();

            if (mSQLDB != null) {
                ContentValues vals = new ContentValues();
                vals.put(SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, ((EditText) findViewById(R.id.sqloc_string_input)).getText().toString());
                vals.put(DBContract.DemoTable.COLUMN_NAME_DEMO_LON, mLonText);
                vals.put(DBContract.DemoTable.COLUMN_NAME_DEMO_LAT, mLatText);
                Log.d(TAG,mLonText);
                Log.d(TAG,mLatText);
                mSQLDB.insert(SqlLocation.DBContract.DemoTable.TABLE_NAME, null, vals);
                populateTable();
            }else{
                Log.d(TAG, "Unable to access database");
            }
        });
    }

    final class DBContract {
        private DBContract(){};

        public final class DemoTable implements BaseColumns {
            public static final String DB_NAME = "table_db";
            public static final String TABLE_NAME = "tb";
            public static final String COLUMN_NAME_DEMO_STRING = "tb_string";
            public static final String COLUMN_NAME_DEMO_LON = "tb_string1";
            public static final String COLUMN_NAME_DEMO_LAT = "tb_string2";
            public static final int DB_VERSION = 4;


            public static final String SQL_CREATE_DEMO_TABLE = "CREATE TABLE " +
                    SqlLocation.DBContract.DemoTable.TABLE_NAME + "(" + SqlLocation.DBContract.DemoTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_STRING + " VARCHAR(255)," +
                    DemoTable.COLUMN_NAME_DEMO_LAT + " VARCHAR(255)," +
                    DemoTable.COLUMN_NAME_DEMO_LON + " VARCHAR(255));";

            public  static final String SQL_DROP_DEMO_TABLE = "DROP TABLE IF EXISTS " + SqlLocation.DBContract.DemoTable.TABLE_NAME;
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
                mSQLCursor = mSQLDB.query(SqlLocation.DBContract.DemoTable.TABLE_NAME,
                        new String[]{SqlLocation.DBContract.DemoTable._ID, SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, DBContract.DemoTable.COLUMN_NAME_DEMO_LON, DBContract.DemoTable.COLUMN_NAME_DEMO_LAT},
                        SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_LAT + " > ?",
                        new String[]{"-1000"},
                        null,
                        null,
                        null);

                ListView SQLListView = (ListView) findViewById(R.id.sqloc_listview);
                mSQLCursorAdapter = new SimpleCursorAdapter(this,
                        R.layout.sqlloc_item,
                        mSQLCursor,
                        new String[]{SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, DBContract.DemoTable.COLUMN_NAME_DEMO_LAT, DBContract.DemoTable.COLUMN_NAME_DEMO_LON},
                        new int[]{R.id.sqlloc_string, R.id.sql_lat, R.id.sql_lon},
                        0);
                SQLListView.setAdapter(mSQLCursorAdapter);
            } catch(Exception e){
                Log.d(TAG, "Error loading data from database");
            }

        }
    }

    class SQLiteExample extends SQLiteOpenHelper {

        public SQLiteExample(Context context) {
            super(context, SqlLocation.DBContract.DemoTable.DB_NAME, null, SqlLocation.DBContract.DemoTable.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SqlLocation.DBContract.DemoTable.SQL_CREATE_DEMO_TABLE);
/*
            ContentValues testValues = new ContentValues();
            testValues.put(SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_LON, "-123.2");
            testValues.put(DBContract.DemoTable.COLUMN_NAME_DEMO_LAT, "44.5");
            testValues.put(SqlLocation.DBContract.DemoTable.COLUMN_NAME_DEMO_STRING, "Hello SQLite");
            db.insert(SqlLocation.DBContract.DemoTable.TABLE_NAME,null,testValues);*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SqlLocation.DBContract.DemoTable.SQL_DROP_DEMO_TABLE);
            onCreate(db);
        }
    }

    private void updateLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            mLonText = String.valueOf(mLastLocation.getLongitude());
            mLatText = String.valueOf(mLastLocation.getLatitude());
        }
        else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    mLonText = "-123.2";
                    mLatText = "44.5";
                    updateLocation();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle){
        mLatText = "on Connect";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String [] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_RESULT);
            mLonText = "Lacking Permissions";
            return;
        }
        updateLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        Dialog errDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0);
        errDialog.show();
        return;
    }

    @Override
    public void onConnectionSuspended(int j){

    }
}
