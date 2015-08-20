package mis.tripioneer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;


/**
 * Created by user on 2015/7/16.
 */
public class ViewAdapter extends BaseAdapter
{
    private final String TAG ="ViewAdapter";
    private final List<ViewModel> viewModels;
    private List<ViewModel> headers;
    private final Context context;
    private final LayoutInflater inflater;

    public ViewAdapter(Context context, List<ViewModel> viewModels)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
    }

    public ViewAdapter(Context context, List<ViewModel> viewModels, List<ViewModel> headers)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
        this.headers = headers;
    }

    public List<ViewModel> viewmodels() {
        return this.viewModels;
    }

    @Override
    public int getCount() {
        return this.viewModels.size();
    }

    public ViewModel getHeader(int position)
    {
        ViewModel model=null;

        try
        {
            switch (position)
            {
                case 0:
                    model = this.headers.get(0);
                    break;
                case 6:
                    model = this.headers.get(1);
                    break;
                case 10:
                    model = this.headers.get(2);
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return model;
    }

    @Override
    public ViewModel getItem(int position)
    {
        ViewModel model=null;
        if(position  < 6)
        {
            model = this.viewModels.get(position-1);
        }
        else if( position > 10 )
        {
            model = this.viewModels.get(position-3);
        }
        else
        {
            model = this.viewModels.get(position-2);
        }
        return model;
    }

    @Override
    public long getItemId(int position)
    {
        // We only need to implement this if we have multiple rows with a different layout. All your rows use the same layout so we can just return 0.
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewModel viewModel;
        ViewRow row=null;

        if(position ==0 | position ==6 | position == 10)
        {
            viewModel = getHeader(position);
            convertView = this.inflater.inflate(R.layout.list_header, parent, false);
            Log.d(TAG,"converview null and is header");
            row = new ViewRow(this.context, convertView, true);
            row.bind_header(viewModel);
        }
        else if(convertView != null )
        {
            viewModel = getItem(position);
            Log.d(TAG, "title=" + viewModel.getTitle());
            convertView = this.inflater.inflate(ViewRow.LAYOUT, parent, false);
            // In that case we also need to create a new row and attach it to the newly created View
            row = new ViewRow(this.context, convertView);
            convertView.setTag(row);
            row.bind(viewModel);
        }

        // After that we get the row associated with this View and bind the view model to it
        row = (ViewRow) convertView.getTag();
        return convertView;
    }
}
