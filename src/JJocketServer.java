import java.io.FileNotFoundException;
import java.net.Socket;

public class JJocketServer implements JocketServer
{

  public void serve(Socket sock)
  {
  }

  public String getLastRequest()
  {
    return null;
  }

  public String getHomePageResponse()
  {
      return getSuccessHeader() + getTextFromFileName("public/index.html");
  }

  public String getSuccessHeader()
  {
    return getTextFromFileName("public/successHeader");
  }

  public String get404Response()
  {
    String header = getTextFromFileName("public/404Header");
    String body = getTextFromFileName("public/404.html");
    return header + body;
  }

  private String getTextFromFileName(String fileName)
  {
    String text = "";
    try{
      text = FileParser.parseFile(fileName);
    }
    catch (Exception e)
    {
      return null;
    }
    return text;
  }

  public String getMyPageResponse()
  {
    return getSuccessHeader() + getTextFromFileName("public/my_page.html");
  }

  public String getPageResponse(String path)
  {
    if(path.equals("/"))
        return getHomePageResponse();
    else{
      String responseBody = null;
      responseBody = getTextFromFileName("public" + path);
      if (responseBody == null)
        return get404Response();

      return getSuccessHeader() + getTextFromFileName("public" + path);
    }
  }
}
