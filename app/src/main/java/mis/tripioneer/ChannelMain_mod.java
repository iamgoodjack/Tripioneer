package mis.tripioneer;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by user on 2015/7/20.
 */
public class ChannelMain_mod extends Fragment
{
    FragmentTabHost TabHost;
    public String id;
    private final String TAG = "ChannelMain_mod";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        id = this.getArguments().getString("channelid");
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        View  v = inflater.inflate(R.layout.channel_main, container, false);

        TabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);
        TabHost.setup(this.getActivity(), getChildFragmentManager(), R.id.container);
        Bundle bundle = new Bundle();
        bundle.putString("channelid", "1");//To do : get advisor's channel id
        TabHost.addTab(TabHost.newTabSpec("content").setIndicator("內容"),
                Channel_content_mod.class, bundle);
        TabHost.addTab(TabHost.newTabSpec("info").setIndicator("簡介"),
                Channel_info_mod.class, bundle);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

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
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }
}

