package mis.tripioneer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.CollectViewHolder> {

    private Context context;
    private List<CollectInfo> contactList;
    private final String TAG = "CollectAdapter";

    public CollectAdapter(List<CollectInfo> contactList)
    {
        this.contactList = contactList;
    }

    public CollectAdapter(List<CollectInfo> contactList, Context context)
    {
        this.contactList = contactList;
        this.context = context;
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
        //contactViewHolder.img.setBackgroundResource(ci.img);
        Picasso.with(context).load(ci.img).transform(new RoundedTransformation(4, 0)).fit().into(contactViewHolder.img);
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
        CardView card;
        public CollectViewHolder(View v)
        {
            super(v);
            txt = (TextView) v.findViewById(R.id.placeName);
            img = (ImageView) v.findViewById(R.id.placePic);
            card = (CardView) v.findViewById(R.id.card_view);
            card.setPreventCornerOverlap(false);
        }
    }
}