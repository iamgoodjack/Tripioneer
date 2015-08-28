package mis.tripioneer;



import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2015/7/31.
 */
public class MainActivity extends AppCompatActivity
{

    private DrawerLayout layDrawer;
    private ListView lstDrawer;

    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] drawer_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        initActionBar();
        initDrawer();
        initDrawerList();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new Recommendation_mod());
        ft.commit();
    }

    private void initActionBar()
    {

        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.tp);

    }

    private void initDrawer()
    {

        layDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        lstDrawer = (ListView) findViewById(R.id.left_drawer);//drawer

        layDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mTitle = mDrawerTitle = "               推薦";
        drawerToggle = new ActionBarDrawerToggle(
                this,
                layDrawer,
                R.string.drawer_close,
                R.string.drawer_open
               ) {

            @Override
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
            }
        };
        drawerToggle.syncState();

        layDrawer.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {

        //home
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        //action buttons
        switch (item.getItemId())
        {
            case R.id.action_edit:
                Toast.makeText(getBaseContext(),"edit click", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_search:
                //....
                Toast.makeText(getBaseContext(),"edit click", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(getBaseContext(),"edit click", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDrawerList()
    {
        drawer_menu = this.getResources().getStringArray(R.array.drawer_menu);
        List<HashMap<String,String>> lstData = new ArrayList<HashMap<String,String>>();
        for (int i = 0; i < 4; i++)
        {
            HashMap<String, String> mapValue = new HashMap<String, String>();
            mapValue.put("icon", Integer.toString(R.mipmap.tp));
            mapValue.put("title", drawer_menu[i]);
            lstData.add(mapValue);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, lstData, R.layout.drawer_list_item, new String[]{"icon", "title"}, new int[]{R.id.imgIcon, R.id.txtItem});
        lstDrawer.setAdapter(adapter);
        lstDrawer.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new Recommendation_mod();

                break;

            case 1:
                fragment = new ChannelMain_mod();
                Bundle bundle = new Bundle();
                bundle.putString("channelid", "1");//To do : set to advisor's channel
                fragment.setArguments(bundle);
                break;

            case 2:

                break;

            case 3:

                break;

            default:
                //還沒製作的選項，fragment 是 null，直接返回
                return;
        }
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // 更新被選擇項目，換標題文字，關閉選單
        lstDrawer.setItemChecked(position, true);
        //setTitle(drawer_menu[position]);
        layDrawer.closeDrawer(lstDrawer);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}