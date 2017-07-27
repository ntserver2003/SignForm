package canopus.esign;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import sun.misc.BASE64Decoder;











public class DataAccessor
{
  private Connection dbConn;
  
  public DataAccessor(String dbDriver, String dbURL, String userID, String passwd)
    throws ClassNotFoundException, SQLException
  {
    Class.forName(dbDriver);
    
    Properties connInfo = new Properties();
    connInfo.put("user", userID);
    connInfo.put("password", passwd);
    connInfo.put("charSet", "Cp1251");
    
    dbConn = DriverManager.getConnection(dbURL, connInfo);
  }
  
  public void disconnect()
  {
    try {
      dbConn.close();
    } catch (Exception e) {
      log(e.toString());
    }
  }
  
  public void destroy()
  {
    disconnect();
  }
  
  protected void log(String msg) {
    System.out.println(msg);
  }
  


  public byte[] getUserKey(String idkey)
    throws SQLException, IOException
  {
    byte[] publicKey = new byte[0];
    


    BASE64Decoder decoder = new BASE64Decoder();
    Statement getkeyStatement = dbConn.createStatement();
    ResultSet rs = getkeyStatement.executeQuery(" SELECT PublicKey = pk.KeyValue FROM RemoteUsers ru, InternetDSAPublicKey pk  WHERE  ru.ClientID = pk.ClientID AND ru.IDKey = '" + idkey + "'");
    
    while (rs.next()) {
      String pkBase64 = rs.getString("PublicKey");
      publicKey = decoder.decodeBuffer(pkBase64);
    }
    rs.close();
    getkeyStatement.close();
    return publicKey;
  }
  
  public int DocumentToMessage(Document doc)
    throws SQLException
  {
    int result = -3;
    
    CallableStatement procStatement = dbConn.prepareCall("{call sp_InternetDocSignatureOK(?,?,?,?,?)}");
    
    procStatement.setString(1, doc.getIDkey());
    procStatement.setInt(2, doc.getDocType());
    procStatement.setInt(3, doc.getDocID());
    procStatement.setString(4, doc.getSignatureBase64());
    

    procStatement.registerOutParameter(5, 4);
    procStatement.setInt(5, result);
    
    ResultSet rs = procStatement.executeQuery();
    while (rs.next()) {
      result = rs.getInt(1);
    }
    rs.close();
    procStatement.close();
    
    return result;
  }
  


  public void decTries(String idkey)
    throws SQLException, IOException
  {
    CallableStatement procStatement = dbConn.prepareCall("{call sp_InternetDecTriesLeft(?)}");
    
    procStatement.setString(1, idkey);
    
    procStatement.executeUpdate();
    
    procStatement.close();
  }
  
  public int updateUserKey(int clientID, String publicKey) throws SQLException
  {
    int result = 0;
    
    CallableStatement updateStatement = dbConn.prepareCall("{call sp_InternetNewDSAPublicKey(?,?,0)}");
    updateStatement.setInt(1, clientID);
    updateStatement.setString(2, publicKey);
    

    ResultSet rs = updateStatement.executeQuery();
    while (rs.next()) {
      result = rs.getInt(1);
    }
    rs.close();
    updateStatement.close();
    return result;
  }
}
