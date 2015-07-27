package mis.tripioneer;

import android.app.Activity;
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
public class Channel_info extends Fragment
{
    private final String TAG = "Channel_info";
    private String ID;
    final int DOWNLOAD_COMPLETE=1;
    private static ArrayList<String> ret_Channel_Name = new ArrayList<String>();
    private static ArrayList<String> ret_Channel_Intro = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        /* Bundle b = this.getIntent().getExtras();
                int id = b.getInt("id");   //接收id參數
                String id = Integer.toString(id);*/
        this.ID = Integer.toString(1);

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
        return inflater.inflate(R.layout.channel_info, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        new Thread(run_Channel_info).start();
        Log.d(TAG, "onActivityCreated");

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
            request_name[0] = "channel_id";
            request_value[0] = ID;
            ConnectServer connection = new ConnectServer(CHANNEL_INTRO_URLL);
            ret = connection.connect(request_name, request_value, 1);
            Log.d("www","iiiii");
            JsonParser parser = new JsonParser(CASE);
            ret_Channel_Name  = parser.Parse(ret, "channel_Name");
            ret_Channel_Intro =  parser.Parse(ret,"channel_Intro");
            Log.d("Jenny",ret_Channel_Intro.get(0));
            Log.d("haha",ret_Channel_Name.get(0));
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
                    Log.d(TAG, "haha");
            }
        }

    };


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

}


