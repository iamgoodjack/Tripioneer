package mis.tripioneer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 2015/9/4.
 */
public class Trip_mdsign extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private final static int DB_LOAD_COMPLETE = 2;
    private static List<Item> items;
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
    private final int REQUEST_CODE=1;
    private ArrayList<String> replace_id_list;
    private ArrayList<String> replace_name_list;
    private ArrayList<String> replace_pic_list;
    private ArrayList<String> ret_trip_time;

    private String ID ;
    private String TITLE;
    private String TITLE_prefix;
    private String TITLE_postfix;



    String TITLES[] = {"推薦","訂閱","收藏庫","快選行程"};
    int ICONS[] = {R.drawable.ic_menu_recommand,R.drawable.ic_menu_channel,R.drawable.ic_menu_treasurebox,R.drawable.ic_ic_flag_black_32dp};
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

    ItemDAO itemDAO;

    private long key;
    private String label_collect;

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
                Replace.setVisibility(View.INVISIBLE);
                Like.setVisibility(View.INVISIBLE);
                Navigation.setVisibility(View.INVISIBLE);
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                Replace.setVisibility(View.VISIBLE);
                Like.setVisibility(View.VISIBLE);
                Navigation.setVisibility(View.VISIBLE);
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
            private ArrayList<String> navi_place_id = new ArrayList<String>();
            @Override
            public void onClick(View v)
            {
                if(!navi_place_id.isEmpty()){navi_place_id.clear();}
                Intent intent = new Intent();
                intent.setClass(Trip_mdsign.this, roadplan_choose_mode.class);
                for(int i=0;i<viewModels.size();i++)
                {
                    navi_place_id.add(viewModels.get(i).getID());
                }
                intent.putStringArrayListExtra("place_id_list",navi_place_id);
                startActivity(intent);
            }
        });

        itemDAO = new ItemDAO(context);

        Like.setOnClickListener(new View.OnClickListener() {
            //Item(String tripid, String spotid, String spotname, String title, int spotorder, String spottime, String spotpic, int ttltime, long date)
            Item item;
            Date date = new Date();

            //TODO:TTLTIME & DATE
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm");
                String now = formatter.format(new Date());
                /*Like.setBackgroundResource(R.drawable.ic_star_aftertpushed_version2);
                Like.setBackgroundTintList(ColorStateList.valueOf(R.color.White));
                Like.setBackgroundResource(R.drawable.ic_star_version2);
                Like.setBackgroundTintList(ColorStateList.valueOf(R.color.History));*/
                if (isnotDup()) {
                    for (int i = 0; i < viewModels.size(); i++) {
                        /*Log.d(TAG,"id"+ID);
                        Log.d(TAG,"placeid"+ret_place_ID.get(i));
                        Log.d(TAG,"placename"+ret_place_Name.get(i));
                        Log.d(TAG,"title"+TITLE);
                        Log.d(TAG,"placeorder"+i);
                        Log.d(TAG,"placetime"+ret_place_SuggestTime.get(i));
                        Log.d(TAG,"placepicname"+ret_place_Pic.get(i));
                        Log.d(TAG,"spotsize"+viewModels.size());
                        Log.d(TAG,"date"+now);*/
                        item = new Item(ID, ret_place_ID.get(i), ret_place_Name.get(i), TITLE, i, ret_place_SuggestTime.get(i)
                                , ret_place_Pic.get(i), viewModels.size(), now);

                        itemDAO.insert(item);
                    }
                    Toast.makeText(getBaseContext(), "成功加入收藏!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "已經加入過喽!", Toast.LENGTH_SHORT).show();
                }

                //itemDAO.close();
            }
        });

        Replace.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent();
                intent.setClass(Trip_mdsign.this, Place_replace.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });



        Bundle tripdata = this.getIntent().getExtras();
        label_collect = tripdata.getString("label");
        if(label_collect.equals("CollectAdapter"))
        {
            key = tripdata.getLong("key");
            Log.d("Gina","key="+key);
        }
        else
        {
            ID = tripdata.getString("tripid");
            TITLE_prefix = tripdata.getString("title");
            if(ID.equals("2"))
            {TITLE_postfix ="(二日遊)";}
            else{TITLE_postfix ="(一日遊)";}
            TITLE = TITLE_prefix+TITLE_postfix;
            getSupportActionBar().setTitle(TITLE);

            request_value[0] = ID;
            request_name[0]="ID";
        }


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
                            //TITLE_postfix = "("+viewModels.size()+"小時)";
                        }

                        TITLE_postfix = "(" + (viewModels.size() + 1) + "小時)";


                        TITLE = TITLE_prefix + TITLE_postfix;
                        getSupportActionBar().setTitle(TITLE);
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

        if(label_collect.equals("CollectAdapter"))
        {
            new Thread(run_From_Collect).start();
        }
        else
        {
            new Thread(run_Trip).start();
        }

    }

    Runnable run_From_Collect = new Runnable()
    {
        @Override
        public void run() {
            ItemDAO itemDAO = new ItemDAO(context);

            items = itemDAO.getAll();

            handler.sendEmptyMessage(DB_LOAD_COMPLETE);

        }
    };

    /*@Override
    public void onDestroy()
    {
        itemDAO.close();
    }*/


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
                //Toast.makeText(getBaseContext(), "edit", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_search:
                //TODO
                //Toast.makeText(getBaseContext(),"search", Toast.LENGTH_SHORT).show();
                break;

            default:
                //TODO
                //Toast.makeText(getBaseContext(),"default", Toast.LENGTH_SHORT).show();
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
            case "快選行程":
                intent = new Intent(Trip_mdsign.this,Search.class);
                Drawer.closeDrawer(mRecyclerView);
                startActivity(intent);
                break;
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
                case DB_LOAD_COMPLETE:
                    try
                    {
                        for(int i=0;i<items.size();i++)
                        {
                            if (!"".equals( items.get(i).getSpotpic() ) )
                            {
                                ViewModel row = new ViewModel
                                        (
                                                items.get(i).getSpotname(),
                                                URL_PREFIX_IMAGE + URLEncoder.encode(items.get(i).getSpotpic(), "UTF-8") + ".jpg",
                                                "建議停留時間:"+items.get(i).getSpottime()+"小時"
                                        );
                                viewModels.add(row);
                                row.setID(items.get(i).getSpotid());
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
        public void run() {
            final String URL_TRIP = "http://140.115.80.224:8080/group4/Android_trip.php";
            final String CASE = "TRIP";
            String ret;

            ConnectServer connection = new ConnectServer(URL_TRIP);
            ret = connection.connect(request_name, request_value, 1);

            JsonParser parser = new JsonParser(CASE);
            ret_trip_time = parser.Parse(ret,"trip_TotalTime");
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
        //Toast.makeText(context, "You clicked on position : " + row + " and id : " + id, Toast.LENGTH_SHORT).show();

        intent.setClass(this, place_mimic_googlemap.class);
        bundle.putString("specifyid", item.getID());

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUEST_CODE:

                if (resultCode != Activity.RESULT_CANCELED)
                {
                    if (resultCode == RESULT_OK)
                    {
                        if( data.getStringArrayListExtra("replace_id_list") != null)
                        {
                            replace_id_list = data.getStringArrayListExtra("replace_id_list");
                            replace_name_list = data.getStringArrayListExtra("replace_name_list");
                            replace_pic_list = data.getStringArrayListExtra("replace_pic_list");;
                            for(int i=0;i<replace_id_list.size();i++)
                            {
                                try
                                {
                                    ViewModel row = new ViewModel
                                            (
                                                    replace_name_list.get(i),
                                                    replace_pic_list.get(i),
                                                    "建議停留時間:1小時"
                                            );
                                    row.setID(replace_id_list.get(i));
                                    viewModels.add(row);
Log.d(TAG,URL_PREFIX_IMAGE + URLEncoder.encode(replace_pic_list.get(i), "UTF-8") + ".jpg");
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            ((GeneralViewAdapter_tmp) listView.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }

                break;
            default:
                break;
        }
    }

    public void swap(List<ViewModel> models_in_adapter)
    {
        viewModels = models_in_adapter;
    }

    public boolean isnotDup()
    {
        List<Item> items;
        List<Item> items_key;

        // 取得所有記事資料
        items_key = itemDAO.getCollectSetKey(ID);
        items = itemDAO.Chk_dup(ID);

        if(items_key.isEmpty())
        {
            return true;
        }

        for(int i=0; i< items_key.size();i++)
        {
            long _id=items_key.get(i).getID();
            final int _id_start = 91;
            for(int j=0;j<viewModels.size();j++)
            {
                Log.d("ShowDb", "Spotid" + items.get(i).getSpotid());
                Log.d("ShowDb", "Spotorder" + items.get(i).getSpotorder());
                Log.d("ShowDb","j="+j);
                Log.d("ShowDb","_id="+_id);
                Log.d("ShowDb","_id-1+j="+((int)_id-1+j));
                Log.d("ShowDb", "DB_SPOT_ID" + items.get((int)_id-1+j).getSpotid());
                Log.d("ShowDb", "View models id" + viewModels.get(j).getID());
                if(!items.get((int)_id-1+j).getSpotid().equals(viewModels.get(j).getID()))
                {
                    return true;
                }
            }

            if(i+1 < items_key.size())
            {
                if( items_key.get(i+1).getID()-i-1 > viewModels.size() )
                {
                    return true;
                }
            }

        }

        return false;
    }


}
