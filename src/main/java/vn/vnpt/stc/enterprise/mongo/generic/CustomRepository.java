package vn.vnpt.stc.enterprise.mongo.generic;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by huyvv
 * Date: 06/03/2020
 * Time: 11:01 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
@NoRepositoryBean
public interface CustomRepository<T, ID> extends MongoRepository<T, ID> {

}
