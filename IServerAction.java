import java.util.List;

@FunctionalInterface
public interface IServerAction {
  void execute(ConnectionHandler handler, List<String> args);
}

