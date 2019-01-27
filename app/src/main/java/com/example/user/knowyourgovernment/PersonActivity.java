package com.example.user.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PersonActivity extends AppCompatActivity {

    private static final String TAG = "PersonActivity";
    GovtOfficial officialData = null;

    TextView tvLoc;
    ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        tvLoc = (TextView) findViewById(R.id.tv2_location);
        ivPicture = (ImageView) findViewById(R.id.iv2_person);

        TextView tvName = (TextView) findViewById(R.id.tv2_name);
        TextView tvOffice = (TextView) findViewById(R.id.tv2_office);
        TextView tvParty = (TextView) findViewById(R.id.tv2_party);

        TextView tvAddr = (TextView) findViewById(R.id.tv2_addr);
        TextView tvPhone = (TextView) findViewById(R.id.tv2_phone);
        TextView tvEmail = (TextView) findViewById(R.id.tv2_email);
        TextView tvWebsite = (TextView) findViewById(R.id.tv2_website);

        ScrollView sv = (ScrollView) findViewById(R.id.scrollView_person);
        ImageView ivYoutube = (ImageView) findViewById(R.id.iv2_youtube);
        ImageView ivGooglePlus = (ImageView) findViewById(R.id.iv2_google);
        ImageView ivTwitter = (ImageView) findViewById(R.id.iv2_twitter);
        ImageView ivFb = (ImageView) findViewById(R.id.iv2_fb);


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
                tvParty.setText("(" + officialData.getParty() + ")");

                if(officialData.getAddress().equals(""))
                    tvAddr.setText(getString(R.string.no_data_provided));
                else
                    tvAddr.setText(officialData.getAddress());

                tvPhone.setText(officialData.getPhone_no());
                tvEmail.setText(officialData.getEmail_id());
                tvWebsite.setText(officialData.getWebsite_url());

                if (officialData.getParty().equalsIgnoreCase("Republican"))
                    sv.setBackgroundColor(Color.RED);
                else if (officialData.getParty().equalsIgnoreCase("Democratic") || officialData.getParty().equalsIgnoreCase("Democrat")
                        || officialData.getParty().equalsIgnoreCase("Democratic Party"))
                    sv.setBackgroundColor(Color.BLUE);
                else {
                    sv.setBackgroundColor(Color.BLACK);
                    tvParty.setVisibility(View.INVISIBLE);
                }

                if (officialData.getChannel_youtube_id().equals(""))
                    ivYoutube.setVisibility(View.INVISIBLE);
                if (officialData.getChannel_googlePlus_id().equals(""))
                    ivGooglePlus.setVisibility(View.INVISIBLE);
                if (officialData.getChannel_twitter_id().equals(""))
                    ivTwitter.setVisibility(View.INVISIBLE);
                if (officialData.getChannel_facebook_id().equals(""))
                    ivFb.setVisibility(View.INVISIBLE);

                if (!officialData.getAddress().equals("")) {
                    Linkify.addLinks(tvAddr, Linkify.MAP_ADDRESSES);

                    String addText = officialData.getAddress();
                    addText = addText.replace(" ","+");
                    String test = "<a href=\"http://maps.google.com/maps?q="+ addText +"\">"+ officialData.getAddress() +"</a>";
                    tvAddr.setText(Html.fromHtml(""+test));
                    tvAddr.setMovementMethod(LinkMovementMethod.getInstance());
                }
                if (!officialData.getPhone_no().equals(getString(R.string.no_data_provided)))
                    Linkify.addLinks(tvPhone, Linkify.PHONE_NUMBERS);
                if (!officialData.getEmail_id().equals(getString(R.string.no_data_provided)))
                    Linkify.addLinks(tvEmail, Linkify.EMAIL_ADDRESSES);
                if (!officialData.getWebsite_url().equals(getString(R.string.no_data_provided)))
                    Linkify.addLinks(tvWebsite, Linkify.WEB_URLS);

                addProfileImage();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void openPhotoActivity(View v)
    {
        if(officialData != null && !officialData.getPhotoUrl().equals(""))
        {
            Intent it = new Intent(this, PhotoActivity.class);
            it.putExtra("title", tvLoc.getText());
            it.putExtra("officialObj", officialData);
            startActivity(it);
        }
    }

    public void googlePlusClicked(View v)
    {
        if(officialData == null || officialData.getChannel_googlePlus_id().equals(""))
            return;

        String name = officialData.getChannel_googlePlus_id();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v)
    {
        if(officialData == null || officialData.getChannel_youtube_id().equals(""))
            return;

        String name = officialData.getChannel_youtube_id();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void twitterClicked(View v)
    {
        if(officialData == null || officialData.getChannel_twitter_id().equals(""))
            return;

        Intent intent = null;
        String name = officialData.getChannel_twitter_id();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v)
    {
        if(officialData == null || officialData.getChannel_facebook_id().equals(""))
            return;

        String FACEBOOK_URL = "https://www.facebook.com/" + officialData.getChannel_facebook_id();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + officialData.getChannel_facebook_id();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

}
