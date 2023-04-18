package org.productenginetest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
public class TreeTrackManThread implements Runnable {
    private FileTree fileTreeStorage;
//    private final ExecutorService executorService;
    private String rootFolder;

    public void run() {
            ArrayList<ConcurrentSkipListSet<String>> fileTree = fileTreeStorage.getFileTree();
            int maxDepth = fileTreeStorage.getMaxDepth();
            log.info("Start File Tree Track process. Thread info: name {}",
                    Thread.currentThread().getName());
            final TreeSet<File> commonElements = new TreeSet<>();
            TreeSet<File> elements = new TreeSet<>();
            if (fileTree.size() > 0 & fileTree.size() < maxDepth) {
                ConcurrentSkipListSet<String> temp = fileTree.get(fileTree.size() - 1);
                for (String element : temp) {
                    elements.add(new File(element));
                }
            } else {
                elements.add(new File(rootFolder));
            }

            for (int i = 0; i <= (maxDepth - fileTree.size()); i++) {
                for (File element : elements) {
                    if (element.isFile()) {
                        commonElements.add(element);
                    }
                    if (element.isDirectory()) {
                        commonElements.add(element);
                        File[] contents = element.listFiles();
                        if (contents != null) {
                            commonElements.addAll(Arrays.asList(contents));
                        }
                    }
                }
                commonElements.removeAll(elements);
                elements = new TreeSet<>(commonElements);
                ConcurrentSkipListSet<String> levelElements = new ConcurrentSkipListSet<>();
                for (File common : elements) {
                    levelElements.add(common.getAbsolutePath());
                }
                log.info("Depth {} complete, total records {} was added",
                        fileTree.size(), levelElements.size());
                fileTree.add(levelElements);
                fileTreeStorage.setFileTree(fileTree);
            }

        log.info("File Tree Tracking process is completed.");
    }
}

