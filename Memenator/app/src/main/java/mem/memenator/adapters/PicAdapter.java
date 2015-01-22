package mem.memenator.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

import mem.memenator.R;

/**
 * Adapter for Samples
 */
public class PicAdapter extends BaseAdapter {
    //use the default gallery background image
    int defaultItemBackground;
    //gallery context
    private Context galleryContext;
    //array to store bitmaps to display
    private Bitmap[] imageBitmaps;

    public PicAdapter(Context c) {
        //instantiate context
        galleryContext = c;
        //create bitmap array
        int size = c.getResources().getIntArray(R.array.samples_icons).length;
        imageBitmaps = new Bitmap[size];
        TypedArray imgs = c.getResources().obtainTypedArray(R.array.samples_icons);
        for (int i = 0; i < imageBitmaps.length; i++) {
            Bitmap copy = BitmapFactory.decodeResource(c.getResources(), imgs.getResourceId(i, -1));
            double newHeight = c.getResources().getDimension(R.dimen.image_edited_width) * copy.getHeight() / copy.getWidth();
            copy = copy.createScaledBitmap(copy, (int) c.getResources().getDimension(R.dimen.image_edited_width), (int) (newHeight), true);
            imageBitmaps[i] = copy;
        }
        //get the styling attributes - use default Andorid system resources
        TypedArray styleAttrs = galleryContext.obtainStyledAttributes(R.styleable.PicGallery);

//get the background resource
        defaultItemBackground = styleAttrs.getResourceId(
                R.styleable.PicGallery_android_galleryItemBackground, 0);
//recycle attributes
        styleAttrs.recycle();
    }

    public PicAdapter(List<Bitmap> list, Context c) {

        this.galleryContext = c;
        imageBitmaps = list.toArray(new Bitmap[list.size()]);
        //get the styling attributes - use default Andorid system resources
        TypedArray styleAttrs = galleryContext.obtainStyledAttributes(R.styleable.PicGallery);
        //get the background resource
        defaultItemBackground = styleAttrs.getResourceId(
                R.styleable.PicGallery_android_galleryItemBackground, 0);
        //recycle attributes
        styleAttrs.recycle();
    }

    //return number of data items i.e. bitmap images
    public int getCount() {
        return imageBitmaps.length;
    }

    //return item at specified position
    public Object getItem(int position) {
        return position;
    }

    //return item ID at specified position
    public long getItemId(int position) {
        return position;
    }

    //get view specifies layout and display options for each thumbnail in the gallery
    public View getView(int position, View convertView, ViewGroup parent) {

        //create the view
        ImageView imageView = new ImageView(galleryContext);
        //specify the bitmap at this position in the array
        imageView.setImageBitmap(imageBitmaps[position]);
        //set layout options
        imageView.setLayoutParams(new Gallery.LayoutParams(300, 200));
        //scale type within view area
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //set default gallery item background
        imageView.setBackgroundResource(defaultItemBackground);
        //return the view
        return imageView;
    }

    //return bitmap at specified position for larger display
    public Bitmap getPic(int posn) {
        //return bitmap at posn index
        return imageBitmaps[posn];
    }

    public void addBitmap(Bitmap b) {
        if (imageBitmaps.length >= 4) {
            for (int i = 0; i < 3; i++) {
                imageBitmaps[i] = imageBitmaps[i + 1];
            }
            imageBitmaps[3] = b;
        } else {
            Bitmap[] pomBitmaps = new Bitmap[imageBitmaps.length];
            for (int i = 0; i < imageBitmaps.length; i++) {
                pomBitmaps[i] = imageBitmaps[i];
            }
            pomBitmaps[imageBitmaps.length] = b;
            imageBitmaps = pomBitmaps;
        }
    }
}
