package com.rd.qnz.share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;

public class SystemUtil {
	private static Activity mActivity = null;

	public SystemUtil(Activity activity) {
		mActivity = activity;
	}

	/** 打开本地图片 */
	public void openLocalPic() {

		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		mActivity.startActivityForResult(i, 0);

	}

	/** 打开相机 */
	public void openCamera() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		mActivity.startActivityForResult(intent, 1);

	}

	/** 打开微信 */
	public void openWx(String packageName) {

		PackageManager manager = mActivity.getPackageManager();

		Intent intent = manager.getLaunchIntentForPackage(packageName);

		mActivity.startActivity(intent);
	}

	/** 是否安装微信 */
	public boolean isInstallWx(String packageName) {
		try {

			PackageManager manager = mActivity.getPackageManager();

			PackageInfo info = manager.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);

			if (info != null) {

				return true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 是否存在SDCard
	 * 
	 */
	public static final boolean hasSdCard() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		}
		return false;
	}
}
