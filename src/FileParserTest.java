import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileParserTest
{


  @Test
  public void canParseAFileAndReturnAString() throws Exception
  {
    String parsedText = FileParser.parseFile("public/successHeader");
    assertEquals("HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: 20000\n\r\n", parsedText);
  }

}
