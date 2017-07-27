package canopus.esign;

import java.io.Serializable;
import sun.misc.BASE64Encoder;











public class Document
  implements Serializable
{
  private int doctype;
  private byte[] message;
  private String idkey;
  private byte[] signature;
  private String fromaddress;
  private String subject;
  private int docid;
  
  public Document() {}
  
  public Document(String keyid, int type, int id, String msg, byte[] sign)
  {
    doctype = type;
    message = msg.getBytes();
    idkey = keyid;
    signature = sign;
    docid = id;
  }
  
  public int getDocType() {
    return doctype;
  }
  
  public byte[] getMessage() {
    return message;
  }
  
  public String getIDkey() {
    return idkey;
  }
  
  public byte[] getSignature() {
    return signature;
  }
  
  public String getSignatureBase64() {
    String result = "";
    try {
      BASE64Encoder encoder = new BASE64Encoder();
      result = encoder.encode(signature);
    }
    catch (Exception e) {}finally {}
    label43:
    break label43;
  }
  
  public int getDocID() {
    return docid;
  }
}
