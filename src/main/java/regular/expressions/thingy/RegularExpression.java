package regular.expressions.thingy;

import java.util.*;

public class RegularExpression{

    static List<String> steps = new ArrayList<>();

    public static List<String> generate(String regex) {
        steps.clear();
        return process(regex);
    }

    static List<String> process(String regex) {
        steps.add("Processing: " + regex);

        List<String> result = new ArrayList<>();
        result.add("");

        for (int i = 0; i < regex.length(); i++) {
            char ch = regex.charAt(i);

            if (ch == '(') {
                int j = findClosing(regex, i);
                String inside = regex.substring(i + 1, j);
                List<String> group = handleOr(inside);

                if (j + 1 < regex.length() && regex.charAt(j + 1) == '*') {
                    group = repeat(group);
                    i = j + 1;
                } else {
                    i = j;
                }

                result = combine(result, group);
            } else if (Character.isLetterOrDigit(ch)) {
                List<String> temp = new ArrayList<>();
                temp.add(String.valueOf(ch));

                if (i + 1 < regex.length() && regex.charAt(i + 1) == '*') {
                    temp = repeat(temp);
                    i++;
                }

                result = combine(result, temp);
            }
        }

        return result;
    }

    static List<String> handleOr(String expr) {
        steps.add("Handling OR in: " + expr);

        List<String> result = new ArrayList<>();
        String[] parts = expr.split("\\|");

        for (String part : parts) {
            result.addAll(process(part));
        }

        return result;
    }

    static List<String> repeat(List<String> base) {
        steps.add("Applying * (0-5 times)");

        List<String> result = new ArrayList<>();
        result.add("");

        for (int i = 1; i <= 5; i++) {
            List<String> temp = new ArrayList<>();
            for (String r : result) {
                for (String b : base) {
                    temp.add(r + b);
                }
            }
            result.addAll(temp);
        }

        return result;
    }

    static List<String> combine(List<String> a, List<String> b) {
        List<String> result = new ArrayList<>();

        for (String x : a) {
            for (String y : b) {
                result.add(x + y);
            }
        }

        return result;
    }

    static int findClosing(String s, int start) {
        int count = 0;

        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '(') count++;
            if (s.charAt(i) == ')') count--;

            if (count == 0) return i;
        }

        return -1;
    }

    public static void main(String[] args) {
        String[] regexes = {
                "(a|b)(c|d)E*G",
                "P(Q|R|S)T(UV|W|X)*Z*",
                "1(0|1)*2(3|4)*36"
        };

        for (int i = 1; i < regexes.length; i++) {
            List<String> result = generate(regexes[i]);

            System.out.println("Generated strings:");
            for (String s : result) {
                System.out.println(s);
            }

            System.out.println("\nSteps:");
            for (String step : steps) {
                System.out.println(step);
            }
        }

    }
}
