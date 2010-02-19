import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JJocketServerTest
{
  private JJocketServer server;

  @Before
  public void setUp()
  {
     server = new JJocketServer();
  }

  @Test
  public void canCreateSuccessResponseHeader()
  {
    String header = server.getSuccessHeader();

    String expectedHeader = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 200\n\r\n";

    assertEquals(expectedHeader, header);
  }

  @Test
  public void canCreateHomePageResponse()
  {
    String response = server.getHomePageResponse();

    String homeResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 200\n\r\n<html><h2>Home Page!</h2></html>\n\r\n";
    assertEquals(homeResponse, response);
  }

  @Test
  public void canCreate404Response()
  {
    String response = server.get404Response();

    String expectedResponse = "HTTP/1.1 404 Not Found\nContent-Type: text/html\nContent-Length: 200\n\r\n"+
        "<html><h2>404 Page Not Found!</h2></html>\n\r\n";

    assertEquals(expectedResponse, response);
  }

  @Test
  public void canCreateMyPageResponse()
  {
    String response = server.getMyPageResponse();
    
    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 200\n\r\n"+
        "<html><h2>This Is my page</h2><h1>YOU BETTER LIKE IT!</h1></html>\n\r\n";

    assertEquals(expectedResponse, response);
  }

  @Test
  public void canDetermineWhichPageToRespondWith()
  {
    String response = server.getPageResponse("/");

    String expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 200\n\r\n<html><h2>Home Page!</h2></html>\n\r\n";

    assertEquals(expectedResponse, response);

    response = server.getPageResponse("/index.html");

    assertEquals(expectedResponse, response);

    response = server.getPageResponse("/my_page.html");
    expectedResponse = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 200\n\r\n"+
        "<html><h2>This Is my page</h2><h1>YOU BETTER LIKE IT!</h1></html>\n\r\n";
    assertEquals(expectedResponse, response );
  }

  @Test
  public void canRespondWithAnErrorPageForANonExistentFile()
  {
    String response = server.getPageResponse("/thispageisfake.html");

    assertEquals(server.get404Response(), response);
  }

//  JocketService service = new JocketService();
//    JJocketServer server = new JJocketServer();
//    JocketClient jocket = new JocketClient(8888);
//    service.serve(8888, server);
//    jocket.connect("localhost");
//    String response = jocket.getHomePage();
}
