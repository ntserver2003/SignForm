import canopus.esign.MessageBox;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import sun.misc.BASE64Encoder;






public class SignApplet
  extends Applet
{
  private Label lbPass;
  private TextField tfPass;
  private Button bSign;
  private String login = "ib_canopus";
  private String buttonTxt = "set signature and send";
  private String webServerStr = null;
  private String cfmPath = "SignVerify.cfm";
  
  private String message = "";
  private String idkey = "";
  private int doctype = 0;
  private int docid = 0;
  private boolean disable = false;
  private boolean debug = false;
  
  public SignApplet() {}
  
  public void init() {
    try {
      message = getParameter("Message");
      idkey = getParameter("IDKey");
      doctype = Integer.parseInt(getParameter("DocType"));
      docid = Integer.parseInt(getParameter("DocID"));
      disable = Boolean.valueOf(getParameter("Disable")).booleanValue();
      debug = Boolean.valueOf(getParameter("Debug")).booleanValue();
      

      URL hostURL = getCodeBase();
      webServerStr = (hostURL.toString() + cfmPath);
      

      setBackground(Color.lightGray);
      
      lbPass = new Label("Password");
      tfPass = new TextField(16);
      tfPass.setBackground(Color.white);
      tfPass.setEchoChar('*');
      bSign = new Button(buttonTxt);
      
      add(lbPass);
      add(tfPass);
      add(bSign);
      

      bSign.addActionListener(new SignApplet.SignButtonActionListener());
    }
    catch (Exception e) {
      log(e.toString());
    }
  }
  





  protected void signButtonAction()
  {
    boolean signed = false;
    String keystoreFolder = "";
    String keystoreName = "";
    

    try
    {
      FileDialog fd = new FileDialog(new Frame(), "Load keystore");
      fd.setMode(0);
      


      keystoreFolder = getKeystoreFolder();
      keystoreName = getKeystoreName();
      fd.setDirectory(keystoreFolder);
      fd.setFile(keystoreName);
      
      fd.show();
      String fname = fd.getFile();
      
      if (fname != null)
      {

        setKeystoreFile(fd.getDirectory(), fname);
        

        String signPass = tfPass.getText();
        
        KeyStore ks = KeyStore.getInstance("JKS", "SUN");
        FileInputStream is = new FileInputStream(fd.getDirectory() + fname);
        ks.load(is, signPass.toCharArray());
        
        Enumeration aliaslist = ks.aliases();
        while (aliaslist.hasMoreElements()) {
          String str = aliaslist.nextElement().toString();
          if (str.compareTo(login) == 0)
          {
            DSAPrivateKey priv = (DSAPrivateKey)ks.getKey(str, signPass.toCharArray());
            
            Signature sign = Signature.getInstance("SHA1withDSA");
            sign.initSign(priv);
            sign.update(message.getBytes());
            byte[] realSig = sign.sign();
            signed = true;
            

            submitSign(realSig);
            
            break;
          }
        }
        

        if (signed != true)
        {

          log("Alias not found");
        }
      } else {
        log("File not selected");
      }
    } catch (Exception e) {
      log(e.toString());
    }
  }
  
  void submitSign(byte[] sign) {
    String formString = "";
    String sgnstr = "";
    try
    {
      URL testServlet = new URL(webServerStr);
      HttpURLConnection servletConnection = (HttpURLConnection)testServlet.openConnection();
      

      servletConnection.setDoInput(true);
      servletConnection.setDoOutput(true);
      

      servletConnection.setUseCaches(false);
      servletConnection.setDefaultUseCaches(false);
      

      servletConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      
      servletConnection.setRequestMethod("POST");
      

      BASE64Encoder encoder = new BASE64Encoder();
      sgnstr = encoder.encode(sign);
      
      formString = "DocType=" + URLEncoder.encode(String.valueOf(doctype)) + "&" + "Sign=" + URLEncoder.encode(sgnstr);
      

      sendDocumentToServlet(servletConnection, formString);
      readServletResponse(servletConnection);
    }
    catch (Exception e) {
      log(e.toString());
    }
  }
  




  protected void sendDocumentToServlet(HttpURLConnection servletConnection, String formString)
  {
    try
    {
      OutputStream outputToServlet = servletConnection.getOutputStream();
      

      outputToServlet.write(formString.getBytes());
      
      outputToServlet.flush();
      outputToServlet.close();
    } catch (Exception e) {
      log(e.toString());
    }
  }
  
  protected void readServletResponse(HttpURLConnection servletConnection)
  {
    BufferedReader inFromServlet = null;
    
    try
    {
      inFromServlet = new BufferedReader(new InputStreamReader(servletConnection.getInputStream()));
      
      String str;
      if (null != (str = inFromServlet.readLine()))
      {
        if (str.startsWith("OK"))
        {
          remove(lbPass);
          remove(tfPass);
          remove(bSign);
          
          submitForm();
        }
        else {
          log(str);
          if (debug) {
            while (null != (str = inFromServlet.readLine())) {
              log("debug: " + str);
            }
          }
        }
      } else {
        log("No response from servlet");
      }
      inFromServlet.close();
    }
    catch (Exception e)
    {
      log(e.toString());
    }
  }
  
  protected void log(String msg) {
    MessageBox.createMessageBox(msg, "Sign applet", true);
  }
  
  protected void submitForm()
  {
    try {
      Class c = Class.forName("netscape.javascript.JSObject");
      Method[] ms = c.getMethods();
      Method getwin = null;
      Method eval = null;
      
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) {
          getwin = ms[i];
        } else if (ms[i].getName().compareTo("eval") == 0) {
          eval = ms[i];
        }
      }
      
      Object[] a = new Object[1];
      a[0] = this;
      Object win = getwin.invoke(c, a);
      a[0] = "f = document.forms['signform']";
      eval.invoke(win, a);
      
      a[0] = "inputTag = document. createElement('input') ";
      eval.invoke(win, a);
      a[0] = "inputTag.name = 'ToMessages'";
      eval.invoke(win, a);
      a[0] = "inputTag.type = 'hidden'";
      eval.invoke(win, a);
      a[0] = "f.appendChild(inputTag) ";
      eval.invoke(win, a);
      
      a[0] = "f.submit()";
      eval.invoke(win, a);
    }
    catch (Exception e) {
      log(e.toString());
    }
  }
  
  protected void setKeystoreFile(String folder, String name)
  {
    try
    {
      Calendar cal = Calendar.getInstance();
      cal.add(1, 1);
      String expires = "; expires=" + cal.getTime().toString();
      

      Class c = Class.forName("netscape.javascript.JSObject");
      Method[] ms = c.getMethods();
      Method getwin = null;
      Method getmem = null;
      Method setmem = null;
      
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) {
          getwin = ms[i];
        } else if (ms[i].getName().compareTo("getMember") == 0) {
          getmem = ms[i];
        } else if (ms[i].getName().compareTo("setMember") == 0) {
          setmem = ms[i];
        }
      }
      

      Object[] a = new Object[1];
      
      a[0] = this;
      Object win = getwin.invoke(c, a);
      
      a[0] = "document";
      Object doc = getmem.invoke(win, a);
      
      a = new Object[2];
      
      a[0] = "cookie";
      a[1] = ("storedir=" + folder + expires);
      setmem.invoke(doc, a);
      
      a[0] = "cookie";
      a[1] = ("storename=" + name + expires);
      setmem.invoke(doc, a);
    }
    catch (Exception e) {}
  }
  


  protected String getKeystoreFolder()
  {
    String result = "";
    String allCook = "";
    try {
      Class c = Class.forName("netscape.javascript.JSObject");
      Method[] ms = c.getMethods();
      Method getwin = null;
      Method getmem = null;
      Method setmem = null;
      
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) {
          getwin = ms[i];
        } else if (ms[i].getName().compareTo("getMember") == 0) {
          getmem = ms[i];
        }
      }
      

      Object[] a = new Object[1];
      
      a[0] = this;
      Object win = getwin.invoke(c, a);
      
      a[0] = "document";
      Object doc = getmem.invoke(win, a);
      
      a[0] = "cookie";
      allCook = (String)getmem.invoke(doc, a);
      
      String search = "storedir=";
      if (allCook.length() > 0) {
        int offset = allCook.indexOf(search);
        if (offset != -1) {
          offset += search.length();
          int end = allCook.indexOf(";", offset);
          if (end == -1) end = allCook.length();
          result = allCook.substring(offset, end);
        }
      }
    }
    catch (Exception e) {}finally {}
    
    label241:
    
    break label241;
  }
  
  protected String getKeystoreName()
  {
    String result = "";
    String allCook = "";
    try {
      Class c = Class.forName("netscape.javascript.JSObject");
      Method[] ms = c.getMethods();
      Method getwin = null;
      Method getmem = null;
      Method setmem = null;
      
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) {
          getwin = ms[i];
        } else if (ms[i].getName().compareTo("getMember") == 0) {
          getmem = ms[i];
        }
      }
      

      Object[] a = new Object[1];
      
      a[0] = this;
      Object win = getwin.invoke(c, a);
      
      a[0] = "document";
      Object doc = getmem.invoke(win, a);
      
      a[0] = "cookie";
      allCook = (String)getmem.invoke(doc, a);
      
      String search = "storename=";
      if (allCook.length() > 0) {
        int offset = allCook.indexOf(search);
        if (offset != -1) {
          offset += search.length();
          int end = allCook.indexOf(";", offset);
          if (end == -1) end = allCook.length();
          result = allCook.substring(offset, end);
        }
      }
    }
    catch (Exception e) {}finally {}
    
    label241:
    break label241;
  }
  
  protected void disableDocControls()
  {
    try
    {
      Class c = Class.forName("netscape.javascript.JSObject");
      Method[] ms = c.getMethods();
      Method getwin = null;
      Method eval = null;
      
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) {
          getwin = ms[i];
        } else if (ms[i].getName().compareTo("eval") == 0) {
          eval = ms[i];
        }
      }
      
      Object[] a = new Object[1];
      a[0] = this;
      Object win = getwin.invoke(c, a);
      a[0] = "for(i=0;i<document.forms.length;i++)for(j=0;j<document.forms[i].length;j++){document.forms[i].elements[j].disabled=true }";
      eval.invoke(win, a);
    }
    catch (Exception e) {
      log(e.toString());
    }
  }
  
  protected void enableDocControls() {
    try {
      Class c = Class.forName("netscape.javascript.JSObject");
      Method[] ms = c.getMethods();
      Method getwin = null;
      Method eval = null;
      
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) {
          getwin = ms[i];
        } else if (ms[i].getName().compareTo("eval") == 0) {
          eval = ms[i];
        }
      }
      
      Object[] a = new Object[1];
      a[0] = this;
      Object win = getwin.invoke(c, a);
      a[0] = "for(i=0;i<document.forms.length;i++)for(j=0;j<document.forms[i].length;j++){document.forms[i].elements[j].disabled=false}";
      eval.invoke(win, a);
    }
    catch (Exception e) {
      log(e.toString());
    }
  }
  
  class SignButtonActionListener
    implements ActionListener
  {
    SignButtonActionListener() {}
    
    public void actionPerformed(ActionEvent event)
    {
      if (disable) disableDocControls();
      signButtonAction();
      if (disable) enableDocControls();
    }
  }
}
