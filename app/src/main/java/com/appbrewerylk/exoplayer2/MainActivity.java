package com.appbrewerylk.exoplayer2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer simpleExoPlayer;

    IcyStreamMeta streamMeta;
    MetadataTask2 metadataTask2;
    String title_artist;
    TextView textView,textViewArtist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.main_activity_text_view);
        textViewArtist = findViewById(R.id.main_activity_text_view_artist);
        exoPlayerView = findViewById(R.id.exoplayersimpleview);



//        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            //we are connected to a network


            String streamUrl = "http://92.27.238.127:8000/stream.mp3";



            ////////////////
            ////Playing Song///

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);

            Uri radiouri = Uri.parse(streamUrl);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(radiouri,dataSourceFactory,extractorsFactory,null,null);
            exoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);

            ////////

            //////////////
            streamMeta = new IcyStreamMeta();
            try {
                streamMeta.setStreamUrl(new URL(streamUrl));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            metadataTask2 =new MetadataTask2();
            try {
                metadataTask2.execute(new URL(streamUrl));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            Timer timer = new Timer();
            MyTimerTask task = new MyTimerTask();
            timer.schedule(task,0, 10000);
            //////////





        }else{

            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();

        }

//        }
//        else
//        {
//
//            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    protected class MetadataTask2 extends AsyncTask<URL, Void, IcyStreamMeta>
    {
        @Override
        protected IcyStreamMeta doInBackground(URL... urls)
        {
            try
            {
                streamMeta.refreshMeta();
                Log.e("Retrieving MetaData","Refreshed Metadata");
            }
            catch (IOException e)
            {
                Log.e(MetadataTask2.class.toString(), e.getMessage());
            }
            return streamMeta;
        }

        @Override
        protected void onPostExecute(IcyStreamMeta result)
        {
            try
            {
              //  title_artist=streamMeta.getStreamTitle();

                title_artist=streamMeta.getArtist();


                Log.e("Retrieved title_artist", title_artist);
                if(title_artist.length()>0)
                {
                    String title_=streamMeta.getTitle();
                    textView.setText(title_);
                    textViewArtist.setText(title_artist.substring(6));
                }
                else {
                    textView.setText("Instrumental Music...");
                    textViewArtist.setText("Unknown..");
                }
            }
            catch (IOException e)
            {
                Log.e(MetadataTask2.class.toString(), e.getMessage());
            }
        }
    }

    class MyTimerTask extends TimerTask {

        public void run() {
            try {
                streamMeta.refreshMeta();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    String title_artist = null;

                    try {


                        //title_artist = streamMeta.getStreamTitle();
                        title_artist = streamMeta.getArtist();
                        String title_=streamMeta.getTitle();

                        textViewArtist.setText(title_artist.substring(6));
                        textView.setText(title_);

                        Log.i("ARTIST TITLE", title_artist);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
            });


        }
    }


}
