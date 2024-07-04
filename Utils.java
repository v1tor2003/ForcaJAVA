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
    return str;
  }
}


