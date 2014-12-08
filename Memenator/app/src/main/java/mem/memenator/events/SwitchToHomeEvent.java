package mem.memenator.events;

import android.graphics.Bitmap;

/**
 * Event for switching to editor, examples : after choosing sample to edit, photo form gallery or
 * just made photo
 */
public class SwitchToHomeEvent {
    private Bitmap bitmap;

    public SwitchToHomeEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
