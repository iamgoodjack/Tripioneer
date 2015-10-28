package mis.tripioneer;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Jenny on 2015/8/20.
 */
public class RoadPlanRow
{
    public static final int LAYOUT = R.layout.list_single_place_replace;
    private final Context context;
    private final TextView title;
    private final ImageView img;

    //private final TextView info;

    public RoadPlanRow(Context context, View convertView)
    {
        this.context = context;
        this.img = (ImageView) convertView.findViewById(R.id.img);
        this.title = (TextView) convertView.findViewById(R.id.txt);
        //this.info = (TextView) convertView.findViewById(R.id.info);

    }
    public void bind(RoadPlanModel Model) {
        this.title.setText(Model.getTitle());
        //this.info.setText(Model.getInfo());
        Picasso.with(this.context).load(Model.getImageUrl()).into(this.img);
    }
}
