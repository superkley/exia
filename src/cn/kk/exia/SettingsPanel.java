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

import javax.swing.JOptionPane;

public class SettingsPanel extends javax.swing.JPanel {
  private static final long    serialVersionUID = 8616510679134846912L;

  private final ClosableDialog dialog;

  private int                  action;

  /**
   * Creates new form Settings
   * 
   * @param dialog
   * @param searchParams
   * @param sleepFactor
   * @param cookie
   * @param userAgent
   */
  public SettingsPanel(final ClosableDialog dialog, final String userAgent, final String cookie, final int sleepFactor, final String searchParams) {
    this.dialog = dialog;
    this.initComponents();
    this.tfUserAgent.setText(userAgent);
    this.tfCookie.setText(cookie);
    this.tfSearchParams.setText(searchParams);
    this.sliderSleepFactor.setValue(sleepFactor);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">
  private void initComponents() {

    this.lblCookie = new javax.swing.JLabel();
    this.tfCookie = new javax.swing.JTextField();
    this.lblCookieDescription = new javax.swing.JLabel();
    this.lblUserAgentDescription = new javax.swing.JLabel();
    this.lblUserAgent = new javax.swing.JLabel();
    this.tfUserAgent = new javax.swing.JTextField();
    this.lblSleepFactorDescription = new javax.swing.JLabel();
    this.lblSleepFactor = new javax.swing.JLabel();
    this.sliderSleepFactor = new javax.swing.JSlider();
    this.lblSearchParamsDescription = new javax.swing.JLabel();
    this.lblSearchParams = new javax.swing.JLabel();
    this.tfSearchParams = new javax.swing.JTextField();
    this.btnApply = new javax.swing.JButton();
    this.btnReset = new javax.swing.JButton();

    this.lblCookie.setText("Cookie");
    this.lblCookie.setMaximumSize(new java.awt.Dimension(54, 14));
    this.lblCookie.setMinimumSize(new java.awt.Dimension(54, 14));
    this.lblCookie.setPreferredSize(new java.awt.Dimension(54, 14));

    this.lblCookieDescription.setText("通过Cookie服务器可以知道您的身份及其它信息（请在百度里查询怎样导出Cookie）");

    this.lblUserAgentDescription.setText("服务器会针对不同的浏览器用户代理返回不同的页面，如 Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");

    this.lblUserAgent.setText("User Agent");

    this.lblSleepFactorDescription.setText("等待时间控制。注：如修改后下载经常出错请改回原值。值越小等待时间越短。（初始值：1000）");

    this.lblSleepFactor.setText("等待速度");
    this.lblSleepFactor.setPreferredSize(new java.awt.Dimension(54, 14));

    this.sliderSleepFactor.setMajorTickSpacing(500);
    this.sliderSleepFactor.setMaximum(2000);
    this.sliderSleepFactor.setMinorTickSpacing(100);
    this.sliderSleepFactor.setPaintLabels(true);
    this.sliderSleepFactor.setPaintTicks(true);
    this.sliderSleepFactor.setSnapToTicks(true);
    this.sliderSleepFactor.setValue(1000);

    this.lblSearchParamsDescription.setText("在搜索模式里添加其它搜索参数");

    this.lblSearchParams.setText("搜索参数");
    this.lblSearchParams.setMaximumSize(new java.awt.Dimension(54, 14));
    this.lblSearchParams.setMinimumSize(new java.awt.Dimension(54, 14));
    this.lblSearchParams.setPreferredSize(new java.awt.Dimension(54, 14));

    this.btnApply.setText("保存");
    this.btnApply.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        SettingsPanel.this.btnApplyActionPerformed();
      }
    });

    this.btnReset.setText("重置");
    this.btnReset.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        SettingsPanel.this.btnResetActionPerformed();
      }
    });
    final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
        layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(
                        layout.createSequentialGroup().addComponent(this.lblUserAgent).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(this.tfUserAgent))
                    .addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup()
                            .addComponent(this.lblSearchParams, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(this.tfSearchParams, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(
                        layout
                            .createSequentialGroup()
                            .addComponent(this.lblCookie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(this.tfCookie))
                    .addGroup(
                        layout
                            .createSequentialGroup()
                            .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(this.lblCookieDescription)
                                    .addComponent(this.lblUserAgentDescription).addComponent(this.lblSleepFactorDescription)
                                    .addComponent(this.lblSearchParamsDescription)).addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(
                        layout
                            .createSequentialGroup()
                            .addComponent(this.lblSleepFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(this.sliderSleepFactor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(this.btnReset)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btnApply))).addContainerGap()));
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
        layout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(this.lblCookieDescription)
            .addGap(8, 8, 8)
            .addGroup(
                layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(this.lblCookie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(this.tfCookie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(this.lblUserAgentDescription)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(
                layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(this.lblUserAgent)
                    .addComponent(this.tfUserAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(this.lblSleepFactorDescription)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(
                layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(this.lblSleepFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(this.sliderSleepFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(this.lblSearchParamsDescription)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(
                layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(this.lblSearchParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(this.tfSearchParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(this.btnApply).addComponent(this.btnReset))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
  }// </editor-fold>

  void btnResetActionPerformed() {
    this.sliderSleepFactor.setValue(1000);
    this.tfCookie.setText("");
    this.tfSearchParams.setText("");
    this.tfUserAgent.setText("Mozilla/5.0 (Windows NT " + (((int) (Math.random() * 2) + 5)) + ".1) Firefox/" + (((int) (Math.random() * 8) + 3)) + "."
        + (((int) (Math.random() * 6) + 0)) + "." + (((int) (Math.random() * 6) + 0)));
  }

  void btnApplyActionPerformed() {
    this.action = JOptionPane.OK_OPTION;
    this.dialog.setVisible(false);
  }

  // Variables declaration - do not modify
  private javax.swing.JButton    btnApply;

  private javax.swing.JButton    btnReset;

  private javax.swing.JLabel     lblCookie;

  private javax.swing.JLabel     lblCookieDescription;

  private javax.swing.JLabel     lblSearchParams;

  private javax.swing.JLabel     lblSearchParamsDescription;

  private javax.swing.JLabel     lblSleepFactor;

  private javax.swing.JLabel     lblSleepFactorDescription;

  private javax.swing.JLabel     lblUserAgent;

  private javax.swing.JLabel     lblUserAgentDescription;

  private javax.swing.JSlider    sliderSleepFactor;

  private javax.swing.JTextField tfCookie;

  private javax.swing.JTextField tfSearchParams;

  private javax.swing.JTextField tfUserAgent;

  // End of variables declaration

  public int getAction() {
    return this.action;
  }

  public String getUserAgent() {
    return this.tfUserAgent.getText();
  }

  public String getCookie() {
    return this.tfCookie.getText();
  }

  public int getSleepFactor() {
    return this.sliderSleepFactor.getValue();
  }

  public String getSearchParams() {
    return this.tfSearchParams.getText();
  }
}
