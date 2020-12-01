package vn.vnpt.stc.enterprise.mongo.generic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by huyvv
 * Date: 06/03/2020
 * Time: 10:56 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
public class IdEntity extends AbstractEntity {
    @Id
    @Field(value = "_id")
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
