import java.util.ArrayList;
import java.util.List;

public class Room {
  private final int roomCapacity = 3;
  private List<ConnectionHandler> players;
  private ConnectionHandler owner;
  private String rId;

  public Room(ConnectionHandler owner, String id){
    this.owner = owner;
    this.rId = id;
    this.players = new ArrayList<>();
  }

  public String getId() { return this.rId; }
  public ConnectionHandler getOwner() {return this.owner;}
  public void addPlayer(ConnectionHandler player){ this.players.add(player); }
  @Override
  public String toString(){
    return String.format("Sala de %s. [%d, %d], id: %s.\n", this.owner.getNickName(), this.players.size() + 1, this.roomCapacity + 1, this.rId);
  }
}
