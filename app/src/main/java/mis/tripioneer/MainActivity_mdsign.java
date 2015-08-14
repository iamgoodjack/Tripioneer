package mis.tripioneer;

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


    String TITLES[] = {"推薦","頻道","收藏庫","最近瀏覽"};
    int ICONS[] = {R.drawable.ic_thumb_up_black_24dp,R.drawable.ic_radio_black_24dp,R.drawable.ic_favorite_black_24dp,R.drawable.ic_history_black_24dp};


    String NAME = "Gina";//TODO:GET USER NAME
    String EMAIL = "teemo@gmail.com";//TODO:GET USER EMAIL
    int PROFILE = R.drawable.ic_account_box_black_24dp;

    private Toolbar toolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;

    ActionBarDrawerToggle mDrawerToggle;

    private static final String TAG ="MainActivity_mdsign";
    String ttag;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

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
                        ttag =(String)caller.getTag();
                        selectItem();
                    }

                    @Override
                    public void onIcon(ImageView callerImage, String tag)
                    {
                        Log.d(TAG, "onClick, getTag=" + callerImage.getTag());
                        ttag =(String)callerImage.getTag();
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
                getSupportActionBar().setTitle("TestOpen");
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle("TestClose");
            }



        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new Recommendation_mod());
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
        switch ( ttag )
        {
            case "推薦":
                fragment = new Recommendation_mod();
                break;
            case "頻道":
                fragment = new ChannelMain_mod();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//To do : set to advisor's channel
                fragment.setArguments(bundle);
                break;
            /*case "收藏庫":
                break;
            case "最近瀏覽":
                break;*/
            default:
                Drawer.closeDrawer(mRecyclerView);
                return;
                //break;
        }
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        Drawer.closeDrawer(mRecyclerView);
    }
}