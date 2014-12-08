package mem.memenator.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import mem.memenator.R;
import mem.memenator.events.SwitchToHomeEvent;

/**
 * Fragment shown after left navigation select gallery
 */
public class GalleryFragment extends Fragment {
    private boolean allImages=true;
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private boolean oneselect;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    private Context context;

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
        if(!isExternalStorageReadable() || !isExternalStorageWritable())
        {
            return rootView;
        }
        else if(!allImages){
            String tmp = getResources().getText(R.string.album_name).toString();
            File file = getAlbumStorageDir(tmp);
            uri = Uri.parse(Uri.encode(file.getAbsolutePath()));
        }
        Cursor imagecursor = context.getContentResolver().query(
                uri, columns, null,
                null, orderBy);
        if (imagecursor == null) {
            return rootView;
        }
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[this.count];
        this.arrPath = new String[this.count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
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
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                List<Bitmap> selectedImages = new LinkedList<Bitmap>();
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                        selectedImages.add(thumbnails[i]);
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(context,
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,
                            "You've selected Total " + cnt + " image(s).",
                            Toast.LENGTH_LONG).show();
                    Log.d("SelectedImages", selectImages);
                    // Toast = Window.Alert();
                    EventBus.getDefault().post(new SwitchToHomeEvent(selectedImages.get(0)));
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

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath(), String.valueOf(albumName));
        if (!file.mkdirs()) {
            Log.e("Memenator", "Directory not created");
        }
        return file;
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
            ViewHolder holder;
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
                        oneselect = false;
                    } else if((allImages && !oneselect)||(!allImages))
                        {
                            cb.setChecked(true);
                            thumbnailsselection[id] = true;
                            oneselect = true;
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
            holder.imageview.setImageBitmap(thumbnails[position]);
            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }
}

