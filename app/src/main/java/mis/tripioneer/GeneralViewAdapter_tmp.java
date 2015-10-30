package mis.tripioneer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Insertable;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/7/16.
 */
public class GeneralViewAdapter_tmp extends BaseAdapter implements Swappable, UndoAdapter
{

    private final List<ViewModel> viewModels;
    private final Context context;
    private final LayoutInflater inflater;

    public GeneralViewAdapter_tmp(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = new ArrayList<ViewModel>();
    }

    public GeneralViewAdapter_tmp(Context context, List<ViewModel> viewModels) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
    }

    public List<ViewModel> viewmodels() {
        return this.viewModels;
    }

    @Override
    public int getCount() {
        return this.viewModels.size();
    }

    @Override
    public ViewModel getItem(int position) {
        return this.viewModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        // We only need to implement this if we have multiple rows with a different layout. All your rows use the same layout so we can just return 0.
        //return position;
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // We get the view model for this position
        final ViewModel viewModel = getItem(position);

        ViewRow row;
        // If the convertView is null we need to create it
        if(convertView == null) {
            convertView = this.inflater.inflate(ViewRow.LAYOUT, parent, false);
            // In that case we also need to create a new row and attach it to the newly created View
            row = new ViewRow(this.context, convertView,position);
            convertView.setTag(row);
        }

        // After that we get the row associated with this View and bind the view model to it
        row = (ViewRow) convertView.getTag();
        row.bind(viewModel);

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public void swapItems(final int positionOne, final int positionTwo) {
        ViewModel model;
        if ( this instanceof Swappable) {
            model = this.getItem(positionOne);
            viewModels.set(positionOne,viewModels.get(positionTwo));
            viewModels.set(positionTwo,model);
            this.notifyDataSetChanged();
        } else {
            Log.w("ListViewAnimations", "Warning: swapItems called on an adapter that does not implement Swappable!");
        }
        ((Trip_mdsign)context).swap(viewModels);
    }

    public void remove(int position)
    {
        viewModels.remove(position);
        ((Trip_mdsign)context).swap(viewModels);
        this.notifyDataSetChanged();
    }

    public View getUndoView(int i, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = getUndoView(parent);
        }
        return convertView;
    }

    private View getUndoView(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.undo_layout, parent, false);
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull View view) {
        return view.findViewById(R.id.undoButton);
    }

}
