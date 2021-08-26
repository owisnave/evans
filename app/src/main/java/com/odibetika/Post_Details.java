package com.odibetika;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

import static android.content.ContentValues.TAG;
import static com.mopub.common.Constants.TEN_SECONDS_MILLIS;

//import com.google.android.gms.ads.AdView;

public class Post_Details extends AppCompatActivity {


    DatabaseReference mRef;
    String postKey;
    TextView tvTitle, tvBody, tvTime;
    private MoPubView moPubView;
    private MoPubInterstitial moPubInterstitial;
    ImageView imgBody;
    ProgressDialog pd;
    String selection;
    AutoLinkTextView autoLinkTextView, autoLinkTextView2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setContentView(R.layout.activity_post_detailed);


        postKey = getIntent().getExtras().getString("postKey");
        selection=getIntent().getExtras().getString("selection");
        tvBody =  findViewById(R.id.tvBody);
        tvTitle =  findViewById(R.id.tvTitle);
        tvTime =  findViewById(R.id.post_time);
        imgBody =  findViewById(R.id.imgBody);
        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        moPubInterstitial = new MoPubInterstitial(this, getString(R.string.Mopub_Int));
        // Remember that "this" refers to your current activity.
        //moPubInterstitial.setInterstitialAdListener(this);
        moPubInterstitial.load();
       showInterstitial();

        autoLinkTextView = findViewById(R.id.autoLinkrate);
        autoLinkTextView.addAutoLinkMode(AutoLinkMode.MODE_CUSTOM);
        autoLinkTextView.setCustomRegex("\\sHere\\b");
        autoLinkTextView.setAutoLinkText("Rate and update the app Here");

        autoLinkTextView2 = findViewById(R.id.autoLinkrate2);
        autoLinkTextView2.addAutoLinkMode(AutoLinkMode.MODE_CUSTOM);
        autoLinkTextView2.setCustomRegex("\\sTelegram\\b");
        autoLinkTextView2.setAutoLinkText("Join our Telegram channel for more games");

        autoLinkTextView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_CUSTOM)
                try {
                    Intent RateIntent =
                            new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.snave.besttips"));
                    startActivity(RateIntent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Unable to connect try again later...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

        });
        autoLinkTextView2.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_CUSTOM)
                try {
                    Intent RateIntent =
                            new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/betikabets2021"));
                    startActivity(RateIntent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Unable to connect try again later...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

        });


        if (postKey != null) {

            mRef = FirebaseDatabase.getInstance().getReference().child("best").child(selection).child(postKey);
        }
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue().toString();
                String body = dataSnapshot.child("body").getValue().toString();
                Long time = (Long) dataSnapshot.child("time").getValue();
                if (title != null) {
                    tvTitle.setText(title.toUpperCase());
                    pd.dismiss();
                } else {
                    Toast.makeText(Post_Details.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
                if (body != null) {
                    tvBody.setText(body);

                }
                if (time != null) {
                    setTime(time);
                }
                if (dataSnapshot.hasChild("image")){
                    String image= (String) dataSnapshot.child("image").getValue();

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showMopBanner();

    }

    public void showMopBanner(){
        moPubView = findViewById(R.id.adview);
        moPubView.setAdUnitId(getString(R.string.Mopub_Banner)); // Enter your Ad Unit ID from www.mopub.com
        moPubView.loadAd();
    }



    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            /*mInterstitialAd = createNewIntAd();
            loadIntAdd();*/
            //showInterstitials();
            finish();
        } else if (id == R.id.about){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Best Predictions");
            try {
                alert.setMessage(
                        "Version " + getApplication().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode +
                                "\n" + Post_Details.this.getString(R.string.app_name) + "\n" +
                                "All rights reserved \n"
                );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            alert.show();

        } else if (id == R.id.feedback) {
            startActivity(new Intent(Post_Details.this, Feedback.class));
        } else if (id == R.id.rate) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(Post_Details.this, "Unable to find play store", Toast.LENGTH_SHORT).show();
            }

        }else if (id == R.id.ppolicy) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Privacy Policy");
            try {
                alert.setMessage(
                        "Best Pred Developers built the Best Predictions app as a free app. This SERVICE is provided by WinnerTips Developers at no cost and is intended for use as is.\n" +
                                "\n" +

                                "This page is used to inform website visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.\n" +
                                "If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                                "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at Win bet unless otherwise defined in this Privacy Policy.\n" +
                                "\n" +
                                "Information Collection and Use\n" +
                                "For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information, including but not limited to a username. The information that we request is retained on your device and is not collected by us in any way.\n" +
                                "The app does use third-party services that may collect information used to identify you. Google play services are such.\n" +
                                "\n" +
                                "Log Data\n" +
                                "We want to inform you that whenever you use our Service, in case of an error in the app we collect data and information (through third-party products) on your phone called Log Data. This Log Data may include information such as your devices' Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.\n" +
                                "\n" +
                                "Cookies\n" +
                                "Cookies are files with small amount of data that is commonly used as an anonymous unique identifier. These are sent to your browser from the website that you visit and are stored on your devices' internal memory.\n" +
                                "\n" +
                                "Service Providers\n" +
                                "We may employ third-party companies and individuals due to the following reasons:\n" +
                                "· To facilitate our Service;\n" +
                                "· To provide the Service on our behalf;\n" +
                                "· To perform Service-related services; or\n" +
                                "· To assist us in analyzing how our Service is used.\n" +
                                "We want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                                "\n" +
                                "Security\n" +
                                "We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and We cannot guarantee its absolute security.\n" +
                                "\n" +
                                "Links to Other Sites\n" +
                                "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                                "\n" +
                                "Children’s Privacy\n" +
                                "These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case We discover that a child under 13 has provided us with personal information, We immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that We will be able to do necessary actions.\n" +
                                "\n" +
                                "Changes to This Privacy Policy\n" +
                                "We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page. These changes are effective immediately after they are posted on this page.\n" +
                                "\n" +
                                "Contact Us\n" +
                                "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at victorpredictz@gmail.com" + "\n" + getApplication().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode +
                                "\n"
                );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            alert.show();

        } else if (id == R.id.menu_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Start winning BIG with the best football tips app on play store . Download here market://details?id=com.snave.besttipss";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Best Football Predictions App on Play Store");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(sharingIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTime(Long time) {
        TextView txtTime = findViewById(R.id.post_time);
        //long elapsedDays=0,elapsedWeeks = 0, elapsedHours=0,elapsedMin=0;
        long elapsedTime;
        long currentTime = System.currentTimeMillis();
        int elapsed = (int) ((currentTime - time) / 1000);
        if (elapsed < 60) {
            if (elapsed < 2) {
                txtTime.setText("Just Now");
            } else {
                txtTime.setText(elapsed + " sec ago");
            }
        } else if (elapsed > 604799) {
            elapsedTime = elapsed / 604800;
            if (elapsedTime == 1) {
                txtTime.setText(elapsedTime + " week ago");
            } else {

                txtTime.setText(elapsedTime + " weeks ago");
            }
        } else if (elapsed > 86399) {
            elapsedTime = elapsed / 86400;
            if (elapsedTime == 1) {
                txtTime.setText(elapsedTime + " day ago");
            } else {
                txtTime.setText(elapsedTime + " days ago");
            }
        } else if (elapsed > 3599) {
            elapsedTime = elapsed / 3600;
            if (elapsedTime == 1) {
                txtTime.setText(elapsedTime + " hour ago");
            } else {
                txtTime.setText(elapsedTime + " hours ago");
            }
        } else if (elapsed > 59) {
            elapsedTime = elapsed / 60;
            txtTime.setText(elapsedTime + " min ago");

        }

    }

    void showInterstitial() {
        moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
                if(moPubInterstitial.isReady()){
                    moPubInterstitial.show();
                } else {
                    Log.i("MOPUB","ad is not ready");

                }

            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {

            }

            @Override
            public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {

            }
        });
    }
}