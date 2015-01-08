package mem.memenator.facebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mem.memenator.R;

/**
 * Created by Sylwia.Nowak on 2015-01-07.
 */
public class ActionListAdapter extends ArrayAdapter<BaseListElement> {
    private List<BaseListElement> listElements;

    public ActionListAdapter(Context context, int resourceId,
                             List<BaseListElement> listElements) {
        super(context, resourceId, listElements);
        this.listElements = listElements;
        // Set up as an observer for list item changes to
        // refresh the view.
        for (int i = 0; i < listElements.size(); i++) {
            listElements.get(i).setAdapter(this);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) view.getContext() ////////
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.some_item, null);
        }

        BaseListElement listElement = listElements.get(position);
        if (listElement != null) {
            view.setOnClickListener(listElement.getOnClickListener());
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            if (icon != null) {
                icon.setImageDrawable(listElement.getIcon());
            }
            if (text1 != null) {
                text1.setText(listElement.getText1());
            }
            if (text2 != null) {
                text2.setText(listElement.getText2());
            }
        }
        return view;
    }

}
