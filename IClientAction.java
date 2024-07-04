import java.util.List;

@FunctionalInterface
public interface IClientAction {
  void execute(List<String> args);
}
