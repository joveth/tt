package com.mtu.foundation.util;

import android.os.Handler;
import android.os.Message;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jov on 2015/2/13.
 */
public class FileOperRunnable implements Runnable {
    private File targetFile;
    private boolean read;
    private boolean write;
    private String data;
    private Handler handler;
    private BufferedOutputStream outputStream;
    private BufferedInputStream inputStream;

    public FileOperRunnable(File targetFile, boolean read, boolean write, String data, Handler handler) {
        this.targetFile = targetFile;
        this.read = read;
        this.write = write;
        this.data = data;
        this.handler = handler;

    }

    @Override
    public void run() {
        if (read) {
            Message msg = null;
            if (handler != null) {
                msg = handler.obtainMessage();
            }
            try {
                String ret = readFile();
                if (msg != null) {
                    msg.what = Constants.READ_RESULT_OK;
                    msg.obj = ret;
                }
            } catch (IOException e) {
                if (msg != null) {
                    msg.what = Constants.RESULT_FAILED;
                }
            }
            if (handler != null) {
                handler.sendMessage(msg);
            }
            return;
        }
        if (write) {
            Message msg = null;
            if (handler != null) {
                msg = handler.obtainMessage();
            }
            try {
                writeFile();
                if (msg != null) {
                    msg.what = Constants.RESULT_OK;
                }
            } catch (IOException e) {
                if (msg != null) {
                    msg.what = Constants.RESULT_FAILED;
                }
                e.printStackTrace();
            }
            if (handler != null) {
                handler.sendMessage(msg);
            }
            return;
        }

    }

    private String readFile() throws IOException {
        StringBuilder builder = new StringBuilder(0);
        if (targetFile != null) {
            if (inputStream == null) {
                inputStream = new BufferedInputStream(new FileInputStream(targetFile));
            }
            int length = inputStream.available();
            byte[] temp = new byte[length];
            inputStream.read(temp);
            builder.append(EncodingUtils.getString(temp, "utf-8"));
            inputStream.close();
        }
        return builder.toString();
    }

    private void writeFile() throws IOException {
        if (!CommonUtil.isEmpty(data) && targetFile != null) {
            if (outputStream == null) {
                outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
            }
            outputStream.write(data.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
        }
    }

}
