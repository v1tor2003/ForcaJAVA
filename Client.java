import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IncomingMessageHandler implements Runnable {
  private DataInputStream din;
  private Client client;

  public IncomingMessageHandler(DataInputStream din, Client client){
    this.client = client;
    this.din = din;
  }

  @Override
  public void run (){
    try {
      boolean show = true;
      String serverRes;
      while ((serverRes = din.readUTF()) != null) {
        List<String> tokens = Utils.interpProtocol(serverRes);
        IClientAction action = client.getActions().get(tokens.get(0));
        if(action != null) action.execute(tokens);
        if(show) Client.showMenu();
        show = false;
      }      
    } catch (Exception e) {
      System.out.println("Connection closed: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

public class Client {
  private final static String hostAddr = "localhost";
  private final static int hostPort = 6969;
  private final Map<String, IClientAction> actions = new HashMap<>();
  private DataInputStream din;
  private DataOutputStream dout;

  private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

  private boolean mainMenu;
  private boolean ownerMenu;

  public Client(){
    this.registerActions();
    this.mainMenu = true;
    this.ownerMenu = false;
  }

  public void run(){
    try (Socket client = new Socket(hostAddr, hostPort)) {
      din = new DataInputStream(client.getInputStream());
      dout = new DataOutputStream(client.getOutputStream());

      new Thread(new IncomingMessageHandler(this.din, this)).start();

      System.out.println("Forca Multijogador.\nEntre com o seu nome de jogador:");
      String clientInput = this.console.readLine();
      dout.writeUTF("NAME " + clientInput.trim());
      dout.flush();

      while (mainMenu) {
        clientInput = this.console.readLine();  
        handleMenuSelection(clientInput.toCharArray()[0], dout); 
      }

      while (ownerMenu) {
        clientInput = this.console.readLine();  
        handleOwnerMenuSelection(clientInput.toCharArray()[0], dout); 
      }

      din.close();
      dout.close();
    } catch (UnknownHostException e) {
      System.out.println("Server not found: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Client exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
  public static void main(String... args) {
    (new Client()).run();
  }

  public static void showMenu(){
    String menuOptions = "Menu:\n" +
                         "Criar sala de jogo. (C)\n" +
                         "Listar salas de jogo. (L)\n" + 
                         "Juntar-se a uma sala de jogo. (J)\n" +
                         "Sair. (S)\n";

    System.out.print(menuOptions);
  }

  public void showOwnerMenu(){
    System.out.println("Iniciar Partida (I) | Fechar Sala (F).");  
  }

  private void registerActions(){
    this.actions.put("SERVER_LOG", (args) -> this.log(args));
    this.actions.put("SET_AS_ROOM_OWNER", (args) -> {
      mainMenu = false;
      ownerMenu = true;
    });
    this.actions.put("RESET", (args) -> {
      mainMenu = true;
      ownerMenu = false;
    });
  }

  private void log(List<String> text){
    text.remove(0);
    System.out.print(Utils.listToString(text));
  }

  public Map<String, IClientAction> getActions(){
    return this.actions;
  }

  private void handleOwnerMenuSelection(Character selection, DataOutputStream dout) throws IOException {
    selection = Character.toLowerCase(selection);
    switch (selection) {
      case 'i':
        dout.writeUTF("START");
        dout.flush();
        break;
      case 'f':
        dout.writeUTF("CLOSE_ROOM");
        dout.flush();
        break;
      default:
        System.out.println("Opcao inválida. Por favor tente de novo.");
        showOwnerMenu();
        break;
    }
  }

  private void handleMenuSelection(Character selection, DataOutputStream dout) throws IOException {
    selection = Character.toLowerCase(selection);
    switch (selection) {
      case 'c':
        dout.writeUTF("CREATE_ROOM");
        dout.flush();
        break;
      case 'l':
        dout.writeUTF("LIST_ROOMS");
        dout.flush();
        break;
      case 'j':
        System.out.println("Digite o id da sala de jogo:");
        String id = this.console.readLine();
        dout.writeUTF("JOIN_ROOM " + id);
        dout.flush();
        break;
      case 's':
        dout.writeUTF("DISCONNECT");
        dout.flush();
        System.out.println("Saindo...");
        break;
      default:
        System.out.println("Opcao invalida. Por favor tente de novo.");
    }
  }
}