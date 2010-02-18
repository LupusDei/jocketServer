import java.net.Socket;

public interface JocketServer
{
  public abstract void serve(Socket sock);

  public abstract String getLastRequest();
}
