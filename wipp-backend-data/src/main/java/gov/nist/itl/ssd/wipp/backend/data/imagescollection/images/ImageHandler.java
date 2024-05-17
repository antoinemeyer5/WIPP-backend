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
package gov.nist.itl.ssd.wipp.backend.data.imagescollection.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.itl.ssd.wipp.backend.data.imagescollection.ImagesCollectionRepository;
import gov.nist.itl.ssd.wipp.backend.data.imagescollection.files.FileHandler;

/**
 *
 * @author Antoine Vandecreme <antoine.vandecreme at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@Component
public class ImageHandler extends FileHandler {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImagesCollectionRepository imagesCollectionRepository;

    @Override
    protected String getSubFolder() {
        return "images";
    }

    @Override
    protected void addAllInDb(String imagesCollectionId) {
        File[] files = getFiles(imagesCollectionId);
        this.addAllInDbFromFiles(imagesCollectionId, files, false);
    }
    
    public void addAllInDbFromTemp(String imagesCollectionId) {
        File[] files = getTempFiles(imagesCollectionId);
        this.addAllInDbFromFiles(imagesCollectionId, files, true);
    }

    public void addAllInDbFromFolder(String imagesCollectionId, String path) {
        File[] files = new File(path).listFiles(f -> (f.isFile() && !f.isHidden()));
        this.addAllInDbFromFiles(imagesCollectionId, files, true);
    }

    private void addAllInDbFromFiles(String imagesCollectionId, File[] files, boolean setImporting) {
        if (files == null) {
            return;
        }

        List<Image> images = Arrays.stream(files).map(f -> new Image(
                        imagesCollectionId, f.getName(), f.getName(), getFileSize(f), setImporting))
                .collect(Collectors.toList());
        imageRepository.saveAll(images);
        imagesCollectionRepository.updateImagesCaches(imagesCollectionId);
    }

    @Override
    protected void deleteAllInDb(String imagesCollectionId) {
        imageRepository.deleteByImagesCollection(imagesCollectionId);
        imagesCollectionRepository.updateImagesCaches(imagesCollectionId);
    }

    @Override
    protected void deleteInDb(String imagesCollectionId, String fileName) {
        imageRepository.deleteByImagesCollectionAndFileName(
                imagesCollectionId, fileName);
        imagesCollectionRepository.updateImagesCaches(imagesCollectionId);
    }

    public void importFolderForConversion(String imagesCollectionId, File folder)
            throws IOException {
        addAllInDbFromFolder(imagesCollectionId, folder.getPath());
        File imagesFolder = getTempFilesFolder(imagesCollectionId);
        imagesFolder.getParentFile().mkdirs();
        Files.move(folder.toPath(), imagesFolder.toPath());
    }

    public String getOmeXml(String imagesCollectionId, String fileName)
            throws IOException {
        File file = getFile(imagesCollectionId, fileName);

        OMEXMLService service;
        try {
            service = new ServiceFactory().getInstance(OMEXMLService.class);
        } catch (DependencyException ex) {
            throw new IOException("Cannot find OME XML service.", ex);
        }

        try (ImageReader imageReader = new ImageReader()) {
            IFormatReader reader = imageReader.getReader(file.getPath());
            reader.setOriginalMetadataPopulated(false);
            OMEXMLMetadata sourceMetadata = service.createOMEXMLMetadata();
            reader.setMetadataStore(sourceMetadata);
            reader.setId(file.getPath());
            return XMLTools.indentXML(
                    service.getOMEXML(sourceMetadata), 3, true);
        } catch (FormatException ex) {
            throw new IOException("Unsupported format", ex);
        } catch (ServiceException ex) {
            throw new IOException("Error generating OME XML file.", ex);
        }
    }
}
