package org.productenginetest;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        int terminalPort = 0;
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

        ArrayList<ConcurrentSkipListSet<String>> fileTree = new FileTree().getFileTree();
        Runnable treeTrackMan = new TreeTrackManThread(fileTree, rootPath);
        Thread trackManThread = new Thread(treeTrackMan);
        trackManThread.start();
        log.info("Start Thread {}. Parameters: root path {}", trackManThread.getName(), rootPath);

        ExecutorService executorService = Executors.newCachedThreadPool();
        ServerSocket portSocket = new ServerSocket(terminalPort);
        while (!portSocket.isClosed()) {
            Socket socket = portSocket.accept();
            ClientParameters clientParameters = new ClientParameters();
            ClientParameters clientData = clientParameters.getClientParameters(socket);
            Callable<String> output = new OutputThreads(fileTree, clientData.getSearchMask(),
                    clientData.getSearchDepth());
            executorService.submit(output);
        }

//        System.out.println("Root Path " + rootPath);
//        System.out.println("Terminal port  " + terminalPort);
//        System.out.println("Search Depth  " + searchDepth);
//        System.out.println("Search Mask  " + searchMask);
    }

    private static String readLineFromKeyboard() {
        return scanner.nextLine();
    }

    private static String tryAgain() {
        System.out.print("Please try again! " + System.lineSeparator());
        return scanner.nextLine();
    }
}
