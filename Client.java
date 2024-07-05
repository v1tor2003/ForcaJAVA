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
        if(show) Client.showMenu(); show = false;
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

  private String rId;
  private boolean gameStarted;
  private boolean turn;

  public Client(){
    this.registerActions();
    this.rId = "";
    this.gameStarted = false;
    this.turn = false;
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

      
      boolean shouldGetInput = true;
      while (shouldGetInput) {
        clientInput = this.console.readLine();  
        handleMenuSelection(clientInput.toCharArray()[0], dout); 
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
    String menuOptions = "\nMenu:\n" +
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
    this.actions.put("FULL_ROOM", (args) -> this.showError("Nao foi possivel se unir a sala pois ela ja esta cheia."));
    this.actions.put("SERVER_LOG", (args) -> this.log(args));
    this.actions.put("INVALID_ROOM", (args) -> this.showError("Sala inexistente, por favor verifique o id e tente novamente.\n"));
    this.actions.put("GAME_STARTED", (args) -> this.startGame());
    this.actions.put("START_TURN", (args) -> this.startTurn());
    this.actions.put("END_TURN", (args) -> this.endTurn());
    this.actions.put("ROOM_CLOSED", (args) -> this.closeRoom());
    this.actions.put("GAME_OVER", (args) -> this.endGame());
  }

  private synchronized void closeRoom(){
    this.gameStarted = false;
    this.turn = false;
    notifyAll();
  }

  private synchronized void startGame(){
    this.gameStarted = true;
    notify();
  }

  private synchronized void endGame(){
    this.gameStarted = false;
    this.turn = false;
    notifyAll();
    System.out.println("Partida finalizada.");
    showMenu();
  }

  private synchronized void endTurn(){
    this.turn = false;
    notifyAll();
  }

  private synchronized void startTurn(){
    this.turn = true;
    notifyAll();
  }

  private synchronized void waitForGameStart() throws InterruptedException{
    while (!gameStarted) {
      wait();
    }
  }

  private synchronized void waitForTurn() throws InterruptedException {
    while (!turn) {
      wait();
    }
  }

  private synchronized void waitForEndTurn() throws InterruptedException {
    while (turn) {
      wait();
    }
  }

  private void log(List<String> text){
    text.remove(0);
    Utils.cls();
    System.out.println(Utils.listToString(text));
  }

  private void showError(String errMsg){
    System.out.print(errMsg);
  }

  public Map<String, IClientAction> getActions(){
    return this.actions;
  }

  private boolean handleOwnerMenuSelection(Character selection, DataOutputStream dout) throws IOException {
    selection = Character.toLowerCase(selection);
    switch (selection) {
      case 'i':
        System.out.println("Digite a palavra de jogo:");
        String word = this.console.readLine();
        dout.writeUTF("START_GAME " +word);
        dout.flush();
        return true;
      case 'f':
        dout.writeUTF("CLOSE_ROOM");
        dout.flush();
        return false;
      default:
        System.out.println("Opcao invalida. Por favor tente de novo.");
        showOwnerMenu();
        return true;
    }
  }

  private void handleMenuSelection(Character selection, DataOutputStream dout) throws IOException {
    selection = Character.toLowerCase(selection);
    switch (selection) {
      case 'c':
        dout.writeUTF("CREATE_ROOM");
        dout.flush();
        boolean getInput = true;
        String input = "";
        //showOwnerMenu();
        while (getInput) {
          input = this.console.readLine();
          getInput = handleOwnerMenuSelection(input.toCharArray()[0], dout);
        }
        break;
      case 'l':
        dout.writeUTF("LIST_ROOMS");
        dout.flush();
        break;
      case 'j':
        System.out.println("Digite o id da sala de jogo:");
        this.rId = this.console.readLine();
        dout.writeUTF("JOIN_ROOM " + rId);
        dout.flush(); 
        
        try {
          waitForGameStart();
          System.out.println("Partida iniciada.");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        while (this.gameStarted) {
          try {
            waitForTurn();
            System.out.println("Adivinhe a letra da palavra:");
            input = this.console.readLine();
            dout.writeUTF("GUESS " + input);
            dout.flush();
            waitForEndTurn();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      case 's':
        dout.writeUTF("DISCONNECT");
        dout.flush();
        System.out.println("Saindo...");
        System.exit(1);
        break;
      default:
        System.out.println("Opcao invalida. Por favor tente de novo.");
    }
  }
}