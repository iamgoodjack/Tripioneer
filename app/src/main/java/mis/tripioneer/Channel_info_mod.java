package mis.tripioneer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.LogRecord;

/**
 * Created by user on 2015/7/20.
 */
public class Channel_info_mod extends Fragment
{
    private final String TAG = "Channel_info";
    private String ID;
    final int DOWNLOAD_COMPLETE=1;
    private static ArrayList<String> ret_Channel_Name = new ArrayList<String>();
    private static ArrayList<String> ret_Channel_Intro = new ArrayList<String>();
    public Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Log.d(TAG,"id ="+ID);
        ID = this.getArguments().getString("channelid");
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        Log.d(TAG,"id ="+ID);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        Log.d(TAG,"id ="+ID);
        return inflater.inflate(R.layout.channel_info, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        Log.d(TAG,"id ="+ID);
    }

    private Runnable run_Channel_info = new Runnable()
    {
        final String CHANNEL_INTRO_URLL = "http://140.115.80.224:8080/group4/channel_intro.php";
        final int CHANNEL_INTRO = 1;
        final String CASE = "CHANNEL_INFO";
        String[] request_name = new String[CHANNEL_INTRO];
        String[] request_value = new String[CHANNEL_INTRO];

        String ret;
        @Override
        public void run() {
            request_name[0] = "ID";
            request_value[0] = ID;
            Log.d(TAG,"id run="+ID);
            ConnectServer connection = new ConnectServer(CHANNEL_INTRO_URLL);
            ret = connection.connect(request_name, request_value, 1);

            JsonParser parser = new JsonParser(CASE);
            ret_Channel_Name  = parser.Parse(ret, "channel_Name");
            ret_Channel_Intro =  parser.Parse(ret,"channel_Intro");
            Log.d(TAG,ret_Channel_Intro.get(0));
            Log.d(TAG,ret_Channel_Name.get(0));
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
        }

    };

    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:
                    TextView mText = (TextView) getView().findViewById(R.id.text);
                    mText.setText(ret_Channel_Intro.get(0));
                    Log.d(TAG, mText.getText().toString());
                    break;
                default:
                    Log.d(TAG, "Download Failed");
            }
        }

    };


    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart");
        Log.d(TAG,"id ="+ID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        new Thread(run_Channel_info).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        Log.d(TAG,"id ="+ID);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        Log.d(TAG,"id ="+ID);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        Log.d(TAG,"id ="+ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        Log.d(TAG,"id ="+ID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        Log.d(TAG,"id ="+ID);
    }

}


