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

public class MangaDownloader implements Logger {
	public static int													sleepBase							= 1000;

	public static String											userAgent							= "Mozilla/5.0 (Windows NT " + (((int) (Math.random() * 2) + 5)) + ".1) Firefox/"
																																			+ (((int) (Math.random() * 8) + 3)) + "." + (((int) (Math.random() * 6) + 0)) + "."
																																			+ (((int) (Math.random() * 6) + 0));

	public static String											cookieString					= "";

	private static String											targetDir							= "D:\\temp";

	private static boolean										dump									= false;

	private static final Map<String, String>	DEFAULT_CONN_HEADERS	= new HashMap<String, String>();

	public static final Charset								CHARSET_UTF8					= Charset.forName("UTF-8");

	static {
		System.setProperty("http.keepAlive", "false");
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		HttpURLConnection.setFollowRedirects(false);
		resetConnectionHeaders();
	}

	private static int												lineCounter						= 0;

	private static StringBuffer								cookie								= new StringBuffer(512);

	protected static String										searchParams					= "";

	private static String analyzeLine(String line, int num, Logger log, boolean first) {
		String title = Helper.substringBetweenNarrow(line, "<h1>", "</h1>");
		if (Helper.isNotEmptyOrNull(title)) {
			title = Helper.escapeFileName(title);
			File dir = new File(targetDir + File.separator + title);
			if (first) {
				log.log("漫画册名称：" + title);
			}
			dir.mkdirs();
			String nextUrl = Helper.substringBetweenNarrow(line, "<a href=\"", "-" + num + "\"><img");
			if (Helper.isNotEmptyOrNull(nextUrl)) {
				nextUrl += "-" + num;
				String img = Helper.substringBetweenNarrow(line.substring(line.indexOf("<iframe")), "<img src=\"", "\" style=\"");
				if (Helper.isNotEmptyOrNull(img)) {
					try {
						String imgName = img.substring(img.lastIndexOf('/'));
						if (imgName.indexOf('=') != -1) {
							imgName = imgName.substring(imgName.lastIndexOf('=') + 1);
						}
						imgName = Helper.escapeFileName(imgName);
						if (null != download(img, new File(dir, imgName).getAbsolutePath(), false, log)) {
							log.log("图片下载成功：" + img);
							try {
								if (lineCounter++ >= 5) {
									lineCounter = 0;
									Thread.sleep(40 * sleepBase + (int) (Math.random() * 12 * sleepBase)); // 12000
								} else {
									Thread.sleep(8 * sleepBase + (int) (Math.random() * 5 * sleepBase));
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
								log.err("错误：" + e.toString());
							}
						} else {
							log.log("跳过图片下载（文件已存在）：" + img);
							try {
								Thread.sleep(5 * sleepBase + (int) (Math.random() * 5 * sleepBase));
							} catch (InterruptedException e) {
								e.printStackTrace();
								log.err("错误：" + e.toString());
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						log.err("错误：" + e.toString());
					}
					return nextUrl;
				}
			}
		}
		return null;
	}

	public static final boolean appendCookies(final StringBuffer cookie, final HttpURLConnection conn) {
		boolean changed = false;
		List<String> values = conn.getHeaderFields().get("Set-Cookie");
		if (values != null) {
			for (String v : values) {
				if (v.indexOf("deleted") == -1) {
					if (cookie.length() > 0) {
						cookie.append("; ");
					}
					cookie.append(v.split(";")[0]);
					changed = true;
				}
			}
		}
		return changed;
	}

	private static void appendUrl(File downloaded, String mangaUrl) throws IOException {
		FileWriter f = new FileWriter(downloaded, true);
		f.write(mangaUrl);
		f.write('\n');
		f.close();
	}

	public static String download(String url, String to, boolean overwrite, Logger log) throws IOException {
		final File toFile = new File(to);
		// 925 -> forbidden file
		if (!overwrite && toFile.exists() && toFile.length() > 0 && toFile.length() != 925) {
			return null;
		} else {
			OutputStream out = null;
			InputStream in = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(to));
				in = new BufferedInputStream(openUrlInputStream(url));
				Helper.write(in, out);
			} catch (IOException e) {
				toFile.delete();
				e.printStackTrace();
				log.err("错误：" + e.toString());
				throw e;
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
			return to;
		}
	}

	public static boolean downloadGallery(String mangaUrl, String targetDir, Logger log) {
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(openUrlInputStream(mangaUrl), CHARSET_UTF8));
			String line;
			boolean success = false;
			while (null != (line = reader.readLine())) {
				checkAndDump(line, log);
				if (line.contains("<h1")) {
					String pageUrl = Helper.substringBetweenNarrow(line, "<a href=\"", "-1\">");
					if (Helper.isNotEmptyOrNull(pageUrl)) {
						pageUrl += "-1";
						if (downloadManga(pageUrl, targetDir, log)) {
							success = true;
							break;
						}
					}
					break;
				}
			}
			reader.close();
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			log.err("错误：" + e.toString());
		}
		return false;
	}

	private final static void checkAndDump(String line, Logger log) {
		if (line.contains("An Error Has Occurred")) {
			try {
				Thread.sleep(60 * sleepBase + (int) (Math.random() * 120 * sleepBase));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (line.contains("your IP address")) {
			log.err("服务器要求放慢速度：" + line);
			try {
				Thread.sleep(60 * sleepBase + (int) (Math.random() * 120 * sleepBase));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (dump) {
			try {
				File dumpFile = new File(targetDir + File.separator + "exia-dump.xml");
				if (!dumpFile.isFile()) {
					log.err("dump: " + dumpFile.getAbsolutePath());
				}
				FileWriter writer = new FileWriter(dumpFile, true);

				writer.append(line);
				writer.append('\n');
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean downloadManga(String url, String targetDir, Logger log) {
		try {
			boolean first = true;
			int retries = 0;
			while (null != url) {
				log.log("下载网页：" + url);
				String lastUrl = url;
				String substring = url.substring(url.lastIndexOf('-') + 1);
				int num = Integer.parseInt(substring);
				try {
					final BufferedReader reader = new BufferedReader(new InputStreamReader(openUrlInputStream(url), CHARSET_UTF8));
					String line;
					while (null != (line = reader.readLine())) {
						checkAndDump(line, log);
						if (line.contains("<h1>")) {
							try {
								url = analyzeLine(line, num + 1, log, first);
								first = false;
							} catch (Throwable e) {
								log.err("错误：" + e.toString());
							}
							break;
						}
					}
					reader.close();
					if (url == null) {
						dump = false;
						return true;
					}
					if (!url.equals(lastUrl)) {
						// ok
						dump = false;
					} else {
						log.err("错误：网页内容不兼容，请下载或等待最新软件");
						dump = true;
						url = null;
					}
				} catch (IOException e) {
					Thread.sleep(60 * sleepBase + (int) (Math.random() * 10 * sleepBase));
					if (retries++ > 2) {
						throw e;
					} else {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.err("错误：" + e.toString());
		}
		return false;
	}

	public static void downloadSearchResult(String keyword, String targetDir, Logger log, boolean[] optionsTypes, boolean[] optionsSearchFields, int minimumStars) {
		int pageNr = 0;
		int mangaCount;
		File dir = new File(targetDir);
		dir.mkdirs();
		File dumpFile = new File(targetDir + File.separator + "exia-dump.xml");
		dumpFile.delete();
		File downloaded = new File(targetDir + File.separator + "md-downloaded.txt");
		Set<String> downloadedIds = new HashSet<String>();
		if (downloaded.isFile()) {
			try {
				readDownloaded(downloaded, downloadedIds);
			} catch (Exception e) {
				e.printStackTrace();
				log.err("错误：" + e.toString());
			}
		}

		try {
			String mainUrl = createMainUrl(keyword, optionsTypes, optionsSearchFields, minimumStars);

			do {
				mangaCount = 0;
				try {
					String requestUrl = mainUrl + "&page=" + pageNr;
					System.out.println("request: " + requestUrl);

					final BufferedReader reader = new BufferedReader(new InputStreamReader(openUrlInputStream(requestUrl), CHARSET_UTF8));
					String line;
					while (null != (line = reader.readLine())) {
						checkAndDump(line, log);
						if (line.contains("Your IP")) {
							log.err("下载中断（网站保护措施已更新，请下载或等待最新软件）：" + line);
							return;
						} else if (line.contains("gallerytorrents.php")) {
							String[] split = line.split("imgicon.png");
							for (int i = 1; i < split.length; i++) {
								String mangaUrl = split[i];
								mangaUrl = Helper.substringBetween(mangaUrl, "<div class=\"it1\"><a href=\"", "\">");
								if (Helper.isNotEmptyOrNull(mangaUrl)) {
									String mangaId = Helper.substringBetween(mangaUrl, "/g/", "/");
									System.out.println(mangaId + ": " + mangaUrl);
									if (downloadedIds.contains(mangaId)) {
										log.log("跳过漫画册（已下载）：" + mangaId);
										mangaCount++;
										continue;
									} else {
										log.log("下载漫画册：" + mangaId);
										boolean success = false;
										int retries = 0;
										while (retries++ < 4 && !success) {
											if (downloadGallery(mangaUrl, targetDir, log)) {
												mangaCount++;
												downloadedIds.add(mangaId);
												appendUrl(downloaded, mangaId);
												success = true;
											} else {
												log.log("下载暂停。服务器阻止下载：" + mangaUrl);
												try {
													Thread.sleep(60 * sleepBase * 4 + (int) (Math.random() * 10 * sleepBase));
												} catch (Exception e) {
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
					Thread.sleep(15 * sleepBase + (int) (Math.random() * 10 * sleepBase));
				} catch (Exception e) {
					e.printStackTrace();
					log.err("错误：" + e.toString());
				}
				pageNr++;
			} while (mangaCount > 0);
		} catch (Exception e) {
			e.printStackTrace();
			log.err("错误：" + e.toString());
		}
	}

	private static String createMainUrl(String keyword, boolean[] optionsTypes, boolean[] optionsSearchFields, int minimumStars)
			throws UnsupportedEncodingException {
		StringBuffer url = new StringBuffer(200);
		url.append("http://g.e-hentai.org/?");
		url.append("f_search=").append(URLEncoder.encode(keyword, CHARSET_UTF8.name()));
		if (minimumStars > 1) {
			url.append("&f_sr=on&f_srdd=").append(String.valueOf(minimumStars));
		}
		for (int i = 0; i < optionsTypes.length; i++) {
			boolean b = optionsTypes[i];
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
				}
			}
		}
		url.append("&f_sfdd=favall");
		for (int i = 0; i < optionsSearchFields.length; i++) {
			boolean b = optionsSearchFields[i];
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
				}
			}
		}
		if (Helper.isNotEmptyOrNull(searchParams)) {
			if (!searchParams.startsWith("&")) {
				url.append('&');
			}
			url.append(searchParams);
		}
		url.append("&f_apply=Apply+Filter");
		return url.toString();
	}

	public final static HttpURLConnection getUrlConnection(final String url) throws MalformedURLException, IOException {
		return getUrlConnection(url, false, null);
	}

	public final static HttpURLConnection getUrlConnection(final String url, final boolean post, final String output) throws MalformedURLException, IOException {
		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
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
		final Set<String> keys = DEFAULT_CONN_HEADERS.keySet();
		for (String k : keys) {
			final String value = DEFAULT_CONN_HEADERS.get(k);
			if (value != null) {
				conn.setRequestProperty(k, value);
			}
		}
		// conn.setUseCaches(false);
		if (output != null) {
			conn.setDoOutput(true);
			BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
			out.write(output.getBytes(CHARSET_UTF8));
			out.close();
		}
		if (appendCookies(cookie, conn)) {
			putConnectionHeader("Cookie", cookie.toString());
		}
		return conn;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String keyword = "chinese";
		if (args != null && args.length > 0) {
			targetDir = args[0];
			if (args.length > 1) {
				keyword = args[1];
			}
		}

		downloadSearchResult(keyword, targetDir, new MangaDownloader(), new boolean[] { true, true, false, false, true, false, false, false, true, false },
				new boolean[] { true, true, true }, 5);
	}

	public final static InputStream openUrlInputStream(final String url) throws MalformedURLException, IOException {
		return openUrlInputStream(url, false, null);
	}

	public static final InputStream openUrlInputStream(final String url, final boolean post, final String output) throws IOException {
		return getUrlConnection(url, post, output).getInputStream();
	}

	public static final void putConnectionHeader(final String key, final String value) {
		DEFAULT_CONN_HEADERS.put(key, value);
	}

	private static void readDownloaded(File downloaded, Set<String> downloadedUrls) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(downloaded));
		String line;
		while (null != (line = reader.readLine())) {
			downloadedUrls.add(line);
		}
		reader.close();
	}

	public static final void resetConnectionHeaders() {
		DEFAULT_CONN_HEADERS.clear();
		DEFAULT_CONN_HEADERS.put("User-Agent", userAgent);
		if (Helper.isNotEmptyOrNull(cookieString)) {
			cookie = new StringBuffer(cookieString);
			putConnectionHeader("Cookie", cookie.toString());
		}
		// DEFAULT_CONN_HEADERS.put("Cache-Control", "no-cache");
		// DEFAULT_CONN_HEADERS.put("Pragma", "no-cache");
	}

	@Override
	public void err(String message) {
		System.err.println(message);
	}

	@Override
	public void log(String message) {
		System.out.println(message);
	}

}
