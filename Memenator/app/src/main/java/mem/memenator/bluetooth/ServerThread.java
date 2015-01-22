package mem.memenator.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Connects asynchronously with other devices
 */
public class ServerThread extends Thread {
    private final BluetoothServerSocket mmSocket;
    private final BluetoothDevice mmDevice;


    public ServerThread(BluetoothDevice device, BluetoothServerSocket socket) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        mmDevice = device;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        mmSocket = socket;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        MemenatorBluetooth.CancelDiscovery();
        BluetoothSocket tmpSocket;
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            tmpSocket = mmSocket.accept();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }
        // Do work to manage the connection (in a separate thread)
        ServerConnectedThread CT = new ServerConnectedThread(tmpSocket);
        CT.run();
    }
}