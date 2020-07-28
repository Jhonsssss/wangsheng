package com.bird.rockerdome.rocker;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Bitmap工具
 *
 * @author LIKH
 */
@SuppressWarnings("unused")
public class BitmapUtil {

    /**
     * 根据资源ID获取图片
     *
     * @param context：上下文
     * @param resId：资源ID
     * @return 图片
     */
    public static Drawable getDrawableById(Context context, int resId) {
        if (context == null) {
            return null;
        }
        return context.getResources().getDrawable(resId);
    }

    /**
     * 根据资源ID获取Bitmap
     *
     * @param context：上下文
     * @param resId：资源ID
     * @return 位图
     */
    public static Bitmap getBitmapById(Context context, int resId) {
        if (context == null) {
            return null;
        }
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    /**
     * 将Bitmap转化为byte[]
     *
     * @param bitmap：位图
     * @return 字节数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            baos.flush();
            baos.close();
            return b;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将byte[]转化为Bitmap
     *
     * @param b：字节数组
     * @return 位图
     */
    public static Bitmap bytes2Bitmap(byte[] b) {
        if (b == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * 将Bitmap转化为Drawable
     *
     * @param bitmap：位图
     * @return 图片
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(bitmap);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable：图片
     * @return 位图
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bitmap：位图
     * @param quality：质量
     * @return 输入字节流
     */
    public InputStream bitmap2InputStream(Bitmap bitmap, int quality) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * 将InputStream转换成Bitmap
     *
     * @param is：输入字节流
     * @return 位图
     */
    public Bitmap inputStream2Bitmap(InputStream is) {
        if (is == null) {
            return null;
        }
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 将InputStream转换成byte[]
     *
     * @param is：输入字节流
     * @return 字节数组
     */
    public byte[] inputStream2Bytes(InputStream is) {
        if (is == null) {
            return null;
        }
        String str = "";
        byte[] readByte = new byte[1024];
        int readCount = -1;
        try {
            while ((readCount = is.read(readByte, 0, 1024)) != -1) {
                str += new String(readByte).trim();
            }
            return str.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将byte[]转换成InputStream
     *
     * @param b：字节数组
     * @return 输入字节流
     */
    public InputStream bytes2InputStream(byte[] b) {
        if (b == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }

    /**
     * 将Drawable转换成byte[]
     *
     * @param drawable：图片
     * @return 字节数组
     */
    public byte[] drawable2Bytes(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = drawable2Bitmap(drawable);
        return bitmap2Bytes(bitmap);
    }

    /**
     * 将byte[]转换成Drawable
     *
     * @param b
     * @return 图片
     */
    public Drawable bytes2Drawable(byte[] b) {
        if (b == null) {
            return null;
        }
        Bitmap bitmap = bytes2Bitmap(b);
        return bitmap2Drawable(bitmap);
    }

    /**
     * 按指定宽度和高度缩放图片，不保证宽高比例
     *
     * @param bitmap：位图
     * @param w：宽
     * @param h：高
     * @return 新位图
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBitmap;
    }

    /**
     * 将Bitmap位图保存到path路径下
     *
     * @param bitmap：位图
     * @param path：路径
     * @param format：格式
     * @param quality：质量
     * @return boolean
     */
    public static boolean saveBitmap(Bitmap bitmap, String path, Bitmap.CompressFormat format, int quality) {
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            boolean b = bitmap.compress(format, quality, fos);
            fos.flush();
            fos.close();
            return b;
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片缩放居中并且变成正方形
     *
     * @param bitmap：位图
     * @param edgeLength：正方形的边长
     * @return 处理后的位图
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (bitmap == null || edgeLength <= 0) {
            return null;
        }
        Bitmap squareBitmap = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
//        if (widthOrg > edgeLength && heightOrg > edgeLength) {
        //压缩到一个最小长度是edgeLength的bitmap
        int longerEdge = edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg);
        int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
        int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
        Bitmap scaledBitmap;
        try {
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        } catch (Exception e) {
            return null;
        }
        //从图中截取正中间的正方形部分
        int xTopLeft = (scaledWidth - edgeLength) / 2;
        int yTopLeft = (scaledHeight - edgeLength) / 2;
        try {
            squareBitmap = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
            scaledBitmap.recycle();
        } catch (Exception e) {
            return null;
        }
//        }
        return squareBitmap;
    }

    /**
     * 获得圆角图片
     *
     * @param bitmap：位图
     * @param roundPx：X轴上的圆角半径
     * @return 位图
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 回收不用的Bitmap
     *
     * @param bitmap：位图
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 按比例缩放图片，可做缩略图使用
     *
     * @param context：上下文
     * @param resId：资源ID
     * @param scale：缩放倍数
     * @return 处理后的bitmap
     */
    public static Bitmap getThumbnailBitmap(Context context, int resId, int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;//缩放比例
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        //可以通过其他解码方式解码获得压缩图片
        return bitmap;
    }

    /**
     * 解码图片
     *
     * @param url：路径
     * @return
     */
    public static Bitmap decodeBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            return null;
        }
    }



    /**
     * 保存图片到SD卡
     *
     * @param saveBitmap：需要保存的图片
     * @param bitmapSavePath：图片保存的路径
     * @param bitmapName：图片名
     */
    public static void savePhotoToSDCard(Bitmap saveBitmap, String bitmapSavePath, String bitmapName) {
        if (checkSDCardAvailable()) {
            File photoFile = new File(bitmapSavePath, bitmapName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (saveBitmap != null) {
                    if (saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (Exception e) {
                photoFile.delete();
//                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存图片到SD卡
     *
     * @param saveBitmap：需要保存的图片
     * @param bitmapSavePath：图片保存的路径
     */
    public static void saveBitmapToSDCard(Bitmap saveBitmap, String bitmapSavePath) {
        if (checkSDCardAvailable()) {//检查是否存在SD卡
            File bitmapFile = new File(bitmapSavePath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(bitmapFile);
                if (saveBitmap != null) {
                    if (saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {//压缩
                        fos.flush();
                    }
                }
            } catch (Exception e) {
                bitmapFile.delete();
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
//                    e.printStackTrace();

                }
            }
        }
    }

    /**
     * 检查是否存在SD卡
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 根据路径加载Bitmap
     *
     * @param bitmapPath：图片路径
     * @param bitmapWidth：图片宽
     * @param bitmapHeight：图片高
     * @return
     */
    public static Bitmap convertToBitmap(String bitmapPath, int bitmapWidth, int bitmapHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //设置为ture只获取图片大小
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //返回为空
            BitmapFactory.decodeFile(bitmapPath, options);
            int width = options.outWidth;
            int height = options.outHeight;
            float scaleWidth = 0.f, scaleHeight = 0.f;
            if (width > bitmapWidth || height > bitmapHeight) {
                //缩放图片
                scaleWidth = ((float) width) / bitmapWidth;
                scaleHeight = ((float) height) / bitmapHeight;
            }
            options.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            options.inSampleSize = (int) scale;
            WeakReference<Bitmap> weak = new WeakReference<>(BitmapFactory.decodeFile(bitmapPath, options));
            Bitmap bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak.get().getWidth(), weak.get().getHeight(), null, true);
            if (bMapRotate != null) {
                return bMapRotate;
            }
            return null;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存图片为PNG
     *
     * @param bitmap：位图
     * @param bitmapPath：图片路径
     */
    public static void saveBitmapForPNG(Bitmap bitmap, String bitmapPath) {
        File file = new File(bitmapPath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    /**
     * 保存图片为JPEG
     *
     * @param bitmap：位图
     * @param bitmapPath：图片路径
     */
    public static void saveBitmapForJPEG(Bitmap bitmap, String bitmapPath) {
        File file = new File(bitmapPath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float scaling) {
        Matrix m = new Matrix();
        m.postScale(scaling, scaling);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            m,
            true);
        if(bitmap != newBitmap) {
            bitmap.recycle();
        }
        return newBitmap;
    }


    public static byte[] compress(Bitmap bitmap){
//        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] data = baos.toByteArray();
        while (data.length > 100 * 1024 && quality > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 5;
            data = baos.toByteArray();
        }
        return data;
    }


    public static class BitmapViewUtil{

        private List<WeakReference<Bitmap>> mBitmapList = new ArrayList<>();
        public  void setBackgroundResource(Context context, ImageView imageView, int id){
            Bitmap bitmap = readBitMap(context, id);
            if(bitmap!=null){
                mBitmapList.add(new WeakReference<Bitmap>(bitmap));
                imageView.setImageBitmap(bitmap);
            }
        }
        /**
         * 以最省内存的方式读取本地资源的图片
         *
         * @param context
         * @param resId
         * @return
         */
        private Bitmap readBitMap(Context context, int resId) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            InputStream is = null;
            //获取资源图片
            try {
                is = context.getResources().openRawResource(resId);
                return BitmapFactory.decodeStream(is, null, opt);
            } catch (Exception e) {
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        }

        public  void realease() {
            for (WeakReference<Bitmap> bitmapWeakReference : mBitmapList) {
                Bitmap bitmap = bitmapWeakReference.get();
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            mBitmapList.clear();
        }
    }




    public static String saveImage(Bitmap image, File fdst, Context context) {   //boolean isInsertGallery
        if (fdst == null)
            return null;
        //不是目录但此名字可能被人创建了一个文件 modify by dingchenghao
        if (!fdst.getParentFile().isDirectory()) {
            if (fdst.getParentFile().exists()) {
                fdst.getParentFile().delete();
            }
            fdst.getParentFile().mkdirs();
        }
        if (fdst.exists()) {
            fdst.delete();
            scanFileAsync(context, fdst.getPath()); //add by yangchao 2016/10/31:删除后调用这个方法发送广播刷新缩略图
        }

        try {
            FileOutputStream fos = new FileOutputStream(fdst);
            image.compress(Bitmap.CompressFormat.JPEG, 20, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
//            e.printStackTrace();
        }
//        if(isInsertGallery) {
//            try {
//                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        return fdst.getAbsolutePath();
    }

    /**
     * 删除图片成功之后，调用这个方法发送广播刷新缩略图，防止缩略图和原图不匹配的错乱问题
     * @param context
     * @param filePath
     */
    public static void scanFileAsync(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    /**
     * Create a drawable from file path name.
     */
    public static Bitmap createFromPath(String pathName, int reqWidth, int reqHeight) {
        if (pathName == null) {
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    private static Drawable drawableFromBitmap(Resources res, Bitmap bm, byte[] np,
                                               Rect pad, String srcName) {
        if (np != null) {
            return new NinePatchDrawable(res, bm, np, pad, srcName);
        }
        return new BitmapDrawable(res, bm);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Drawable to Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * Bitmap to Drawable
     *
     * @param bitmap
     * @param mcontext
     * @return
     */
    public static Drawable bitmapToDrawble(Bitmap bitmap, Context mcontext) {
        Drawable drawable = new BitmapDrawable(mcontext.getResources(), bitmap);
        return drawable;
    }
}
