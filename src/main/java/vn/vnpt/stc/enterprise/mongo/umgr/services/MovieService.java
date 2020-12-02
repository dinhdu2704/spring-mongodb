package vn.vnpt.stc.enterprise.mongo.umgr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpt.stc.enterprise.mongo.generic.CrudService;
import vn.vnpt.stc.enterprise.mongo.umgr.models.Movie;
import vn.vnpt.stc.enterprise.mongo.umgr.repositories.MovieRepository;

@Service
public class MovieService extends CrudService<Movie, String> {
    private MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository){
        super(Movie.class);
        this.repository = this.movieRepository = movieRepository;
    }
}
