package com.imsdk.imdeveloper.util;

/**
 * Created by Administrator on 2015/12/20.
 */
import imsdk.data.mainphoto.IMSDKMainPhoto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
/**
 *
 * 图片展示（缓存处理）
 *
 */
public class ImageDisplayer
{
    private static ImageDisplayer instance;
    private Context context;
    private static final int THUMB_WIDTH = 256;
    private static final int THUMB_HEIGHT = 256;
    private int mScreenWidth;
    private int mScreenHeight;

    public Handler h = new Handler();
    public final String TAG = getClass().getSimpleName();
    private WeakHashMap<String, SoftReference<Bitmap>> imageCache = null;
    private WeakHashMap<String, SoftReference<ImageView>> imageViewCache = null;

    /** 每次执行限定个数个任务的线程池 */
    private final int ThreadPoolNums = 4;
    private ExecutorService threadPool = null;

    /**正在等待执行任务队列**/
    private ConcurrentMap<String, TaskRunnable> taskMap = null;

    /**设置获取图片的宽度、高度**/
    private int mImageWidth = 180;
    private int mImageHeight = 180;


    public static ImageDisplayer getInstance(Context context)
    {
        if (instance == null)
        {
            synchronized (ImageDisplayer.class)
            {
                instance = new ImageDisplayer(context);
            }
        }

        return instance;
    }

    public ImageDisplayer(Context context)
    {
//		if (context.getApplicationContext() != null) this.context = context
//				.getApplicationContext();
//		else
//			this.context = context;
        this.context = context;

        DisplayMetrics dm = new DisplayMetrics();
        dm = this.context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }


    public void put(String key, Bitmap bmp)
    {
        if (!TextUtils.isEmpty(key) && bmp != null)
        {
            imageCache.put(key, new SoftReference<Bitmap>(bmp));
        }
    }

    public void displayBmp(final ImageView iv, final String thumbPath,
                           final String sourcePath)
    {
        displayBmp(iv, thumbPath, sourcePath, true);
    }

    public void displayBmp(final ImageView iv, final String thumbPath,
                           final String sourcePath, final boolean showThumb)
    {
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath))
        {
            Log.e(TAG, "no paths pass in");
            return;
        }

        if (iv.getTag() != null && iv.getTag().equals(sourcePath))
        {
            return;
        }

        showDefault(iv);

        final String path;
        if (!TextUtils.isEmpty(thumbPath) && showThumb)
        {
            path = thumbPath;
        }
        else if (!TextUtils.isEmpty(sourcePath))
        {
            path = sourcePath;
        }
        else
        {
            return;
        }

        iv.setTag(path);

        if(imageCache == null){
            imageCache = new WeakHashMap<String, SoftReference<Bitmap>>();
        }

        if (imageCache.containsKey(showThumb ? path + THUMB_WIDTH
                + THUMB_HEIGHT : path))
        {
            SoftReference<Bitmap> reference = imageCache.get(showThumb ? path
                    + THUMB_WIDTH + THUMB_HEIGHT : path);
            // 可以用LruCahche会好些
            Bitmap imgInCache = reference.get();
            if (imgInCache != null)
            {
                refreshView(iv, imgInCache, path);
                return;
            }
        }
        iv.setImageBitmap(null);

        // 不在缓存则加载图片
        new Thread()
        {
            Bitmap img;

            public void run()
            {

                try
                {
                    if (path != null && path.equals(thumbPath))
                    {
                        img = BitmapFactory.decodeFile(path);
                    }
                    if (img == null)
                    {
                        img = compressImg(sourcePath, showThumb);
                    }
                }
                catch (Exception e)
                {

                }

                if (img != null)
                {
                    put(showThumb ? path + THUMB_WIDTH + THUMB_HEIGHT : path,
                            img);

                }
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        refreshView(iv, img, path);
                    }
                });
            }
        }.start();

    }


    public void displayBmp(final ImageView iv, final String fileId)
    {
        displayBmp(iv, fileId, 180, 180);
    }
    public void displayBmp(final ImageView iv, final String fileId, int width, int height)
    {
        if(fileId == null || fileId.trim().length() == 0){
            return;
        }

        try {

            if(imageCache == null){
                imageCache = new WeakHashMap<String, SoftReference<Bitmap>>();
            }
            if(imageViewCache == null){
                imageViewCache = new WeakHashMap<String, SoftReference<ImageView>>();
            }

            mImageWidth = width;
            mImageHeight = height;

            if (imageCache.containsKey(fileId))
            {
                SoftReference<Bitmap> reference = imageCache.get(fileId);
                SoftReference<ImageView> ivReference = imageViewCache.get(fileId+(iv.hashCode()));

                // sdcard cache部分在imsdk已实现
                if (reference != null &&
                        reference.get() != null &&
                        ivReference != null &&
                        ivReference.get() != null){

                    ivReference.get().setImageBitmap(reference.get());
                    return;
                }
            }
            //添加任务
            addTask(fileId, iv);

        } catch (OutOfMemoryError e) {
            Log.w("imsdk", "oom===="+e.getMessage());
            displayBmp(iv,fileId);
        }
    }

    private void refreshView(ImageView imageView, Bitmap bitmap, String path)
    {
        if (imageView != null && bitmap != null)
        {
            if (path != null)
            {
                ((ImageView) imageView).setImageBitmap(bitmap);
                imageView.setTag(path);
            }
        }
    }

    private void refreshViewByFileId(ImageView imageView, Bitmap bitmap, String fileId)
    {
        if (imageView != null && bitmap != null)
        {
            if (fileId != null)
            {
                imageCache.put(fileId, new SoftReference<Bitmap>(bitmap));
                imageViewCache.put(fileId+(imageView.hashCode()), new SoftReference<ImageView>(imageView));

                if(imageCache.get(fileId) != null &&
                        imageCache.get(fileId).get() != null &&
                        imageViewCache.get(fileId+(imageView.hashCode())) != null &&
                        imageViewCache.get(fileId+(imageView.hashCode())).get() != null){

                    imageViewCache.get(fileId+(imageView.hashCode())).get()
                            .setImageBitmap(imageCache.get(fileId).get());
                }
            }
        }
    }

    private void showDefault(ImageView iv)
    {
        //iv.setBackgroundResource(R.drawable.photo_picker_bg);
    }

    public Bitmap compressImg(String path, boolean showThumb)
            throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, opt);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        if (showThumb)
        {
            while (true)
            {
                if ((opt.outWidth >> i <= THUMB_WIDTH)
                        && (opt.outHeight >> i <= THUMB_HEIGHT))
                {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    opt.inSampleSize = (int) Math.pow(2.0D, i);
                    opt.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, opt);
                    break;
                }
                i += 1;
            }
        }
        else
        {
            while (true)
            {
                if ((opt.outWidth >> i <= mScreenWidth)
                        && (opt.outHeight >> i <= mScreenHeight))
                {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    opt.inSampleSize = (int) Math.pow(2.0D, i);
                    opt.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, opt);
                    break;
                }
                i += 1;
            }
        }
        return bitmap;
    }

    public interface ImageCallback
    {
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                              Object... params);
    }


    @Deprecated
    public void release(){
        if(imageViewCache != null){
            for (Map.Entry<String, SoftReference<ImageView>> entry : imageViewCache.entrySet()) {
                if(entry.getValue() != null){
                    ImageView iv = entry.getValue().get();
                    iv.setBackgroundResource(0);
                    iv.setImageResource(0);
                    iv.setImageBitmap(null);
                }
            }
        }

        if(imageCache != null){
            for (Map.Entry<String, SoftReference<Bitmap>> entry : imageCache.entrySet()) {
                if(entry.getValue() != null){
                    Bitmap bp = entry.getValue().get();
                    if (!bp.isRecycled()) {
                        bp.recycle();
                        bp = null;
                    }
                }
            }
        }

        imageCache = null;
        imageViewCache = null;
        taskMap = null;
        System.gc();
    }


    private void addTask(String fileId, ImageView iv){

        /**init**/
        if(threadPool == null){
            threadPool = Executors.newFixedThreadPool(ThreadPoolNums);
        }
        if(taskMap == null){
            taskMap = new ConcurrentHashMap<String, TaskRunnable>();
        }

        if(taskMap.get(fileId+"_"+iv.hashCode()) == null){
            TaskRunnable myRunnable = new TaskRunnable(fileId, iv);
            threadPool.submit(myRunnable);
            taskMap.put(fileId+"_"+iv.hashCode(), myRunnable);
        }
    }

    /**
     * 下载任务runnable
     *
     */
    private class TaskRunnable implements Runnable{

        private String mFileId = null;
        private ImageView mIv = null;

        public TaskRunnable(String fileId, ImageView iv)
        {
            mFileId = fileId;
            mIv = iv;
        }

        @Override
        public void run() {

            IMSDKMainPhoto.downloadImage(mFileId,mImageWidth,mImageHeight,
                    new IMSDKMainPhoto.onDownloadImageListener() {

                        @Override
                        public void onSuccess(final String fileId,final Bitmap bp) {

                            taskMap.remove(mFileId+"_"+mIv.hashCode());
                            //刷新列表
                            h.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    refreshViewByFileId(mIv, bp, fileId);
                                }
                            });
                        }

                        @Override
                        public void onProgress(double progress) {


                        }

                        @Override
                        public void onFailure(int errorCode, String errorMsg) {
                            Log.w("imsdk", "====下载图片失败,errorCode="+errorCode + " errorMsg="+errorMsg);
                            taskMap.remove(mFileId+"_"+mIv.hashCode());
                        }
                    });
        }

    }


}
