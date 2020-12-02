package vn.vnpt.stc.enterprise.mongo.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import vn.vnpt.stc.enterpise.commons.constants.Constants;
import vn.vnpt.stc.enterpise.commons.constants.MethodConstants;
import vn.vnpt.stc.enterpise.commons.constants.QueueConstants;
import vn.vnpt.stc.enterpise.commons.entities.dto.PageInfo;
import vn.vnpt.stc.enterpise.commons.entities.dto.SearchInfo;
import vn.vnpt.stc.enterpise.commons.entities.dto.StringIdInfo;
import vn.vnpt.stc.enterpise.commons.entities.generic.MgIdEntity;
import vn.vnpt.stc.enterpise.commons.errors.ErrorInfo;
import vn.vnpt.stc.enterpise.commons.errors.ErrorKey;
import vn.vnpt.stc.enterpise.commons.event.Event;
import vn.vnpt.stc.enterpise.commons.exceptions.RemoveSystemEntityException;
import vn.vnpt.stc.enterpise.commons.rsql.mongo.ComparisonToCriteriaConverter;
import vn.vnpt.stc.enterpise.commons.rsql.mongo.RsqlMongoAdapter;
import vn.vnpt.stc.enterpise.commons.utils.ObjectMapperUtil;
import vn.vnpt.stc.enterprise.utils.SecurityUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyvv
 * Date: 06/03/2020
 * Time: 11:13 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/

@Transactional
@SuppressWarnings({"Duplicates", "unchecked"})
public class CrudService<T extends IdEntity, ID>{
    private static Logger logger = LoggerFactory.getLogger(CrudService.class);
    protected CustomRepository<T,ID> repository;
    private final Class<T> typeEntityClass;

    public CrudService(Class<T> typeEntityClass) {
        this.typeEntityClass = typeEntityClass;
    }

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    public Event process(Event event){
        switch (event.method){
            case MethodConstants.CREATE:
                return processCreate(event);
            case MethodConstants.SEARCH:
                return processSearch(event);
            case MethodConstants.UPDATE:
                return processUpdate(event);
            case MethodConstants.DELETE:
                return processDelete(event);
            case MethodConstants.BATCH_DELETE:
                return processBatchDelete(event);
            case MethodConstants.ACTIVE:
                return processActive(event);
            case MethodConstants.DE_ACTIVE:
                return processDeActive(event);
            case MethodConstants.GET_ONE:
                return processGetOne(event);
            default:
                event.errorCode = QueueConstants.ResultStatus.ERROR;
                return event;
        }
    }

    /**
     * functions event processCreate, processUpdate...
     **/
    protected Event processCreate(Event event){
        T entity = ObjectMapperUtil.objectMapper(event.payload, typeEntityClass);
        event.payload = ObjectMapperUtil.toJsonString(create(entity));
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    protected Event processGetOne(Event event){
        StringIdInfo idInfo = ObjectMapperUtil.objectMapper(event.payload, StringIdInfo.class);
        event.payload = ObjectMapperUtil.toJsonString(get((ID) idInfo.getId()));
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    protected Event processSearch(Event event){
        SearchInfo searchInfo = ObjectMapperUtil.objectMapper(event.payload, SearchInfo.class);
        String orders = searchInfo.getOrders();
        Pageable pageable;
        if(orders == null || "".equals(orders)){
            pageable = PageRequest.of(searchInfo.getPageNumber(), searchInfo.getPageSize());
        }else {
            pageable = PageRequest.of
                    (searchInfo.getPageNumber(), searchInfo.getPageSize(), vn.vnpt.stc.enterpise.commons.utils.StringUtils.toSort(orders));
        }

        Page<T> page = search(searchInfo.getQuery(), pageable);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalCount(page.getTotalElements());
        pageInfo.setData(ObjectMapperUtil.toJsonString(page.getContent()));

        event.payload = ObjectMapperUtil.toJsonString(pageInfo);
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    public String addMultipleTenantQuery(String query){
        //do something to custom search query
        return query;
    }

    protected Event processUpdate(Event event){
        T entity = ObjectMapperUtil.objectMapper(event.payload, typeEntityClass);
        event.payload = ObjectMapperUtil.toJsonString(update((ID) entity.getId(), entity));
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    protected Event processDelete(Event event){
        StringIdInfo idInfo = ObjectMapperUtil.objectMapper(event.payload, StringIdInfo.class);
        T entity = get((ID) idInfo.getId());
        //if entity created by system, can not remove
        if(Constants.SYSTEM.equalsIgnoreCase(entity.getCreatedBy())){
            event.errorCode = QueueConstants.ResultStatus.ERROR;
            ErrorInfo errorInfo= new ErrorInfo();
            errorInfo.setErrorKey(ErrorKey.CommonErrorKey.REMOVE_SYSTEM_ENTITY);
            event.payload = ObjectMapperUtil.toJsonString(errorInfo);
            return event;
        }
        softDelete((ID) idInfo.getId());
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    protected Event processBatchDelete(Event event) {
        List<String> ids = ObjectMapperUtil.listMapper(event.payload, String.class);
        List<String> fail = new ArrayList<>();
        for(String id : ids){
            try{
                softDelete((ID) id);
            }catch (Exception ex){
                logger.error(ex.getMessage(), ex);
                fail.add(id);
            }
        }
        event.payload = ObjectMapperUtil.toJsonString(fail);
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    protected Event processActive(Event event){
        StringIdInfo idInfo = ObjectMapperUtil.objectMapper(event.payload, StringIdInfo.class);
        activate((ID) idInfo.getId());
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    protected Event processDeActive(Event event){
        StringIdInfo idInfo = ObjectMapperUtil.objectMapper(event.payload, StringIdInfo.class);
        deactivate((ID) idInfo.getId());
        event.errorCode = QueueConstants.ResultStatus.SUCCESS;
        return event;
    }

    /**
     * function create, update, ...
     **/

    public T create(T entity){
        beforeCreate(entity);
        repository.save(entity);
        afterCreate(entity);
        return entity;
    }

    public T get(ID id){
        return repository.findById(id).orElse(null);
    }

    public Page<T> search(String query, Pageable pageable){
        if(pageable == null){
            pageable = PageRequest.of(0, 20);
        }
        query = addMultipleTenantQuery(query);
        ComparisonToCriteriaConverter converter = new ComparisonToCriteriaConverter(new DefaultConversionService(), mongoMappingContext);
        RsqlMongoAdapter adapter = new RsqlMongoAdapter(converter);
        Query queryMongo;
        if(query == null || query.isEmpty()){
            queryMongo = new Query().with(pageable);
        }else{
            queryMongo = Query.query(adapter.getCriteria(query, typeEntityClass)).with(pageable);
        }

        long total = mongoOperations.count(queryMongo, typeEntityClass);
        List<T> list = mongoOperations.find(queryMongo, typeEntityClass);
        return new PageImpl<>(list, pageable, total);
    }

    public T update(ID id, T entity){
        beforeUpdate(entity);
        T old = get(id);
        if(entity.getCreated() == null) entity.setCreated(old.getCreated());
        if(entity.getCreatedBy() == null) entity.setCreatedBy(old.getCreatedBy());
        if(old == null) {
            throw new EntityNotFoundException("No entity with id " + id);
        }
        entity = repository.save(entity);
        afterUpdate(old,entity);
        return entity;
    }

    public void activate(ID id) {
        T t = get(id);
        if(t != null) {
            t.setActive(Constants.EntityStatus.ACTIVE);
            update(id, t);
        }
    }

    public void deactivate(ID id) {
        T t = get(id);
        if(t != null) {
            t.setActive(Constants.EntityStatus.IN_ACTIVE);
            update(id, t);
        }
    }

    public void delete(ID id){
        T entity = get(id);
        if(entity.getCreatedBy()!= null && entity.getCreatedBy().equals(Constants.SYSTEM)){
            throw new RemoveSystemEntityException();
        }
        beforeDelete(entity);
        repository.delete(entity);
        afterDelete(entity);
    }

    public void softDelete(ID id){
        T entity = get(id);
        if(entity.getCreatedBy()!= null && entity.getCreatedBy().equals(Constants.SYSTEM)){
            throw new RemoveSystemEntityException();
        }
        beforeSoftDelete(entity);
        repository.save(entity);
        afterSoftDelete(entity);
    }

    /**
     * functions before, after
     **/
    protected void beforeCreate(T entity) {
        entity.setCreated(System.currentTimeMillis());
        if(entity.getCreatedBy() == null) {
            String currentUsername = SecurityUtils.getCurrentUserLogin();
            entity.setCreatedBy(currentUsername);
        }
        if(entity.getActive() == null) {
            entity.setActive(Constants.EntityStatus.ACTIVE);
        }
    }

    protected void afterCreate(T entity) {
        //do something after create
    }

    protected void beforeUpdate(T entity) {
        entity.setUpdated(System.currentTimeMillis());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserLogin());
        if(entity.getActive() == null) {
            entity.setActive(Constants.EntityStatus.ACTIVE);
        }
    }

    protected void afterUpdate(T old, T updated) {
        //do something after update
    }

    protected void beforeDelete(T entity) {
        //do something before delete
    }

    protected void afterDelete(T entity) {
        //do something after delete
    }

    protected void beforeSoftDelete(T entity){
        entity.setActive(Constants.EntityStatus.DELETED);
        entity.setUpdated(System.currentTimeMillis());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserLogin());
    }

    protected void afterSoftDelete(T entity){
        //do something after soft delete
    }
}
