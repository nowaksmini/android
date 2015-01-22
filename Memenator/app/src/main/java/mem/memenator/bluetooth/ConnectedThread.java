package mem.memenator.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Sends And receives Image
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    private int GetArraySize(byte[] bytes) {
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        return (int) wrapper.getShort();
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {

        ByteBuffer B = ByteBuffer.allocate(8);
        B.putInt(bytes.length);

        try {
            mmOutStream.write(B.array());
        } catch (IOException e) {
        }
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
