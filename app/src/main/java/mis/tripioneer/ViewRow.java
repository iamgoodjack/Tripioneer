package mis.tripioneer;

import android.content.Context;
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

    private final Context context;
    private final TextView textView;
    private final ImageView imageView;

    public ViewRow(Context context, View convertView) {
        this.context = context;
        this.imageView = (ImageView) convertView.findViewById(R.id.img);
        this.textView = (TextView) convertView.findViewById(R.id.txt);
    }

    public void bind(ViewModel exampleViewModel) {
        this.textView.setText(exampleViewModel.getText());
        Picasso.with(this.context).load(exampleViewModel.getImageUrl()).into(this.imageView);
    }
}
