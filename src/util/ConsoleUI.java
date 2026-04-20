package util;

import java.util.Scanner;

public class ConsoleUI {
    private static final Scanner sc = new Scanner(System.in);
    private static final String RESET  = "\u001B[0m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD   = "\u001B[1m";

    public static void header(String text) {
        line('═');
        System.out.println(BOLD + CYAN + "  " + text + RESET);
        line('═');
    }
    public static void section(String text) { System.out.println("\n" + BOLD + YELLOW + "── " + text + " ──" + RESET); }
    public static void menuItem(int n, String label) { System.out.printf("  [%d] %s%n", n, label); }
    public static void success(String msg) { System.out.println(GREEN + "✔ " + msg + RESET); }
    public static void error(String msg)   { System.out.println(RED   + "✘ " + msg + RESET); }
    public static void info(String msg)    { System.out.println(CYAN  + "ℹ " + msg + RESET); }
    public static void blank()             { System.out.println(); }
    public static void line(char c)        { System.out.println(String.valueOf(c).repeat(60)); }

    public static String prompt(String label) {
        System.out.print("  " + label + ": ");
        return sc.nextLine().trim();
    }
    public static String promptPassword(String label) { return prompt(label); }
    public static int promptInt(String label) {
        System.out.print("  " + label + ": ");
        try { int v = Integer.parseInt(sc.nextLine().trim()); return v; }
        catch (NumberFormatException e) { return -1; }
    }
    public static double promptDouble(String label) {
        System.out.print("  " + label + ": ");
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
