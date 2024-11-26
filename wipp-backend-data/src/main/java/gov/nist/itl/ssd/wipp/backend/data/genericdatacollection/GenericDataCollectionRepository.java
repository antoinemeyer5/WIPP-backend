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
package gov.nist.itl.ssd.wipp.backend.data.genericdatacollection;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import gov.nist.itl.ssd.wipp.backend.core.model.auth.PrincipalFilteredRepository;
import org.springframework.data.rest.core.annotation.RestResource;

/**
*
* @author Mohamed Ouladi <mohamed.ouladi at labshare.org>
*/
@Tag(name="GenericDataCollection Entity")
@RepositoryRestResource
public interface GenericDataCollectionRepository extends PrincipalFilteredRepository<GenericDataCollection, String>, GenericDataCollectionRepositoryCustom{
	
	/*
	 * Filter collection resources access by object name depending on user
	 */
	@Query(" { '$and' : ["
			+ "{'$or':["
			+ "{'owner': ?#{ hasRole('admin') ? {$exists:true} : (hasRole('ANONYMOUS') ? '':authentication.name)}},"
			+ "{'publiclyShared':true}"
			+ "]} , "
			+ "{'name' : {$eq : ?0}}"
			+ "]}")
    Page<GenericDataCollection> findByName(@Param("name") String name, Pageable p);

	// not exported
	@RestResource(exported = false)
	long countByName(@Param("name") String name);

}
