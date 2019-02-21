package com.rd.qnz.share;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

public class FileUtils {

	/**
	 * 将Bitmap保存在本地
	 * 
	 * @param mBitmap
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public String saveBitmap(Bitmap mBitmap) {

		try {
			String sdCardPath = "";
			if (SystemUtil.hasSdCard()) {
				sdCardPath = Environment.getExternalStorageDirectory().getPath();
			} else {

			}

			String filePath = sdCardPath + "/" + "myImg/";

			Date date = new Date(System.currentTimeMillis());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 时间格式-显示方式

			String imgPath = filePath + sdf.format(date) + ".png";

			File file = new File(filePath);

			if (!file.exists()) {
				file.mkdirs();
			}
			File imgFile = new File(imgPath);

			if (!imgFile.exists()) {
				imgFile.createNewFile();
			}

			FileOutputStream fOut = new FileOutputStream(imgFile);

			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();

			if (fOut != null) {

				fOut.close();
			}
			return imgPath;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
