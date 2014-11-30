package nl.droidcon.conference2014.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Simple abstract class used by an Adapter to inflate a view item.
 * By moving the code from {@link android.widget.BaseAdapter#getView(int,
 * android.view.View, android.view.ViewGroup)} to these subclasses we keep
 * the source code extensible and readable.
 *
 * @author Arnaud Camus
 */
abstract public class ItemInflater<T> {
    protected Context mContext;

    public ItemInflater(Context ctx) {
        mContext = ctx;
    }

    /**
     * Method usually called by an Adapter to inflate a view for the object of type T
     * @param object the object of the list collection to render
     * @param position the position of the item in the {@link android.widget.AbsListView}
     * @param convertView the view to reused
     * @param parent the view parent
     * @return the inflated or reused view rendered
     */
    public abstract View getView(T object, final int position, View convertView, ViewGroup parent);
}
