package ntt.common.jpa.repository;

import ntt.common.jpa.entity.AbstractBaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class EntityRepository implements IEntityRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public <T> long count(Class<T> clazz) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        return jpaRepository.count();
    }

    @Override
    public <T> long count(Class<T> clazz, Specification spec) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        return jpaRepository.count(spec);
    }

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        T item = entityManager.find(clazz, id);
        return item;
    }

    @Override
    public <T> Optional<T> findOne(Class<T> clazz, Specification<T> spec) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        List<T> result = jpaRepository.findAll(spec); //FindOne cause by exception
        if (result == null || result.isEmpty()) {
            return Optional.empty();
        }
        return result.stream().findFirst();
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        List<T> result = jpaRepository.findAll();
        return result;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz, Sort sort) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        List<T> result = jpaRepository.findAll(sort);
        return result;
    }

    @Override
    public <T> Page<T> findAll(Class<T> clazz, Pageable pageable) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        Page<T> result = jpaRepository.findAll(pageable);
        return result;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz, Specification spec) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        List<T> result = jpaRepository.findAll(spec);
        return result;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz, Specification spec, Sort sort) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        List<T> result = jpaRepository.findAll(spec, sort);
        return result;
    }

    @Override
    public <T> Page<T> findAll(Class<T> clazz, Specification spec, Pageable pageable) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        Page<T> result = jpaRepository.findAll(spec, pageable);
        return result;
    }

    private <T> String mapTableName(Class<T> clazz) {
        Assert.notNull(clazz, "The given clazz must not be null!");
        String className = clazz.getSimpleName();
        String fileName = className.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        return fileName;
    }

    private Query queryAllChildrenByParentId(Object parentId, Class<?> parentClazz, Class<?> clazz) {
        Assert.notNull(parentId, "The given parentId must not be null!");
        Assert.notNull(parentClazz, "The given parentClazz must not be null!");
        Assert.notNull(clazz, "The given clazz must not be null!");

        String tableName = mapTableName(clazz);
        String fkName = mapTableName(parentClazz) + "_id";
        String query = String.format("select * from %1$s x where x.%2$s = :%2$s", tableName, fkName);
        Query queryResult = entityManager.createNativeQuery(query, clazz);
        queryResult.setParameter(fkName, parentId);
        return queryResult;
    }

    @Override
    public <T> List<T> findAllByParentId(Class<?> parentClazz, Object parentId, Class<T> clazz) {
        Query queryResult = this.queryAllChildrenByParentId(parentId, parentClazz, clazz);
        if (queryResult == null) {
            return null;
        }
        return queryResult.getResultList();
    }

    @Override
    @Transactional
    public <T> T save(T entity) {
        if(entity == null) return null;
        Class<T> clazz = (Class<T>) entity.getClass();
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(clazz, entityManager);
        if (entityInformation.isNew(entity)) {
            this.entityManager.persist(entity);
            return entity;
        } else {
            return this.entityManager.merge(entity);
        }
    }

    @Override
    public <T> T saveAndFlush(T entity) {
        if(entity == null) return null;
        Class<T> clazz = (Class<T>) entity.getClass();
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        return jpaRepository.saveAndFlush(entity);

    }

    @Override
    public <T> List<T> saveAll(Iterable<T> entities) {
        if(entities == null) return null;
        T entity = entities.iterator().next();
        Class<T> clazz = (Class<T>) entity.getClass();
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        return jpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void flush() {
        this.entityManager.flush();
    }

    @Override
    @Transactional
    public <T> T create(final T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public <T> T update(final T entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public <T extends AbstractBaseEntity> T softDelete(T entity) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        entity.setDeletedAt(timestamp);
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public <T> void delete(final T entity) {
        entityManager.remove(entity);
    }

    @Override
    public <T> void deleteAll(Iterable<T> entities) {
        if(entities == null) return;
        T entity = entities.iterator().next();
        Class<T> clazz = (Class<T>) entity.getClass();
        var jpaRepository = new SimpleJpaRepository<T, Serializable>(clazz, entityManager);
        jpaRepository.deleteAll(entities);
    }

    @Override
    public <T> void deleteById(Class<T> clazz, Object entityId) {
        if(clazz == null || entityId == null) return;
        T entity = this.findById(clazz, entityId);
        if (entity != null) {
            this.delete(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public <T> void deleteById(Class<T> clazz, Object id, Class<?>... childrenClass) {
        if(clazz == null || id == null) return;
        T master = entityManager.find(clazz, id);
        if (master == null) return;
        if (childrenClass != null) {
            for (Class<?> childClazz : childrenClass) {
                List<?> children = this.findAllByParentId(clazz, id, childClazz);
                if (children == null) continue;
                for (var item : children) {
                    entityManager.remove(item);
                }
            }
        }
        entityManager.remove(master);
    }

}
