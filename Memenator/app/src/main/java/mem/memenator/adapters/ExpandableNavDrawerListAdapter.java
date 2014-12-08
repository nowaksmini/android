package mem.memenator.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mem.memenator.R;
import mem.memenator.model.NavDrawerItem;

/**
 * Thanks to adapter we can show items in list view
 * Created for right navigation drawer
 */

public class ExpandableNavDrawerListAdapter extends BaseExpandableListAdapter {

    // Context = Interface to global information about an application environment.
    // This is an abstract class whose implementation is provided by the Android system.
    // It allows access to application-specific resources and classes, as well as up-calls for application-level
    // operations such as launching activities, broadcasting and receiving intents, etc.

    private Context context;
    private List<NavDrawerItem> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<NavDrawerItem, List<NavDrawerItem>> listDataChild;

    public ExpandableNavDrawerListAdapter(Context context, List<NavDrawerItem> listDataHeader,
                                          HashMap<NavDrawerItem, List<NavDrawerItem>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final NavDrawerItem childObject = (NavDrawerItem) getChild(groupPosition, childPosition);
        if (convertView == null) {
            // Instantiates a layout XML file into its corresponding View objects.
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.right_lower_drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(childObject.getIcon());
        txtTitle.setText(childObject.getTitle());

        // displaying count
        // check whether it set visible or not
        if(childObject.getCounterVisibility()){
            txtCount.setText(childObject.getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.listDataChild == null || this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null) {
            return 0;
        }
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        NavDrawerItem header = (NavDrawerItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.right_drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(header.getIcon());
        txtTitle.setText(header.getTitle());

        // displaying count
        // check whether it set visible or not
        if(header.getCounterVisibility()){
            txtCount.setText(header.getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
