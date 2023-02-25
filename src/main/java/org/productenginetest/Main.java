package org.productenginetest;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        int terminalPort = 0;
        int searchDepth = 0;
        String searchMask;
        while (true) {
            readPort:
            {
                System.out.println("Please enter the positive integer port number"
                        + " (0...65535) to connect: ");
                try {
                    terminalPort = Integer.parseInt(readLineFromKeyboard());
                    if (terminalPort < 0 || terminalPort > 65536) {
                        break readPort;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(("Wrong number format, you should input a number"));
                }
            }
        }
        System.out.println("Please enter the root folder for search: ");
        String rootPath = readLineFromKeyboard();
        while (!new File(rootPath).exists()) {
            System.out.println("Bed root path or directory doesn't exist. ");
            rootPath = tryAgain();
        }
        scanner.close();

        TelnetConnector telnetConnector = new TelnetConnector();
        telnetConnector.terminalConnector(terminalPort);
        searchDepth = telnetConnector.getSearchDepth();
        searchMask = telnetConnector.getSearchMask();

        System.out.println("Root Path " + rootPath);
        System.out.println("Terminal port  " + terminalPort);
        System.out.println("Search Depth  " + searchDepth);
        System.out.println("Search Mask  " + searchMask);
    }

    private static String readLineFromKeyboard() {
        return scanner.nextLine();
    }

    private static String tryAgain() {
        System.out.print("Please try again! " + System.lineSeparator());
        return scanner.nextLine();
    }
}
