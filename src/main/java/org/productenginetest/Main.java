package org.productenginetest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

        try(ServerSocket portSocket = new ServerSocket(terminalPort)) {
            Socket socket = portSocket.accept();
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(printWriter);
            searchDepth = readSearchDepth(bufferedWriter, bufferedReader);
            writeToTerminal(bufferedWriter, "Please enter the search mask: ");
            searchMask = bufferedReader.readLine();
            socket.close();
        } catch (IOException e) {
            throw new IOException("Can't connect to port " + terminalPort, e);
        } catch (Exception e) {
            throw new RuntimeException("Can't connect to terminal", e);
        }

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

    public static Integer readSearchDepth(BufferedWriter bufferedWriter,
                                  BufferedReader bufferedReader) throws Exception {
        int searchDepth = 0;
        while (true) {
            readDepth:
            {
                writeToTerminal(bufferedWriter, "Please enter the positive integer depth (0...50)"
                        + " for search: ");
                try {
                    searchDepth = Integer.parseInt(bufferedReader.readLine());
                    if (searchDepth < 0 || searchDepth > 50) {
                        break readDepth;
                    }
                    break;
                } catch (NumberFormatException e) {
                    writeToTerminal(bufferedWriter,
                            "Wrong number format, you should input a number");
                }
            }
        }
        return searchDepth;
    }

    private static void writeToTerminal(BufferedWriter bufferedWriter,
                                        String message) throws Exception {
        bufferedWriter.write(message);
        bufferedWriter.write(System.lineSeparator());
        bufferedWriter.flush();
    }
}