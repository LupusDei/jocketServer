import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
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
  public void canRequestToViewTheHomePage() throws IOException
  {
    service.serve(myPort, mocketServer);
    client.connect("localhost");
    String homePage = client.getHomePage();
    assertEquals(properGetHomeRequest, mocketServer.getLastRequest());
    assertEquals("HOME PAGE!", homePage);
  }

  @Test
  public void canRequestToViewAnImagePage() throws IOException
  {
    service.serve(myPort, mocketServer);
    client.connect("localhost");
    File imagePage = client.getImagePage();
    assertEquals("GET /images/me.jpg HTTP/1.1\nHost: localhost:" + myPort + "\n", mocketServer.getLastRequest());
    assertEquals("Image PAGE!", imagePage);
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
        if(request.toString().contains("me.jpg")) {
          File file = new File("public/images/me.jpg");
          FileInputStream fis = new FileInputStream(file);
          byte[] b = new byte[(int)file.length()];
          fis.read(b);
          ByteArrayOutputStream os = new ByteArrayOutputStream();
          os.write(b,0,b.length);
          os.writeTo(sock.getOutputStream());

        }
        else
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
