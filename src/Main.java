import java.io.IOException;

public class Main
{
  public static void main(String args[]) throws IOException
  {
    JocketService service = new JocketService();
    if(args.length != 1) {
      System.out.println("Incorrect Number of args.  Specify a port");
      return;
    }
    System.out.println("args = " + args[0]);    
    int port = Integer.parseInt(args[0]);
    JocketServer server = new JJocketServer();
    service.serve(port, server);
  }
}
