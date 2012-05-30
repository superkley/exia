package cn.kk.exia;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

public final class Helper {

	public static final int					BUFFER_SIZE	= 1024 * 1024 * 4;

	private static final ByteBuffer	IO_BB				= ByteBuffer.wrap(new byte[BUFFER_SIZE]);

	public static boolean isEmptyOrNull(String text) {
		return text == null || text.isEmpty();
	}

	public final static boolean isNotEmptyOrNull(String text) {
		return text != null && text.length() > 0;
	}

	public static final String substringBetween(final String text, final String start, final String end) {
		return substringBetween(text, start, end, true);
	}

	public static final String substringBetween(final String text, final String start, final String end, final boolean trim) {
		final int nStart = text.indexOf(start);
		final int nEnd = text.indexOf(end, nStart + start.length() + 1);
		if (nStart != -1 && nEnd > nStart) {
			if (trim) {
				return text.substring(nStart + start.length(), nEnd).trim();
			} else {
				return text.substring(nStart + start.length(), nEnd);
			}
		} else {
			return null;
		}
	}

	public static String substringBetweenNarrow(String text, String start, String end) {
		final int nEnd = text.indexOf(end);
		int nStart = -1;
		if (nEnd != -1) {
			nStart = text.lastIndexOf(start, nEnd - 1);
		}
		if (nStart < nEnd && nStart != -1 && nEnd != -1) {
			return text.substring(nStart + start.length(), nEnd);
		} else {
			return null;
		}
	}

	public static final void write(final InputStream in, final OutputStream out) throws IOException {
		int len;
		while ((len = in.read(IO_BB.array())) > 0) {
			out.write(IO_BB.array(), 0, len);
		}
	}

	public static final boolean checkUrl(final String link) {
		try {
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static final boolean checkPort() {
		try {
			InetAddress host = InetAddress.getByName(System.getProperty("http.proxyHost"));
			Socket so = new Socket(host, Integer.parseInt(System.getProperty("http.proxyPort")));
			so.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static final String chopNull(String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}

	public static final String toString(final boolean[] bools) {
		StringBuffer sb = new StringBuffer(bools.length);
		for (boolean b : bools) {
			sb.append(b ? '1' : '0');
		}
		return sb.toString();
	}

	public static final void fromString(final String boolsString, final boolean[] bools) {
		if (boolsString.length() == bools.length) {
			for (int i = 0; i < bools.length; i++) {
				bools[i] = boolsString.charAt(i) == '1';
			}
		}
	}
}
