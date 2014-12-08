package mem.memenator.events;

import android.graphics.Bitmap;

/**
 * Event for switching to editor, examples : after choosing sample to edit, photo form gallery or
 * just made photo
 */
public class SwitchToEditorEvent {
    private Bitmap bitmap;

    public SwitchToEditorEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
