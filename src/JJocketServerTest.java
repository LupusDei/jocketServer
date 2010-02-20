import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class JJocketServerTest
{
  private JJocketServer server;
  private JocketClient jocket;

  private JocketService service;

  @Before
  public void setUp()
  {
    server = new JJocketServer();
  }

  private void setUpServe()
      throws IOException
  {
    service = new JocketService();
    jocket = new JocketClient(8888);
    service.serve(8888, server);
    jocket.connect("localhost");
  }

  @After
  public void tearDown() throws IOException, InterruptedException
  {
    if(service != null && service.isOpen())
      service.close();
  }

  @Test
  public void canCreateSuccessResponseHeader()
  {
    String header = server.getSuccessHeader();

    String expectedHeader = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n";

    assertEquals(expectedHeader, header);
  }

  @Test
  public void canCreateHomePageResponse()
  {
    String response = server.getHomePageResponse();

    String homeResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n<html><h2>Home Page!</h2></html>\n\r\n";
    assertEquals(homeResponse, response);
  }

  @Test
  public void canCreate404Response()
  {
    String response = server.get404Response();

    String expectedResponse = "HTTP/1.1 404 Not Found\nContent-Type: text/html\nContent-Length: 200\n\r\n" +
        "<html><h2>404 Page Not Found!</h2></html>\n\r\n";

    assertEquals(expectedResponse, response);
  }

  @Test
  public void canCreateMyPageResponse()
  {
    String response = server.getMyPageResponse();

    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n" +
        "<html><h2>This Is my page</h2><h1>YOU BETTER LIKE IT!</h1></html>\n\r\n";

    assertEquals(expectedResponse, response);
  }

  @Test
  public void canDetermineWhichPageToRespondWith()
  {
    String response = server.getPageResponse("/");

    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n<html><h2>Home Page!</h2></html>\n\r\n";

    assertEquals(expectedResponse, response);

    response = server.getPageResponse("/index.html");

    assertEquals(expectedResponse, response);

    response = server.getPageResponse("/my_page.html");
    expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n" +
        "<html><h2>This Is my page</h2><h1>YOU BETTER LIKE IT!</h1></html>\n\r\n";
    assertEquals(expectedResponse, response);
  }

  @Test
  public void canRespondWithAnErrorPageForANonExistentFile()
  {
    String response = server.getPageResponse("/thispageisfake.html");

    assertEquals(server.get404Response(), response);
  }

  @Test
  public void canParseAFormalHTTPRequestForThePath()
  {
    String request = "GET /index.html HTTP/1.1\nHost: localhost:8888\n\r\n";

    String path = server.parseRequestForPathAndArgs(request);

    assertEquals("/index.html", path);
  }

  @Test
  public void canReadMultipleParamsAndPrintThemOff() throws IOException
  {
    String request = "GET /index.html?a=1&b=2&c=3 HTTP/1.1\nHost: localhost:8888\n\r\n";

    PrintStream stream = System.out;
    byte[] bytes = new byte[20];
    stream.write(bytes);
    String path = server.parseRequestForPathAndArgs(request);

    String output = new String(bytes);
    System.out.println("output = " + output);

    assertEquals("/index.html", path );
  }

  @Test
  public void canParseArgs()
  {
    String request = "GET /echo?a=1&b=2&c=3 HTTP/1.1\nHost: localhost:8888\n\r\n";
    server.parseRequestForPathAndArgs(request);
    String[] expectedArgs = new String[3];
    expectedArgs[0] = "a=1";
    expectedArgs[1] = "b=2";
    expectedArgs[2] = "c=3";
    String[] responseArgs = server.getInputArgs();
    for(int i = 0;i < 3;i++)
      assertEquals(expectedArgs[i], responseArgs[i]);
  }

  @Test
  public void hasAnEchoFunctionPage()
  {
    String request = "GET /echo?a=1&b=2&c=3 HTTP/1.1\nHost: localhost:8888\n\r\n";

    server.parseRequestForPathAndArgs(request);
    String response = server.getPageResponse("/echo");

    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n<html><h1>This is the Echo Page.</h1>\n<h2>Here they are:</h2>\n<p>a=1</p>\n<p>b=2</p>\n<p>c=3</p>\n</html>\n\r\n";
    assertEquals(expectedResponse,response );
  }

  @Test
  public void canServeASocket() throws IOException
  {
    setUpServe();
    String response = jocket.getHomePage();

    assertEquals("HTTP/1.1 200 OK", response);
  }

  @Test
  public void canDetectDifferentMIMETypes()
  {
    String type = server.getMIMEType("/index.html");

    assertEquals("text/html", type);

    type = server.getMIMEType("/images/me.jpg");

    assertEquals("image/jpeg", type);
  }

  @Test
  public void canCreateFileResponse()
  {
    File image = new File("public/images/me.jpg");
    long length = image.length();
    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: image/jpeg\nContent-Length:"+length+"\n\r\n";

    String response = server.getImageHeaderResponse("/images/me.jpg", "image/jpeg");

    assertEquals(expectedResponse,response);
  }

  @Test
  public void canGetFileResponseHeaderFromPageSearch()
  {
    File image = new File("public/images/me.jpg");
    long length = image.length();
    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: image/jpeg\nContent-Length:"+length+"\n\r\n";

    String response = server.getPageResponse("/images/me.jpg");

    assertEquals(expectedResponse,response);
  }

  @Test
  public void canServeAnImageFile() throws IOException
  {
    setUpServe();
    File response = jocket.getImagePage();
    int imageLength = (int) new File("public/images/me.jpg").length();
    assertEquals(response.length(),imageLength);
  }

  @Test
  public void canGiveOtherMimeTypeResponse()
  {
    String response = server.getOtherMimeTypeResponse("public/images/fake.gif");

    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 500\n\r\n<html><h2>I Know Your Sneakiness!</h2>\n"+
        "<p>Don't Think I don't see what you are trying to do here.</p>\n"+
        "<p>I'd like to help you with this MIME type, but...</p>\n"+
        "<p>I just don't have the files.  So here is the Mime Type: </p>\n</html>\n\r\n"+
        "<html><p>image/gif</p><p>Now leave me alone</p></html>\n\r\n";

    assertEquals(expectedResponse, response);
  }

}
