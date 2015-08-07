package mis.tripioneer;



import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.util.Log;


/**
 * Created by user on 2015/7/20.
 */
public class ChannelMain extends AppCompatActivity
{
    FragmentTabHost TabHost;
    public String id;
    private final String TAG ="ChannelMain";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_main);
        Log.d(TAG,"onCreate");

        Bundle data = this.getIntent().getExtras();
        id = data.getString("channelid");

        TabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        TabHost.setup(this, getSupportFragmentManager(), R.id.container);

        TabHost.addTab(TabHost.newTabSpec("content").setIndicator("內容"),
                Channel_content.class, null);
        TabHost.addTab(TabHost.newTabSpec("info").setIndicator("簡介"),
                Channel_info.class, null);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_recommendation, menu);
        //return true;
        Log.d(TAG,"menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart");

    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}

