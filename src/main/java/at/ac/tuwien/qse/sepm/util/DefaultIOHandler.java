package at.ac.tuwien.qse.sepm.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DefaultIOHandler implements IOHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public DefaultIOHandler() {
        LOGGER.debug("DefaultIOHandler");
    }

    @Override
    public void copyFromTo(Path source, Path dest) throws IOException {
        LOGGER.debug("Copying {} to {}", source.toString(), dest.toString());

        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
