package com.mtu.foundation.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileUtil {
	private static final String ROOT_DIR = "/mtu";
	private static final String IMG_ROOT_DIR = "/mtu/images";
	private static final String CACHE_FILE = ROOT_DIR + "/cache";

	public static File getRootDir() {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File rootFile = new File(Environment.getExternalStorageDirectory()
					+ ROOT_DIR);
			if (!rootFile.exists()) {
				rootFile.mkdir();
			}
			return rootFile;
		}
		return null;
	}

	public static File getCacheFile(String name) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File rootFile = new File(Environment.getExternalStorageDirectory()
					+ CACHE_FILE);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File retFile = new File(rootFile, name + ".dat");
			if (!retFile.exists()) {
				try {
					retFile.createNewFile();
				} catch (IOException e) {
					return null;
				}
			}
			return retFile;
		}
		return null;
	}

	public static File getUpdateFile(String code) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File rootFile = new File(Environment.getExternalStorageDirectory()
					+ ROOT_DIR);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File retFile = new File(rootFile, "mtufoundation_" + code + ".apk");
			if (!retFile.exists()) {
				try {
					retFile.createNewFile();
				} catch (IOException e) {
					return null;
				}
			}
			return retFile;
		}
		return null;
	}

	public static File getImageFile(String filename) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File rootFile = new File(Environment.getExternalStorageDirectory()
					+ IMG_ROOT_DIR);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File retFile = new File(rootFile, filename);
			/*
			 * if (!retFile.exists()) { try { retFile.createNewFile(); } catch
			 * (IOException e) { return null; } }
			 */
			return retFile;
		}
		return null;
	}

	public static boolean delImageFile(String filename) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File rootFile = new File(Environment.getExternalStorageDirectory()
					+ IMG_ROOT_DIR);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File retFile = new File(rootFile, filename);
			if (!retFile.exists()) {
				return true;
			} else {
				retFile.delete();
			}
			return true;
		}
		return true;
	}
}
