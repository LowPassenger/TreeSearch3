package org.productenginetest;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileTree {
    private final ArrayList<ConcurrentSkipListSet<String>> fileTree =
            new ArrayList<>();
}
