import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConnectionHandler implements Runnable {
  private Socket client; // should make this singleton i think
  private Server server; // this one as well
  private DataInputStream din;
  private DataOutputStream dout;

  @Override
  public void run(){
    try {
      din = new DataInputStream(client.getInputStream());
      dout = new DataOutputStream(client.getOutputStream());
      
      String text = "";
      while (!text.equals("exit")) {
        text = din.readUTF();
        
        System.out.println("Rcv: " + text); 
        if(text.startsWith("brod /"))
          server.broadcast(text.split("/", 2)[1]);
        else
          send(text.toUpperCase());
      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        client.close();  
      } catch (Exception e) {
        System.out.println("Server exception on closing conn: " + e.getMessage());
        e.printStackTrace();
      }
      server.removeConnection(this);
    }
  }

  public ConnectionHandler(Socket client, Server server){
    this.client = client;
    this.server = server;
  }

  public synchronized void send(String message){
    try {
      dout.writeUTF("Sent: " + message);
      dout.flush();
    } catch (IOException e) {
      System.out.println("Send error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

public class Server implements Runnable{
  private List<ConnectionHandler> connections;
  private ExecutorService pool;
  private static int port = 6969;
  private boolean shut;
  
  public Server(){
    this.connections = new CopyOnWriteArrayList<ConnectionHandler>();
    this.shut = false;
  }

  @Override
  public void run() {
    try (ServerSocket ss = new ServerSocket(port)) {
      pool = Executors.newCachedThreadPool();
      System.out.println("Server up on port: " + port);
      while (!shut) {
        Socket client = ss.accept();
        ConnectionHandler handler = new ConnectionHandler(client, this);
        connections.add(handler);
        pool.execute(handler);
        System.out.println("Active conns: " + connections.size());
      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public synchronized void broadcast(String message){
    System.out.println("Broadcasting message: " + message);
    connections.forEach((conn) -> conn.send(message));
  }

  public synchronized void removeConnection(ConnectionHandler conn){
    connections.remove(conn);
    System.out.println("Conn removed. Active conns: " + connections.size());
  }

  public static void main(String... args) {
    (new Server()).run();
  }
}