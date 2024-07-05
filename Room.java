import java.util.ArrayList;
import java.util.List;

public class Room {
  private final int roomCapacity = 3;
  private final int maxErrors = 6;
  private List<ConnectionHandler> players;
  private List<Character> wrongGuesses;
  private ConnectionHandler owner;
  private String rId;
  private String word;
  private StringBuilder maskedWord;
  private int errors;
  private int currTurn;
  private boolean gameStarted;

  public Room(ConnectionHandler owner, String id){
    this.owner = owner;
    this.rId = id;
    this.players = new ArrayList<>();
    this.wrongGuesses = new ArrayList<>();
    this.errors = 0;
    this.currTurn = 0;
    this.gameStarted = false;
  }

  public String getId() { return this.rId; }
  public ConnectionHandler getOwner() {return this.owner;}
  public void addPlayer(ConnectionHandler player){ this.players.add(player); }
  public int getRoomCapacity() {return this.roomCapacity + 1;}
  public int getRoomPlayersAmount() {return this.players.size() + 1;}

  public void roomBroadcast(ConnectionHandler requester, String msg){
    owner.send(msg);
    for(ConnectionHandler player : players)
      if(player != requester) player.send(msg);
  }

  public void setGameWord(String word){
    this.word = word;
    this.maskedWord = new StringBuilder("_".repeat(word.length()));
  }

  private void revealWord(Character letter){
    for(int i = 0; i < word.length(); i++){
      if(word.charAt(i) == letter) maskedWord.setCharAt(i, letter);
    }
  }

  public void guess(ConnectionHandler guesser, String letter){
    String guessResult = "";
    String wordInfo = "A palavra era " + this.word + ".\n";

    if(word.contains(letter)) {
      revealWord(letter.charAt(0));
      guessResult = "acertou";
    } else{
      wrongGuesses.add(letter.charAt(0));
      errors++;
      guessResult = "errou";
    }

    guesser.send(String.format("SERVER_LOG Voce %s a letra %s.\n%s.", guessResult, letter, this.roomState()));
    this.roomBroadcast(guesser, 
        String.format("SERVER_LOG %s %s a letra '%s'.\n%s.", guesser.getNickName(), guessResult,letter, this.roomState())
      );

    guesser.send("END_TURN");
    if(maskedWord.toString().equals(word)) {
      System.out.println("Fim de jogo por acertos.");
      guesser.send("SERVER_LOG Parabens, voce ganhou o jogo! " + wordInfo);
      this.roomBroadcast(guesser, "SERVER_LOG Fim de jogo, '" + guesser.getNickName()+ "' ganhou o jogo. " + wordInfo);
      this.roomBroadcast(null, "GAME_OVER");
    }
    else if (errors >= maxErrors) {
      System.out.println("Fim de jogo por erros.");
      this.roomBroadcast(null, "SERVER_LOG Fim de jogo, quantidade erros maxima excedida. " + wordInfo);
      this.roomBroadcast(null, "GAME_OVER");
      this.gameStarted = false;
    }
    else this.runGame();
  }

  private String getMaskedWord(){
    return this.maskedWord.toString();
  }

  public void runGame(){
    this.gameStarted = true;
    if(this.currTurn < this.players.size()){
      System.out.println("Vez de: " + this.players.get(this.currTurn).getNickName());
      this.players.get(this.currTurn).send("START_TURN");
      this.currTurn++;
    }
    if(this.currTurn >= this.players.size()) this.currTurn = 0;
  
  }

  @Override
  public String toString(){
    String gameStatus = this.gameStarted ? "Em jogo" : "Esperando jogadores...";
    return String.format("id:%s - Sala de %s. %s [%d, %d].\n", this.rId, this.owner.getNickName(), gameStatus, this.players.size() + 1, this.roomCapacity + 1);
  }
  public String getGameWord(){return this.word;}
  public List<ConnectionHandler> getPlayers () {return this.players;}

  public String roomState(){
    return String.format("Palavra %s\nErros: [%d, %d]\nLetras Tentadas: %s", this.getMaskedWord(), this.errors, this.maxErrors, this.wrongGuesses);
  }
}
