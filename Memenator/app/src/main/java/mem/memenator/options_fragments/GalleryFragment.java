package mem.memenator.options_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import mem.memenator.MainActivity;
import mem.memenator.R;


/**
 * Fragment shown after left navigation select gallery
 */
public class GalleryFragment extends Fragment {
    private boolean allImages=false;
    private int count;
    private Bitmap[] thumbnails;
    private Boolean[] thumbnailsselection;
    private boolean oneSelect;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    private Context context;
    private String savePath;


    @SuppressLint("ValidFragment")
    public GalleryFragment(boolean allImages){
        this.allImages = allImages;
    }
    public GalleryFragment (){}

    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        context = rootView.getContext();
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        savePath = uri.getPath();
        if(!isExternalStorageReadable() || !isExternalStorageWritable())
        {
            return rootView;
        }
        else if(!allImages){
         //   SharedPreferences settings = rootView.getContext().getSharedPreferences(getResources().getString(R.string.shared_preferences_file), 0);
           // savePath = settings.getString(getResources().getString(R.string.path_key), "");
          //  savePath = Environment.getExternalStoragePublicDirectory(savePath).getPath();
            savePath="MemenatorMyMeme";
        }
        if(allImages) savePath = "";
        final String[] selectionArgs = {"%" +  savePath + "%"};
        final String selection = MediaStore.Images.ImageColumns.TITLE+ " like ?";
        Cursor imagecursor = context.getContentResolver().query(uri, columns,selection,selectionArgs, orderBy);
        if (imagecursor == null) {
            ((Button) rootView.findViewById(R.id.selectBtn)).setVisibility(View.INVISIBLE);
            return rootView;
        }
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[this.count];
        this.arrPath = new String[this.count];
        this.thumbnailsselection = new Boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            thumbnailsselection[i] = false;
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                    context.getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MICRO_KIND, null); // KIND if like size
            arrPath[i]= imagecursor.getString(dataColumnIndex);
        }

        GridView imagegrid = (GridView) rootView.findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imagecursor.close();
        final Button selectBtn = (Button) rootView.findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = thumbnails.length;
                int cnt = 0;
                String selectImages = "";
                List<String> selectedImagesPath = new LinkedList<String>();
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectedImagesPath.add(arrPath[i]);
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(context,
                            getResources().getString(R.string.select_at_least_one_photo),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,getResources().getString(R.string.selected_images_prefix)
                                    + cnt + getResources().getString(R.string.selected_images_suffix),
                            Toast.LENGTH_LONG).show();
                    Log.d("SelectedImages", selectImages);
                    // Toast = Window.Alert();
                    MainActivity.pictureToEditPath = selectedImagesPath.get(0);
                    MainActivity.editedPicture = null;

                }
            }
        });
        return rootView;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        // ViewGroup = base class for layouts and containers, can storage views
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.gallery_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]){
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        oneSelect = false;
                    } else if((allImages && !oneSelect)||(!allImages))
                        {
                            cb.setChecked(true);
                            thumbnailsselection[id] = true;
                            oneSelect = true;
                        }
                    else {
                        cb.setChecked(false);
                    }
                }
            });
            holder.imageview.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
                        startActivity(intent);
                }
            });
            if (position < thumbnailsselection.length) {
                holder.imageview.setImageBitmap(thumbnails[position]);
                holder.checkbox.setChecked(thumbnailsselection[position]);
                holder.id = position;
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }
}

