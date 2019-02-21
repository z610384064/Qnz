package com.rd.qnz.share;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.rd.qnz.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WXUtil {
	private IWXAPI api;
	private Activity mActivity = null;
	private final int THUMB_SIZE = 150;
	private String filePath = null;
	private Bitmap mBitmap = null;

	public WXUtil(Activity activity, IWXAPI api) {
		mActivity = activity;
		this.api = api;
	}

	/**
	 * 更新为微信朋友圈状态
	 * 
	 * @param text
	 *            文本
	 * @param desc
	 *            描述
	 */
	public void updateWXStatus(String text, String desc) {

		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = desc;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); //
		// transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		// 调用api接口发送数据到微信
		api.sendReq(req);
	}

	/**
	 * 上传本地照片
	 * 
	 * @param data
	 *            onActivityResult 中Intent返回数据
	 */
	public void uploadLocalPic(Intent data) {
		Uri uri = data.getData();// 相册图片路径

		Cursor cursor = mActivity.getContentResolver().query(uri, null, null,
				null, null);
		cursor.moveToFirst();

		// String imgNo = cursor.getString(0); // 图片编号
		String imgPath = cursor.getString(1); // 图片文件路径
		// String imgSize = cursor.getString(2); // 图片大小
		// String imgName = cursor.getString(3); // 图片文件名

		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(imgPath);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap bmp = BitmapFactory.decodeFile(imgPath);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
				THUMB_SIZE, true);
		bmp.recycle();
		msg.thumbData = bmpToByteArray(thumbBmp, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	/**
	 * 上传Bitmap至微信朋友圈
	 * 
	 * @param bitmap
	 */
	public void uploadPic(Bitmap bitmap) {

		WXImageObject imgObj = new WXImageObject(bitmap);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE,
				THUMB_SIZE, true);

		bitmap.recycle();
		msg.thumbData = bmpToByteArray(thumbBmp, true); // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);

	}

	/**
	 * 照相上传
	 * 
	 * @param data
	 *            onActivityResult 中Intent返回数据
	 */
	public void uploadCameraPic(Intent data) {
		if (data != null) {
			// HTC
			if (data.getData() != null) {
				// 根据返回的URI获取对应的SQLite信息
				Cursor cursor = mActivity.getContentResolver().query(
						data.getData(), null, null, null, null);
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取绝对路径
				}
				cursor.close();
			} else {
				// 三星 小米(小米手机不会自动存储DCIM
				mBitmap = (Bitmap) (data.getExtras() == null ? null : data
						.getExtras().get("data"));
			}

			// 直接强转报错 这个主要是为了去高宽比例
			Bitmap bitmap = mBitmap == null ? null : (Bitmap) mBitmap;

			if (bitmap == null) {
				/**
				 * 该Bitmap是为了获取压缩后的文件比例 如果没有缩略图的比例
				 * 就获取真实文件的比例(真实图片比例会耗时很长，所以如果有缩略图最好)
				 */
				bitmap = BitmapFactory.decodeFile(filePath);
			}
			String imgPath = new FileUtils().saveBitmap(bitmap);

			if (imgPath != null && !"".equals(imgPath)) {
				WXImageObject imgObj = new WXImageObject();
				imgObj.setImagePath(imgPath);

				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = imgObj;

				Bitmap bmp = BitmapFactory.decodeFile(imgPath);
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
						THUMB_SIZE, true);

				bmp.recycle();
				msg.thumbData = bmpToByteArray(thumbBmp, true);

				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("img");
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
				api.sendReq(req);
			}
		}
	}

	/**
	 * 上传Url类型照片
	 * 
	 * @param url
	 *            图片链接地址
	 */
	public void uploadUrlPic(String url) {

		try {

			WXImageObject imgObj = new WXImageObject();
			imgObj.imageUrl = url;

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imgObj;

			Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			bmp.recycle();
			msg.thumbData = bmpToByteArray(thumbBmp, true);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("img");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			api.sendReq(req);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadMusic(String url) {
		WXMusicObject music = new WXMusicObject();
		// music.musicUrl = "http://www.baidu.com";
		music.musicUrl = url;
		// music.musicUrl="http://120.196.211.49/XlFNM14sois/AKVPrOJ9CBnIN556OrWEuGhZvlDF02p5zIXwrZqLUTti4o6MOJ4g7C6FPXmtlh6vPtgbKQ==/31353278.mp3";

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = music;
		msg.title = "Music Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
		msg.description = "Music Album Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";

		Bitmap thumb = BitmapFactory.decodeResource(mActivity.getResources(),
				R.drawable.draw_ad_top);
		msg.thumbData = bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("music");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	private String buildTransaction(final String type) {

		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();

	}

	private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
