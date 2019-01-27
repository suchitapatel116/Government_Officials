package com.example.user.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    GovtOfficial officialData = null;
    TextView tvLoc;
    ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Log.d(TAG, "onCreate: ");

        tvLoc = (TextView) findViewById(R.id.tv3_location);
        ivPicture = (ImageView) findViewById(R.id.iv3_person);
        TextView tvOffice = (TextView) findViewById(R.id.tv3_office);
        TextView tvName = (TextView) findViewById(R.id.tv3_name);

        Intent it = getIntent();
        if(it.hasExtra("title"))
            tvLoc.setText(it.getStringExtra("title"));
        if(it.hasExtra("officialObj"))
        {
            officialData = (GovtOfficial) it.getSerializableExtra("officialObj");
            if(officialData != null)
            {
                tvOffice.setText(officialData.getOffice());
                tvName.setText(officialData.getOfficial_name());

                if (officialData.getParty().equalsIgnoreCase("Republican"))
                    findViewById(R.id.constraintLayout3).setBackgroundColor(Color.RED);
                else if (officialData.getParty().equalsIgnoreCase("Democratic") || officialData.getParty().equalsIgnoreCase("Democrat"))
                    findViewById(R.id.constraintLayout3).setBackgroundColor(Color.BLUE);
                else
                    findViewById(R.id.constraintLayout3).setBackgroundColor(Color.BLACK);

                addProfileImage();
            }
        }
    }

    private void addProfileImage()
    {
        if(officialData != null)
        {
            if (!officialData.getPhotoUrl().equals(""))
            {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the http image attempt failed
                        final String changedUrl = officialData.getPhotoUrl().replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(ivPicture);
                    }
                }).build();
                picasso.load(officialData.getPhotoUrl())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(ivPicture);
            }
            else
            {
                Picasso.with(this).load(R.drawable.missingimage)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(ivPicture);
            }
        }
    }

}
