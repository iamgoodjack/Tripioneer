package mis.tripioneer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 2015/8/16.
 */
public class Collect_frag extends Fragment
{
    private static final String TAG ="Collect";
    int IMGS[] ={R.drawable.collectimg1,R.drawable.collectimg2,R.drawable.collectimg3,R.drawable.collectimg4,R.drawable.collectimg5};
    private Context context;
    private List<CollectInfo> collectList;

    private ItemDAO itemDAO;
    private List<Item> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        //ID = this.getArguments().getString("channelid");
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

        return inflater.inflate(R.layout.collect, container, false);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        RecyclerView recList = (RecyclerView) getView().findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        //sample();

        // 建立資料庫物件
        itemDAO = new ItemDAO(getActivity());
        // 取得所有記事資料
        items = itemDAO.getCollect();
        initializeData();

        CollectAdapter adapter = new CollectAdapter(collectList,getActivity());
        recList.setAdapter(adapter);
        super.onCreate(savedInstanceState);


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

    private void sample()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String now = formatter.format(new Date());
        int label = R.drawable.ic_tripioneer_treasurebox_location_2;
        collectList = new ArrayList<>();
        collectList.add(new CollectInfo("億載金城",IMGS[0],now,label));
        collectList.add(new CollectInfo("安平古堡", IMGS[1],now,label));
        collectList.add(new CollectInfo("安平豆花", IMGS[2],now,label));
        collectList.add(new CollectInfo("台南孔廟",IMGS[3],now,label));
        collectList.add(new CollectInfo("馬沙溝濱海遊憩區",IMGS[4],now,label));
    }

    private void initializeData()
    {
        final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
        int label = R.drawable.ic_tripioneer_treasurebox_atob_2;
        collectList = new ArrayList<>();

        for(int i=0;i<items.size();i++)
        {
            try
            {
                collectList.add(
                        new CollectInfo
                                (       items.get(i).getID(),
                                        items.get(i).getTitle(),
                                        URL_PREFIX_IMAGE + URLEncoder.encode(items.get(i).getSpotpic(), "UTF-8") + ".jpg",
                                        "加入收藏時間:"+items.get(i).getDate(),
                                        label
                                )
                );
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

        }

    }
}
