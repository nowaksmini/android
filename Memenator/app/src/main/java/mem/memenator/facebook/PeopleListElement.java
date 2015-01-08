package mem.memenator.facebook;

import android.view.View;

/**
 * Created by Sylwia.Nowak on 2015-01-07.
 */
public class PeopleListElement extends BaseListElement {

    public PeopleListElement(int requestCode) {
        super(null, null, null, requestCode); ///////
    }

    @Override
    protected View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do nothing for now
            }
        };
    }
}
