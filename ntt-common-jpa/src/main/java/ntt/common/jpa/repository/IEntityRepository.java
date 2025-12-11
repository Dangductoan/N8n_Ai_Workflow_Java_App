package ntt.common.jpa.repository;

import ntt.common.jpa.entity.AbstractBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface IEntityRepository {

    <T> long count(Class<T> clazz);

    <T> long count(Class<T> clazz, @Nullable Specification spec);

    <T> T findById(Class<T> clazz, Object id);

    <T> Optional<T> findOne(Class<T> clazz, @Nullable Specification<T> spec);

    <T> List<T> findAll(Class<T> clazz);

    <T> List<T> findAll(Class<T> clazz, Sort sort);

    <T> Page<T> findAll(Class<T> clazz, Pageable pageable);

    <T> List<T> findAll(Class<T> clazz, Specification spec);

    <T> List<T> findAll(Class<T> clazz, Specification spec, Sort sort);

    <T> Page<T> findAll(Class<T> clazz, Specification spec, Pageable pageable);

    <T> List<T> findAllByParentId(Class<?> parentClazz, Object parentId, Class<T> clazz);

    <T> T save(T entity);

    <T> T saveAndFlush(T entity);

    <T> List<T> saveAll(Iterable<T> entities);

    void flush();

    <T> T create(final T entity);

    <T> T update(final T entity);

    <T extends AbstractBaseEntity> T softDelete(final T entity);

    <T> void delete(final T entity);

    <T> void deleteAll(Iterable<T> entities);

    <T> void deleteById(Class<T> clazz, Object entityId);

    <T> void deleteById(Class<T> clazz, Object id, Class<?>... childrenClass);

}
