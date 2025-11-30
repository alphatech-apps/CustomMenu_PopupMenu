package com.jakir.custommanuandpopupmenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageHelper {

    private Context context;

    public ImageHelper(Context context) {
        this.context = context;
    }

    // Callback interface
    public interface ImageDownloadCallback {
        void onSuccess(Bitmap bitmap, File savedFile);
     }

    public void downloadAndSaveImage(String imageUrl, ImageDownloadCallback callback) {
        new DownloadImageTask(callback).execute(imageUrl);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageDownloadCallback callback;
        private Exception exception;
        private File savedFile;

        public DownloadImageTask(ImageDownloadCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlString = urls[0];
            Bitmap bitmap = null;
            try {
                // Download image
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

                // Save to internal storage
                savedFile = new File(context.getFilesDir(), "account_pic.png");
                FileOutputStream out = new FileOutputStream(savedFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                exception = e;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (callback != null) {
                if (bitmap != null && exception == null) {
                    callback.onSuccess(bitmap, savedFile);
                } else {
                }
            }
        }
    }
}
