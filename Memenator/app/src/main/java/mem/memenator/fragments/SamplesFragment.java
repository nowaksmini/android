package mem.memenator.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import mem.memenator.MainActivity;
import mem.memenator.R;
import mem.memenator.adapters.PicAdapter;

/**
 * Fragment for viewing samples from application
 * There is possibility to chose sample and edit it in own way
 */
public class SamplesFragment extends Fragment {
    //gallery object
    private Gallery picGallery;
    //image view for larger display
    private ImageView picView;
    private PicAdapter imgAdapt;
    private View rootView;
    private Bitmap pic;
    public SamplesFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        rootView = inflater.inflate(R.layout.samples_fragment, container, false);
        //get the large image view
        picView = (ImageView) rootView.findViewById(R.id.picture);
        //get the gallery view
        picGallery = (Gallery) rootView.findViewById(R.id.gallery);//create a new adapter
        imgAdapt = new PicAdapter(rootView.getContext());
        //set the gallery adapter
        picGallery.setAdapter(imgAdapt);
        //display the newly selected image at larger size
//        picView.setImageBitmap(pic);
        //scale options
        picView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //set the click listener for each item in the thumbnail gallery
        picGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //handle clicks
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //set the larger image view to display the chosen bitmap calling method of adapter class
                picView.setImageBitmap(imgAdapt.getPic(position));
            }
        });
        Button selectImage = (Button) rootView.findViewById(R.id.selectSampleBtn);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.editedPicture = imgAdapt.getPic(picGallery.getSelectedItemPosition());
                Toast.makeText(rootView.getContext(),rootView.getResources().getString(R.string.successfully_selected_sample),Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
}