package mis.tripioneer;

/**
 * Created by iamgo on 2015/9/9.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class history extends AppCompatActivity {
    private String[] sources = {
            "http://lorempixel.com/600/200/food"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    mis.tripioneer.ItemData itemsData[] = {
            new mis.tripioneer.ItemData("餐廳",sources[0]),
            new mis.tripioneer.ItemData("餐廳",sources[0]),
            new mis.tripioneer.ItemData("餐廳",sources[0]),
            new mis.tripioneer.ItemData("餐廳",sources[0]),
            new mis.tripioneer.ItemData("餐廳",sources[0]),
            new mis.tripioneer.ItemData("餐廳",sources[0]),
            new mis.tripioneer.ItemData("餐廳",sources[0]),
    };

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    HistoryAdapter mAdapter = new HistoryAdapter(itemsData);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}