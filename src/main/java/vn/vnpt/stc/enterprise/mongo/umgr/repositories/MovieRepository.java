package vn.vnpt.stc.enterprise.mongo.umgr.repositories;

import org.springframework.stereotype.Repository;
import vn.vnpt.stc.enterprise.mongo.generic.CustomRepository;
import vn.vnpt.stc.enterprise.mongo.umgr.models.Movie;

@Repository
public interface MovieRepository extends CustomRepository<Movie, String> {
}
