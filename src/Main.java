import java.io.IOException;

public class Main
{
  static int port;
  static String rootDir;

  public static void main(String args[]) throws IOException
  {
    JocketService service = new JocketService();
    if (!parseArgs(args)){
      argsError();
      return;
    }
    System.out.println("port = " + port);
    System.out.println("rootDir = " + rootDir);
    JocketServer server = new JJocketServer(rootDir);
    service.serve(port, server);
  }

  public static boolean parseArgs(String[] args)
  {
    if (args.length == 0)
    {
      port = 8888;
      rootDir = "public";
      return true;
    }
    else if (args.length == 2)
      return getArg(args, 0);

    else if (args.length == 4)
     return getArg(args, 0) && getArg(args, 2) && !args[0].equals(args[2]);

    else
      return false;
  }

  public static boolean getArg(String[] args, int argIndex)
  {
    if (args[argIndex].equals("-p"))
    {
      port = Integer.parseInt(args[argIndex + 1]);
      return true;
    }
    else if (args[argIndex].equals("-r"))
    {
      rootDir = args[argIndex + 1];
      return true;
    }
    else
    {
      return false;
    }
  }

  private static void argsError()
  {
    System.out.println("Incorrect args format.  Format: -p port -r root");
    System.out.println("Both args are optional");
  }
}
