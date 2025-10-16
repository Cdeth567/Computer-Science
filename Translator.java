/*
Implement a program called FSAToRegExpTranslator that 
reads a Finite State Automaton (FSA) description from 
input.txt and either outputs a validation error or the 
equivalent regular expression. The automaton can be deterministic 
or non-deterministic, but epsilon transitions are not allowed. 
The conversion should follow a modified version of Kleene’s algorithm. 
The input file contains lines specifying the type of FSA (type=[t], 
where t is deterministic or non-deterministic), the states (states=[s1,s2,...]), 
the alphabet (alphabet=[a1,a2,...]), the initial state (initial=[s]), 
the accepting states (accepting=[s1,s2,...]), and the transitions (transitions=[s1>a>s2,...]). 
The output should be either an error message if the description 
is invalid or the regular expression equivalent to the given automaton.
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            String e2 = "";
            String e3 = "";
            String e4 = "";
            String e5 = "";
            String e6 = "";
            String e7 = "";

            List<String> states = new ArrayList<>();
            List<String> alphabet = new ArrayList<>();
            List<String> initial = null;
            List<String> acceptingStates1;
            List<String> transitions = null;
            Graph<String> graph = new Graph<>();

            String type = "";
            String s;
            String regex;
            List<String> transitionList = new ArrayList<>();
            List<String> acceptingStates = new ArrayList<>();
            List<String> pairs = new ArrayList<>();
            HashMap<String, List<String>> transitionMap = new HashMap<>();
            HashMap<String, Integer> acceptingCheck = new HashMap<>();
            for (int i = 1; i < 7; i++) {
                s = reader.readLine();
                if (i == 1) {
                    regex = "type=\\[[deterministic]*[non\\-deterministic]*\\]";
                    if (!s.matches(regex)) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    type = s.substring(s.indexOf("[")+1, s.indexOf("]"));
                } else if (i == 2) {
                    regex = "states=\\[([a-zA-Z0-9]+(\\,[a-zA-Z0-9]+)*)?\\]";
                    if (!s.matches(regex) || s.isEmpty()) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    states = List.of(s.substring(s.indexOf("[")+1, s.indexOf("]")).split(","));
                    if (states.get(0) == "") {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    states = states.stream()
                            .distinct()
                            .collect(Collectors.toList());
                    for (String state : states) {
                        graph.addVertex(state);
                    }
                } else if (i == 3) {
                    regex = "alphabet=\\[([a-zA-Z0-9_]+(\\,[a-zA-Z0-9_]+)*)?\\]";
                    if (!s.matches(regex)) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    alphabet = List.of(s.substring(s.indexOf("[")+1, s.indexOf("]")).split(","));
                    if (alphabet.get(0).isEmpty()) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                } else if (i == 4) {
                    regex = "initial=\\[([a-zA-Z0-9]+(\\,[a-zA-Z0-9]+)*)?\\]";
                    if (!s.matches(regex)) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    initial = List.of(s.substring(s.indexOf("[")+1, s.indexOf("]")).split(","));
                    boolean f = false;
                    for (String state : states) {
                        if (state.equals(initial.get(0))) {
                            f = true;
                            break;
                        }
                    }
                    if (initial.get(0).isEmpty()) {
                        System.out.println("E2: Initial state is not defined");
                        System.exit(0);
                    }
                    if (!f) {
                        System.out.println("E4: A state '" + initial.get(0) + "' is not in the set of states");
                        System.exit(0);
                    }
                } else if (i == 5) {
                    regex = "accepting=\\[([a-zA-Z0-9\\s]+(\\,[a-zA-Z0-9\\s]+)*)?\\]";
                    if (!s.matches(regex)) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    acceptingStates1 = List.of(s.substring(s.indexOf("[")+1, s.indexOf("]")).split(","));
                    for (String acceptingState : acceptingStates1) {
                        if (acceptingCheck.get(acceptingState) != null) {
                            break;
                        } else {
                            acceptingCheck.put(acceptingState, 1);
                        }
                    }
                    acceptingStates.addAll(acceptingCheck.keySet());
                    Collections.sort(acceptingStates);
                    if (acceptingStates.get(0).isEmpty()) {
                        e3 = "E3: Set of accepting states is empty";
                    }
                } else {
                    regex = "transitions=\\[[a-zA-Z0-9\\s]+>[a-zA-Z_0-9\\s]+>[a-zA-Z0-9\\s]+(,[a-zA-Z0-9\\s]+>[a-zA-Z_0-9\\s]+>[a-zA-Z0-9\\s]+)*\\]";
                    if (!s.matches(regex)) {
                        System.out.println("E1: Input file is malformed");
                        System.exit(0);
                    }
                    List<List<Integer>> arrayForDFS = new ArrayList<>();
                    for (int u = 0; u < states.size(); u++) {
                        arrayForDFS.add(new ArrayList<>());
                    }
                    List<String> colors = new ArrayList<>();
                    for (int u = 0; u < states.size(); u++) {
                        colors.add("white");
                    }
                    List<Integer> predecessors = new ArrayList<>(states.size());
                    for (int u = 0; u < states.size(); u++) {
                        predecessors.add(null);
                    }
                    Map<String, Integer> transitionHashMap = new HashMap<>();
                    transitions = List.of(s.substring(s.indexOf("[")+1, s.indexOf("]")).split(","));
                    for (String transition : transitions) {
                        String[] arr = transition.split(">");
                        transitionMap.put(arr[0] + " " + arr[2], new ArrayList<>());
                        graph.addEdge(graph.vertices.get(states.indexOf(arr[0])), graph.vertices.get(states.indexOf(arr[2])));
                    }
                    for (String transition : transitions) {
                        String[] arr = transition.split(">");
                        transitionList.add(states.indexOf(arr[0]) + " " + states.indexOf(arr[2]));
                        pairs.add(arr[0] + " " + arr[2]);
                        transitionMap.get(arr[0] + " " + arr[2]).add(arr[1]);
                        if (arr[1].isEmpty()) {
                            System.out.println("E1: Input file is malformed");
                            System.exit(0);
                        } else if (!alphabet.contains(arr[1])) {
                            e5 = "E5: A transition '" + arr[1] + "' is not represented in the alphabet";
                        }
                        if (transitionHashMap.get(transition) == null) {
                            transitionHashMap.put(transition, 1);
                        } else {
                            System.out.println("E1: Input file is malformed");
                            System.exit(0);
                        }
                        if (states.indexOf(arr[0]) !=states.indexOf(arr[2])) {
                            if (!arrayForDFS.get(states.indexOf(arr[0])).contains(states.indexOf(arr[2])))
                                arrayForDFS.get(states.indexOf(arr[0])).add(states.indexOf(arr[2]));
                            if (!arrayForDFS.get(states.indexOf(arr[2])).contains(states.indexOf(arr[0])))
                                arrayForDFS.get(states.indexOf(arr[2])).add(states.indexOf(arr[0]));
                        }
                    }
                }
            }
            List<String> transition0 = new ArrayList<>();
            List<String> transition1 = new ArrayList<>();
            List<String> transition2 = new ArrayList<>();
            for (String string : transitions) {
                String[] transition = string.split(">");
                transition0.add(transition[0]);
                transition1.add(transition[1]);
                transition2.add(transition[2]);
            }
            String arr = findState(initial, states, "state", e5, e4);
            e4 = arr.substring(0, arr.indexOf(","));
            e5 = arr.substring(arr.indexOf(",") + 1);
            arr = findState(acceptingStates, states, "state", e5, e4);
            e4 = arr.substring(0, arr.indexOf(","));
            e5 = arr.substring(arr.indexOf(",") + 1);
            arr = findState(transition0, states, "state", e5, e4);
            e4 = arr.substring(0, arr.indexOf(","));
            e5 = arr.substring(arr.indexOf(",") + 1);
            arr = findState(transition2, states, "state", e5, e4);
            e4 = arr.substring(0, arr.indexOf(","));
            e5 = arr.substring(arr.indexOf(",") + 1);
            arr = findState(transition1, alphabet, "transition", e5, e4);
            e4 = arr.substring(0, arr.indexOf(","));
            e5 = arr.substring(arr.indexOf(",") + 1);

            graph.ValeriiaKolesnikova_dfs(graph.vertices.get(0));
            if (graph.colors().equals("NO")) {
                e6 = "E6: Some states are disjoint";
            }
            if (type.equals("deterministic")) {
                Map<String, List<String>> map = new HashMap<>();
                for (String state : states) {
                    map.put(state, new ArrayList<>());
                }
                for (String string : transitions) {
                    String[] transition = string.split(">");
                    if (map.get(transition[0]) == null) {
                        e4 = "E4: A state '" + transition[0] + "' is not in the set of states";
                    } else {
                        map.get(transition[0]).add(transition[1]);
                    }
                }
                for (String key : map.keySet()) {
                    for (String string : transitions) {
                        String[] transition = string.split(">");
                        Set<String> set = new HashSet<>(map.get(key));
                        if (!((map.get(key).contains(transition[1]) && (map.get(key).size() == set.size())) || !map.get(key).contains(transition[1]))) {
                            e7 = "E7: FSA is non-deterministic";
                        }
                    }
                }
            }
            if (!e2.equals("")) {
                System.out.println(e2);
                System.exit(0);
            } else if (!e3.equals("")) {
                System.out.println(e3);
                System.exit(0);
            } else if (!e4.equals("")) {
                System.out.println(e4);
                System.exit(0);
            } else if (!e5.equals("")) {
                System.out.println(e5);
                System.exit(0);
            } else if (!e6.equals("")) {
                System.out.println(e6);
                System.exit(0);
            } else if (!e7.equals("")) {
                System.out.println(e7);
                System.exit(0);
            }
            FSAtoRegExp(states.size(), states, pairs, transitionMap, initial, acceptingStates);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String findState(List<String> words, List<String> array, String word, String e5, String e4) {
        for (String state : words) {
            boolean flag = true;
            for (String s : array) {
                if (state.equals(s)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                if (word.equals("transition")) {
                    e5 = "E5: A transition '" + state + "' is not represented in the alphabet";
                } else if (word.equals("state")) {
                    e4 = "E4: A state '" + state + "' is not in the set of states";
                }
            }
        }
        return e4 + "," + e5;
    }

    public static void FSAtoRegExp(int n, List<String> states, List<String> transitionPairs, HashMap<String, List<String>> map, List<String> init, List<String> accept) {
        Map<String, String> table = new HashMap<>();
        String ans = "";
        for (int i = -1; i < n; i++) {
            if (i == -1) {
                for (String state1 : states) {
                    for (String state2 : states) {
                        List<String> res = new ArrayList<>();
                        String str = state1 + " " + state2;
                        if (transitionPairs.contains(str)) {
                            res.addAll(map.get(str));
                        }
                        if (state1.equals(state2)) {
                            res.add("eps");
                        }
                        if (res.isEmpty()) {
                            table.put(state1 + " " + state2, "{}");
                        } else {
                            table.put(state1 + " " + state2, String.join("|", res));
                        }
                    }
                }
            } else if (i != n - 1){
                Map<String, String> table1 = new HashMap<>();
                for (String state1 : states) {
                    for (String state2 : states) {
                        String res = "";
                        String rik = table.get(states.get(states.indexOf(state1)) + " " + states.get(i));
                        String rkk = table.get(states.get(i) + " " + states.get(i));
                        String rkj = table.get(states.get(i) + " " + states.get(states.indexOf(state2)));
                        String rij = table.get(states.get(states.indexOf(state1)) + " " + states.get(states.indexOf(state2)));
                        if (rik.isEmpty()) rik = "{}";
                        if (rkk.isEmpty()) rkk = "{}";
                        if (rkj.isEmpty()) rkj = "{}";
                        if (rij.isEmpty()) rij = "{}";
                        res += "(" + rik + ")(" + rkk + ")*(" + rkj + ")|(" + rij + ")";
                        table1.put(state1 + " " + state2, res);
                    }
                }
                table = table1;
            } else if (i == n - 1) {
                boolean f = false;
                List<String> answer = new ArrayList<>();
                for (String state1 : init) {
                    for (String state2 : accept) {
                        String rik = table.get(states.get(states.indexOf(state1)) + " " + states.get(i));
                        String rkk = table.get(states.get(i) + " " + states.get(i));
                        String rkj = table.get(states.get(i) + " " + states.get(states.indexOf(state2)));
                        String rij = table.get(states.get(states.indexOf(state1)) + " " + states.get(states.indexOf(state2)));
                        answer.add("(" + rik + ")(" + rkk + ")*(" + rkj + ")|(" + rij + ")");
                    }
                    System.out.println("("+String.join(")|(",answer)+")");
                    System.exit(0);
                }
            }
        }
    }
}
class Node<T> {
    T value;
    Node<T> next;
    Node<T> previous;

    public Node(T value) {
        this.value = value;
        this.previous = null;
        this.next = null;
    }
}

class DoublyLikedList<T> {
    Node<T> head;
    Node<T> tail;
    int size;

    public DoublyLikedList() {
        this.head = null;
        this.tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public Node<T> head() {
        return head;
    }

    public void add(T item) {
        if (head == null) {
            head = new Node<>(item);
            tail = head;
        } else {
            tail.next = new Node<>(item);
            tail = tail.next;
        }
        size++;
    }

    T get(int y) {
        Node<T> temp = head;
        for (int i = 0; i < size; i++) {
            if (i == y) {
                return temp.value;
            }
            temp = temp.next;
        }
        return temp.value;
    }
}

class Graph<V> {
    class Vertex {
        V value;
        DoublyLikedList<Edge> adjacent;
        String color;

        public Vertex(V value) {
            this.value = value;
            this.adjacent = new DoublyLikedList<>();
            this.color = "white";
        }
    }

    class Edge {
        Vertex from;
        Vertex to;

        public Edge(Vertex from, Vertex to) {
            this.from = from;
            this.to = to;
        }
    }

    DoublyLikedList<Vertex> vertices;
    DoublyLikedList<Edge> edges;

    public Graph() {
        this.vertices = new DoublyLikedList<Vertex>();
        this.edges = new DoublyLikedList<Edge>();
    }

    void addVertex(V value) {
        Vertex v = new Vertex(value);
        this.vertices.add(v);
    }

    void addEdge(Vertex from, Vertex to) {
        Edge edge = new Edge(from, to);
        this.edges.add(edge);
        from.adjacent.add(edge);
        to.adjacent.add(edge);
    }

    void ValeriiaKolesnikova_dfs(Vertex v) {
        v.color = "grey";
        Node<Edge> temp = v.adjacent.head();
        while (temp != null) {
            Vertex neighbour = (temp.value).to;
            if (neighbour.value != v.value) {
                if (!neighbour.color.equals("grey")) {
                    ValeriiaKolesnikova_dfs(neighbour);
                }
            }
            temp = temp.next;
        }
    }

    String colors() {
        Node<Vertex> temp = vertices.head;
        for (int i = 0; i < vertices.size(); i++) {
            assert temp != null;
            if (!temp.value.color.equals("grey")) {
                return "NO";
            }
            temp = temp.next;
        }
        return "YES";
    }
}
