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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MangaDownloader implements Logger {
	private static String targetDir = "D:\\temp";

	private static final Map<String, String> DEFAULT_CONN_HEADERS = new HashMap<String, String>();

	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	static {
		System.setProperty("http.keepAlive", "false");
		CookieHandler.setDefault(new CookieManager(null,
				CookiePolicy.ACCEPT_ALL));
		HttpURLConnection.setFollowRedirects(false);
		resetConnectionHeaders();
	}

	private static int lineCounter = 0;

	private static StringBuffer cookie = new StringBuffer(512);

	public static final int BUFFER_SIZE = 1024 * 1024 * 4;

	private static final ByteBuffer IO_BB = ByteBuffer
			.wrap(new byte[BUFFER_SIZE]);

	private static String analyzeLine(String line, int num, Logger log,
			boolean first) {
		String title = substringBetweenNarrow(line, "<h1>", "</h1>");
		if (isNotEmptyOrNull(title)) {
			title = title.replace('/', '_').replace('?', '_').replace('*', '_');
			File dir = new File(targetDir + File.separator + title);
			if (first) {
				log.log("漫画册名称：" + title);
			}
			dir.mkdirs();
			String nextUrl = substringBetweenNarrow(line, "<a href=\"", "-"
					+ num + "\"><img");
			if (isNotEmptyOrNull(nextUrl)) {
				nextUrl += "-" + num;
				String img = substringBetweenNarrow(
						line.substring(line.indexOf("<iframe")), "<img src=\"",
						"\" style=\"");
				if (isNotEmptyOrNull(img)) {
					try {
						String imgName = img.substring(img.lastIndexOf('/'))
								.replace('/', '_').replace('?', '_')
								.replace('*', '_');
						if (imgName.indexOf('=') != -1) {
							imgName = imgName.substring(imgName
									.lastIndexOf('=') + 1);
						}
						if (null != download(img,
								new File(dir, imgName).getAbsolutePath(),
								false, log)) {
							log.log("图片下载成功：" + img);
							try {
								if (lineCounter++ >= 5) {
									lineCounter = 0;
									Thread.sleep(30000 + (int) (Math.random() * 10000)); // 12000
								} else {
									Thread.sleep(5000 + (int) (Math.random() * 5000));
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
								log.err("错误：" + e.toString());
							}
						} else {
							log.log("跳过图片下载（文件已存在）：" + img);
							try {
								Thread.sleep(5000 + (int) (Math.random() * 5000));
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

	public static final boolean appendCookies(final StringBuffer cookie,
			final HttpURLConnection conn) {
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

	private static void appendUrl(File downloaded, String mangaUrl)
			throws IOException {
		FileWriter f = new FileWriter(downloaded, true);
		f.write(mangaUrl);
		f.write('\n');
		f.close();
	}

	public static String download(String url, String to, boolean overwrite,
			Logger log) throws IOException {
		final File toFile = new File(to);
		if (!overwrite && toFile.exists() && toFile.length() > 0) {
			return null;
		} else {
			OutputStream out = null;
			InputStream in = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(to));
				in = new BufferedInputStream(openUrlInputStream(url));
				write(in, out);
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

	public static boolean downloadGallery(String mangaUrl, String targetDir,
			Logger log) {
		try {
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(openUrlInputStream(mangaUrl),
							CHARSET_UTF8));
			String line;
			while (null != (line = reader.readLine())) {
				if (line.contains("<h1")) {
					String pageUrl = substringBetweenNarrow(line, "<a href=\"",
							"-1\">");
					if (isNotEmptyOrNull(pageUrl)) {
						pageUrl += "-1";
						downloadManga(pageUrl, targetDir, log);
						return true;
					}
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.err("错误：" + e.toString());
		}
		return false;
	}

	public static void downloadManga(String url, String targetDir, Logger log) {
		try {
			boolean first = true;
			int retries = 0;
			while (null != url) {
				log.log("下载网页：" + url);
				String lastUrl = url;
				String substring = url.substring(url.lastIndexOf('-') + 1);
				int num = Integer.parseInt(substring);
				try {
					final BufferedReader reader = new BufferedReader(
							new InputStreamReader(openUrlInputStream(url),
									CHARSET_UTF8));
					String line;
					while (null != (line = reader.readLine())) {
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
					if (url != null && !url.equals(lastUrl)) {
						// ok
					} else {
						if (url != null && url.equals(lastUrl)) {
							log.err("错误：网页内容不兼容，请下载或等待最新软件");
						}
						url = null;
					}
				} catch (IOException e) {
					Thread.sleep(50000 + (int) (Math.random() * 10000));
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
	}

	public static void downloadSearchResult(String keyword, String targetDir,
			Logger log) {
		int pageNr = 0;
		int mangaCount;
		File dir = new File(targetDir);
		dir.mkdirs();
		File downloaded = new File(targetDir + File.separator
				+ "md-downloaded.txt");
		Set<String> downloadedIds = new HashSet<String>();
		if (downloaded.isFile()) {
			try {
				readDownloaded(downloaded, downloadedIds);
			} catch (Exception e) {
				e.printStackTrace();
				log.err("错误：" + e.toString());
			}
		}

		String mainUrl = null;
		try {
			mainUrl = "http://g.e-hentai.org/?f_doujinshi=on&f_manga=on&f_asianporn=on&f_search="
					+ URLEncoder.encode(keyword, CHARSET_UTF8.name())
					+ "&f_srdd=5&f_sfdd=favall&f_sname=on&f_stags=on&f_sr=on&f_apply=Apply+Filter";

			do {
				mangaCount = 0;
				try {
					final BufferedReader reader = new BufferedReader(
							new InputStreamReader(openUrlInputStream(mainUrl
									+ "&page=" + pageNr), CHARSET_UTF8));
					String line;
					while (null != (line = reader.readLine())) {
						if (line.contains("Your IP")) {
							log.err("下载中断（网站保护措施已更新，请下载或等待最新软件）：" + line);
							return;
						} else if (line.contains("gallerytorrents.php")) {
							String[] split = line.split("imgicon.png");
							for (int i = 1; i < split.length; i++) {
								String mangaUrl = split[i];
								mangaUrl = substringBetween(mangaUrl,
										"<div class=\"it1\"><a href=\"", "\">");
								if (isNotEmptyOrNull(mangaUrl)) {
									String mangaId = substringBetween(mangaUrl,
											"/g/", "/");
									if (downloadedIds.contains(mangaId)) {
										log.log("跳过漫画册（已下载）：" + mangaId);
										mangaCount++;
										continue;
									} else {
										log.log("下载漫画册：" + mangaId);
										boolean success = false;
										int retries = 0;
										while (retries++ < 4 && !success) {
											if (downloadGallery(mangaUrl,
													targetDir, log)) {
												mangaCount++;
												downloadedIds.add(mangaId);
												appendUrl(downloaded, mangaId);
												success = true;
											} else {
												log.log("下载暂停。服务器阻止下载："
														+ mangaUrl);
												try {
													Thread.sleep(60000 * 4 + (int) (Math
															.random() * 10000));
												} catch (Exception e) {
													e.printStackTrace();
													log.err(e.toString());
												}
											}
										}
										if (!success) {
											log.err("下载中断（没有找到漫画页，请下载或等待最新软件）："
													+ mangaUrl);
											return;
										}
									}
								}
							}
							break;
						}
					}
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
					log.err("错误：" + e.toString());
				}
				pageNr++;
			} while (mangaCount > 0);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.err("错误：" + e.toString());
		}
	}

	public final static HttpURLConnection getUrlConnection(final String url)
			throws MalformedURLException, IOException {
		return getUrlConnection(url, false, null);
	}

	public final static HttpURLConnection getUrlConnection(final String url,
			final boolean post, final String output)
			throws MalformedURLException, IOException {
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
			BufferedOutputStream out = new BufferedOutputStream(
					conn.getOutputStream());
			out.write(output.getBytes(CHARSET_UTF8));
			out.close();
		}
		if (appendCookies(cookie, conn)) {
			putConnectionHeader("Cookie", cookie.toString());
		}
		return conn;
	}

	public static boolean isEmptyOrNull(String text) {
		return text == null || text.isEmpty();
	}

	public final static boolean isNotEmptyOrNull(String text) {
		return text != null && text.length() > 0;
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

		downloadSearchResult(keyword, targetDir, new MangaDownloader());
	}

	public final static InputStream openUrlInputStream(final String url)
			throws MalformedURLException, IOException {
		return openUrlInputStream(url, false, null);
	}

	public static final InputStream openUrlInputStream(final String url,
			final boolean post, final String output) throws IOException {
		return getUrlConnection(url, post, output).getInputStream();
	}

	public static final void putConnectionHeader(final String key,
			final String value) {
		DEFAULT_CONN_HEADERS.put(key, value);
	}

	private static void readDownloaded(File downloaded,
			Set<String> downloadedUrls) throws FileNotFoundException,
			IOException {
		BufferedReader reader = new BufferedReader(new FileReader(downloaded));
		String line;
		while (null != (line = reader.readLine())) {
			downloadedUrls.add(line);
		}
		reader.close();
	}

	public static final void resetConnectionHeaders() {
		DEFAULT_CONN_HEADERS.clear();
		DEFAULT_CONN_HEADERS.put("User-Agent",
				"Mozilla/5.0 (Windows NT " + (((int) (Math.random() * 2) + 5))
						+ ".1) Firefox/" + (((int) (Math.random() * 8) + 3))
						+ "." + (((int) (Math.random() * 6) + 0)) + "."
						+ (((int) (Math.random() * 6) + 0)));
		// DEFAULT_CONN_HEADERS.put("Cache-Control", "no-cache");
		// DEFAULT_CONN_HEADERS.put("Pragma", "no-cache");
	}

	public static final String substringBetween(final String text,
			final String start, final String end) {
		return substringBetween(text, start, end, true);
	}

	public static final String substringBetween(final String text,
			final String start, final String end, final boolean trim) {
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

	public static String substringBetweenNarrow(String text, String start,
			String end) {
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

	public static final void write(final InputStream in, final OutputStream out)
			throws IOException {
		int len;
		while ((len = in.read(IO_BB.array())) > 0) {
			out.write(IO_BB.array(), 0, len);
		}
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
