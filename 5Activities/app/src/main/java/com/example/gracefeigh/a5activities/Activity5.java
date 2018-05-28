package com.example.gracefeigh.a5activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;

public class Activity5 extends Activity {

    Button button;
    ImageView image;
    Integer [] images = {R.mipmap.mycroft1_round, R.mipmap.mycroft2, R.mipmap.mycroft2_round, R.mipmap.mycroft3, R.mipmap.mycroft3_round};
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5);

        addListenerOnButton();

    }

    public void addListenerOnButton() {

        image = (ImageView) findViewById(R.id.imageView1);

        button = (Button) findViewById(R.id.btnChangeImage);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                    image.setImageResource(images[i]);
                    if (i < 4) {
                        i++;
                    }
            }

        });

    }

}
