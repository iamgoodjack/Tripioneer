package mis.tripioneer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by user on 2015/8/26.
 */
public class testAdapter extends RecyclerView.Adapter<testAdapter.ViewHolder>
{
    private String[] TITLES;
    private int[] ICONS;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View v)
        {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.imgIcon);
            mTextView = (TextView) v.findViewById(R.id.txtItem);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public testAdapter(String[] myDataset, int[] icons)
    {
        TITLES = myDataset;
        ICONS = icons;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public testAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(TITLES[position]);
        holder.mImageView.setBackgroundResource(ICONS[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return TITLES.length;
    }
}