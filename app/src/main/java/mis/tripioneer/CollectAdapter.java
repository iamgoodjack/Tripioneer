package mis.tripioneer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.CollectViewHolder> {


    private List<CollectInfo> contactList;

    public CollectAdapter(List<CollectInfo> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(CollectViewHolder contactViewHolder, int i)
    {
        CollectInfo ci = contactList.get(i);
        contactViewHolder.txt.setText(ci.name);
        contactViewHolder.img.setBackgroundResource(ci.img);
        //Picasso.with(context).load(ci.img).into(contactViewHolder.img);
    }

    @Override
    public CollectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card, viewGroup, false);

        return new CollectViewHolder(itemView);
    }

    public static class CollectViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt;
        ImageView img;
        public CollectViewHolder(View v)
        {
            super(v);
            txt = (TextView) v.findViewById(R.id.placeName);
            img = (ImageView) v.findViewById(R.id.placePic);
        }
    }
}