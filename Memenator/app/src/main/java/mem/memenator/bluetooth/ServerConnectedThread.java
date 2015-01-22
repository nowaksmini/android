package mem.memenator.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Sends And receives Image
 */
public class ServerConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ServerConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    private int GetArraySize(byte[] bytes)
    {
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        return wrapper.getInt();
    }
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        int ByteArraySize=0;
        int TotalBytes=0;
        ArrayList<Byte> tmp= new ArrayList<Byte>();
        try {
            // Read from the InputStream
            bytes = mmInStream.read(buffer);
            // Send the obtained bytes to the UI activity
            ByteArraySize = GetArraySize(buffer);

        } catch (IOException e) {
            return;
        }
        // Keep listening to the InputStream until an exception occurs
        while (TotalBytes<ByteArraySize) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                TotalBytes+=bytes;
                for(int i = 0; i < bytes; i++)
                {
                    tmp.add(buffer[i]);
                }
                // Send the obtained bytes to the UI activity

            } catch (IOException e) {
                break;
            }
        }
        MemenatorBluetooth.ImageReceived(this,tmp.toArray());
    }
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
