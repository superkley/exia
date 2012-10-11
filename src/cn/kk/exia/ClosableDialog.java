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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class ClosableDialog extends JDialog {
  private static final long serialVersionUID = -4682747886190477495L;

  public ClosableDialog() {
    this((Frame) null, false);
  }

  public ClosableDialog(final Frame owner) {
    this(owner, false);
  }

  public ClosableDialog(final Frame owner, final boolean modal) {
    this(owner, null, modal);
  }

  public ClosableDialog(final Frame owner, final String title) {
    this(owner, title, false);
  }

  public ClosableDialog(final Frame owner, final String title, final boolean modal) {
    super(owner, title, modal);
  }

  public ClosableDialog(final Dialog owner) {
    this(owner, false);
  }

  public ClosableDialog(final Dialog owner, final boolean modal) {
    this(owner, null, modal);
  }

  public ClosableDialog(final Dialog owner, final String title) {
    this(owner, title, false);
  }

  public ClosableDialog(final Dialog owner, final String title, final boolean modal) {
    super(owner, title, modal);
  }

  @Override
  protected JRootPane createRootPane() {
    final ActionListener actionListener = new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent actionEvent) {
        ClosableDialog.this.setVisible(false);
      }
    };
    final JRootPane pane = new JRootPane();
    final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    pane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    return pane;
  }
}
