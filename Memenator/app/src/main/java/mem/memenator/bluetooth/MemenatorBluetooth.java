package mem.memenator.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import mem.memenator.MainActivity;
import mem.memenator.R;

/**
 * Makes connection via bluetooth, sends and receives images
 */
public class MemenatorBluetooth {
    private static UUID mUUID;
    static private BluetoothAdapter mBluetoothAdapter;
    public static MainActivity mainActivity;
    private static ServerThread sThread;

    static public boolean isInitialized() {
        return (mBluetoothAdapter != null);
    }

    static public boolean Initialize(MainActivity mem) {
        mainActivity = mem;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        String mUUIDCode = mem.getResources().getString(R.string.uuid);
        mUUID = UUID.fromString(mUUIDCode);
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        mem.startActivity(discoverableIntent);
        if(!mBluetoothAdapter.isEnabled())
        {
            try {
                Thread.sleep(2000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!mBluetoothAdapter.isEnabled())
            {
                mBluetoothAdapter=null;
                return false;
            }
        }
        return true;
    }

    static public void CancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    static public void StartDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    static private BluetoothDevice mPartner;

    public static void EstablishPair(BluetoothDevice device) {
        mPartner = device;
    }

    static public boolean isConnectionEstablished() {
        return (mPartner != null);
    }

    static public void SendPicture(Bitmap B) {
        BluetoothSocket mSocket;
        try {
            mSocket = mPartner.createRfcommSocketToServiceRecord(mUUID);
        } catch (IOException e) {
            return;
        }
        cThread = new ConnectThread(mPartner, mSocket, B);
        cThread.run();
    }

    static public void ReceivePictures() {
        BluetoothServerSocket mSocket = null;
        try {
            mSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Memenator", mUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sThread = new ServerThread(mPartner,mSocket);
        sThread.run();
    }

    static ConnectThread cThread;

    // if iamge was successfully sent
    static public void ImageSent(ConnectedThread CT) {
        CT.cancel();
        Toast.makeText(mainActivity.getApplicationContext(), "Image from home successfully send ", Toast.LENGTH_LONG);
    }

    static public void ImageReceived(ServerConnectedThread CT, Object[] image) {
        byte[] B = new byte[image.length];
        for (int i = 0; i < B.length; i++) B[i] = (Byte) image[i];
        mainActivity.ChangePicture(BitmapFactory.decodeByteArray(B, 0, image.length));
        CT.cancel();
//call the main window with the image to show
    }

    static public void EstablishConnection() {


// Create a BroadcastReceiver for ACTION_FOUND
        BroadcastReceiver mReceiver;
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    MemenatorBluetooth.CancelDiscovery();
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    EstablishPair(device);
                }
            }

        };

// Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mainActivity.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        StartDiscovery();
    }
}

