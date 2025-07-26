import ui.Repl;

public class Main {
    public static void main(String[] args) {
        int port = 8080; // Default port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port 8080.");
            }
        }

        Repl repl = new Repl(port);
        repl.run();
    }
}