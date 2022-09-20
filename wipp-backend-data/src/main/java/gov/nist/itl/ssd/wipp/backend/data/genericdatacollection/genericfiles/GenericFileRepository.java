package gov.nist.itl.ssd.wipp.backend.data.genericdatacollection.genericfiles;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
*
* @author Mohamed Ouladi <mohamed.ouladi at labshare.org>
*/
@RepositoryRestResource(exported = false)
public interface GenericFileRepository extends MongoRepository<GenericFile, String>, GenericFileRepositoryCustom {
	
    List<GenericFile> findByGenericDataCollection(String genericDataCollection);

    Page<GenericFile> findByGenericDataCollection(String genericDataCollection, Pageable p);

    List<GenericFile> findByGenericDataCollectionAndFileNameRegex(String genericDataCollection, String fileName);

    Page<GenericFile> findByGenericDataCollectionAndFileNameRegex(String genericDataCollection, String fileName, Pageable p);

    List<GenericFile> findByImporting(boolean importing);

}
