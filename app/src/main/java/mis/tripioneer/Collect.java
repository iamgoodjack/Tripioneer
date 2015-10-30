package mis.tripioneer;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class Collect extends AppCompatActivity
{

    private static final String TAG ="Collect";
    int IMGS[] ={R.drawable.collectimg1,R.drawable.collectimg2,R.drawable.collectimg3,R.drawable.collectimg4,R.drawable.collectimg5};
    private List<CollectInfo> collectInfoList;
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

    private ItemDAO itemDAO;
    private List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_difacbar);
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

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        //sample();

        // 建立資料庫物件
        itemDAO = new ItemDAO(getApplicationContext());

        // 取得所有記事資料
        items = itemDAO.getCollect();

        initializeData();

        CollectAdapter adapter = new CollectAdapter(collectInfoList);
        recList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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
                Toast.makeText(getBaseContext(), "search", Toast.LENGTH_SHORT).show();
                break;

            default:
                //TODO
                Toast.makeText(getBaseContext(), "default", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**/
    public void selectItem()
    {
        Fragment fragment = null;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch ( label )
        {
            case "推薦":
                intent.setClass(Collect.this, MainActivity_mdsign.class);
                startActivity(intent);
                break;
            case "訂閱":
                /*fragment = new ChannelMain_frag();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//TODO:SET TO SUBSCRIPTED CHANNEL
                fragment.setArguments(bundle);*/
                /*intent.setClass(Collect.this, ChannelMain.class);
                bundle.putString("channelid", "1");//item.getID()
                intent.putExtras(bundle);
                startActivity(intent);*/
                break;
            case "收藏庫":
                //fragment = new Collect_frag();
                //fragment.setArguments(bundle);
                break;
            case "快選行程":
                intent = new Intent(Collect.this,Search.class);
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


    private void sample()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String now = formatter.format(new Date());
        int label = R.drawable.ic_tripioneer_treasurebox_location_2;
        collectInfoList = new ArrayList<>();
        collectInfoList.add(new CollectInfo("億載金城",IMGS[0],now,label));
        collectInfoList.add(new CollectInfo("安平古堡", IMGS[1],now,label));
        collectInfoList.add(new CollectInfo("安平豆花", IMGS[2],now,label));
        collectInfoList.add(new CollectInfo("台南孔廟",IMGS[3],now,label));
        collectInfoList.add(new CollectInfo("馬沙溝濱海遊憩區",IMGS[4],now,label));
    }

    private void initializeData()
    {
        final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/tainan_pic/";
        int label = R.drawable.ic_tripioneer_treasurebox_atob_2;
        collectInfoList = new ArrayList<>();

        for(int i=0;i<items.size();i++)
        {
            try
            {
                collectInfoList.add(
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