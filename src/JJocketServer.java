import java.net.Socket;

public class JJocketServer implements JocketServer
{
  private String successHeader = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 20000\n\r\n";
  private String the404Response = "HTTP/1.1 404 Not Found\nContent-Type: text/html\nContent-Length: 20000\n\r\n"+
        "<html><h2>404 Page Not Found!</h2></html>\n\r\n";

  public void serve(Socket sock)
  {
  }

  public String getLastRequest()
  {
    return null;
  }

  public String getHomePageResponse()
  {
      return successHeader + "<html><h2>Home Page!</h2></html>\n\r\n";
  }

  public String getSuccessHeader()
  {
    return successHeader;
  }

  public String get404Response()
  {
    return the404Response;
  }
}
