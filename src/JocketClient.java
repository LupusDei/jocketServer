import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;

public class JocketClient
{
  public int myPort;
  private Socket mySocket;

  public JocketClient(int port)
  {
    myPort = port;
  }

  public String getHomePage() throws IOException
  {
    if (mySocket == null)
      return null;
    PrintStream ps = JocketService.getPrintStream(mySocket);
    ps.println(formGETReqest("/index.html"));
    BufferedReader br = JocketService.getBufferedReader(mySocket);
    return br.readLine();
  }

  public boolean connect(String hostName) throws IOException
  {
    try
    {
      mySocket = new Socket(hostName, myPort);
    }
    catch (ConnectException e)
    {
      return false;
    }
    return true;
  }

  public String formGETReqest(String relativePath)
  {
    StringBuffer request = new StringBuffer("GET " + relativePath + " HTTP/1.1\nHost: localhost:" + myPort + "\n\r\n");
    return request.toString();
  }
}
