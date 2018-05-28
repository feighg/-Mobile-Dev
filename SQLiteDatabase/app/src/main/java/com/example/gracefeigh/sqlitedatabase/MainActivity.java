package com.example.gracefeigh.sqlitedatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView sqlLiteAct = (TextView) findViewById(R.id.Layout1);

        sqlLiteAct.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SQLLiteActivity.class);
                startActivity(intent);
            }
        });

        TextView sqlLocation = (TextView) findViewById(R.id.Layout2);
        sqlLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SqlLocation.class);
                startActivity(intent);
            }
        });
    }
}
