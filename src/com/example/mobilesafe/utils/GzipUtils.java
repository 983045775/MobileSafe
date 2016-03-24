package com.example.mobilesafe.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {
	/**
	 * 进行压缩文件
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @throws IOException
	 */
	public static void zip(InputStream in, OutputStream out) throws IOException {
		GZIPOutputStream gout = null;
		try {
			gout = new GZIPOutputStream(out);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				gout.write(buffer, 0, len);
			}
		} finally {
			close(in);
			close(gout);
		}
	}

	/**
	 * 解压文件
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @throws IOException
	 */
	public static void unZip(InputStream in, OutputStream out)
			throws IOException {
		GZIPInputStream gin = null;
		try {
			gin = new GZIPInputStream(in);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = gin.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
		} finally {
			close(gin);
			close(out);
		}
	}

	public static void close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}
}
