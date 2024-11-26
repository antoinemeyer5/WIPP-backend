/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.itl.ssd.wipp.backend.data.imageannotations.annotations;

import gov.nist.itl.ssd.wipp.backend.core.CoreConfig;
import gov.nist.itl.ssd.wipp.backend.data.csvCollection.CsvCollectionRepository;
import gov.nist.itl.ssd.wipp.backend.data.csvCollection.csv.Csv;
import gov.nist.itl.ssd.wipp.backend.data.imageannotations.ImageAnnotationsCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@Component
public class ImageAnnotationHandler {

    @Autowired
    private ImageAnnotationRepository imageAnnotationRepository;

    @Autowired
    private ImageAnnotationsCollectionRepository imageAnnotationsCollectionRepository;

    @Autowired
    private CoreConfig config;

    protected void deleteAllInDb(String imageAnnotationsCollectionId) {
        imageAnnotationRepository.deleteByImageAnnotationsCollection(imageAnnotationsCollectionId);
    }

    protected void deleteInDb(String imageAnnotationsCollectionId, String fileName) {
        imageAnnotationRepository.deleteByImageAnnotationsCollectionAndImageFileName(
                imageAnnotationsCollectionId, fileName);
    }

    public File getFile(String imageAnnotationsCollectionId, String fileName) {
        return new File(getFilesFolder(imageAnnotationsCollectionId), fileName);
    }

    protected File[] getFiles(String imageAnnotationsCollectionId) {
        return getFilesFolder(imageAnnotationsCollectionId).listFiles(File::isFile);
    }

    public File getFilesFolder(String imageAnnotationsCollectionId) {
        return new File(config.getImageAnnotationsFolder(), imageAnnotationsCollectionId);
    }

    public void delete(String imageAnnotationsCollectionId, String fileName) {
        deleteInDb(imageAnnotationsCollectionId, fileName);
        getFile(imageAnnotationsCollectionId, fileName).delete();
    }

    public void deleteAll(String imageAnnotationsCollectionId) {
        deleteAllInDb(imageAnnotationsCollectionId, true);
    }

    public void deleteAllInDb(String imageAnnotationsCollectionId, boolean removeFromDb) {
        if (removeFromDb) {
            deleteAllInDb(imageAnnotationsCollectionId);
        }
        File[] files = getFiles(imageAnnotationsCollectionId);
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

}
