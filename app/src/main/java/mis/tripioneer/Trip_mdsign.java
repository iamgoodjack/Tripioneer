package mis.tripioneer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.squareup.picasso.Picasso;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/9/4.
 */
public class Trip_mdsign extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private static final String TAG = "Trip_mdsign";
    private static final int TRIP_NUM_PARAM = 1;
    private String[] request_name = new String[TRIP_NUM_PARAM];
    private String[] request_value = new String[TRIP_NUM_PARAM];
    private static int RET_PARAM_NUM;
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private static ArrayList<String> ret_place_SuggestTime = new ArrayList<String>();
    private static DynamicListView  listView;
    private static List<ViewModel> viewModels;
    private static GeneralViewAdapter_tmp adapter;
    private static ImageView imageView;
    private static Context context;
    private FloatingActionButton Navigation;
    private FloatingActionButton Like;
    private FloatingActionButton Replace;

    String TITLES[] = {"推薦","訂閱","收藏庫","最近瀏覽"};
    int ICONS[] = {R.drawable.ic_menu_recommand,R.drawable.ic_menu_channel,R.drawable.ic_menu_treasurebox,R.drawable.ic_menu_history};
    String NAME = "Gina";//TODO:GET USER NAME
    String EMAIL = "teemo@gmail.com";//TODO:GET USER EMAIL
    int PROFILE = R.drawable.ic_menu_account;
    private Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    String label;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_mdsign);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

        mRecyclerView.setHasFixedSize(true);// Letting the system know that the list objects are of fixed size

        mAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,
                new DrawerAdapter.IMyViewHolderClicks()
                {

                    @Override
                    public void onTitle(View caller, String tag)
                    {
                        Log.d(TAG, "onClick, getTag=" + caller.getTag());
                        label =(String)caller.getTag();
                        selectItem();
                    }

                    @Override
                    public void onIcon(ImageView callerImage, String tag)
                    {
                        Log.d(TAG, "onClick, getTag=" + callerImage.getTag());
                        label =(String)callerImage.getTag();
                        selectItem();
                    }
                });

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }



        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        context = this;
        imageView = (ImageView) findViewById(R.id.tripPic);
        listView =(DynamicListView )findViewById(R.id.listView);
        Navigation = (FloatingActionButton) findViewById(R.id.fab_navigation);
        Like = (FloatingActionButton) findViewById(R.id.fab_like);
        Replace = (FloatingActionButton) findViewById(R.id.fab_replace);
        viewModels = new ArrayList<ViewModel>();
        adapter = new GeneralViewAdapter_tmp(this, viewModels);

        Navigation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(Trip_mdsign.this, roadplan_choose_mode.class);
                intent.putStringArrayListExtra("place_id_list", ret_place_ID);
                startActivity(intent);
            }
        });

        Like.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Like button clicked!");
            }
        });

        Replace.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(Trip_mdsign.this, Place_replace.class);
                startActivity(intent);
            }
        });


        String ID ;
        Bundle tripdata = this.getIntent().getExtras();
        ID = tripdata.getString("tripid");
        String TITLE = tripdata.getString("title");
        getSupportActionBar().setTitle(TITLE);
        request_value[0] = ID;
        request_name[0]="ID";
        listView.setOnItemClickListener(this);

        listView.enableDragAndDrop();
        listView.setDraggableManager(new TouchViewDraggableManager(R.id.img));
        listView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                   final int position, final long id) {
                        listView.startDragging(position);
                        return true;
                    }
                }
        );

        listView.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            adapter.remove(position);
                        }
                    }
                }
        );

        /*SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(adapter, Trip_mdsign.this,
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            adapter.remove(position);
                        }
                    }
                }
        );
        swipeUndoAdapter.setAbsListView(listView);
        listView.setAdapter(swipeUndoAdapter);
        listView.enableSimpleSwipeUndo();*/
        new Thread(run_Trip).start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        //home
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        //action buttons
        switch (item.getItemId())
        {
            case R.id.action_edit:
                //TODO
                Toast.makeText(getBaseContext(), "edit", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_search:
                //TODO
                Toast.makeText(getBaseContext(),"search", Toast.LENGTH_SHORT).show();
                break;

            default:
                //TODO
                Toast.makeText(getBaseContext(),"default", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectItem()
    {
        //Fragment fragment = null;
        //FOR Diff color action bar
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch ( label )
        {
            case "推薦":
                intent.setClass(Trip_mdsign.this, MainActivity_mdsign.class);
                startActivity(intent);
                break;
            case "訂閱":
                /*fragment = new ChannelMain_frag();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//TODO:SET TO SUBSCRIPTED CHANNEL
                fragment.setArguments(bundle);*/
                intent.setClass(Trip_mdsign.this, ChannelMain.class);
                bundle.putString("channelid", "1");//item.getID()
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "收藏庫":
                //fragment = new Collect_frag();
                //fragment.setArguments(bundle);
                intent.setClass(Trip_mdsign.this, Collect.class);
                //TODO:SET TO COLLECT
                startActivity(intent);
                break;
            /*case "最近瀏覽":
                break;*/
            default:
                Drawer.closeDrawer(mRecyclerView);
                return;
            //break;
        }
        getSupportActionBar().setTitle(label);
        /*android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
        Drawer.closeDrawer(mRecyclerView);
    }

    static Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
            switch(msg.what)
            {
                case  DOWNLOAD_COMPLETE :
                    try
                    {

                        for(int i=0;i<RET_PARAM_NUM;i++)
                        {
                            if (!"".equals( ret_place_Pic.get(i) ) )
                            {
                                ViewModel row = new ViewModel
                                        (
                                                ret_place_Name.get(i),
                                                URL_PREFIX_IMAGE + URLEncoder.encode(ret_place_Pic.get(i), "UTF-8") + ".jpg",
                                                "建議停留時間:"+ret_place_SuggestTime.get(i)+"小時"
                                        );
                                viewModels.add(row);
                                row.setID(ret_place_ID.get(i));
                            }
                        }
                        Picasso.with(context).load(viewModels.get(0).getImageUrl()).into(imageView);//Bind header trip_picture
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    listView.setAdapter(adapter);
                    break;

                default:
                    Log.d(TAG,"Download Failure");
                    break;
            }
        }
    };

    Runnable run_Trip = new Runnable()
    {
        @Override
        public void run()
        {
            final String URL_TRIP = "http://140.115.80.224:8080/group4/Android_trip.php";
            final String CASE = "TRIP";
            String ret;

            ConnectServer connection = new ConnectServer(URL_TRIP);
            ret = connection.connect(request_name, request_value, 1);

            JsonParser parser = new JsonParser(CASE);
            ret_place_Pic = parser.Parse(ret, "place_Pic");
            ret_place_Name = parser.Parse(ret, "place_Name");
            ret_place_ID = parser.Parse(ret, "place_ID");
            ret_place_SuggestTime = parser.Parse(ret, "PTplace_Recomtime");

            RET_PARAM_NUM = ret_place_Name.size();

            for(int i=0; i<RET_PARAM_NUM;i++)
            {

                Log.d(TAG,ret_place_Name.get(i)+"\n");
                Log.d(TAG, ret_place_Pic.get(i) + "\n");
                Log.d(TAG,ret_place_ID.get(i)+"\n");
                Log.d(TAG, ret_place_SuggestTime.get(i) + "\n");
            }
            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int row, long id)
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ViewModel item = (ViewModel) listView.getItemAtPosition(row);
        Log.d(TAG, "item = " + item.getTitle() + "type=" + item.getType() + "id=" + item.getID());
        Toast.makeText(context, "You clicked on position : " + row + " and id : " + id, Toast.LENGTH_SHORT).show();

        intent.setClass(this, place_mimic_googlemap.class);
        bundle.putString("specifyid", item.getID());

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
