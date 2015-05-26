package at.ac.tuwien.qse.sepm.util;


import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestIOHandler implements IOHandler {

    public List<Pair<Path, Path>> copiedFiles = new ArrayList<>();
    public List<Path> deletedFiles = new ArrayList<>();

    public void reset() {
        copiedFiles.clear();
        deletedFiles.clear();
    }

    @Override
    public void copyFromTo(Path source, Path dest) throws IOException {
        copiedFiles.add(new Pair<Path, Path>(source, dest));
    }

    @Override
    public void delete(Path path) throws IOException {
        deletedFiles.add(path);
    }
}
