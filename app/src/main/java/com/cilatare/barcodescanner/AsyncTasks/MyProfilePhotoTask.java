package com.cilatare.barcodescanner.AsyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by LightSpark on 7/26/2016.
 */
public class MyProfilePhotoTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView photoImageView;

    public MyProfilePhotoTask(ImageView imageView) {
        photoImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap circleBitmap = null;
        try {
            URL myURL = new URL(params[0]);
            InputStream inputStream = (InputStream) myURL.getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);

            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return circleBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            photoImageView.setImageBitmap(bitmap);
        }
    }
}
