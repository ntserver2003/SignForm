package canopus.esign;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MessageBox extends Dialog
{
  private Label labelMsg;
  private Button buttonOk;
  protected static Frame createdFrame;
  
  public MessageBox(String msg, Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    

    ClassLoader cl = getClass().getClassLoader();
    Image icon = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/mb.gif"));
    parent.setIconImage(icon);
    

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints constr = new GridBagConstraints();
    labelMsg = new Label(msg);
    buttonOk = new Button("  OK  ");
    
    setLayout(gridbag);
    anchor = 17;
    ipadx = 20;
    ipady = 20;
    weightx = 1.0D;
    weighty = 1.0D;
    gridwidth = 0;
    gridheight = -1;
    gridbag.setConstraints(labelMsg, constr);
    add(labelMsg);
    
    anchor = 10;
    ipadx = 0;
    ipady = 0;
    weightx = 0.0D;
    weighty = 0.0D;
    gridwidth = 0;
    gridheight = 1;
    gridbag.setConstraints(buttonOk, constr);
    add(buttonOk);
    gridbag.setConstraints(buttonOk, constr);
    
    pack();
    

    Toolkit kit = getToolkit();
    Dimension wndSize = kit.getScreenSize();
    setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);
    

    EvtWindow adapterWindow = new EvtWindow();
    addWindowListener(adapterWindow);
    BtnAction eventAction = new BtnAction();
    buttonOk.addActionListener(eventAction);
  }
  
  class EvtWindow extends WindowAdapter
  {
    EvtWindow() {}
    
    public void windowClosing(WindowEvent event) {
      Object object = event.getSource();
      if (object == MessageBox.this) {
        mbWindowClosing(event);
      }
    }
  }
  
  void mbWindowClosing(WindowEvent event) { dispose(); }
  
  class BtnAction implements ActionListener {
    BtnAction() {}
    
    public void actionPerformed(ActionEvent event) {
      Object object = event.getSource();
      if (object == buttonOk) {
        okButtonAction();
      }
    }
  }
  
  protected void okButtonAction() {
    setVisible(false);
    if (createdFrame != null) { createdFrame.hide();
    }
  }
  
  public static void createMessageBox(String msg, String title, boolean modal)
  {
    if (createdFrame == null) { createdFrame = new Frame();
    }
    MessageBox mb = new MessageBox(msg, createdFrame, title, modal);
    
    createdFrame.setSize(getSizewidth, getSizeheight);
    mb.show();
  }
}
