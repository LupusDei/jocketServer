import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class JocketClientTest
{
  private JocketServer mocketServer;
  private JocketService service;
  private JocketClient client;
  private int myPort = 8888;
  private String properGetHomeRequest = "GET /index.html HTTP/1.1\nHost: localhost:" + myPort + "\n";


  @Before
  public void setUp()
  {
    mocketServer = new MocketServer();
    service = new JocketService();
    client = new JocketClient(myPort);

  }
  @After
  public void tearDown() throws IOException, InterruptedException
  {
    if(service.isOpen())
    service.close();
  }

  @Test
  public void canNotConnectToTheServerOnTheWringPort() throws IOException
  {
    service.serve(31415, mocketServer);

    assertFalse(client.connect("localhost"));
  }
  
  @Test
  public void canConnectToTheServerOnTheRightPort() throws IOException
  {

    service.serve(myPort, mocketServer);

    assertTrue(client.connect("localhost"));
  }

  @Test
  public void canFormRequest()
  {
   String request = client.formGETReqest("/index.html");

    assertEquals(properGetHomeRequest + "\r\n", request);
  }

  @Test
  public void canRequestToViewTheHomePage() throws IOException, InterruptedException
  {
    service.serve(myPort, mocketServer);
    client.connect("localhost");
    Thread.sleep(15);
    String homePage = client.getHomePage();
    assertEquals(properGetHomeRequest, mocketServer.getLastRequest());
    assertEquals("HOME PAGE!", homePage);
  }

  private class MocketServer implements JocketServer
  {
    public StringBuffer request = new StringBuffer();
    public void serve(Socket sock)
    {
      try
      {
        BufferedReader br = JocketService.getBufferedReader(sock);
        String inputLine = null; 
        while((inputLine = br.readLine()) != null && inputLine.length() != 0)
        request.append(inputLine + "\n");
        PrintStream ps = JocketService.getPrintStream(sock);
        ps.println("HOME PAGE!");
        sock.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    public String getLastRequest()
    {
      return request.toString();
    }
  }
}
