package mem.memenator.options_fragments;

/**
 * Fragment for sending and receiving images via bluetooth
 */


import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import mem.memenator.MainActivity;
import mem.memenator.R;
import mem.memenator.adapters.PicAdapter;
import mem.memenator.bluetooth.MemenatorBluetooth;

/**
 * Fragment shown to share image by bluetooth
 */
public class SendByBluetoothFragment extends Fragment {
    public static final int TIMEOUT = 5;
    public static final int MILLIS = 4000;
    private WifiP2pDeviceList deviceList;
    private List<Bitmap> receivedImages = new LinkedList<Bitmap>();
    private boolean isListening;
    //gallery object
    private Gallery picGallery;
    //image view for larger display
    private ImageView picView;
    public static PicAdapter imgAdapt;
    private View rootView;


    public SendByBluetoothFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        rootView = inflater.inflate(R.layout.send_by_bluetooth_fragment, container, false);
        //get the large image view
        picView = (ImageView) rootView.findViewById(R.id.pictureFindFriends);
        //get the gallery view
        picGallery = (Gallery) rootView.findViewById(R.id.galleryReceived);//create a new adapter
        this.generateView();
        //scale options
        imgAdapt = new PicAdapter(rootView.getContext());
        picView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //set the click listener for each item in the thumbnail gallery
        picGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //handle clicks
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //set the larger image view to display the chosen bitmap calling method of adapter class
                picView.setImageBitmap(imgAdapt.getPic(position));
            }
        });
        Button selectImage = (Button) rootView.findViewById(R.id.selectReceivedBtn);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.editedPicture = imgAdapt.getPic(picGallery.getSelectedItemPosition());
                    Toast.makeText(rootView.getContext(), rootView.getResources().getString
                            (R.string.successfully_selected_receive_image), Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                } catch (Exception e) {
                    Log.e("Cannot choose received image", e.getMessage());
                }
            }
        });
        Button receiveBtn = (Button) rootView.findViewById(R.id.receiveBtn);
        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MemenatorBluetooth.isInitialized())
                    if (!MemenatorBluetooth.Initialize(((MainActivity) getActivity()))) {
                        Toast.makeText(rootView.getContext(), "Bluetooth is not supported by your device", Toast.LENGTH_LONG).show();
                        return;
                    }

                if (!MemenatorBluetooth.isConnectionEstablished())
                    MemenatorBluetooth.EstablishConnection();
                int t = TIMEOUT;
                while (!MemenatorBluetooth.isConnectionEstablished()) {
                    try {
                        Thread.sleep(MILLIS, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    t--;
                    if (t == 0) {
                        Toast.makeText(rootView.getContext(), "Device not found", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                MemenatorBluetooth.SendPicture(MainActivity.editedPicture);
                MemenatorBluetooth.ReceivePictures();
            }
        });
        Button sendBtn = (Button) rootView.findViewById(R.id.sendToWorldBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MemenatorBluetooth.isInitialized())
                    if (!MemenatorBluetooth.Initialize(((MainActivity) getActivity()))) {
                        Toast.makeText(rootView.getContext(), "Bluetooth is not supported by your device", Toast.LENGTH_LONG).show();
                        return;
                    }
                if (!MemenatorBluetooth.isConnectionEstablished())
                    MemenatorBluetooth.EstablishConnection();
                int t = TIMEOUT;
                while (!MemenatorBluetooth.isConnectionEstablished()) {
                    try {
                        Thread.sleep(MILLIS, 0);
                    } catch (InterruptedException e) {
                        Log.e("Problems with connection", e.getMessage());
                    }
                    t--;
                    if (t == 0) {
                        Toast.makeText(rootView.getContext(), "Device not found", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                MemenatorBluetooth.SendPicture(MainActivity.editedPicture);
            }
        });
        return rootView;
    }

    public void generateView() {
        imgAdapt = new PicAdapter(receivedImages, rootView.getContext());
        //set the gallery adapter
        picGallery.setAdapter(imgAdapt);
        //display the newly selected image at larger size
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
    }

    /* unregister the broadcast receiver */
    @Override
    public void onPause() {
        super.onPause();
    }

    public void addImageToList(Bitmap bitmap) {
        receivedImages.add(bitmap);
        generateView();
        // make list on view actual
    }
}