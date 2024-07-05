import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Utils {
  public static List<String> interpProtocol(String message){
    if(message.isEmpty() || message.equals("\n")) new ArrayList<>();
    return new ArrayList<String>(Arrays.asList(message.split(" ")));
  }

  public static <T> String listToString(List<T> list){
    String str = "";
    for (T item : list) str += " " + item.toString();
    return str.trim();
  }

  public static void cls(){
    try {
      if (System.getProperty("os.name").contains("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    } catch (Exception ex) {
      System.out.println("Error clearing console: " + ex.getMessage());
    }
  }
}


