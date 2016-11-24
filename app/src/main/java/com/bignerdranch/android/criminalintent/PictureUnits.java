package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by zkhk on 2016/11/23.
 */
// 用于图片处理

public class PictureUnits {

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        // 获取原图片的尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // 计算过缩放大小
        int inSampleSize = 1; // 缩放倍数
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;  // 假设这个值是1的话，就表明缩略图和原始照片的水平像素大小一样。如果是2的话，它们的水平像素比就是1/2。因 此，inSampleSize值为2时，缩略图的像素数就是原始文件的4分之一    这是分母 , 等比缩放

        return BitmapFactory.decodeFile(path, options);
    }

    // 根据屏幕 估算大小
    public static Bitmap getScaledBitmap(String path, Activity activity) {

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

}
