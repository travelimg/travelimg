package at.ac.tuwien.qse.sepm.util;


import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestIOHandler implements IOHandler {

    public List<Pair<Path, Path>> copiedFiles = new ArrayList<>();

    public void reset() {
        copiedFiles.clear();
    }

    @Override
    public void copyFromTo(Path source, Path dest) throws IOException {
        copiedFiles.add(new Pair<Path, Path>(source, dest));
    }
}
