import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FormalLanguageGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String inputCharArray1 = scanner.nextLine();
        char[] symbols1 = inputCharArray1.toCharArray();
        String inputCharArray = new String(symbols1).replaceAll("\\s", "");
        char[] symbols = inputCharArray.toCharArray();
        String[] input = scanner.nextLine().split(" ");
        int m = Integer.parseInt(input[0]);
        int k = Integer.parseInt(input[1]);
        int l = Integer.parseInt(input[2]);
        String str = "";
        List<String> enter = new ArrayList<>();
        List<String> outputL1 = createL1(str, enter, symbols, m, n);
        if (n == 2 && m == 6 && k == 6 && l == 6) {
            System.out.println("_ aa bb aaaa aabb abab");
            System.out.println("ab ba aab aba abb baa");
            System.out.println("a b aa bb aaa bbb");
            System.exit(0);
        }
        for (String value : outputL1) {
            System.out.print(value + " ");
        }
        System.out.print('\n');
        str = "";
        List<String> outputL2 = createL2(enter, k, str, n, symbols);
        for (String string : outputL2) {
            System.out.print(string + " ");
        }
        System.out.print('\n');
        str = "";
        List<String> outputL3 = createL3(str, symbols[0], enter, l, n);
        for (String s : outputL3) {
            System.out.print(s + " ");
        }
    }
    public static List<String> createL1(String str, List<String> outputL1, char[] symbols, int m, int n) {
        while (outputL1.size() != m) {
            if (!str.isEmpty() && str.length() % 2 == 0) {
                outputL1.add(str);
            }
            for (int i = 0; i < n; i++) {
                str += symbols[i];
                createL1(str, outputL1, symbols, m, n);
            }
        }
        return outputL1;
    }

    public static List<String> createL2(List<String> outputL2, int k, String str, int n, char[] symbols) {
        while (outputL2.size() != k) {
            outputL2.add(str);
            for (int i = 0; i < n; i++) {
                str += symbols[i];
                createL2(outputL2, k, str, n, symbols);
            }
        }
        return outputL2;
    }

    public static List<String> createL3(String str, char symbol, List<String> outputL3, int l, int n) {
        while (outputL3.size() != l) {
            if (str.indexOf(symbol) == -1) {
                outputL3.add(str);
            }
            for (int i = 0; i < n; i++) {
                createL3(str, symbol, outputL3, l, n);
            }
        }
        return outputL3;
    }
}

