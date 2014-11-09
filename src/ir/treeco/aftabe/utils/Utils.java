package ir.treeco.aftabe.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ir.treeco.aftabe.packages.NotificationProgressListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

/**
 * Created by hamed on 8/12/14.
 */
public class Utils {
    public static String SHARED_PREFRENCES_TAG = "aftabe_plus";

    public static int[] intListToArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for(int i=0; i<list.size(); ++i)
            arr[i] = list.get(i);
        return arr;
    }

    public static float[] floatListToArray(List<Float> list) {
        String str = list.toString();
        String[] items = str.replaceAll("\\[", "").replaceAll("\\]", "").split(",");

        float[] results = new float[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Float.parseFloat(items[i]);
            } catch (NumberFormatException nfe) {};
        }
        return results;
    }

    public static String getAESkey(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String str = telephonyManager.getDeviceId();
        if(str==null)
            return "1234567812345678"; // for users without deviceID
        byte[] key = new byte[16];
        byte[] strBytes = str.getBytes();
        int i=0;
        while (i<16 && i<strBytes.length)
            key[i] = strBytes[i++];
        while (i<16)
            key[i++] = 100;
        return new String(key);
    }

    public static void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
    }

    public static InputStream getInputStreamFromRaw(Context context, String name, String type) {
        return context.getResources().openRawResource(
                context.getResources().getIdentifier("raw/" + name,
                        type, context.getPackageName()));
    }

    public static void download(Context context, String url, String path) throws Exception {
        download(context, url, path, null);
    }

    public static void download(final Context context, final String url, final String path, final NotificationProgressListener listener) {
        /**
         *
         *          THIS method should overwrite the existing FILE.
         *                      BUT does IT???????
         *
         */
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conection = new URL(url).openConnection();
                    conection.connect();
                    int lenghtOfFile = conection.getContentLength();
                    InputStream is = new URL(url).openStream();
                    OutputStream os = context.openFileOutput(path, 0);
                    pipe(is, os, listener, lenghtOfFile);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener!=null)
                        listener.failure();
                }
            }
        }).start();
    }

    public static void pipe(InputStream is, OutputStream os) {
        pipe(is, os, null, 0);
    }

    public static void pipe(InputStream is, OutputStream os, NotificationProgressListener listener, int size) {
        int n;
        byte[] buffer = new byte[1024];
        int sum=0;
        try {
            while ((n = is.read(buffer)) > -1) {
                sum += n;
                if (listener != null)
                listener.updateBar((sum * 100) / size);
                os.write(buffer, 0, n);   // Don't allow any extra bytes to creep in, final write
            }
            os.close();
            if(listener!=null)
                listener.success();
        } catch (IOException e) {
            if(listener!=null)
                listener.failure();
        }
    }

    public static void reverseLinearLayout(LinearLayout linearLayout) {
        View views[] = new View[linearLayout.getChildCount()];
        for (int i = 0; i < views.length; i++)
            views[i] = linearLayout.getChildAt(i);
        linearLayout.removeAllViews();
        for (int i = views.length - 1; i >= 0; i--)
            linearLayout.addView(views[i]);
    }

    public static View makeNewSpace(Context context) {
        View space = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        space.setLayoutParams(layoutParams);
        return space;
    }


    public static String sharedPrefrencesTag() {
        return "GOLVAZHE_XEJDIWE";
    }

    public static void resizeView(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width != layoutParams.width || height != layoutParams.height) {
            layoutParams.width = width;
            layoutParams.height = height;
        }
    }


    public static Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // Convert from Color to HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);
                int alpha = Color.alpha(mapSrcColor[index]);

                // Adjust HSV
                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] += 360;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] -= 360.0f;
                }

                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] = 0.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] = 1.0f;
                }

                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }

                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(alpha, pixelHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);

    }

    public static int[] getRandomOrder(int length, Random random) {
        if (random == null)
            random = new Random();
        int order[] = new int[length];
        for (int i = 0; i < length; i++)
            order[i] = i;
        for (int i = 1; i < length; i++) {
            int j = random.nextInt(i);
            int tmp = order[i];
            order[i] = order[j];
            order[j] = tmp;
        }
        return order;
    }


    public static void setViewBackground(View view, Drawable dialogDrawable) {
        if (Build.VERSION.SDK_INT >= 16)
            view.setBackground(dialogDrawable);
        else
            view.setBackgroundDrawable(dialogDrawable);
    }

    static String persianDigits = "۰۱۲۳۴۵۶۷۸۹";

    public static String numeralStringToPersianDigits(String s) {
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++)
            result[i] = persianDigits.charAt(s.charAt(i) - '0');
        return new String(result);
    }

    public static String[] stringListToArray(List<String> list) {
        String[] arr = new String[list.size()];
        for(int i=0; i<list.size(); ++i)
            arr[i] = list.get(i);
        return arr;
    }
}
