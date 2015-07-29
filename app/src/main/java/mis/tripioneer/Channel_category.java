package mis.tripioneer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by user on 2015/7/20.
 */
public class Channel_category extends Fragment
{
    private final String TAG = "Channel_category";
    private static int RET_PARAM_NUM;
    private String ID;

    private final static int DOWNLOAD_COMPLETE = 1;
    private final String CASE = "CHANNEL_CATEGORY";
    private static ArrayList<String> ret_channel_category = new ArrayList<String>();

    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
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
        View  v = inflater.inflate(R.layout.fragment_channel_content, container, false);
        listView =(ListView)v.findViewById(R.id.list);
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(getActivity(), viewModels);

        return v;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        new Thread(run_Channel_category).start();
    }

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

   Runnable run_Channel_category = new Runnable()
   {
       private final String URL_Channel_Category = "http://140.115.80.224:8080/channel_category.php";
       private static final int CHANNEL_CATEGORY_NUM_PARAM = 1;
       private String[] request_name = new String[CHANNEL_CATEGORY_NUM_PARAM];
       private String[] request_value = new String[CHANNEL_CATEGORY_NUM_PARAM];
       private String ret = "";
       @Override
       public void run() {
           request_name[0] = "channel_id";
           request_value[0] = ID;
           ConnectServer connection = new ConnectServer(URL_Channel_Category);

           ret = connection.connect(request_name, request_value, CHANNEL_CATEGORY_NUM_PARAM);

           JsonParser parser = new JsonParser(CASE);
           ret_channel_category = parser.Parse(ret,"");


           RET_PARAM_NUM = ret_channel_category.size();

           for(int i=0; i<RET_PARAM_NUM;i++)
           {
               Log.d("test",ret_channel_category.get(i)+"\n");

           }

          // handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
       }


   };
}
