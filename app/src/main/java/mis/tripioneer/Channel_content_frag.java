package mis.tripioneer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/7/20.
 */
public class Channel_content_frag extends Fragment implements AdapterView.OnItemClickListener
{
    private static int RET_PARAM_NUM;
    private static int TYPE;
    private static final int PLACE = 0;
    private static final int TRIP = 1;
    private final static int DOWNLOAD_COMPLETE = 1;
    private final String CASE = "CHANNEL_CONTENT";
    private static final String TAG = "Channel_content";
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ShortIntro = new ArrayList<String>();
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static GeneralViewAdapter_tmp adapter;//TODO:CHANGE TO NEW LAYOUT
    private String id;
    public Context context;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Log.d(TAG, "id =" + id);
        id = this.getArguments().getString("channelid");
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        Log.d(TAG,"id ="+id);
        View  v = inflater.inflate(R.layout.fragment_channel_content, container, false);
        listView =(ListView)v.findViewById(R.id.list);
        viewModels = new ArrayList<ViewModel>();
        adapter = new GeneralViewAdapter_tmp(getActivity(), viewModels);

        listView.setOnItemClickListener(this);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
        new Thread(run_Channel_content).start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        Log.d(TAG, "id =" + id);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop");
        Log.d(TAG,"id ="+id);
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
            request_name[0] ="ID";
            request_value[0] = id;
            TYPE = PLACE;
            ConnectServer connection = new ConnectServer(URL_Channel_Content);
            Log.d(TAG,"id run="+id);
            ret = connection.connect(request_name, request_value, 1);

            JsonParser parser = new JsonParser(CASE);

            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_ShortIntro = parser.Parse(ret,"place_ShortIntro");
            ret_place_ID = parser.Parse(ret,"place_ID");

            RET_PARAM_NUM = ret_place_Name.size();

            for(int i=0; i<RET_PARAM_NUM;i++)
            {
                Log.d(TAG,ret_place_Name.get(i)+"\n");
                Log.d(TAG, ret_place_Pic.get(i) + "\n");
                Log.d(TAG, ret_place_ShortIntro.get(i) + "\n");
                Log.d(TAG, ret_place_ID.get(i) + "\n");
            }

            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };

    static Handler handler = new Handler()
    {
        private int TYPE;
        private final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
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
                    Log.d(TAG,"Download Failed");
                    break;
            }
        }
    };


    public void onItemClick(AdapterView<?> arg0, View arg1, int row, long id)
    {
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ViewModel item = (ViewModel) listView.getItemAtPosition(row);
        Log.d(TAG, "item = " + item.getTitle() + "type=" + item.getType() + "id=" + item.getID());
        //Toast.makeText(getActivity(), "You clicked on position : " + row + " and id : " + id, Toast.LENGTH_SHORT).show();

        TYPE = item.getType();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (TYPE)
        {
            case PLACE:
                intent.setClass(context, place.class);
                bundle.putString("specifyid", item.getID());
                break;
            case TRIP:
                intent.setClass(context, Trip.class);
                bundle.putString("tripid", item.getID());
                break;
            default:
                break;
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
