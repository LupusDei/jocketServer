import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JocketService
{
  private ServerSocket serverSocket = null;
  private boolean serverSocketOpen;
  private JocketServer clientServer;
  private Thread socketAcceptorThread;
  private List<Thread> nobleServiceThreads = Collections.synchronizedList(new ArrayList<Thread>());
  public static int TIMEOUT_PERIOD = 500;
  private Object mutex = new Object();

  public void serve(int port, JocketServer inputServer) throws IOException
  {
    clientServer = inputServer;
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
      Socket clientSocket = serverSocket.accept();

      Thread servicerThread = new Thread(new ServiceRunner(clientSocket));
      nobleServiceThreads.add(servicerThread);
      servicerThread.start();

    }
    catch (IOException e)
    {
    }
  }


  public void close() throws IOException, InterruptedException
  {
    if (serverSocketOpen)
    {
      serverSocketOpen = false;
      serverSocket.close();
      socketAcceptorThread.join();
      while (nobleServiceThreads.size() > 0)
      {
        Thread thread = null;
        synchronized (mutex)
        {
          if (nobleServiceThreads.size() > 0)
          {
            thread = nobleServiceThreads.get(0);
          }
        }

        if (thread != null)
        {
          thread.join(TIMEOUT_PERIOD);

          synchronized (mutex)
          {
            if (nobleServiceThreads.contains(thread))
            {
              nobleServiceThreads.remove(0);
            }
          }
        }
      }
    }
    else
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

  public int getServerThreadCount()
  {
    return nobleServiceThreads.size();
  }

  private class ServiceRunner implements Runnable
  {
    Socket clientSocket;

    public ServiceRunner(Socket clientSocket)
    {
      this.clientSocket = clientSocket;
    }

    public void run()
    {
      try
      {

        clientServer.serve(clientSocket);
        clientSocket.close();

      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      finally{
        lastBreath();
      }
    }

    private void lastBreath()
    {
      synchronized (mutex)
       {
         nobleServiceThreads.remove(Thread.currentThread());
       }
    }
  }
}
