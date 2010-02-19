import java.io.*;

public class FileParser
{

  public static String parseFile(String fileName) throws Exception
  {
    return FileParser.parseFile(new File(fileName));
  }

  public static String parseFile(File file) throws Exception
  {
    StringBuffer text = new StringBuffer();
    String inputLine = null;
    BufferedReader fileReader = new BufferedReader(new FileReader(file));
    while((inputLine = fileReader.readLine()) != null && inputLine.length() != 0)
      text.append(inputLine + "\n");
    text.append("\r\n");
   return text.toString();
  }
}
