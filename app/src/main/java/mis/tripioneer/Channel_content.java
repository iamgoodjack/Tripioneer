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
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/7/20.
 */
public class Channel_content extends Fragment
{
    private static int RET_PARAM_NUM;
    private final static int DOWNLOAD_COMPLETE = 1;
    private final String CASE = "CHANNEL_CONTENT";
    private final String TAG = "Channel_content";
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ShortIntro = new ArrayList<String>();
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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


        new Thread(run_Channel_content).start();

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
    Runnable run_Channel_content = new Runnable()
    {
        private final String URL_Channel_Content = "http://140.115.80.224:8080/Channel_content.php";
        private static final int CHANNEL_CONTENT_NUM_PARAM = 1;
        private String[] request_name = new String[CHANNEL_CONTENT_NUM_PARAM];
        private String[] request_value = new String[CHANNEL_CONTENT_NUM_PARAM];
        private String ret = "";

        @Override
        public void run()
        {
            request_name[0] ="id";
            request_value[0] = "1";
            ConnectServer connection = new ConnectServer(URL_Channel_Content);

            ret = connection.connect(request_name, request_value, CHANNEL_CONTENT_NUM_PARAM);

            JsonParser parser = new JsonParser(CASE);

            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_ShortIntro = parser.Parse(ret,"place_ShortIntro");
            ret_place_ID = parser.Parse(ret,"place_ID");

            RET_PARAM_NUM = ret_place_Name.size();

            for(int i=0; i<RET_PARAM_NUM;i++)
            {
                Log.d("Gina",ret_place_Name.get(i)+"\n");
                Log.d("Gina", ret_place_Pic.get(i) + "\n");
                Log.d("Gina", ret_place_ShortIntro.get(i) + "\n");
                Log.d("Gina", ret_place_ID.get(i) + "\n");
            }

            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };

    static Handler handler = new Handler()
    {
        private int TYPE;
        private final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/image/";
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:

                    try
                    {
                        for(int i=0; i<RET_PARAM_NUM;i++)
                        {
                            if(!"".equals(ret_place_Pic.get(i)) )
                            {
                                ViewModel row = new ViewModel
                                        (
                                                ret_place_Name.get(i),
                                                URL_PREFIX_IMAGE + URLEncoder.encode(ret_place_Pic.get(i), "UTF-8") + ".jpg",
                                                ret_place_ShortIntro.get(i)
                                        );
                                row.setType(TYPE);
                                row.setID(ret_place_ID.get(i));
                                viewModels.add(row);
                            }
                        }
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    listView.setAdapter(adapter);
                    break;

                default:
                    Log.d("Gina","Download Failed");
                    break;
            }
        }
    };
}
