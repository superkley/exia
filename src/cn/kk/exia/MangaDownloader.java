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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

public class MangaDownloader implements Logger {
  private static class DownloadThread extends Thread {
    private final String to;
    private final String url;
    private final Logger log;
    boolean              success;

    public DownloadThread(final String to, final String url, final Logger log) {
      super();
      this.to = to;
      this.url = StringEscapeUtils.unescapeHtml4(url);

      this.log = log;
      this.success = false;
    }

    @SuppressWarnings("resource")
    @Override
    public void run() {
      OutputStream out = null;
      InputStream in = null;
      try {
        out = new BufferedOutputStream(new FileOutputStream(this.to));
        in = new BufferedInputStream(MangaDownloader.openUrlInputStream(this.url));
        Helper.write(in, out);
        final File test = new File(this.to);
        if (test.isFile() && (test.length() > MangaDownloader.MIN_IMG_OK_BYTES)) {
          this.success = true;
        } else {
          throw new FileNotFoundException("下载文件不存在或者不正确：" + this.url);
        }
      } catch (final IOException e) {
        new File(this.to).delete();
        // e.printStackTrace();
        this.log.err("错误：" + e.toString());
        if (e instanceof FileNotFoundException) {
          throw new RuntimeException(e);
        }
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (final IOException e) {
            e.printStackTrace();
          }
        }
        if (out != null) {
          try {
            out.close();
          } catch (final IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public static int                        sleepBase            = 1000;

  public static String                     userAgent            = "Mozilla/5.0 (Windows NT " + (((int) (Math.random() * 2) + 5)) + ".1) Firefox/"
                                                                    + (((int) (Math.random() * 8) + 3)) + "." + (((int) (Math.random() * 6) + 0)) + "."
                                                                    + (((int) (Math.random() * 6) + 0));

  public static String                     cookieString         = "";

  private static boolean                   dump                 = false;

  private static final Map<String, String> DEFAULT_CONN_HEADERS = new HashMap<String, String>();

  public static final Charset              CHARSET_UTF8         = Charset.forName("UTF-8");

  private static String                    mangaTitle;

  static {
    System.setProperty("http.keepAlive", "false");
    CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    HttpURLConnection.setFollowRedirects(false);
    MangaDownloader.resetConnectionHeaders();
  }

  private static int                       lineCounter          = 0;

  private static StringBuffer              cookie               = new StringBuffer(512);

  protected static String                  searchParams         = "";

  private static final int                 MIN_IMG_OK_BYTES     = 2000;

  private static String                    nextUrl;

  private static String analyzeAndDownload(final String line, final int num, final Logger log, final boolean first, final String targetDir) throws IOException {
    String pageTitle = Helper.substringBetweenNarrow(line, "<h1>", "</h1>");
    if (Helper.isNotEmptyOrNull(pageTitle)) {
      if (Helper.isNotEmptyOrNull(MangaDownloader.mangaTitle)) {
        // System.out.println("漫画册名称-title: " + MangaDownloader.mangaTitle);
        pageTitle = Helper.escapeFileName(StringEscapeUtils.unescapeHtml4(MangaDownloader.mangaTitle));
      } else {
        // System.out.println("漫画册名称-h1: " + pageTitle);
        pageTitle = Helper.escapeFileName(StringEscapeUtils.unescapeHtml4(pageTitle));
      }
      final File dir = new File(targetDir + File.separator + pageTitle);
      if (first) {
        log.log("漫画册名称：" + pageTitle);
      }
      dir.mkdirs();
      MangaDownloader.nextUrl = Helper.substringBetweenNarrow(line, "<a href=\"", "-" + num + "\"><img");
      if (Helper.isNotEmptyOrNull(MangaDownloader.nextUrl)) {
        MangaDownloader.nextUrl += "-" + num;
        final String img = Helper.substringBetweenNarrow(line.substring(line.indexOf("<iframe")), "<img src=\"", "\" style=\"");
        if (Helper.isNotEmptyOrNull(img)) {
          try {
            String imgName = img.substring(img.lastIndexOf('/'));
            if (imgName.indexOf('=') != -1) {
              imgName = imgName.substring(imgName.lastIndexOf('=') + 1);
            }
            imgName = Helper.escapeFileName(imgName);
            String ext = ".jpg";
            final int idx = imgName.lastIndexOf('.');
            if (idx != -1) {
              ext = imgName.substring(idx);
            }
            if (null != MangaDownloader.download(img, new File(dir, pageTitle + "_" + num + ext).getAbsolutePath(), false, log)) {
              log.log("图片下载成功：" + img);
              try {
                if (MangaDownloader.lineCounter++ >= 5) {
                  MangaDownloader.lineCounter = 0;
                  Thread.sleep((40 * MangaDownloader.sleepBase) + (int) (Math.random() * 12 * MangaDownloader.sleepBase)); // 12000
                } else {
                  Thread.sleep((8 * MangaDownloader.sleepBase) + (int) (Math.random() * 5 * MangaDownloader.sleepBase));
                }
              } catch (final InterruptedException e) {
                e.printStackTrace();
                log.err("错误：" + e.toString());
              }
            } else {
              log.log("跳过图片下载（文件已存在）：" + img);
              try {
                Thread.sleep((5 * MangaDownloader.sleepBase) + (int) (Math.random() * 5 * MangaDownloader.sleepBase));
              } catch (final InterruptedException e) {
                e.printStackTrace();
                log.err("错误：" + e.toString());
              }
            }
          } catch (final IOException e) {
            // e.printStackTrace();
            log.err("错误：" + e.toString());
            throw e;
          }
          return MangaDownloader.nextUrl;
        }
      }
    }
    return null;
  }

  public static final boolean appendCookies(final StringBuffer cookies, final HttpURLConnection conn) throws IOException {
    try {
      boolean changed = false;
      final List<String> values = conn.getHeaderFields().get("Set-Cookie");
      if (values != null) {
        for (final String v : values) {
          if (v.indexOf("deleted") == -1) {
            if (cookies.length() > 0) {
              cookies.append("; ");
            }
            cookies.append(v.split(";")[0]);
            changed = true;
          }
        }
      }
      return changed;
    } catch (final Throwable e) {
      throw new IOException(e);
    }
  }

  private static void appendUrl(final File downloaded, final String mangaUrl) throws IOException {
    final FileWriter f = new FileWriter(downloaded, true);
    f.write(mangaUrl);
    f.write('\n');
    f.close();
  }

  private final static void checkAndDump(final String line, final Logger log, final String targetDir) {
    if (line.contains("An Error Has Occurred")) {
      try {
        Thread.sleep((60 * MangaDownloader.sleepBase) + (int) (Math.random() * 120 * MangaDownloader.sleepBase));
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (line.contains("your IP address")) {
      log.err("服务器要求放慢速度：" + line);
      try {
        Thread.sleep((60 * MangaDownloader.sleepBase) + (int) (Math.random() * 120 * MangaDownloader.sleepBase));
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (MangaDownloader.dump) {
      try {
        final File dumpFile = new File(targetDir + File.separator + "exia-dump.xml");
        if (!dumpFile.isFile()) {
          log.err("dump: " + dumpFile.getAbsolutePath());
        }
        final FileWriter writer = new FileWriter(dumpFile, true);

        writer.append(line);
        writer.append('\n');
        writer.close();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String createMainUrl(final String keyword, final boolean[] optionsTypes, final boolean[] optionsSearchFields, final int minimumStars)
      throws UnsupportedEncodingException {
    final StringBuffer url = new StringBuffer(200);
    url.append("http://g.e-hentai.org/?");
    url.append("f_search=").append(URLEncoder.encode(keyword, MangaDownloader.CHARSET_UTF8.name()));
    if (minimumStars > 1) {
      url.append("&f_sr=on&f_srdd=").append(String.valueOf(minimumStars));
    }
    for (int i = 0; i < optionsTypes.length; i++) {
      final boolean b = optionsTypes[i];
      if (b) {
        switch (i) {
          case 0:
            url.append("&f_doujinshi=on");
            break;
          case 1:
            url.append("&f_manga=on");
            break;
          case 2:
            url.append("&f_artistcg=on");
            break;
          case 3:
            url.append("&f_gamecg=on");
            break;
          case 4:
            url.append("&f_western=on");
            break;
          case 5:
            url.append("&f_non-h=on");
            break;
          case 6:
            url.append("&f_imageset=on");
            break;
          case 7:
            url.append("&f_cosplay=on");
            break;
          case 8:
            url.append("&f_asianporn=on");
            break;
          case 9:
            url.append("&f_misc=on");
            break;
          default:
            break;
        }
      }
    }
    url.append("&f_sfdd=favall");
    for (int i = 0; i < optionsSearchFields.length; i++) {
      final boolean b = optionsSearchFields[i];
      if (b) {
        switch (i) {
          case 0:
            url.append("&f_sname=on");
            break;
          case 1:
            url.append("&f_stags=on&f_sdts=on");
            break;
          case 2:
            url.append("&f_sdesc=on");
            break;
          default:
            break;
        }
      }
    }
    if (Helper.isNotEmptyOrNull(MangaDownloader.searchParams)) {
      if (!MangaDownloader.searchParams.startsWith("&")) {
        url.append('&');
      }
      url.append(MangaDownloader.searchParams);
    }
    url.append("&f_apply=Apply+Filter");
    return url.toString();
  }

  public static String download(final String url, final String to, final boolean overwrite, final Logger log) throws IOException {
    final File toFile = new File(to);
    // 925 -> forbidden file
    if (!overwrite && toFile.exists() && (toFile.length() > MangaDownloader.MIN_IMG_OK_BYTES) && (toFile.length() != 925)) {
      return null;
    } else {
      int retries = 0;
      DownloadThread test = null;
      while ((retries++ < 3) && !(test = new DownloadThread(to, url, log)).success) {
        try {
          test.start();
          test.join(60000);
          test.interrupt();
        } catch (final Throwable e) {
          try {
            Thread.sleep((3 * MangaDownloader.sleepBase) + (int) (Math.random() * 3 * MangaDownloader.sleepBase));
          } catch (InterruptedException e1) {
            // ignore
          }
          if ((e instanceof RuntimeException) && (e.getCause() != null)) {
            throw (IOException) e.getCause();
          }
        }
      }
      if ((test != null) && !test.success) {
        throw new IOException("下载失败：" + to);
      }
      return to;
    }
  }

  public static boolean downloadGallery(final String mangaUrl, final String targetDir, final Logger log) {
    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(MangaDownloader.openUrlInputStream(mangaUrl), MangaDownloader.CHARSET_UTF8));
      String line;
      boolean success = false;
      while (null != (line = reader.readLine())) {
        MangaDownloader.checkAndDump(line, log, targetDir);
        if (line.contains("<h1")) {
          String pageUrl = Helper.substringBetweenNarrow(line, "<a href=\"", "-1\">");
          if (Helper.isNotEmptyOrNull(pageUrl)) {
            pageUrl += "-1";
            if (MangaDownloader.downloadManga(pageUrl, targetDir, log)) {
              success = true;
              break;
            }
          }
          break;
        }
      }
      reader.close();
      return success;
    } catch (final Exception e) {
      e.printStackTrace();
      log.err("严重错误：" + e.toString());
    }
    return false;
  }

  public static boolean downloadManga(final String url, final String targetDir, final Logger log) {
    try {
      boolean first = true;
      int retries = 0;
      String imgUrl = url;
      String backupImgUrl = imgUrl;
      MangaDownloader.mangaTitle = null;
      while (null != imgUrl) {
        log.log("下载网页：" + imgUrl);
        final String lastUrl = imgUrl;
        final String substring = imgUrl.substring(imgUrl.lastIndexOf('-') + 1);
        final int num = Integer.parseInt(substring);
        try {
          final BufferedReader reader = new BufferedReader(new InputStreamReader(MangaDownloader.openUrlInputStream(imgUrl), MangaDownloader.CHARSET_UTF8));
          String line;
          while (null != (line = reader.readLine())) {
            MangaDownloader.checkAndDump(line, log, targetDir);
            if (line.contains("<h1>")) {
              imgUrl = MangaDownloader.analyzeAndDownload(line, num + 1, log, first, targetDir);
              backupImgUrl = lastUrl;
              first = false;
              break;
            } else {
              if (Helper.isEmptyOrNull(MangaDownloader.mangaTitle)) {
                MangaDownloader.mangaTitle = Helper.substringBetweenNarrow(line, "<title>", "</title>");
              }
            }
          }
          reader.close();
          if (imgUrl == null) {
            MangaDownloader.dump = false;
            return true;
          }
          if (!imgUrl.equals(lastUrl)) {
            // ok
            MangaDownloader.dump = false;
          } else {
            // log.err("发现网页内容更新。。。");
            // MangaDownloader.dump = true;
            // imgUrl = null;
          }
        } catch (final Exception e) {
          Thread.sleep((60 * MangaDownloader.sleepBase) + (int) (Math.random() * 10 * MangaDownloader.sleepBase));
          if (retries++ > 2) {
            e.printStackTrace();
            log.err("跳过：" + imgUrl);
            imgUrl = MangaDownloader.nextUrl;
          } else {
            imgUrl = backupImgUrl;
            log.err("重试！错误：" + e);
          }
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
      log.err("错误：" + e.toString());
    }
    return false;
  }

  public static void downloadSearchResult(final String keyword, final String targetDir, final Logger log, final boolean[] optionsTypes,
      final boolean[] optionsSearchFields, final int minimumStars) {
    int pageNr = 0;
    int mangaCount;
    final File dir = new File(targetDir);
    dir.mkdirs();
    final File dumpFile = new File(targetDir + File.separator + "exia-dump.xml");
    dumpFile.delete();
    final File downloaded = new File(targetDir + File.separator + "md-downloaded.txt");
    final Set<String> downloadedIds = new HashSet<String>();
    if (downloaded.isFile()) {
      try {
        MangaDownloader.readDownloaded(downloaded, downloadedIds);
      } catch (final Exception e) {
        e.printStackTrace();
        log.err("错误：" + e.toString());
      }
    }

    try {
      final String mainUrl = MangaDownloader.createMainUrl(keyword, optionsTypes, optionsSearchFields, minimumStars);

      do {
        mangaCount = 0;
        try {
          final String requestUrl = mainUrl + "&page=" + pageNr;
          log.log("提交搜索数据：" + requestUrl);

          final BufferedReader reader = new BufferedReader(new InputStreamReader(MangaDownloader.openUrlInputStream(requestUrl), MangaDownloader.CHARSET_UTF8));
          String line;
          while (null != (line = reader.readLine())) {
            MangaDownloader.checkAndDump(line, log, targetDir);
            if (line.contains("Your IP")) {
              log.err("下载中断（网站保护措施已更新，请下载或等待最新软件）：" + line);
              return;
            } else if (line.contains("gallerytorrents.php")) {
              final String[] split = line.split("imgicon.png");
              for (int i = 1; i < split.length; i++) {
                String mangaUrl = split[i];
                mangaUrl = Helper.substringBetween(mangaUrl, "<div class=\"it1\"><a href=\"", "\">");
                if (Helper.isNotEmptyOrNull(mangaUrl)) {
                  final String mangaId = Helper.substringBetween(mangaUrl, "/g/", "/");
                  System.out.println("ID/URL: " + mangaId + " / " + mangaUrl);
                  if (downloadedIds.contains(mangaId)) {
                    log.log("跳过下载过的漫画页：" + mangaUrl);
                    mangaCount++;
                    continue;
                  } else {
                    log.log("下载漫画册：" + mangaId);
                    boolean success = false;
                    int retries = 0;
                    while ((retries++ < 4) && !success) {
                      if (MangaDownloader.downloadGallery(mangaUrl, targetDir, log)) {
                        mangaCount++;
                        downloadedIds.add(mangaId);
                        MangaDownloader.appendUrl(downloaded, mangaId);
                        success = true;
                      } else {
                        log.log("下载暂停。服务器阻止下载：" + mangaUrl + ", 等待设定时间段后继续下载。。。");
                        try {
                          Thread.sleep((60 * MangaDownloader.sleepBase * 4) + (int) (Math.random() * 10 * MangaDownloader.sleepBase));
                        } catch (final Exception e) {
                          e.printStackTrace();
                          log.err(e.toString());
                        }
                      }
                    }
                    if (!success) {
                      log.err("下载中断（没有找到漫画页，请下载或等待最新软件）：" + mangaUrl);
                      return;
                    }
                  }
                }
              }
              break;
            }
          }
          reader.close();
          Thread.sleep((15 * MangaDownloader.sleepBase) + (int) (Math.random() * 10 * MangaDownloader.sleepBase));
        } catch (final Exception e) {
          e.printStackTrace();
          log.err("错误：" + e.toString());
        }
        pageNr++;
      } while (mangaCount > 0);
    } catch (final Exception e) {
      e.printStackTrace();
      log.err("错误：" + e.toString());
    }
  }

  public final static HttpURLConnection getUrlConnection(final String url) throws Exception {
    return MangaDownloader.getUrlConnection(url, false, null);
  }

  public final static HttpURLConnection getUrlConnection(final String url, final boolean post, final String output) throws IOException {
    int retries = 0;
    HttpURLConnection conn;
    while (true) {
      try {
        final URL urlObj = new URL(url);
        conn = (HttpURLConnection) urlObj.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        if (post) {
          conn.setRequestMethod("POST");
        }
        final String referer;
        final int pathIdx;
        if ((pathIdx = url.lastIndexOf('/')) > "https://".length()) {
          referer = url.substring(0, pathIdx);
        } else {
          referer = url;
        }
        conn.setRequestProperty("Referer", referer);
        final Set<String> keys = MangaDownloader.DEFAULT_CONN_HEADERS.keySet();
        for (final String k : keys) {
          final String value = MangaDownloader.DEFAULT_CONN_HEADERS.get(k);
          if (value != null) {
            conn.setRequestProperty(k, value);
          }
        }
        // conn.setUseCaches(false);
        if (output != null) {
          conn.setDoOutput(true);
          final BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
          out.write(output.getBytes(MangaDownloader.CHARSET_UTF8));
          out.close();
        }
        if (MangaDownloader.appendCookies(MangaDownloader.cookie, conn)) {
          MangaDownloader.putConnectionHeader("Cookie", MangaDownloader.cookie.toString());
        }
        break;
      } catch (final Throwable e) {
        // 连接中断
        System.err.println(e.toString());
        if (retries++ > 10) {
          throw new IOException(e);
        } else {
          try {
            Thread.sleep((60 * retries * MangaDownloader.sleepBase) + ((int) Math.random() * MangaDownloader.sleepBase * 60 * retries));
          } catch (final InterruptedException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
    return conn;
  }

  /**
   * @param args
   */
  public static void main(final String[] args) {
    String keyword = "chinese";
    String targetDir = "D:\\temp";
    if ((args != null) && (args.length > 0)) {
      targetDir = args[0];
      if (args.length > 1) {
        keyword = args[1];
      }
    }

    MangaDownloader.downloadSearchResult(keyword, targetDir, new MangaDownloader(), new boolean[] { true, true, false, false, true, false, false, false, true,
        false }, new boolean[] { true, true, true }, 5);
  }

  public final static InputStream openUrlInputStream(final String url) throws MalformedURLException, IOException {
    return MangaDownloader.openUrlInputStream(url, false, null);
  }

  public static final InputStream openUrlInputStream(final String url, final boolean post, final String output) throws IOException {
    return MangaDownloader.getUrlConnection(url, post, output).getInputStream();
  }

  public static final void putConnectionHeader(final String key, final String value) {
    MangaDownloader.DEFAULT_CONN_HEADERS.put(key, value);
  }

  private static void readDownloaded(final File downloaded, final Set<String> downloadedUrls) throws FileNotFoundException, IOException {
    final BufferedReader reader = new BufferedReader(new FileReader(downloaded));
    String line;
    while (null != (line = reader.readLine())) {
      downloadedUrls.add(line);
    }
    reader.close();
  }

  public static final void resetConnectionHeaders() {
    MangaDownloader.DEFAULT_CONN_HEADERS.clear();
    MangaDownloader.DEFAULT_CONN_HEADERS.put("User-Agent", MangaDownloader.userAgent);
    if (Helper.isNotEmptyOrNull(MangaDownloader.cookieString)) {
      MangaDownloader.cookie = new StringBuffer(MangaDownloader.cookieString);
      MangaDownloader.putConnectionHeader("Cookie", MangaDownloader.cookie.toString());
    }
    // DEFAULT_CONN_HEADERS.put("Cache-Control", "no-cache");
    // DEFAULT_CONN_HEADERS.put("Pragma", "no-cache");
  }

  @Override
  public void err(final String message) {
    System.err.println(message);
  }

  @Override
  public void log(final String message) {
    System.out.println(message);
  }

}
