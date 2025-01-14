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
package gov.nist.itl.ssd.wipp.backend.data.imagescollection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import gov.nist.itl.ssd.wipp.backend.core.model.data.Data;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.IdExposed;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.ManualRef;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

//import gov.nist.itl.ssd.fes.job.Job;

/**
 *
 * @author Antoine Vandecreme <antoine.vandecreme at nist.gov>
 * @author Mohamed Ouladi <mohamed.ouladi at nist.gov>
 * @author Mylene Simon <mylene.simon at nist.gov>
 */
@IdExposed
@Document
public class ImagesCollection extends Data {

    @Id
    private String id;

    private String name;

    private String owner;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date creationDate;

    @Indexed(unique = true, sparse = true)
    @ManualRef(Job.class)
    private String sourceJob;
    
    private String sourceCatalog;

    private String sourceBackendImport;
    
    private ImagesCollectionImportMethod importMethod;

    private ImagesCollectionFormat format;

    private boolean locked;

    private boolean publiclyShared;

    private String pattern;

    private String notes;

    @JsonProperty(access = Access.READ_ONLY)
    private int numberOfImages;

    @JsonProperty(access = Access.READ_ONLY)
    private long imagesTotalSize;

    @JsonProperty(access = Access.READ_ONLY)
    private int numberImportingImages;

    @JsonProperty(access = Access.READ_ONLY)
    private int numberOfImportErrors;

    @JsonProperty(access = Access.READ_ONLY)
    private int numberOfMetadataFiles;

    @JsonProperty(access = Access.READ_ONLY)
    private long metadataFilesTotalSize;
    public ImagesCollection() {
    }

    public ImagesCollection(String name) {
        this(name, false, ImagesCollectionImportMethod.UPLOADED, ImagesCollectionFormat.OMETIFF);
    }

    public ImagesCollection(String name, ImagesCollectionFormat format) {
        this(name, false, ImagesCollectionImportMethod.UPLOADED, format);
    }

    public ImagesCollection(String name, boolean locked, ImagesCollectionImportMethod importMethod, ImagesCollectionFormat format){
        this.name = name;
        this.locked = locked;
        this.creationDate = new Date();	
        this.importMethod = importMethod;
        this.format = format;
    }

    public ImagesCollection(Job job, String outputName) {
        this(job, outputName, ImagesCollectionFormat.OMETIFF);
    }
    public ImagesCollection(Job job, String outputName, ImagesCollectionFormat format) {
        this.name = job.getName() + "-" + outputName;
        this.sourceJob = job.getId();
        this.locked = true;
        this.creationDate = new Date();
        this.importMethod = ImagesCollectionImportMethod.JOB;
        this.format = format;
    }

    public ImagesCollection(String name, String sourceCatalog){
        this(name, sourceCatalog, ImagesCollectionFormat.OMETIFF);
    }
    public ImagesCollection(String name, String sourceCatalog, ImagesCollectionFormat format){
        this.name = name;
        this.locked = true;
        this.creationDate = new Date();	
        this.sourceCatalog = sourceCatalog;
        this.importMethod = ImagesCollectionImportMethod.CATALOG;
        this.format = format;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSourceJob() {
        return sourceJob;
    }

    public String getSourceBackendImport() {
        return sourceBackendImport;
    }

    public void setSourceBackendImport(String sourceBackendImport) {
        this.sourceBackendImport = sourceBackendImport;
    }

    public boolean isLocked() {
        return locked;
    }

	public String getPattern() {
        return pattern;

	}

	public String getNotes() {
        return notes;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }

	public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public long getImagesTotalSize() {
        return imagesTotalSize;
    }

    public int getNumberImportingImages() {
        return numberImportingImages;
    }

    public int getNumberOfImportErrors() {
        return numberOfImportErrors;
    }

    public int getNumberOfMetadataFiles() {
        return numberOfMetadataFiles;
    }

    public long getMetadataFilesTotalSize() {
        return metadataFilesTotalSize;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { 
    	this.owner = owner; 
    }

    public boolean isPubliclyShared() {
        return publiclyShared;
    }

    public void setPubliclyShared(boolean publiclyShared) { 
    	this.publiclyShared = publiclyShared; 
    }

	public String getSourceCatalog() {
        return sourceCatalog;

	}

	public ImagesCollectionImportMethod getImportMethod() {
        return importMethod;
        
	}

	public void setImportMethod(ImagesCollectionImportMethod importMethod) {
		this.importMethod = importMethod;
	}

    public ImagesCollectionFormat getFormat() {
        return format;
    }

    public void setFormat(ImagesCollectionFormat format) {
        this.format = format;
    }
	
    public enum ImagesCollectionImportMethod {UPLOADED, JOB, CATALOG, BACKEND_IMPORT}

    public enum ImagesCollectionFormat {OMETIFF, OMEZARR, RAW}

}
