/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
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

  public static final int         BUFFER_SIZE = 1024 * 1024 * 4;

  private static final ByteBuffer IO_BB       = ByteBuffer.wrap(new byte[Helper.BUFFER_SIZE]);

  public static boolean isEmptyOrNull(final String text) {
    return (text == null) || text.isEmpty();
  }

  public final static boolean isNotEmptyOrNull(final String text) {
    return (text != null) && (text.length() > 0);
  }

  public static final String substringBetween(final String text, final String start, final String end) {
    return Helper.substringBetween(text, start, end, true);
  }

  public static final String substringBetween(final String text, final String start, final String end, final boolean trim) {
    final int nStart = text.indexOf(start);
    final int nEnd = text.indexOf(end, nStart + start.length() + 1);
    if ((nStart != -1) && (nEnd > nStart)) {
      if (trim) {
        return text.substring(nStart + start.length(), nEnd).trim();
      }
      return text.substring(nStart + start.length(), nEnd);
    }
    return null;
  }

  public static String substringBetweenNarrow(final String text, final String start, final String end) {
    final int nEnd = text.indexOf(end);
    int nStart = -1;
    if (nEnd != -1) {
      nStart = text.lastIndexOf(start, nEnd - 1);
    }
    if ((nStart < nEnd) && (nStart != -1) && (nEnd != -1)) {
      return text.substring(nStart + start.length(), nEnd);
    }
    return null;
  }

  public static final void write(final InputStream in, final OutputStream out) throws IOException {
    int len;
    while ((len = in.read(Helper.IO_BB.array())) > 0) {
      out.write(Helper.IO_BB.array(), 0, len);
    }
  }

  public static final boolean checkUrl(final String link) {
    try {
      final URL url = new URL(link);
      final URLConnection conn = url.openConnection();
      conn.setConnectTimeout(5000);
      conn.connect();
      return true;
    } catch (final Exception e) {
      // silent
    }
    return false;
  }

  public static final boolean checkPort() {
    try {
      final InetAddress host = InetAddress.getByName(System.getProperty("http.proxyHost"));
      final Socket so = new Socket(host, Integer.parseInt(System.getProperty("http.proxyPort")));
      so.close();
      return true;
    } catch (final Exception e) {
      // silent
    }
    return false;
  }

  public static final String chopNull(final String str) {
    if (str == null) {
      return "";
    } else {
      return str;
    }
  }

  public static final String toString(final boolean[] bools) {
    final StringBuffer sb = new StringBuffer(bools.length);
    for (final boolean b : bools) {
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

  public static final String escapeFileName(final String str) {
    String result = str.replace('<', '_').replace('<', '_').replace('<', '_').replace('<', '_').replace('<', '_').replace('<', '_').replace('<', '_')
        .replace('<', '_').replace('<', '_');
    return result;
  }
}
