package mem.memenator.options_fragments;

/**
 * Fragment for finding people in neighbourhood by wi-fi connection
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mem.memenator.MainActivity;
import mem.memenator.R;
import mem.memenator.adapters.PicAdapter;
import mem.memenator.wifi.MemenatorClientAsyncTask;
import mem.memenator.wifi.MemenatorServerAsyncTask;
import mem.memenator.wifi.WiFiDirectBroadcastReceiver;

/**
 * Fragment shown after left navigation select find people's images shared
 */
public class FindPeopleFragment extends Fragment {
    private WifiP2pDeviceList deviceList;
    private List<Bitmap> receivedImages = new LinkedList<Bitmap>();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private boolean isListening;
    //gallery object
    private Gallery picGallery;
    //image view for larger display
    private ImageView picView;
    private PicAdapter imgAdapt;
    private View rootView;

    public FindPeopleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        rootView = inflater.inflate(R.layout.find_people_fragment, container, false);
        //get the large image view
        picView = (ImageView) rootView.findViewById(R.id.pictureFindFriends);
        //get the gallery view
        picGallery = (Gallery) rootView.findViewById(R.id.galleryReceived);//create a new adapter
        this.generateView();
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
        Button selectImage = (Button) rootView.findViewById(R.id.selectReceivedBtn);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.editedPicture = imgAdapt.getPic(picGallery.getSelectedItemPosition());
                } catch (Exception e) {
                }
                Toast.makeText(rootView.getContext(), rootView.getResources().getString(R.string.successfully_selected_sample), Toast.LENGTH_LONG).show();
            }
        });
        Button receiveBtn = (Button) rootView.findViewById(R.id.receiveBtn);
        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen(view);
            }
        });
        Button sendBtn = (Button) rootView.findViewById(R.id.sendToWorldBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(view);
            }
        });
        // Wifi connection part
        mManager = (WifiP2pManager) rootView.getContext().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(rootView.getContext(), rootView.getContext().getMainLooper(), new
                WifiP2pManager.ChannelListener() {
                    @Override
                    public void onChannelDisconnected() {
                        // just for test breaking
                    }
                });
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        return rootView;
    }

    public boolean getListenerState() // is a sender or a receiver
    {
        return isListening;
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
        rootView.getContext().registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    public void onPause() {
        super.onPause();
        rootView.getContext().unregisterReceiver(mReceiver);
    }

    private void DoAndConnect(Boolean listener) {
        if (listener) {
            for (WifiP2pDevice device : deviceList.getDeviceList()) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                final List<MemenatorServerAsyncTask> Tasks = new ArrayList<MemenatorServerAsyncTask>();
                final FindPeopleFragment fragment = this;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        //success logic
                        Tasks.add(new MemenatorServerAsyncTask(fragment));
                    }

                    @Override
                    public void onFailure(int reason) {
                        //failure logic
                    }
                });
            }
        } else {
            for (WifiP2pDevice device : deviceList.getDeviceList()) {
                final WifiP2pDevice deviceInfo = device;
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                final List<MemenatorClientAsyncTask> Tasks = new ArrayList<MemenatorClientAsyncTask>();
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        //success logic
                        Tasks.add(new MemenatorClientAsyncTask(MainActivity.editedPicture, deviceInfo.deviceAddress));
                    }

                    @Override
                    public void onFailure(int reason) {
                        //failure logic
                    }
                });
                for (int i = 0; i < Tasks.size(); i++) {
                    Tasks.get(i).execute(new Object[1]);
                }
            }
        }
    }

    public void listen(View viev) {
        isListening = true;
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mChannel.toString();
                Toast.makeText(rootView.getContext(), rootView.getResources().getString(R.string.connected_wifi), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                mChannel.toString();
                Toast.makeText(rootView.getContext(), rootView.getResources().getString(R.string.not_connected_wifi) + reasonCode, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void send(View view) {
        isListening = false;
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(rootView.getContext(), rootView.getResources().getString(R.string.connected_wifi), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(rootView.getContext(), rootView.getResources().getString(R.string.not_connected_wifi) + reasonCode, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setDeviceList(WifiP2pDeviceList deviceList, Boolean listener) {
        this.deviceList = deviceList;
        Toast.makeText(rootView.getContext(), deviceList.toString(), Toast.LENGTH_LONG);
        DoAndConnect(listener);
    }

    public void addImageToList(Bitmap bitmap) {
        receivedImages.add(bitmap);
        generateView();
        // make list on view actual
    }
}