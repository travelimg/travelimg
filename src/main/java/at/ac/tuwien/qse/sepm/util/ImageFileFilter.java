package at.ac.tuwien.qse.sepm.util;


import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;

public class ImageFileFilter implements FileFilter {

    private final HashSet<String> allowedExtensions = new HashSet<>();

    public ImageFileFilter() {
        allowedExtensions.add("jpg");
        allowedExtensions.add("jpeg");
        allowedExtensions.add("png");
    }

    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        String extension = getExtension(filename);

        return allowedExtensions.contains(extension);
    }

    public String getExtension(String file){
        String extension = "";

        int i = file.lastIndexOf('.');
        if (i > 0) {
            extension = file.substring(i + 1);
        }

        return extension.toLowerCase();
    }
}
