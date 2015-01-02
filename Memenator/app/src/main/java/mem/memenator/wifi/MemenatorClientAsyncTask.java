package mem.memenator.wifi;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class MemenatorClientAsyncTask extends AsyncTask {
    Bitmap image;
    String host;
    public MemenatorClientAsyncTask(Bitmap imageToSend, String deviceAddress) {
        host=deviceAddress;
        image = imageToSend;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Socket socket = new Socket();
        try {
            socket.bind(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect(new InetSocketAddress(host,8888),500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream output = null;
        try {
            output=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image.compress(Bitmap.CompressFormat.PNG,100,output);

        try {
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
