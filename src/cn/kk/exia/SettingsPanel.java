package cn.kk.exia;

import javax.swing.JOptionPane;

public class SettingsPanel extends javax.swing.JPanel {
	private ClosableDialog	dialog;

	private int							action;

	/**
	 * Creates new form Settings
	 * 
	 * @param dialog
	 * @param searchParams
	 * @param sleepFactor
	 * @param cookie
	 * @param userAgent
	 */
	public SettingsPanel(ClosableDialog dialog, String userAgent, String cookie, int sleepFactor, String searchParams) {
		this.dialog = dialog;
		initComponents();
		tfUserAgent.setText(userAgent);
		tfCookie.setText(cookie);
		tfSearchParams.setText(searchParams);
		sliderSleepFactor.setValue(sleepFactor);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		lblCookie = new javax.swing.JLabel();
		tfCookie = new javax.swing.JTextField();
		lblCookieDescription = new javax.swing.JLabel();
		lblUserAgentDescription = new javax.swing.JLabel();
		lblUserAgent = new javax.swing.JLabel();
		tfUserAgent = new javax.swing.JTextField();
		lblSleepFactorDescription = new javax.swing.JLabel();
		lblSleepFactor = new javax.swing.JLabel();
		sliderSleepFactor = new javax.swing.JSlider();
		lblSearchParamsDescription = new javax.swing.JLabel();
		lblSearchParams = new javax.swing.JLabel();
		tfSearchParams = new javax.swing.JTextField();
		btnApply = new javax.swing.JButton();
		btnReset = new javax.swing.JButton();

		lblCookie.setText("Cookie");
		lblCookie.setMaximumSize(new java.awt.Dimension(54, 14));
		lblCookie.setMinimumSize(new java.awt.Dimension(54, 14));
		lblCookie.setPreferredSize(new java.awt.Dimension(54, 14));

		lblCookieDescription.setText("通过Cookie服务器可以知道您的身份及其它信息（请在百度里查询怎样导出Cookie）");

		lblUserAgentDescription.setText("服务器会针对不同的浏览器用户代理返回不同的页面，如 Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");

		lblUserAgent.setText("User Agent");

		lblSleepFactorDescription.setText("等待时间控制。注：如修改后下载经常出错请改回原值。值越小等待时间越短。（初始值：1000）");

		lblSleepFactor.setText("等待速度");
		lblSleepFactor.setPreferredSize(new java.awt.Dimension(54, 14));

		sliderSleepFactor.setMajorTickSpacing(500);
		sliderSleepFactor.setMaximum(2000);
		sliderSleepFactor.setMinorTickSpacing(100);
		sliderSleepFactor.setPaintLabels(true);
		sliderSleepFactor.setPaintTicks(true);
		sliderSleepFactor.setSnapToTicks(true);
		sliderSleepFactor.setValue(1000);

		lblSearchParamsDescription.setText("在搜索模式里添加其它搜索参数");

		lblSearchParams.setText("搜索参数");
		lblSearchParams.setMaximumSize(new java.awt.Dimension(54, 14));
		lblSearchParams.setMinimumSize(new java.awt.Dimension(54, 14));
		lblSearchParams.setPreferredSize(new java.awt.Dimension(54, 14));

		btnApply.setText("保存");
		btnApply.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnApplyActionPerformed(evt);
			}
		});

		btnReset.setText("重置");
		btnReset.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnResetActionPerformed(evt);
			}
		});
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												layout.createSequentialGroup().addComponent(lblUserAgent).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(tfUserAgent))
										.addGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup()
														.addComponent(lblSearchParams, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(tfSearchParams, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(
												layout
														.createSequentialGroup()
														.addComponent(lblCookie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(tfCookie))
										.addGroup(
												layout
														.createSequentialGroup()
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblCookieDescription)
																		.addComponent(lblUserAgentDescription).addComponent(lblSleepFactorDescription).addComponent(lblSearchParamsDescription))
														.addGap(0, 0, Short.MAX_VALUE))
										.addGroup(
												layout
														.createSequentialGroup()
														.addComponent(lblSleepFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(sliderSleepFactor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(btnReset)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnApply))).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblCookieDescription)
						.addGap(8, 8, 8)
						.addGroup(
								layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(lblCookie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(tfCookie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblUserAgentDescription)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(lblUserAgent)
										.addComponent(tfUserAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblSleepFactorDescription)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(lblSleepFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(sliderSleepFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblSearchParamsDescription)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(lblSearchParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(tfSearchParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnApply).addComponent(btnReset))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}// </editor-fold>

	private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {
		sliderSleepFactor.setValue(1000);
		tfCookie.setText("");
		tfSearchParams.setText("");
		tfUserAgent.setText("Mozilla/5.0 (Windows NT " + (((int) (Math.random() * 2) + 5)) + ".1) Firefox/" + (((int) (Math.random() * 8) + 3)) + "."
				+ (((int) (Math.random() * 6) + 0)) + "." + (((int) (Math.random() * 6) + 0)));
	}

	private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {
		action = JOptionPane.OK_OPTION;
		dialog.setVisible(false);
	}

	// Variables declaration - do not modify
	private javax.swing.JButton			btnApply;

	private javax.swing.JButton			btnReset;

	private javax.swing.JLabel			lblCookie;

	private javax.swing.JLabel			lblCookieDescription;

	private javax.swing.JLabel			lblSearchParams;

	private javax.swing.JLabel			lblSearchParamsDescription;

	private javax.swing.JLabel			lblSleepFactor;

	private javax.swing.JLabel			lblSleepFactorDescription;

	private javax.swing.JLabel			lblUserAgent;

	private javax.swing.JLabel			lblUserAgentDescription;

	private javax.swing.JSlider			sliderSleepFactor;

	private javax.swing.JTextField	tfCookie;

	private javax.swing.JTextField	tfSearchParams;

	private javax.swing.JTextField	tfUserAgent;

	// End of variables declaration

	public int getAction() {
		return action;
	}

	public String getUserAgent() {
		return tfUserAgent.getText();
	}

	public String getCookie() {
		return tfCookie.getText();
	}

	public int getSleepFactor() {
		return sliderSleepFactor.getValue();
	}

	public String getSearchParams() {
		return tfSearchParams.getText();
	}
}
