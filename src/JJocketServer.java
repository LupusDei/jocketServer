import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.net.URLConnection;

public class JJocketServer implements JocketServer
{
  private String rootDir = "public";
  private boolean wasSpecialMimeType;
  private File specialMime;
  private String[] inputArgs;

  public JJocketServer(String dir)
  {
    rootDir = dir;
  }

  public JJocketServer(){
    
  }
  public void serve(Socket sock)
  {
    wasSpecialMimeType = false;
    try
    {
      StringBuffer request = new StringBuffer();
      BufferedReader br = JocketService.getBufferedReader(sock);
      String inputLine = null;
      while ((inputLine = br.readLine()) != null && inputLine.length() != 0)
        request.append(inputLine + "\n");
      System.out.println("request = " + request);
      String path = parseRequestForPathAndArgs(request.toString());
      String response = getPageResponse(path);
      PrintStream ps = JocketService.getPrintStream(sock);
      System.out.println("response = " + response);
      ps.print(response);
      if (wasSpecialMimeType)
      {
        FileInputStream fis = new FileInputStream(specialMime);
        byte[] b = new byte[(int) specialMime.length()];
        fis.read(b);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(b, 0, b.length);
        os.writeTo(sock.getOutputStream());

      }
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
    return getSuccessHeader() + getTextFromFileName(rootDir + "/index.html");
  }

  public String getSuccessHeader()
  {
    return getTextFromFileName(rootDir + "/successHeader");
  }

  public String get404Response()
  {
    String header = getTextFromFileName(rootDir + "/404Header");
    String body = getTextFromFileName(rootDir + "/404.html");
    return header + body;
  }

  public String getMyPageResponse()
  {
    return getSuccessHeader() + getTextFromFileName(rootDir + "/my_page.html");
  }

  private String getEchoResponse()
  {
	try
	{
		Thread.sleep(5000);
	}
	catch(Exception e)
	{}
    StringBuffer argPage = new StringBuffer();
    argPage.append("<html><h1>This is the Echo Page.</h1>\n<h2>Here they are:</h2>\n");
    for(int i = 0; i < inputArgs.length;i++)
      argPage.append("<p>" + inputArgs[i] + "</p>\n");
    argPage.append("</html>\n\r\n");

    return getSuccessHeader() + argPage.toString();
  }

  public String getPageResponse(String path)
  {
    if (path.equals("/"))
      return getHomePageResponse();
    if(path.equals("/echo"))
      return getEchoResponse();
    String fileType = getMIMEType(path);
    if(fileType == null)
      return get404Response();
    if (fileType.equals("text/html"))
    {
      String responseBody = null;
      responseBody = getTextFromFileName(rootDir + path);
      if (responseBody == null)
        return get404Response();

      return getSuccessHeader() + getTextFromFileName(rootDir + path);
    }
    else if(fileType.equals("image/jpeg") || fileType.equals("image/vnd.microsoft.icon") || fileType.equals("application/pdf"))
    {
      return getImageHeaderResponse(path, fileType);
    }
    else if (fileType.equals("text/javascript"))
      return getImageHeaderResponse(path, fileType);
    else
      return getOtherMimeTypeResponse(path);
  }

  private String getTextFromFileName(String pathName)
  {
    String text = "";
    try
    {
      text = FileParser.parseFile(pathName);
    }
    catch (Exception e)
    {
      return null;
    }
    return text;
  }

  public String parseRequestForPathAndArgs(String request)
  {
    String methodRequestLine = request.substring(0, request.indexOf("\n"));
    String pathAndArgs = methodRequestLine.substring(4, methodRequestLine.indexOf("HTTP") - 1);
    int argsIndex = pathAndArgs.indexOf("?");
    if(argsIndex == -1){
      return pathAndArgs;
    }
    else
    {
      String path = pathAndArgs.substring(0, argsIndex);
      inputArgs = getArgs(pathAndArgs.substring(argsIndex + 1));
      System.out.println(" args:");
      for (String arg : inputArgs)
      System.out.println(arg);
      return path;
    }
  }

  private String[] getArgs(String args)
  {
    int argCount = 1;
    int seperatorIndex = 0;
    String newArgs = args;
    while((seperatorIndex = newArgs.indexOf("&") + 1) != 0)
    {
      newArgs = newArgs.substring(seperatorIndex);
      argCount++;
    }
    String[] parsedArgs = new String[argCount];
    for(int i = 0; i < argCount;i++)
    {
      if(i < argCount -1) {
        parsedArgs[i] = args.substring(0,args.indexOf("&"));
        args = args.substring(args.indexOf("&")+ 1);
      }
      else
        parsedArgs[i] = args;
    }
    return parsedArgs;
  }

  public String getMIMEType(String filePath)
  {
    String fileName = filePath.substring(filePath.lastIndexOf("/"));
    FileNameMap map = URLConnection.getFileNameMap();
    String type = map.getContentTypeFor(fileName);
    System.out.println("FILE NAME: " + fileName);
    if (fileName.indexOf(".js") != -1)
      type = "text/javascript";
    if (fileName.indexOf(".ico") != -1)
      type = "image/vnd.microsoft.icon";
    System.out.println("FILE TYPE: " + type);
    return type;
  }

  public String getImageHeaderResponse(String filePath, String fileType)
  {
    System.out.println("FILE PATH: " + rootDir + filePath);
    File image = new File(rootDir + filePath);
    if (image.exists())
    {
      wasSpecialMimeType = true;      
      specialMime = image;
      long length = image.length();
      return "HTTP/1.1 200 OK\nContent-Type: " + fileType + "\nContent-Length:" + length + "\n\r\n";
    }
    else
      return get404Response();
  }

  public String getOtherMimeTypeResponse(String path)
  {
    String fileType = getMIMEType(path);

    return getSuccessHeader() + getTextFromFileName(rootDir + path);
  }

  public String[] getInputArgs()
  {
    return inputArgs;
  }
}
