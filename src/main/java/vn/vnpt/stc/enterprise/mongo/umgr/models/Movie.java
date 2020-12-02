package vn.vnpt.stc.enterprise.mongo.umgr.models;

import org.springframework.data.mongodb.core.mapping.Document;
import vn.vnpt.stc.enterprise.mongo.generic.IdEntity;

import javax.persistence.Entity;

@Entity
@Document(collection = "movies")
public class Movie extends IdEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
