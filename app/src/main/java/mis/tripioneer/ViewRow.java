package mis.tripioneer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


/**
 * Created by user on 2015/7/16.
 */
public class ViewRow {

    // This is a reference to the layout we defined above
    public static final int LAYOUT = R.layout.list_single;

    private Context context;
    private TextView textView_header;
    private TextView textView_title;
    private TextView textView_info;
    private ImageView imageView;

    public ViewRow(Context context, View convertView)
    {
        this.context = context;
        this.imageView = (ImageView) convertView.findViewById(R.id.img);
        this.textView_title = (TextView) convertView.findViewById(R.id.txt);
        this.textView_info = (TextView) convertView.findViewById(R.id.info);
    }

    public ViewRow(Context contxt, View converView, boolean isHeader)
    {
        this.context = contxt ;
        this.textView_header = (TextView) converView.findViewById(R.id.textSeparator);
    }

    public void bind(ViewModel Model)
    {
        Log.d("ViewRow","bind");
        this.textView_title.setText(Model.getTitle());
        this.textView_info.setText(Model.getInfo());
        Picasso.with(this.context).load(Model.getImageUrl()).into(this.imageView);
    }

    public void bind_header(ViewModel Model)
    {
        Log.d("ViewRow","bind_header");
        this.textView_header.setText(Model.getSectionHeader());
    }
}
