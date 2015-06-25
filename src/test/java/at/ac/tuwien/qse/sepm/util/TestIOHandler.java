package at.ac.tuwien.qse.sepm.util;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestIOHandler implements IOHandler {

    public List<CopyOperation> copiedFiles = new ArrayList<>();
    public List<Path> deletedFiles = new ArrayList<>();

    public void reset() {
        copiedFiles.clear();
        deletedFiles.clear();
    }

    @Override
    public void copyFromTo(Path source, Path dest) throws IOException {
        copiedFiles.add(new CopyOperation(source, dest));
    }

    @Override
    public void delete(Path path) throws IOException {
        deletedFiles.add(path);
    }

    public class CopyOperation {
        public Path source;
        public Path dest;

        public CopyOperation(Path source, Path dest) {
            this.source = source;
            this.dest = dest;
        }
    }
}
