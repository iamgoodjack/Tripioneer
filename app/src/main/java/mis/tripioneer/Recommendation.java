package mis.tripioneer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;




public class Recommendation extends Activity implements OnItemClickListener
{
    private final static int DOWNLOAD_COMPLETE = 1;
    private final String CASE = "RECOMMENDATION";
    private final int PLACE = 0;
    //private final int TRIP = 1;
    //private final int CHANNEL = 2;
    private static int TYPE;
    private static int RET_PARAM_NUM;
    private static ArrayList<String> ret_place_ID = new ArrayList<String>();
    private static ArrayList<String> ret_place_Pic = new ArrayList<String>();
    private static ArrayList<String> ret_place_Name = new ArrayList<String>();
    private static ArrayList<String> ret_place_ShortIntro = new ArrayList<String>();
    private static ListView listView;
    private static List<ViewModel> viewModels;
    private static ViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        TYPE = PLACE;
        new Thread(run_Recommendation).start();
        viewModels = new ArrayList<ViewModel>();
        adapter = new ViewAdapter(this, viewModels);
        listView =(ListView)findViewById(R.id.list);
        listView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommendation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static Handler handler = new Handler()
    {
        private final String URL_PREFIX_IMAGE = "http://140.115.80.224:8080/group4/image/";
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD_COMPLETE:

                    try
                    {
                        for(int i=0; i<RET_PARAM_NUM;i++)
                        {
                            if(!"".equals( ret_place_Pic.get(i) ) )
                            {
                                ViewModel row = new ViewModel
                                        (
                                            ret_place_Name.get(i),
                                            URL_PREFIX_IMAGE + URLEncoder.encode(ret_place_Pic.get(i), "UTF-8") + ".jpg",
                                            ret_place_ShortIntro.get(i)
                                        );
                                row.setType(TYPE);
                                row.setID(ret_place_ID.get(i));
                                viewModels.add(row);
                            }
                        }
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    listView.setAdapter(adapter);
                    break;

                default:
                    Log.d("Gina","Download Failed");
                    break;
            }
        }
    };

    Runnable run_Recommendation = new Runnable()
    {
        @Override
        public void run()
        {
            final String URL_RECOMMENDATION_Place = "http://140.115.80.224:8080/Recommendation_Place.php";
            final int RECOMMENDATION_NUM_PARAM = 1;
            String[] request_name = new String[RECOMMENDATION_NUM_PARAM];
            String[] request_value = new String[RECOMMENDATION_NUM_PARAM];
            String ret;

            ConnectServer connection = new ConnectServer(URL_RECOMMENDATION_Place);
            ret = connection.connect(request_name, request_value, 0);

            JsonParser parser = new JsonParser(CASE);
            ret_place_Pic = parser.Parse(ret,"place_Pic");
            ret_place_Name = parser.Parse(ret,"place_Name");
            ret_place_ShortIntro = parser.Parse(ret,"place_ShortIntro");
            ret_place_ID = parser.Parse(ret,"place_ID");

            RET_PARAM_NUM = ret_place_Name.size();

            for(int i=0; i<RET_PARAM_NUM;i++)
            {
                Log.d("Gina",ret_place_Name.get(i)+"\n");
                Log.d("Gina", ret_place_Pic.get(i) + "\n");
                Log.d("Gina", ret_place_ShortIntro.get(i) + "\n");
                Log.d("Gina", ret_place_ID.get(i) + "\n");
            }

            handler.sendEmptyMessage(DOWNLOAD_COMPLETE);

        }
    };

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int row, long id)
    {
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ViewModel item = (ViewModel) listView.getItemAtPosition(row);
        Log.d("TEST","item = "+item.getTitle()+"type="+item.getType()+"id="+item.getID());
        Toast.makeText(getBaseContext(), "You clicked on position : " + row + " and id : " + id, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(Recommendation.this, place.class);
        Bundle bundle = new Bundle();
        bundle.putString("specifyid", item.getID());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
