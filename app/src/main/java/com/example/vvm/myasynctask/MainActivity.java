package com.example.vvm.myasynctask;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends ActionBarActivity {

    private static final String IMAGE_URL = "http://eastbancgroup.com/images/ebtLogo.gif";

    Button button;
    TextView textView;
    ImageView imageView;
    ProgressDialog progressDialog;
    DownloadTask downloadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

//        button = (Button) findViewById(R.id.button);
//        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);

        downloadTask = new DownloadTask();
        downloadTask.execute(IMAGE_URL);
    }

    @Override
    protected void onStop() {
        downloadTask.cancel(true);
        super.onStop();
    }

    private class DownloadTask extends AsyncTask<String, Integer, Drawable> {

        private int progressBarValue = 0;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Downloading");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            imageView.setImageDrawable(drawable);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
        }

        @Override
        protected Drawable doInBackground(String... params) {
            int count;
            try {
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream("/sdcard/newfile.jpg");

                byte[] data = new byte[256];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        return null;
                    }
                    total += count;
                    output.write(data, 0, count);
                    publishProgress((int) ((total * 100) / lengthOfFile));
                }

                output.flush();
                output.close();
                input.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String imagePath = Environment.getExternalStorageDirectory().toString() + "/newfile.jpg";
            return Drawable.createFromPath(imagePath);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
