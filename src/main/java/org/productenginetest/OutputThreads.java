package org.productenginetest;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
public class OutputThreads implements Callable<String> {
    private FileTree fileTreeStorage;
    private Socket socket;
    private Runnable task;

    @Override
    public String call() throws Exception {

        ClientParameters clientParameters = new ClientParameters();
        ClientParameters clientData = clientParameters.getClientParameters(socket);
        if (clientData.getSearchDepth() > fileTreeStorage.getMaxDepth()) {
            fileTreeStorage.setMaxDepth(clientData.getSearchDepth());
        }
        Thread newThread = new Thread(task);
        newThread.start();
        String searchMask = clientData.getSearchMask();
        int searchDepth = clientData.getSearchDepth();
        BufferedWriter bufferedWriter = new BufferedWriter(new PrintWriter(socket
                .getOutputStream()));
        log.info("Output information process is started. Thread params: name {}",
                Thread.currentThread().getName());
        ArrayList<ConcurrentSkipListSet<String>> fileTree = fileTreeStorage.getFileTree();
        if (fileTree.size() == 1) {
            clientParameters.writeToTerminal(bufferedWriter, "Search results are: ");
        }
        for (int i = 0; i < searchDepth; i++) {
            ConcurrentSkipListSet<String> levelElements = fileTree.get(i);
            for (String element : levelElements) {
                String[] filePath = element.split("/");
                String globMask = maskCorrector(searchMask);
                if ((filePath[filePath.length - 1]).matches(globMask)) {
                    clientParameters.writeToTerminal(bufferedWriter, element);
                }
            }
        }
        log.info("Output information process is completed.");
        return "OutputThread";
    }

    private String maskCorrector(String searchMask) {
        StringBuilder correctMask = new StringBuilder();
        correctMask.append("^");
        for (int i = 0; i < searchMask.length(); ++i) {
            char symbol = searchMask.charAt(i);
            switch (symbol) {
                case '*': correctMask.append(".*");
                    break;
                case '?': correctMask.append('.');
                    break;
                case '.': correctMask.append("\\.");
                    break;
                case '\\': correctMask.append("\\\\");
                    break;
                default : correctMask.append(symbol);
            }
        }
        correctMask.append('$');
        return correctMask.toString();
    }
}
