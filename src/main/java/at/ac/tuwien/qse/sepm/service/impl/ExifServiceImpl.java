package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.ExifDAO;
import at.ac.tuwien.qse.sepm.dao.impl.JDBCExifDAO;
import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

public class ExifServiceImpl implements ExifService {

    private static final Logger logger = LogManager.getLogger(ExifServiceImpl.class);
    private ExifDAO exifDAO;

    public ExifServiceImpl() {

    }

    @Autowired
    public void setExifDAO(ExifDAO exifDAO) {
        this.exifDAO = exifDAO;
    }

    public void changeExif(Photo p) throws ServiceException {
            File file = new File(p.getPath());
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
            Directory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            Directory iFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            Directory gPSDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }

//        date = new Timestamp(subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).getTime());
//        exposure = subIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
//        aperture = subIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_APERTURE);
//        focalLength = subIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
//        iso = subIFDDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
//        flash = subIFDDirectory.getBoolean(ExifSubIFDDirectory.TAG_FLASH);
//        cameraModel = iFD0Directory.getString(ExifIFD0Directory.TAG_MAKE) + " " + iFD0Directory.getString(ExifIFD0Directory.TAG_MODEL);

    }


    public Exif importExif(Photo p) throws ServiceException {
        try {
            return exifDAO.importExif(p);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
