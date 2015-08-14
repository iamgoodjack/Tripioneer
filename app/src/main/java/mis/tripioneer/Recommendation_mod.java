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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;




public class Recommendation_mod extends Fragment implements OnItemClickListener
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private static final String TAG = "RECOMMENDATION_mod";
    private static final String CASE = "RECOMMENDATION";
    private static final int PLACE = 0;
    private static final int TRIP = 1;
    private static final int CHANNEL = 2;
    private static int TYPE;
    private static int RET_PARAM_NUM;
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ShortIntro = new ArrayList<String>();
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;
    public Context context;

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
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        View  v = inflater.inflate(R.layout.activity_recommendation, container, false);
        TYPE = PLACE;
        new Thread(run_Place).start();
        listView =(ListView)v.findViewById(R.id.list);
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(getActivity(), viewModels);

        listView.setOnItemClickListener(this);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onStart() {
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




    static Handler handler = new Handler()
    {
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
                            if( ( TYPE == PLACE && !"".equals( ret_place_Pic.get(i) ) ) | (TYPE != PLACE)  )
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
                        switch (TYPE)
                        {
                            case PLACE:
                                new Thread(run_Trip).start();
                                break;
                            case TRIP:
                                new Thread(run_Channel).start();
                                break;
                            default:
                                break;
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

    Runnable run_Place = new Runnable()
    {
        @Override
        public void run()
        {
            final String URL_RECOMMENDATION_Place = "http://140.115.80.224:8080/Recommendation_Place.php";
            final int RECOMMENDATION_NUM_PARAM = 1;
            String[] request_name = new String[RECOMMENDATION_NUM_PARAM];
            String[] request_value = new String[RECOMMENDATION_NUM_PARAM];
            String ret;

            TYPE = PLACE;

            ConnectServer connection = new ConnectServer(URL_RECOMMENDATION_Place);
            ret = connection.connect(request_name, request_value, 0);
            Log.d(TAG,"in place");
            JsonParser parser = new JsonParser(CASE);
            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_ShortIntro = parser.Parse(ret,"place_ShortIntro");
            ret_place_ID = parser.Parse(ret, "place_ID");

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

    static Runnable run_Trip = new Runnable()
    {

        @Override
        public void run()
        {
            final String URL_RECOMMENDATION_Trip = "http://140.115.80.224:8080/Recommendation_Trip.php";
            final int RECOMMENDATION_NUM_PARAM = 1;
            String[] request_name = new String[RECOMMENDATION_NUM_PARAM];
            String[] request_value = new String[RECOMMENDATION_NUM_PARAM];
            String ret;

            TYPE = TRIP;

            ConnectServer connection = new ConnectServer(URL_RECOMMENDATION_Trip);
            ret = connection.connect(request_name, request_value, 0);

            JsonParser parser = new JsonParser(CASE);
            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"trip_Name");
            ret_place_ShortIntro = parser.Parse(ret,"place_ShortIntro");
            ret_place_ID = parser.Parse(ret,"PTtrip_ID");

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

    static Runnable run_Channel = new Runnable()
    {
        @Override
        public void run()
        {
            final String URL_RECOMMENDATION_Channel = "http://140.115.80.224:8080/Recommendation_Channel.php";
            final int RECOMMENDATION_NUM_PARAM = 1;
            String[] request_name = new String[RECOMMENDATION_NUM_PARAM];
            String[] request_value = new String[RECOMMENDATION_NUM_PARAM];
            String ret;

            TYPE = CHANNEL;

            ConnectServer connection = new ConnectServer(URL_RECOMMENDATION_Channel);
            ret = connection.connect(request_name, request_value, 0);
            Log.d(TAG,"in channel");
            JsonParser parser = new JsonParser(CASE);
            ret_place_Pic = parser.Parse(ret,"channel_Cover");
            ret_place_Name = parser.Parse(ret,"channel_Name");
            ret_place_ShortIntro = parser.Parse(ret,"channel_Intro");
            ret_place_ID = parser.Parse(ret,"channel_ID");

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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int row, long id)
    {
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ViewModel item = (ViewModel) listView.getItemAtPosition(row);
        Log.d(TAG, "item = " + item.getTitle() + "type=" + item.getType() + "id=" + item.getID());
        Toast.makeText(context, "You clicked on position : " + row + " and id : " + id, Toast.LENGTH_SHORT).show();

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
                Log.d(TAG,"tripid="+item.getID());
                break;
            case CHANNEL:
                intent.setClass(context, ChannelMain.class);
                bundle.putString("channelid", item.getID());
                break;
            default:
                break;
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
