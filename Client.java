import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

class IncomingMessageHandler implements Runnable {
  private DataInputStream din;
  
  public IncomingMessageHandler(DataInputStream din){
    this.din = din;
  }

  @Override
  public void run (){
    try {
      String msg;
      while ((msg = din.readUTF()) != null) {
        System.out.println("\nServer: " + msg);
      }      
    } catch (Exception e) {
      System.out.println("Connection closed: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

public class Client {
  private static String hostAddr = "localhost";
  private static int hostPort = 6969;
  public static void main(String... args) {
    try (Socket client = new Socket(hostAddr, hostPort)) {
      DataInputStream din = new DataInputStream(client.getInputStream());
      DataOutputStream dout = new DataOutputStream(client.getOutputStream());

      BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

      new Thread(new IncomingMessageHandler(din)).start();

      String clientInput = "";
      while (!clientInput.equals("exit")) {
        clientInput = console.readLine();
        dout.writeUTF(clientInput);
        dout.flush();
      }
    } catch (UnknownHostException e) {
      System.out.println("Server not found: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}