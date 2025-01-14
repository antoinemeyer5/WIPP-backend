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
package gov.nist.itl.ssd.wipp.backend.argo.workflows.spec;

/**
 * @author Mylene Simon <mylene.simon at nist.gov>
 *
 */
public class ArgoSecurityContext {

	private final int runAsUser = Integer.parseInt(System.getenv().getOrDefault("RUN_WORKFLOWS_AS_USER", "1000"));
	private final int runAsGroup = Integer.parseInt(System.getenv().getOrDefault("RUN_WORKFLOWS_AS_GROUP", "1000"));
	
	public int getRunAsUser() {
		return runAsUser;
	}

	public int getRunAsGroup() {
		return runAsGroup;
	}
}
