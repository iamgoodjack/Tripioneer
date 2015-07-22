package mis.tripioneer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;


/**
 * Created by user on 2015/7/20.
 */
public class ChannelMain extends FragmentActivity
{
    FragmentTabHost TabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_main);

        TabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        TabHost.setup(this,  getSupportFragmentManager(), R.id.container);

        TabHost.addTab(TabHost.newTabSpec("content").setIndicator("content"),
                Channel_content.class, null);
        TabHost.addTab(TabHost.newTabSpec("info").setIndicator("info"),
                Channel_info.class, null);
        TabHost.addTab(TabHost.newTabSpec("category").setIndicator("category"),
                Channel_category.class, null);


    }

}
