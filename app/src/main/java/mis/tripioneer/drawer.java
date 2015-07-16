package mis.tripioneer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class drawer extends ActionBarActivity {

    private DrawerLayout layDrawer;
    private ListView lstDrawer;

    private ActionBarDrawerToggle drawerToggle;

    private String[] drawer_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        initActionBar();
        initDrawer();
        initDrawerList();

        if (savedInstanceState == null) {
            //selectItem(0);
        }
    }

    //================================================================================
    // Init
    //================================================================================
    private void initActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer(){
        layDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        lstDrawer = (ListView) findViewById(R.id.left_drawer);

        layDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerToggle = new ActionBarDrawerToggle(this, layDrawer, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();

        layDrawer.setDrawerListener(drawerToggle);
    }

    private void initDrawerList(){
        drawer_menu = this.getResources().getStringArray(R.array.drawer_menu);

        List<HashMap<String,String>> lstData = new ArrayList<HashMap<String,String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> mapValue = new HashMap<String, String>();
            //mapValue.put("icon", Integer.toString(R.drawable.ic_launcher));
            mapValue.put("title", drawer_menu[i]);
            lstData.add(mapValue);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, lstData, R.layout.drawer_list_item2, new String[]{"icon", "title"}, new int[]{R.id.imgIcon, R.id.txtItem});
        lstDrawer.setAdapter(adapter);

        //側選單點選偵聽器
        //lstDrawer.setOnItemClickListener(new DrawerItemClickListener());
    }

    //================================================================================
    // Action Button 建立及點選事件
    //================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //home
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //action buttons
        /*switch (item.getItemId()) {
            case R.id.action_edit:
                //....
                break;

            case R.id.action_search:
                //....
                break;

            default:
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }


    //================================================================================
    // 側選單點選事件
    //================================================================================
   /* private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }*/

    /*private void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new FragmentApple();
                break;

            case 1:
                fragment = new FragmentBook();
                break;

            case 2:
                fragment = new FragmentCat();
                Bundle args = new Bundle();
                args.putString(FragmentCat.CAT_COLOR, "Brown");
                fragment.setArguments(args);
                break;

            default:
                //還沒製作的選項，fragment 是 null，直接返回
                return;
        }

        FragmentManager fragmentManager = getFragmentManager();
        //[方法1]直接置換，無法按 Back 返回
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        //[方法2]開啟並將前一個送入堆疊
        //重要！ 必須加寫 "onBackPressed"
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();


        // 更新被選擇項目，換標題文字，關閉選單
        lstDrawer.setItemChecked(position, true);
        setTitle(drawer_menu[position]);
        layDrawer.closeDrawer(lstDrawer);
    }*/

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Back 鍵處理
     * 當最後一個 stack 為 R.id.content_frame, 結束 App
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = this.getFragmentManager();
        int stackCount = fragmentManager.getBackStackEntryCount();
        if (stackCount == 0) {
            this.finish();
        }
    }

}