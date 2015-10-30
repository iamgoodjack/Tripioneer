package mis.tripioneer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 2015/10/29.
 */
public class ShowDb extends AppCompatActivity
{
    ItemDAO itemDAO;
    List<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 建立資料庫物件
        itemDAO = new ItemDAO(getApplicationContext());
        //itemDAO.deleteTable();
        //itemDAO.createTable();
        // 取得所有記事資料
        items = itemDAO.getAll();
        if(!items.isEmpty())
        {
            int first = (int)items.get(0).getID();
            int last = (int)items.get(items.size()-1).getID();

            //String tripid, String spotid, String spotname, String title, int spotorder, String spottime, String spotpic, int ttltime, long date)
            for(int i=0;i<items.size();i++)
            {
                Log.d("ShowDb", "ID" + items.get(i).getID());
                Log.d("ShowDb","Tripid"+items.get(i).getTripid());
                Log.d("ShowDb", "Spotid" + items.get(i).getSpotid());
                Log.d("ShowDb", "Spotname" + items.get(i).getSpotname());
                Log.d("ShowDb", "Title" + items.get(i).getTitle());
                Log.d("ShowDb", "Spotorder" + items.get(i).getSpotorder());
                Log.d("ShowDb", "Spottime" + items.get(i).getSpottime());
                Log.d("ShowDb", "Spotpic" + items.get(i).getSpotpic());
                Log.d("ShowDb", "Ttltime" + items.get(i).getTtltime());
                Log.d("ShowDb", "Date" + items.get(i).getDate());
            }

            for(int i=first; i<= last; i++)
            {
                itemDAO.delete(i);
            }
        }
    }
}
