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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/8/16.
 */
public class Collect_frag extends Fragment
{
    private static final String TAG ="Collect";
    int IMGS[] ={R.drawable.collectimg1,R.drawable.collectimg2,R.drawable.collectimg3,R.drawable.collectimg4,R.drawable.collectimg5};
    private Context context;
    private List<CollectInfo> persons;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        //ID = this.getArguments().getString("channelid");
        super.onCreate(savedInstanceState);

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
        initializeData();
        CollectAdapter adapter = new CollectAdapter(persons);
        recList.setAdapter(adapter);
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



    private void initializeData()
    {
        //TODO:IMG 圓角、Out of memory error
        persons = new ArrayList<>();
        persons.add(new CollectInfo("億載金城",IMGS[0]));
        persons.add(new CollectInfo("安平古堡", IMGS[1]));
        persons.add(new CollectInfo("安平豆花", IMGS[2]));
        /*persons.add(new ContactInfo("台南孔廟",IMGS[3]));
        persons.add(new ContactInfo("馬沙溝濱海遊憩區",IMGS[4]));*/
    }
}
