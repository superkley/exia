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
import java.awt.image.BufferedImage;
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
  private static final long   serialVersionUID = -905798657761130334L;

  private final static String cfgFile          = System.getProperty("user.home") + File.separator + "exia.cfg";

  private static final String helpTarget       = "本地文件夹，如：C:\\temp";

  private static final String HELP_TEXT        = "<html>例：<br>1. 关键词：如\"chinese\" <br>--> 下载所有包含此关键词的漫画册<br>2. 漫画册网址：http://g.e-hentai.org/g/494953/7c3ec35c08/ <br>--> 下载整本漫画 <br>3. 漫画图网址：http://g.e-hentai.org/s/14b9c859ed/493328-1 <br>--> 下载从本页开始所有的漫画 </html>";

  boolean[]                   optionsTypes;

  boolean[]                   optionsSearchFields;

  int                         minimumStars;

  String                      cookie           = MangaDownloader.cookieString;

  String                      userAgent        = MangaDownloader.userAgent;

  int                         sleepFactor      = MangaDownloader.sleepBase;

  String                      searchParams     = MangaDownloader.searchParams;

  /**
   * @param args
   *          the command line arguments
   */
  public static void main(final String args[]) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (final ClassNotFoundException ex) {
      ex.printStackTrace();
    } catch (final InstantiationException ex) {
      ex.printStackTrace();
    } catch (final IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (final javax.swing.UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    }

    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          final Main main = new Main();
          main.setVisible(true);
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              if (!main.updateProxyBtn()) {
                System.err.println("连接错误！");
              }
            }

          });
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  final StyledDocument            doc;

  // Variables declaration - do not modify
  javax.swing.JButton             btnDownload;

  private javax.swing.JButton     btnProxy;

  private javax.swing.JButton     btnSettings;

  private javax.swing.JButton     btnOptions;

  private javax.swing.JLabel      lblTarget;

  private javax.swing.JLabel      lblUrl;

  private javax.swing.JScrollPane spLog;

  private javax.swing.JTextField  tfTarget;

  private javax.swing.JTextField  tfUrl;

  javax.swing.JTextPane           tpLog;

  // End of variables declaration

  /**
   * Creates new form Find
   * 
   * @throws IOException
   */
  public Main() throws IOException {
    BufferedImage icon = ImageIO.read(this.getClass().getResource("/e-down.png"));
    this.setIconImage(icon);
    this.initComponents();

    this.optionsTypes = new boolean[10];
    this.optionsSearchFields = new boolean[3];
    this.minimumStars = 1;

    Arrays.fill(this.optionsTypes, true);
    Arrays.fill(this.optionsSearchFields, true);

    this.doc = this.tpLog.getStyledDocument();

    try {
      final Properties props = new Properties();
      final FileReader reader = new FileReader(Main.cfgFile);
      props.load(reader);
      reader.close();

      String val = props.getProperty("url");
      if (Helper.isNotEmptyOrNull(val)) {
        this.tfUrl.setText(val);
      }
      val = props.getProperty("target");
      if (Helper.isNotEmptyOrNull(val)) {
        this.tfTarget.setText(val);
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
        this.userAgent = val;
      }

      val = props.getProperty("cookie");
      if (Helper.isNotEmptyOrNull(val)) {
        this.cookie = val;
      }

      val = props.getProperty("searchparams");
      if (Helper.isNotEmptyOrNull(val)) {
        this.searchParams = val;
      }

      val = props.getProperty("sleepfactor");
      if (Helper.isNotEmptyOrNull(val)) {
        try {
          this.sleepFactor = Integer.parseInt(val);
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }

      val = props.getProperty("useragent");
      if (Helper.isNotEmptyOrNull(val)) {
        this.userAgent = val;
      }

      val = props.getProperty("stars");
      if (Helper.isNotEmptyOrNull(val)) {
        try {
          final int stars = Integer.parseInt(val);
          if ((stars > 0) && (stars < 6)) {
            this.minimumStars = stars;
          }
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }

      this.onUrlChanged();
      this.log("成功读取设置文件'" + Main.cfgFile + "'。");
    } catch (final Throwable t) {
      // ignore
    }
  }

  boolean updateProxyBtn() {
    this.btnDownload.setEnabled(false);
    System.setProperty("useSystemProxies", "false");
    final String host = System.getProperty("http.proxyHost");
    if ((host != null) && (host.length() > 0)) {
      if (Helper.checkPort()) {
        this.btnProxy.setBackground(Color.GREEN);
      } else {
        this.btnProxy.setBackground(Color.RED);
        this.err("网络连接错误：代理不可用");
      }
    } else {
      this.btnProxy.setBackground(this.btnDownload.getBackground());
    }
    if (!Helper.checkUrl("http://www.baidu.com")) {
      System.setProperty("useSystemProxies", "true");
      if (!Helper.checkUrl("http://www.baidu.com")) {
        this.err("网络连接错误：没有联网");
        return false;
      } else {
        this.log("使用系统代理。");
      }
    }
    this.btnDownload.setEnabled(true);
    return true;
  }

  void btnOptionsActionPerformed() {
    final ClosableDialog dialog = new ClosableDialog(this, "搜索参数设置", true);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setLocationRelativeTo(this);
    final OptionsPanel panel = new OptionsPanel(this.optionsTypes, this.optionsSearchFields, this.minimumStars);
    dialog.getContentPane().add(panel);
    dialog.pack();
    dialog.setResizable(false);
    dialog.setVisible(true);
    System.arraycopy(panel.getTypes(), 0, this.optionsTypes, 0, this.optionsTypes.length);
    System.arraycopy(panel.getSearchFields(), 0, this.optionsSearchFields, 0, this.optionsSearchFields.length);
    this.minimumStars = panel.getMinimumStars();
  }

  void btnSettingsActionPerformed() {
    final ClosableDialog dialog = new ClosableDialog(this, "高级设置", true);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setLocationRelativeTo(this);
    final SettingsPanel panel = new SettingsPanel(dialog, this.userAgent, this.cookie, this.sleepFactor, this.searchParams);
    dialog.getContentPane().add(panel);
    dialog.pack();
    dialog.setResizable(false);
    dialog.setVisible(true);
    if (panel.getAction() == JOptionPane.OK_OPTION) {
      this.userAgent = panel.getUserAgent();
      this.cookie = panel.getCookie();
      this.sleepFactor = panel.getSleepFactor();
      this.searchParams = panel.getSearchParams();
      MangaDownloader.resetConnectionHeaders();
    }
  }

  void btnProxyActionPerformed() {
    final ClosableDialog dialog = new ClosableDialog(this, "代理设置", true);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setLocationRelativeTo(this);
    final ProxyPanel panel = new ProxyPanel(dialog);
    dialog.getContentPane().add(panel);
    dialog.pack();
    dialog.setResizable(false);
    dialog.setVisible(true);
    if (panel.getAction() == JOptionPane.OK_OPTION) {
      final String host = panel.getHost();
      final String port = panel.getPort();
      final String user = panel.getUser();
      final String pass = panel.getPass();
      System.setProperty("http.proxyHost", host);
      System.setProperty("http.proxyPort", port);
      System.setProperty("http.proxyUser", user);
      System.setProperty("http.proxyPassword", pass);
      this.updateProxyBtn();
    }
  }

  void btnDownloadActionPerformed() {
    this.btnDownload.setEnabled(false);
    final String targetDir = this.tfTarget.getText();
    final String keyword = this.tfUrl.getText();
    new Thread() {
      @Override
      public void run() {
        try {
          MangaDownloader.userAgent = Main.this.userAgent;
          MangaDownloader.sleepBase = Main.this.sleepFactor;
          MangaDownloader.searchParams = Main.this.searchParams;
          MangaDownloader.cookieString = Main.this.cookie;
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
              Main.this.btnDownload.setEnabled(true);
            }
          });
        }
      }
    }.start();
  }

  @Override
  public synchronized void err(final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          final Style style = Main.this.doc.getStyle(StyleContext.DEFAULT_STYLE);
          StyleConstants.setForeground(style, Color.RED);
          Main.this.doc.insertString(Main.this.doc.getLength(), text + "\n", style);
          Main.this.tpLog.scrollRectToVisible(new Rectangle(0, Main.this.tpLog.getHeight() - 2, 1, 1));
        } catch (final BadLocationException ex) {
          System.err.println(ex.toString());
        }
      }
    });
  }

  void formWindowClosing() {// GEN-FIRST:event_formWindowClosing
    try {
      final Properties props = new Properties();
      props.put("url", this.tfUrl.getText());
      props.put("target", this.tfTarget.getText());
      props.put("host", Helper.chopNull(System.getProperty("http.proxyHost")));
      props.put("port", Helper.chopNull(System.getProperty("http.proxyPort")));
      props.put("user", Helper.chopNull(System.getProperty("http.proxyUser")));
      props.put("password", Helper.chopNull(System.getProperty("http.proxyPassword")));
      props.put("types", Helper.toString(this.optionsTypes));
      props.put("search", Helper.toString(this.optionsSearchFields));
      props.put("useragent", this.userAgent);
      props.put("cookie", this.cookie);
      props.put("searchparams", this.searchParams);
      props.put("sleepfactor", String.valueOf(this.sleepFactor));
      props.put("stars", String.valueOf(this.minimumStars));
      try {
        final FileWriter writer = new FileWriter(Main.cfgFile, false);
        props.store(writer, null);
        writer.close();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    } catch (final Throwable t) {
      t.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  public void onUrlChanged() {
    final String url = this.tfUrl.getText();
    if (Helper.isNotEmptyOrNull(url)) {
      if (url.startsWith("http://")) {
        this.btnOptions.setEnabled(false);
      } else {
        this.btnOptions.setEnabled(true);
      }
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">
  private void initComponents() {

    this.lblUrl = new javax.swing.JLabel();
    this.tfUrl = new javax.swing.JTextField();
    this.btnDownload = new javax.swing.JButton();
    this.lblTarget = new javax.swing.JLabel();
    this.tfTarget = new javax.swing.JTextField();
    this.spLog = new javax.swing.JScrollPane();
    this.tpLog = new javax.swing.JTextPane();
    this.btnProxy = new javax.swing.JButton();
    this.btnOptions = new javax.swing.JButton();
    this.btnSettings = new javax.swing.JButton();

    this.tfUrl.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(final DocumentEvent e) {
        Main.this.onUrlChanged();
      }

      @Override
      public void removeUpdate(final DocumentEvent e) {
        Main.this.onUrlChanged();
      }

      @Override
      public void insertUpdate(final DocumentEvent e) {
        Main.this.onUrlChanged();
      }
    });

    this.btnOptions.setText("参数");
    this.btnOptions.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        Main.this.btnOptionsActionPerformed();
      }
    });

    this.btnSettings.setText("设置");
    this.btnSettings.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        Main.this.btnSettingsActionPerformed();
      }
    });

    this.btnProxy.setText("代理");
    this.btnProxy.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        Main.this.btnProxyActionPerformed();
      }
    });
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        Main.this.formWindowClosing();
      }
    });
    this.setTitle("e下 （ 漫画流e-hentai.org\"慢\"速下载器）");

    this.lblUrl.setText("关键词或网址：");
    this.lblUrl.setToolTipText(Main.HELP_TEXT);

    this.tfUrl.setText("chinese");
    this.tfUrl.setToolTipText(Main.HELP_TEXT);

    this.btnDownload.setFont(this.btnDownload.getFont().deriveFont(this.btnDownload.getFont().getStyle() | java.awt.Font.BOLD));
    this.btnDownload.setText("下载");
    this.btnDownload.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        Main.this.btnDownloadActionPerformed();
      }
    });

    this.lblTarget.setText("保存至：");
    this.lblTarget.setToolTipText(Main.helpTarget);

    this.tfTarget.setText(System.getProperty("user.home"));
    this.tfTarget.setToolTipText(Main.helpTarget);

    this.spLog.setViewportView(this.tpLog);

    final GroupLayout layout = new GroupLayout(this.getContentPane());
    this.getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
        layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                layout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.spLog)
                    .addGroup(
                        layout
                            .createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.lblUrl).addComponent(this.lblTarget))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(
                                        layout.createSequentialGroup().addComponent(this.tfTarget, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(this.btnProxy, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(this.btnDownload, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(
                                        layout.createSequentialGroup().addComponent(this.tfUrl).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(this.btnOptions, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(this.btnSettings, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))))).addContainerGap()));
    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
        layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.lblUrl)
                    .addComponent(this.tfUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(this.btnOptions)
                    .addComponent(this.btnSettings))
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.lblTarget)
                    .addComponent(this.tfTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(this.btnProxy)
                    .addComponent(this.btnDownload)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(this.spLog, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE).addContainerGap()));

    this.pack();
  }// </editor-fold>

  @Override
  public synchronized void log(final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          final Style style = Main.this.doc.getStyle(StyleContext.DEFAULT_STYLE);
          StyleConstants.setForeground(style, Color.BLACK);
          Main.this.doc.insertString(Main.this.doc.getLength(), text + "\n", style);
          Main.this.tpLog.scrollRectToVisible(new Rectangle(0, Main.this.tpLog.getHeight() - 2, 1, 1));
        } catch (final BadLocationException ex) {
          System.err.println(ex.toString());
        }
      }
    });
  }
}
