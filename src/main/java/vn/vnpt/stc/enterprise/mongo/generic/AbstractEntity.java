package vn.vnpt.stc.enterprise.mongo.generic;

import javax.persistence.MappedSuperclass;

/**
 * Created by huyvv
 * Date: 07/03/2020
 * Time: 9:16 AM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
@MappedSuperclass
public class AbstractEntity {
    private Long created;
    private Long updated;
    private String createdBy;
    private String updatedBy;
    private Integer active;

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
