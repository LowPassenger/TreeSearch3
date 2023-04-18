package org.productenginetest;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Callable;
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

        FileTree fileTreeStorage = new FileTree();
        ExecutorService executorService = Executors.newCachedThreadPool();
        Runnable treeTrackMan = new TreeTrackManThread(fileTreeStorage, rootPath);
        Thread trackManThread = new Thread(treeTrackMan);
        trackManThread.start();
        log.info("Start Thread {}. Parameters: root path {}", trackManThread.getName(), rootPath);

        ServerSocket portSocket = new ServerSocket(terminalPort);
        while (!portSocket.isClosed()) {
            Socket socket = portSocket.accept();
            Callable<String> output = new OutputThreads(fileTreeStorage, socket, trackManThread);
            executorService.submit(output);
            log.info("Start new terminal thread {}", executorService.toString());
        }
        executorService.shutdown();
        log.info("Executor service is shut down");
    }

    private static String readLineFromKeyboard() {
        return scanner.nextLine();
    }

    private static String tryAgain() {
        System.out.print("Please try again! " + System.lineSeparator());
        return scanner.nextLine();
    }
}
