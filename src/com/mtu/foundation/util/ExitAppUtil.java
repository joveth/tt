package com.mtu.foundation.util;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by jov on 2015/2/5.
 */
public class ExitAppUtil {
	private static LinkedList<Activity> acys = new LinkedList<Activity>();

	public static void add(Activity acy) {
		acys.add(acy);
	}

	public static void remove(Activity acy) {
		acys.remove(acy);
	}

	public static void exit() {
		Activity acy;
		while (acys.size() != 0) {
			acy = acys.poll();
			if (!acy.isFinishing()) {
				acy.finish();
			}
		}
		System.exit(0);
	}
}
