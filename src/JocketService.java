import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class JocketService
{
  private ServerSocket serverSocket = null;
  private boolean serverSocketOpen;
  private JocketServer userServer;
  private Thread socketAcceptorThread;

  public void serve(int port, JocketServer inputServer) throws IOException
  {
    userServer = inputServer;
    serverSocket = new ServerSocket(port);
    serverSocketOpen = true;
    socketAcceptorThread = makeSocketThread();
    socketAcceptorThread.start();
  }

  private Thread makeSocketThread()
  {
    return new Thread(new Runnable()
    {

      public void run()
      {
        while (serverSocketOpen)
        {
          runServerSocket();
        }
      }
    });
  }

  private void runServerSocket()
  {
    try
    {
      Socket mySocket = serverSocket.accept();
      userServer.serve(mySocket);
      mySocket.close();
    }
    catch (IOException e)
    {
    }
  }


  public void close() throws IOException
  {
    serverSocketOpen = false;
    serverSocket.close();
  }

  public static PrintStream getPrintStream(Socket sock) throws IOException
  {
    PrintStream ps = new PrintStream(sock.getOutputStream());
    return ps;
  }

  public static BufferedReader getBufferedReader(Socket sock) throws IOException
  {
    InputStream is = sock.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    return br;
  }
}
