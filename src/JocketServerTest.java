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
  public void canMakeOneConnection() throws IOException, InterruptedException
  {

    servicer.serve(31415, countingServer);
    connect(31415);
    servicer.close();
    assertEquals(1, counter);
  }

  @Test
  public void canMakeMoreThanOneConnection() throws IOException, InterruptedException
  {
    servicer.serve(31415, countingServer);
    connect(31415);
    connect(31415);
    connect(31415);
    servicer.close();
    assertEquals(3, counter);
  }

  @Test
  public void canProcessASentMessage() throws IOException, InterruptedException
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
  public void canRecieveAMessageFromTheClient() throws IOException, InterruptedException
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

  @Test
  public void canServeMultipleConnections() throws IOException, InterruptedException
  {
    servicer.serve(8888, new ResponderServer());

    Socket sock1 = new Socket("localhost", 8888);
    BufferedReader br1 = JocketService.getBufferedReader(sock1);
    PrintStream ps1 = JocketService.getPrintStream(sock1);

    Socket sock2 = new Socket("localhost", 8888);
    BufferedReader br2 = JocketService.getBufferedReader(sock2);
    PrintStream ps2 = JocketService.getPrintStream(sock2);

    ps2.println("YAP!");
    String yap2 = br2.readLine();
    br2.close();

    ps1.println("YAP!");
    String yap1 = br1.readLine();
    br1.close();

    assertEquals("YAP!", yap1);
    assertEquals("YAP!", yap2);
    servicer.close();
  }

  @Test
  public void wontCloseServerInstanceUntilSocketsClosed() throws IOException, InterruptedException
  {
    servicer.serve(8888, new ResponderServer());

    Socket sock1 = new Socket("localhost", 8888);
    BufferedReader br1 = JocketService.getBufferedReader(sock1);
    PrintStream ps1 = JocketService.getPrintStream(sock1);

    Socket sock2 = new Socket("localhost", 8888);
    BufferedReader br2 = JocketService.getBufferedReader(sock2);
    PrintStream ps2 = JocketService.getPrintStream(sock2);

    Thread.sleep(30);
    
    assertEquals(2, servicer.getServerThreadCount());
    JocketService.TIMEOUT_PERIOD = 10;
    servicer.close();

    assertEquals(0, servicer.getServerThreadCount());
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
