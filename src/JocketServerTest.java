import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

public class JocketServerTest
{
  private JocketService servicer;
  private int counter;
  private JocketServer countingServer;

  @Before
  public void setUp()
  {
    servicer = new JocketService();
    counter = 0;
    countingServer = new JocketServer()
    {
      public void serve(Socket sock)
      {
        counter++;
      }
    };
  }

  @Test
  public void canMakeOneConnection() throws IOException
  {

    servicer.serve(31415, countingServer);
    connect(31415);
    servicer.close();
    assertEquals(1, counter);
  }

  @Test
  public void canMakeMoreThanOneConnection() throws IOException
  {
    servicer.serve(31415, countingServer);
    connect(31415);
    connect(31415);
    connect(31415);
    servicer.close();
    assertEquals(3, counter);
  }

  @Test
  public void canProcessASentMessage() throws IOException
  {
    servicer.serve(8080, new YapperServer());
    Socket sock = new Socket("localhost", 8080);
    BufferedReader br = JocketService.getBufferedReader(sock);
    String message = br.readLine();
    sock.close();
    servicer.close();
    assertEquals("YAP!", message);
  }

  @Test
  public void canRecieveAMessageFromTheClient() throws IOException
  {
    servicer.serve(1337, new ResponderServer());
    Socket sock = new Socket("localhost", 1337);
    BufferedReader br = JocketService.getBufferedReader(sock);
    PrintStream ps = JocketService.getPrintStream(sock);
    ps.println("This is the response");
    String message = br.readLine();
    sock.close();
    servicer.close();
    assertEquals("This is the response", message);
  }

  private void connect(int port)
  {
    try
    {
      Socket socket = new Socket("localhost", port);
      Thread.sleep(50);
      socket.close();
    }
    catch (IOException e)
    {
      fail("could not connect to port: " + port);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  private class YapperServer implements JocketServer
  {
    public void serve(Socket sock)
    {
      try
      {
        PrintStream ps = JocketService.getPrintStream(sock);
        ps.println("YAP!");
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private class ResponderServer implements JocketServer
  {
    public void serve(Socket sock)
    {
      try
      {
        BufferedReader br = JocketService.getBufferedReader(sock);
        PrintStream ps = JocketService.getPrintStream(sock);
        ps.println(br.readLine());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
