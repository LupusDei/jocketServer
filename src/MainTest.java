import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class MainTest
{

  @Test
  public void canParseAnArgPair()
  {
    String[] args = new String[2];
    args[0] = "-p";
    args[1] = "31415";
    assertTrue(Main.getArg(args,0));
    assertEquals(31415,Main.port);
  }

  @Test
  public void willReturnFalseOnABadArgPair()
  {
    String[] args = new String[2];
    args[0] = "-cow";
    args[1] = "31415";
    assertFalse(Main.getArg(args,0));
    assertEquals(8888, Main.port);
  }

  @Test
  public void canParseAGroupOfArgs()
  {
    String[] args = new String[4];
    args[0] = "-p";
    args[1] = "31415";
    args[2] = "-r";
    args[3] = "homer";

    assertTrue(Main.parseArgs(args));
    assertEquals(31415, Main.port);
    assertEquals("homer", Main.rootDir);
  }

  @Test
  public void willReturnFalseIfReceivesWrongNumberOfArgs()
  {
    String[] badArgs1 = new String[1];
    String[] badArgs3 = new String[3];
    String[] badArgs5 = new String[5];

    assertFalse(Main.parseArgs(badArgs1));
    assertFalse(Main.parseArgs(badArgs3));
    assertFalse(Main.parseArgs(badArgs5));
  }

  @Test
  public void willReturnFalseIfReceivesTheSameArgTwice()
  {
     String[] args = new String[4];
    args[0] = "-p";
    args[1] = "31415";
    args[2] = "-p";
    args[3] = "4242";

    assertFalse(Main.parseArgs(args));
  }
}
