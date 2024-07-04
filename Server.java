import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ConnectionHandler implements Runnable {
  private Socket client; // should make this singleton i think
  private Server server; // this one as well
  private DataInputStream din;
  private DataOutputStream dout;
  private final Map<String, IServerAction> actions = new HashMap<>();
  private boolean connected;
  private String nickName;

  @Override
  public void run(){
    try {
      din = new DataInputStream(client.getInputStream());
      dout = new DataOutputStream(client.getOutputStream());
      
      while (connected) {
        List<String> tokens = Utils.interpProtocol(din.readUTF());
        System.out.println(tokens);
        IServerAction action = actions.get(tokens.get(0));
        if(action != null) action.execute(this, tokens);
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
    registerActions();
    this.connected = true;
    this.client = client;
    this.server = server;
    this.nickName = "";
  }

  private void registerActions() {
    this.actions.put("NAME", (handler, args) -> this.namePlayer(args.get(1)));
    this.actions.put("LIST_ROOMS", (handler, args) -> this.server.listRooms(handler));
    this.actions.put("CREATE_ROOM", (handler, agrs) -> this.server.createRoom(handler));
    this.actions.put("JOIN_ROOM", (handler, args) -> this.server.joinPlayerToRoom(handler,args.get(1)));
    this.actions.put("DISCONNECT", (handler, agrs) -> {
    try { this.disconnect(); } catch (IOException e) { e.printStackTrace();}
    });
    this.actions.put("CLOSE_ROOM", (handler, args) -> this.server.closeRoom(handler));
  }

  private void disconnect() throws IOException{
    this.connected = false;
    this.din.close();
    this.dout.close();
    this.server.removeConnection(this);
  }

  private void namePlayer(String name) {
    this.nickName = name;
    this.send("SERVER_LOG Jogador renomeado para '" + name + "'.");
  }

  public String getNickName(){
    return this.nickName;
  }

  public synchronized void send(String message){
    try {
      dout.writeUTF(message);
      dout.flush();
    } catch (IOException e) {
      System.out.println("Send error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

public class Server implements Runnable{
  private List<ConnectionHandler> connections;
  private List<Room> rooms;
  private ExecutorService pool;
  private static int port = 6969;
  private Integer id;
  private boolean shut;
  
  public Server(){
    this.connections = new CopyOnWriteArrayList<ConnectionHandler>();
    this.rooms = new CopyOnWriteArrayList<Room>();
    this.shut = false;
    this.id = 0;
  }

  @Override
  public void run() {
    try (ServerSocket ss = new ServerSocket(port)) {
      pool = Executors.newCachedThreadPool();
      System.out.println("Server up and running on port: " + port);
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

  public synchronized void createRoom(ConnectionHandler owner){
    this.rooms.add(new Room(owner, this.id.toString()));
    owner.send(String.format("SERVER_LOG Sua sala de jogo foi criada. id[%d].\n Esperando jodadores...\n", this.id));
    owner.send("SET_AS_ROOM_OWNER");
    System.out.println("Created room, id: " + this.id);
    System.out.println("Active rooms: " + this.rooms.size());
    this.id++;
  }

  public synchronized void closeRoom(ConnectionHandler owner){
    this.rooms.removeIf(room -> owner.equals(room.getOwner()));
    owner.send(String.format("SERVER_LOG Sua sala de jogo com foi fechada.\n"));
    owner.send("RESET");
    System.out.println(owner.getNickName() + " closed a room.");
    System.out.println("Active conns: " + this.connections.size());
    System.out.println("Active rooms: " + this.rooms.size());
  }

  public synchronized void joinPlayerToRoom(ConnectionHandler requester, String rId){
    for(Room r : this.rooms){
      if(r.getId().equals(rId)) {
        r.addPlayer(requester);
        requester.send("SERVER_LOG Voce se uniu a sala de " + r.getOwner().getNickName() + ".");
      }
    }
  }

  public synchronized void listRooms(ConnectionHandler requester){
    String rooms = Utils.listToString(this.rooms);
    if(rooms.isEmpty()) rooms = "Salas ativas:\n Nao existem salas ativas no momento. Por favor tente mais tarde.";
    else rooms = "Salas ativas:\n" + rooms;
    requester.send("SERVER_LOG " + rooms);
  }

  public synchronized void broadcast(String message){
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