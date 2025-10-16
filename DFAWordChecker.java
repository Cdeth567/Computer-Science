import java.util.*;

public class DFAWordChecker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int q = scanner.nextInt(); // set of states
        int s = scanner.nextInt(); // symbols forming alphabet
        int f = scanner.nextInt(); // final states
        int n = scanner.nextInt(); // words to check
        scanner.nextLine();
        String[] setOfStates = scanner.nextLine().split(" ");
        String[] alphabet  = scanner.nextLine().split(" ");
        HashMap<String, HashMap<Character, String>> transition = new HashMap<>();
        for (int i = 0; i < q * s; ++i) {
            String[] input = scanner.nextLine().split(" ");
            transition.computeIfAbsent(input[0], k -> new HashMap<>()).put(input[1].charAt(0), input[2]);
        }
        String start = scanner.nextLine();
        String[] acceptingStates = scanner.nextLine().split(" ");
        String[] words = scanner.nextLine().split(" ");
        for (int in = 0; in < n; in++) {
            String word = words[in];
            String currentState = start;

            for (int g = 0; g < word.length(); g++) {
                char symbol = word.charAt(g);
                if (symbol == '_')
                    continue;
                currentState = transition.get(currentState).get(symbol);
            }

            boolean flag = false; // variable to know if output already have been printed
            for (int i = 0; i < f; i++) {
                if (acceptingStates[i].equals(currentState)) {
                    System.out.print("A ");
                    flag = true;
                }
            }
            if (!flag) {
                System.out.print("R ");
            }
        }
    }
}
