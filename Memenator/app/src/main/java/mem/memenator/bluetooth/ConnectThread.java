package mem.memenator.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Connects asynchronously with other devices
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Bitmap image;

    public ConnectThread(BluetoothDevice device, BluetoothSocket socket, Bitmap image) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        mmDevice = device;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        mmSocket = socket;
        this.image = image;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        MemenatorBluetooth.CancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }
        // Do work to manage the connection (in a separate thread)
        ConnectedThread CT = new ConnectedThread(mmSocket);
        if (image != null) {
            MemenatorBluetooth.makeToast("Sending image...");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            CT.write(byteArray);
            MemenatorBluetooth.ImageSent(CT);
        }
    }
}