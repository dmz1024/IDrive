package com.ccpress.izijia.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wu Jingyu
 * Date: 2015/5/15
 * Time: 18:29
 */
public class CameraUtil {
    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId,
                                                   Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static Camera.Size getBestSizeForPreview(Context ctx, Camera.Parameters params) {
        List<Camera.Size> list = params.getSupportedPreviewSizes();
        long square = ScreenUtil.getScreenHeight(ctx) * ScreenUtil.getScreenWidth(ctx);
        for(int i=0; i<list.size(); i++){
            Camera.Size size = list.get(i);
            long size_square = size.height * size.width;
            if(size_square == square){
                return size;
            }
        }
        return  null;
    }


    private static CameraSizeComparator sizeComparator = new CameraSizeComparator();
    public static Camera.Size getPreviewSize(List<Camera.Size> list, int th) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.width > th) && equalRate(s, 1.33f)) {
//                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        return list.get(i);
    }

    public static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }

    public static class CameraSizeComparator implements Comparator<Camera.Size> {
        //按升序排列
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
