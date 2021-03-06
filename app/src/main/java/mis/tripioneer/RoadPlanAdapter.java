package mis.tripioneer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Jenny on 2015/8/20.
 */
public class RoadPlanAdapter extends BaseAdapter
{
    private final LayoutInflater inflater;
    private final List<RoadPlanModel> viewModels;
    private final Context context;

    public RoadPlanAdapter(Context context,List<RoadPlanModel> viewModels)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
    }

    @Override
    public int getCount() {
        return this.viewModels.size();
    }

    @Override
    public Object getItem(int position) {
        return this.viewModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d("Mag", "getView");
        final RoadPlanModel model = (RoadPlanModel) getItem(position);

        RoadPlanRow row;
        // If the convertView is null we need to create it
        if(convertView == null) {
            convertView = this.inflater.inflate(RoadPlanRow.LAYOUT, parent, false);

            // In that case we also need to create a new row and attach it to the newly created View
            row = new RoadPlanRow(this.context, convertView);
            convertView.setTag(row);
        }
        Log.d("Mag", "TAG");
        // After that we get the row associated with this View and bind the view model to it
        row = (RoadPlanRow) convertView.getTag();
        row.bind(model);
        ImageView add_btn = (ImageView)convertView.findViewById(R.id.addButton);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Place_replace) context).add_to_replace(model.getID());
                ((Place_replace) context).add_Name_to_replace(model.getTitle());
                ((Place_replace) context).add_Pic_to_replace(model.getImageUrl());
                Toast.makeText(context, "Successfully added to your trip!", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
