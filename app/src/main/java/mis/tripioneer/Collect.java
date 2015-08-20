package mis.tripioneer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;



public class Collect extends AppCompatActivity
{

    private static final String TAG ="Collect";
    int IMGS[] ={R.drawable.collectimg1,R.drawable.collectimg2,R.drawable.collectimg3,R.drawable.collectimg4,R.drawable.collectimg5};
    private List<CollectInfo> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        initializeData();
        CollectAdapter adapter = new CollectAdapter(persons);
        recList.setAdapter(adapter);
    }



    private void initializeData()
    {
        //TODO:IMG 圓角、Out of memory error
        persons = new ArrayList<>();
        persons.add(new CollectInfo("億載金城",IMGS[0]));
        persons.add(new CollectInfo("安平古堡",IMGS[1]));
        persons.add(new CollectInfo("安平豆花",IMGS[2]));
        /*persons.add(new ContactInfo("台南孔廟",IMGS[3]));
        persons.add(new ContactInfo("馬沙溝濱海遊憩區",IMGS[4]));*/
    }
}