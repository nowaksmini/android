package mem.memenator.wifi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import mem.memenator.fragments.FindPeopleFragment;

/**
 * Async Task for getting image
 */
public class MemenatorServerAsyncTask extends AsyncTask {
    FindPeopleFragment fragment;

    public MemenatorServerAsyncTask(FindPeopleFragment _Fragment)
    {
        fragment=_Fragment;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        ServerSocket socket = null;
        try {
           socket = new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket client =null;
        try {
            client = socket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream=null;
        try {
             inputStream = client.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap img = BitmapFactory.decodeStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fragment.addImageToList(img);
        return img;
    }

}
