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

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class Main extends javax.swing.JFrame implements Logger {
	private final static String	cfgFile				= System.getProperty("user.home") + File.separator + "exia.cfg";

	private static final String	helpTarget		= "本地文件夹，如：C:\\temp";

	private static final String	HELP_TEXT			= "<html>例：<br>1. 关键词：如\"chinese\" <br>--> 下载所有包含此关键词的漫画册<br>2. 漫画册网址：http://g.e-hentai.org/g/494953/7c3ec35c08/ <br>--> 下载整本漫画 <br>3. 漫画图网址：http://g.e-hentai.org/s/14b9c859ed/493328-1 <br>--> 下载从本页开始所有的漫画 </html>";

	private boolean[]						optionsTypes;

	private boolean[]						optionsSearchFields;

	private int									minimumStars;

	private String							cookie				= MangaDownloader.cookieString;

	private String							userAgent			= MangaDownloader.userAgent;

	private int									sleepFactor		= MangaDownloader.sleepBase;

	private String							searchParams	= MangaDownloader.searchParams;

	/**
	 * @param args
	 *          the command line arguments
	 */
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					new Main().setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private final StyledDocument		doc;

	// Variables declaration - do not modify
	private javax.swing.JButton			btnDownload;

	private javax.swing.JButton			btnProxy;

	private javax.swing.JButton			btnSettings;

	private javax.swing.JButton			btnOptions;

	private javax.swing.JLabel			lblTarget;

	private javax.swing.JLabel			lblUrl;

	private javax.swing.JScrollPane	spLog;

	private javax.swing.JTextField	tfTarget;

	private javax.swing.JTextField	tfUrl;

	private javax.swing.JTextPane		tpLog;

	// End of variables declaration

	/**
	 * Creates new form Find
	 * 
	 * @throws IOException
	 */
	public Main() throws IOException {
		setIconImage(ImageIO.read(getClass().getResource("/e-down.png")));
		initComponents();

		optionsTypes = new boolean[10];
		optionsSearchFields = new boolean[3];
		minimumStars = 1;

		Arrays.fill(optionsTypes, true);
		Arrays.fill(optionsSearchFields, true);

		doc = this.tpLog.getStyledDocument();

		try {
			Properties props = new Properties();
			props.load(new FileReader(cfgFile));

			String val = props.getProperty("url");
			if (Helper.isNotEmptyOrNull(val)) {
				tfUrl.setText(val);
			}
			val = props.getProperty("target");
			if (Helper.isNotEmptyOrNull(val)) {
				tfTarget.setText(val);
			}
			val = props.getProperty("host");
			if (Helper.isNotEmptyOrNull(val)) {
				System.setProperty("http.proxyHost", val);
			}
			val = props.getProperty("port");
			if (Helper.isNotEmptyOrNull(val)) {
				System.setProperty("http.proxyPort", val);
			}
			val = props.getProperty("user");
			if (Helper.isNotEmptyOrNull(val)) {
				System.setProperty("http.proxyUser", val);
			}
			val = props.getProperty("password");
			if (Helper.isNotEmptyOrNull(val)) {
				System.setProperty("http.proxyPassword", val);
			}

			val = props.getProperty("types");
			if (Helper.isNotEmptyOrNull(val)) {
				Helper.fromString(val, this.optionsTypes);
			}
			val = props.getProperty("search");
			if (Helper.isNotEmptyOrNull(val)) {
				Helper.fromString(val, this.optionsSearchFields);
			}

			val = props.getProperty("useragent");
			if (Helper.isNotEmptyOrNull(val)) {
				userAgent = val;
			}

			val = props.getProperty("cookie");
			if (Helper.isNotEmptyOrNull(val)) {
				cookie = val;
			}

			val = props.getProperty("searchparams");
			if (Helper.isNotEmptyOrNull(val)) {
				searchParams = val;
			}

			val = props.getProperty("sleepfactor");
			if (Helper.isNotEmptyOrNull(val)) {
				try {
					sleepFactor = Integer.parseInt(val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			val = props.getProperty("useragent");
			if (Helper.isNotEmptyOrNull(val)) {
				userAgent = val;
			}

			val = props.getProperty("stars");
			if (Helper.isNotEmptyOrNull(val)) {
				try {
					int stars = Integer.parseInt(val);
					if (stars > 0 && stars < 6) {
						this.minimumStars = stars;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			updateProxyBtn();

			onUrlChanged();
			log("成功读取设置文件'" + cfgFile + "'。");
		} catch (Throwable t) {
			// ignore
		}
	}

	private void updateProxyBtn() {
		btnDownload.setEnabled(false);
		System.setProperty("useSystemProxies", "false");
		String host = System.getProperty("http.proxyHost");
		if (host != null && host.length() > 0) {
			if (Helper.checkPort()) {
				btnProxy.setBackground(Color.GREEN);
			} else {
				btnProxy.setBackground(Color.RED);
				err("网络连接错误：代理不可用");
			}
		} else {
			btnProxy.setBackground(btnDownload.getBackground());
		}
		if (!Helper.checkUrl("http://www.baidu.com")) {
			System.setProperty("useSystemProxies", "true");
			if (!Helper.checkUrl("http://www.baidu.com")) {
				err("网络连接错误：没有联网");
				return;
			} else {
				log("使用系统代理。");
			}
		}
		btnDownload.setEnabled(true);
	}

	private void btnOptionsActionPerformed(java.awt.event.ActionEvent evt) {
		ClosableDialog dialog = new ClosableDialog(this, "搜索参数设置", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(this);
		OptionsPanel panel = new OptionsPanel(dialog, this.optionsTypes, this.optionsSearchFields, this.minimumStars);
		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setVisible(true);
		System.arraycopy(panel.getTypes(), 0, this.optionsTypes, 0, this.optionsTypes.length);
		System.arraycopy(panel.getSearchFields(), 0, this.optionsSearchFields, 0, this.optionsSearchFields.length);
		this.minimumStars = panel.getMinimumStars();
	}

	private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {
		ClosableDialog dialog = new ClosableDialog(this, "高级设置", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(this);
		SettingsPanel panel = new SettingsPanel(dialog, userAgent, cookie, sleepFactor, searchParams);
		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setVisible(true);
		if (panel.getAction() == JOptionPane.OK_OPTION) {
			userAgent = panel.getUserAgent();
			cookie = panel.getCookie();
			sleepFactor = panel.getSleepFactor();
			searchParams = panel.getSearchParams();
			MangaDownloader.resetConnectionHeaders();
		}
	}

	private void btnProxyActionPerformed(java.awt.event.ActionEvent evt) {
		ClosableDialog dialog = new ClosableDialog(this, "代理设置", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(this);
		ProxyPanel panel = new ProxyPanel(dialog);
		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setVisible(true);
		if (panel.getAction() == JOptionPane.OK_OPTION) {
			String host = panel.getHost();
			String port = panel.getPort();
			String user = panel.getUser();
			String pass = panel.getPass();
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", port);
			System.setProperty("http.proxyUser", user);
			System.setProperty("http.proxyPassword", pass);
			updateProxyBtn();
		}
	}

	private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {
		btnDownload.setEnabled(false);
		final String targetDir = tfTarget.getText();
		final String keyword = tfUrl.getText();
		new Thread() {
			@Override
			public void run() {
				try {
					MangaDownloader.userAgent = userAgent;
					MangaDownloader.sleepBase = sleepFactor;
					MangaDownloader.searchParams = searchParams;
					MangaDownloader.cookieString = cookie;
					if (-1 == keyword.indexOf("http://")) {
						MangaDownloader.downloadSearchResult(keyword, targetDir, Main.this, Main.this.optionsTypes, Main.this.optionsSearchFields, Main.this.minimumStars);
					} else if (-1 == keyword.indexOf("/g/")) {
						MangaDownloader.downloadManga(keyword, targetDir, Main.this);
					} else {
						MangaDownloader.downloadGallery(keyword, targetDir, Main.this);
					}
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							btnDownload.setEnabled(true);
						}
					});
				}
			}
		}.start();
	}

	public synchronized void err(final String text) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Style style = doc.getStyle(StyleContext.DEFAULT_STYLE);
					StyleConstants.setForeground(style, Color.RED);
					doc.insertString(doc.getLength(), text + "\n", style);
					tpLog.scrollRectToVisible(new Rectangle(0, tpLog.getHeight() - 2, 1, 1));
				} catch (BadLocationException ex) {
					System.err.println(ex.toString());
				}
			}
		});
	}

	private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
		try {
			Properties props = new Properties();
			props.put("url", tfUrl.getText());
			props.put("target", tfTarget.getText());
			props.put("host", Helper.chopNull(System.getProperty("http.proxyHost")));
			props.put("port", Helper.chopNull(System.getProperty("http.proxyPort")));
			props.put("user", Helper.chopNull(System.getProperty("http.proxyUser")));
			props.put("password", Helper.chopNull(System.getProperty("http.proxyPassword")));
			props.put("types", Helper.toString(this.optionsTypes));
			props.put("search", Helper.toString(this.optionsSearchFields));
			props.put("useragent", userAgent);
			props.put("cookie", cookie);
			props.put("searchparams", searchParams);
			props.put("sleepfactor", String.valueOf(sleepFactor));
			props.put("stars", String.valueOf(this.minimumStars));
			try {
				FileWriter writer = new FileWriter(cfgFile, false);
				props.store(writer, null);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void onUrlChanged() {
		String url = tfUrl.getText();
		if (Helper.isNotEmptyOrNull(url)) {
			if (url.startsWith("http://")) {
				btnOptions.setEnabled(false);
			} else {
				btnOptions.setEnabled(true);
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		lblUrl = new javax.swing.JLabel();
		tfUrl = new javax.swing.JTextField();
		btnDownload = new javax.swing.JButton();
		lblTarget = new javax.swing.JLabel();
		tfTarget = new javax.swing.JTextField();
		spLog = new javax.swing.JScrollPane();
		tpLog = new javax.swing.JTextPane();
		btnProxy = new javax.swing.JButton();
		btnOptions = new javax.swing.JButton();
		btnSettings = new javax.swing.JButton();

		tfUrl.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				onUrlChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				onUrlChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				onUrlChanged();
			}
		});

		btnOptions.setText("参数");
		btnOptions.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOptionsActionPerformed(evt);
			}
		});

		btnSettings.setText("设置");
		btnSettings.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSettingsActionPerformed(evt);
			}
		});

		btnProxy.setText("代理");
		btnProxy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnProxyActionPerformed(evt);
			}
		});
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});
		setTitle("e下 （ 漫画流e-hentai.org\"慢\"速下载器）");

		lblUrl.setText("关键词或网址：");
		lblUrl.setToolTipText(HELP_TEXT);

		tfUrl.setText("chinese");
		tfUrl.setToolTipText(HELP_TEXT);

		btnDownload.setFont(btnDownload.getFont().deriveFont(btnDownload.getFont().getStyle() | java.awt.Font.BOLD));
		btnDownload.setText("下载");
		btnDownload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnDownloadActionPerformed(evt);
			}
		});

		lblTarget.setText("保存至：");
		lblTarget.setToolTipText(helpTarget);

		tfTarget.setText(System.getProperty("user.home"));
		tfTarget.setToolTipText(helpTarget);

		spLog.setViewportView(tpLog);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				layout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout
										.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(spLog)
										.addGroup(
												layout
														.createSequentialGroup()
														.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lblUrl).addComponent(lblTarget))
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(
																layout
																		.createParallelGroup(GroupLayout.Alignment.LEADING)
																		.addGroup(
																				layout.createSequentialGroup().addComponent(tfTarget, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
																						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																						.addComponent(btnProxy, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
																						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																						.addComponent(btnDownload, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
																		.addGroup(
																				layout.createSequentialGroup().addComponent(tfUrl).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																						.addComponent(btnOptions, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
																						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																						.addComponent(btnSettings, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))))).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				layout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblUrl)
										.addComponent(tfUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnOptions)
										.addComponent(btnSettings))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblTarget)
										.addComponent(tfTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnProxy)
										.addComponent(btnDownload)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(spLog, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>

	public synchronized void log(final String text) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Style style = doc.getStyle(StyleContext.DEFAULT_STYLE);
					StyleConstants.setForeground(style, Color.BLACK);
					doc.insertString(doc.getLength(), text + "\n", style);
					tpLog.scrollRectToVisible(new Rectangle(0, tpLog.getHeight() - 2, 1, 1));
				} catch (BadLocationException ex) {
					System.err.println(ex.toString());
				}
			}
		});
	}
}
