package vn.vnpt.stc.enterprise.mongo.umgr.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterprise.event.EventBus;
import vn.vnpt.stc.enterprise.mongo.generic.CrudEndpoint;
import vn.vnpt.stc.enterprise.mongo.umgr.models.Movie;
import vn.vnpt.stc.enterprise.mongo.umgr.services.MovieService;

public class MovieEndpoint extends CrudEndpoint<Movie, String> {
    private static Logger logger = LoggerFactory.getLogger(MovieEndpoint.class);

    @Autowired
    public MovieEndpoint(MovieService movieService, EventBus eventBus){
        super(movieService, eventBus);
    }

    @Override
    public void process(Event event) {
        logger.info("#{} receive method: #{}", this.getClass().getSimpleName(), event.method);
        super.process(event);
    }
}
