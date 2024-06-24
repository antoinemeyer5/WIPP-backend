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
package gov.nist.itl.ssd.wipp.backend.data.aimodel;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import gov.nist.itl.ssd.wipp.backend.core.model.data.Data;
import gov.nist.itl.ssd.wipp.backend.core.model.job.Job;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.IdExposed;
import gov.nist.itl.ssd.wipp.backend.core.rest.annotation.ManualRef;

/**
 *
 * @author Mohamed Ouladi <mohamed.ouladi at nist.gov>
 */
@IdExposed
@Document
public class AiModel extends Data {
	@Id
	private String id;

	private String name;

	private String owner;

	private MachineLearningLibraries machineLearningLibraries; // = framework

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date creationDate;

	@Indexed(unique = true, sparse = true)
    @ManualRef(Job.class)
    private String sourceJob;

	private boolean publiclyShared;

	public AiModel(){
	}

	public AiModel(String name, MachineLearningLibraries library){
		this.name = name;
		this.creationDate = new Date();
		this.machineLearningLibraries = library;
	}

	public AiModel(Job job){
		this.name = job.getName();
		this.sourceJob = job.getId();
		this.creationDate = new Date();
	}
	
	public AiModel(Job job, String outputName, MachineLearningLibraries library){
		this.name = job.getName() + "-" + outputName;
		this.sourceJob = job.getId();
		this.creationDate = new Date();
		this.machineLearningLibraries = library;
	}

	public String getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public Date getCreationDate(){
		return creationDate;
	}

	public String getSourceJob() {
        return sourceJob;
    }

	public String getOwner() {
		return owner;
	}

	public String getMachineLearningLibrary() { return machineLearningLibraries.label; }

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isPubliclyShared() {
		return publiclyShared;
	}

	public void setPubliclyShared(boolean publiclyShared) {
		this.publiclyShared = publiclyShared;
	}
}
