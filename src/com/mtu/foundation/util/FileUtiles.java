package com.mtu.foundation.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

/**
 * 把网络图片保存到本地 1.强引用，正常实例化一个对象。 jvm无论内存是否够用系统都不会释放这块内存
 * 2.软引用（softReference）:当我们系统内存不够时，会释放掉 3.弱引用：当我们系统清理内存时发现是一个弱引用对象，直接清理掉
 * 4.虚引用：当我们清理内存时会 把虚引用对象放入一个清理队列当中， 让我们程序员保存当前对象的状态
 * 
 * FileUtiles 作用: 用来向我们的sdcard保存网络接收来的图片
 * */
public class FileUtiles {

	public static String getImagePath(String imageUrl) {
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath()
				+ "/mtu/images/";
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		return imagePath;
	}

	public static boolean isHasImage(String url) {
		File imageFile = new File(getImagePath(url));
		return imageFile.exists();
	}

	public static File updateDir = null;
	public static File updateFile = null;

	/***
	 * 创建文件
	 */
	public static void createFile(String name) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/mtu");
			updateFile = new File(updateDir + "/" + name + ".apk");
			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			deleteExistsApk(updateDir);
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void deleteExistsApk(File dir) {
		if (dir != null && dir.exists()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				file.deleteOnExit();
			}
		}
	}

}
