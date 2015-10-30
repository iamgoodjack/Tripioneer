package mis.tripioneer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
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
import mis.tripioneer.DrawerAdapter.IMyViewHolderClicks;


public class MainActivity_mdsign extends AppCompatActivity
{

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

    ActionBarDrawerToggle mDrawerToggle;
    private static final String TAG ="mainactivity_mdsign";
    String label;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_mdsign);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

        mRecyclerView.setHasFixedSize(true);// Letting the system know that the list objects are of fixed size

        mAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,
                new IMyViewHolderClicks()
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

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new Recommendation_frag());
        ft.commit();
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

    /**/
    public void selectItem()
    {
        Fragment fragment = null;
        //FOR Diff color action bar
        /*Intent intent = new Intent();
        Bundle bundle = new Bundle();*/
        switch ( label )
        {
            case "推薦":
                fragment = new Recommendation_frag();
                break;
            case "訂閱":
                //FOR FRAGMENT STRUCTURE(Same color action bar)
                fragment = new ChannelMain_frag();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//TODO:SET TO SUBSCRIPTED CHANNEL
                fragment.setArguments(bundle);
                //FOR Diff color action bar
                /*intent.setClass(MainActivity_mdsign.this, ChannelMain.class);
                bundle.putString("channelid", "1");//item.getID()
                intent.putExtras(bundle);
                startActivity(intent);*/
                break;
            case "收藏庫":
                fragment = new Collect_frag();
                //FOR Diff color action bar
                /*intent.setClass(MainActivity_mdsign.this, Collect.class);
                bundle.putString("channelid", "1");//TODO:SET TO COLLECT
                intent.putExtras(bundle);
                startActivity(intent);*/
                break;
            //TODO:FRAGMENT PLACE_REPLACE
            case "快選行程":
                Intent intent = new Intent(MainActivity_mdsign.this,Search.class);
                Drawer.closeDrawer(mRecyclerView);
                getSupportActionBar().setTitle(label);
                startActivity(intent);
                return;
                //break;
            default:
                Drawer.closeDrawer(mRecyclerView);
                return;
                //break;
        }
        getSupportActionBar().setTitle(label);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Drawer.closeDrawer(mRecyclerView);
    }
}