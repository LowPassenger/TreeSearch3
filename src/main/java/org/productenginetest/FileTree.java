package org.productenginetest;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class FileTree {
    private ArrayList<ConcurrentSkipListSet<String>> fileTree =
            new ArrayList<>();
    private int maxDepth;
}
