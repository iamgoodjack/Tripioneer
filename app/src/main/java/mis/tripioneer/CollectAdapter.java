package mis.tripioneer;

import android.content.Context;
import android.content.Intent;
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

import de.hdodenhof.circleimageview.CircleImageView;


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
        contactViewHolder.time.setText(ci.date);
        contactViewHolder.label.setBackgroundResource(ci.label);
        Picasso.with(context).load(ci.img).transform(new RoundedTransformation(4, 0)).fit().into(contactViewHolder.img);
    }

    @Override
    public CollectViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i)
    {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                Intent intent = new Intent(v.getContext(), Trip_mdsign.class);
                intent.putExtra("key",contactList.get(i).getID());
                intent.putExtra("label","CollectAdapter");
                context.startActivity(intent);
            }
        });

        return new CollectViewHolder(itemView);
    }

    public static class CollectViewHolder extends RecyclerView.ViewHolder
    {
        TextView txt;
        TextView time;
        ImageView img;
        CardView card;
        ImageView label;

        public CollectViewHolder(View v)
        {
            super(v);
            txt = (TextView) v.findViewById(R.id.placeName);
            img = (ImageView) v.findViewById(R.id.placePic);
            card = (CardView) v.findViewById(R.id.card_view);
            time = (TextView) v.findViewById(R.id.addTime);
            label = (ImageView) v.findViewById(R.id.circleView);
            card.setPreventCornerOverlap(false);
        }
    }
}