import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.net.URLConnection;

public class JJocketServer implements JocketServer
{

  public void serve(Socket sock)
  {
    try
    {
      StringBuffer request = new StringBuffer();
      BufferedReader br = JocketService.getBufferedReader(sock);
      String inputLine = null;
      while ((inputLine = br.readLine()) != null && inputLine.length() != 0)
        request.append(inputLine + "\n");
      System.out.println("request = " + request);
      String path = parseRequestForPath(request.toString());
      String response = getPageResponse(path);
      PrintStream ps = JocketService.getPrintStream(sock);
      System.out.println("response = " + response);
      ps.print(response);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
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

  public String getMyPageResponse()
  {
    return getSuccessHeader() + getTextFromFileName("public/my_page.html");
  }

  public String getPageResponse(String path)
  {
    if (path.equals("/"))
      return getHomePageResponse();
    String fileType = getMIMEType(path);
    if (fileType.equals("text/html"))
    {
      String responseBody = null;
      responseBody = getTextFromFileName("public" + path);
      if (responseBody == null)
        return get404Response();

      return getSuccessHeader() + getTextFromFileName("public" + path);
    }
    else
    {
      return getImageHeaderResponse(path,fileType);
    }
  }

  private String getTextFromFileName(String fileName)
  {
    String text = "";
    try
    {
      text = FileParser.parseFile(fileName);
    }
    catch (Exception e)
    {
      return null;
    }
    return text;
  }

  public String parseRequestForPath(String request)
  {
    String methodRequestLine = request.substring(0, request.indexOf("\n"));
    String path = methodRequestLine.substring(4, methodRequestLine.indexOf("HTTP") - 1);
    return path;
  }

  public String getMIMEType(String filePath)
  {
    String fileName = filePath.substring(filePath.lastIndexOf("/"));
    FileNameMap map = URLConnection.getFileNameMap();
    String type = map.getContentTypeFor(fileName);
    return type;
  }

  public String getImageHeaderResponse(String filePath, String fileType)
  {

    File image = new File("public" + filePath);
    if (image.exists())
    {
      long length = image.length();
      return "HTTP/1.1 200 OK\nContent-Type: " + fileType + "\nContent-Length:" + length + "\n\r\n";
    }
    else
      return get404Response();
  }
}
