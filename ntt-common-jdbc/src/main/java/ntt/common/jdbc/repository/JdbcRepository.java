/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description : Create common Jdbc Repository
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

package ntt.common.jdbc.repository;

import ntt.common.jdbc.model.FieldInfo;
import ntt.common.jdbc.utility.ClassUtility;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.*;

@Getter
@Setter
public class JdbcRepository implements IJdbcRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public static final String SQL_COMMA = ", ";

    private Logger logger = LoggerFactory.getLogger(JdbcRepository.class);

    private JdbcTemplate jdbcTemplate;

    public JdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private KeyHolder insertForKeyHolder(String sql, Object[] params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int updateCount = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if(params != null) {
                for (int i = 1; i <= params.length; i++) {
                    preparedStatement.setObject(i, params[i - 1]);
                }
            }
            return preparedStatement;
        }, keyHolder);
        if(updateCount < 1){
            return null;
        }
        return keyHolder;
    }

    @Override
    public Map<String, Object> insertForKeys(String sql, Object... params) {
        KeyHolder keyHolder = this.insertForKeyHolder(sql, params);
        return keyHolder.getKeys();
    }

    @Override
    public String insert(String sql, Object... params) {
        KeyHolder keyHolder = insertForKeyHolder(sql, params);
        if(keyHolder == null) return null;
        return keyHolder.getKeys() != null && keyHolder.getKeys().get("id") != null?
                keyHolder.getKeys().get("id").toString(): null;
    }

    @Override
    @Transactional
    public String dynamicInsert(String sql, List<Object> params) {
        Object[] dynamicParams = params != null? params.toArray(): null;
        return this.insert(sql, dynamicParams);
    }

    @Override
    public int update(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    @Override
    @Transactional
    public int dynamicUpdate(String sql, List<Object> params) {
        return params == null? this.update(sql): this.update(sql, params.toArray());
    }

    @Override
    public int delete(String sql, Object... params) {
        return jdbcTemplate.update(sql, params); //Delete using update function
    }

    @Override
    @Transactional
    public int dynamicDelete(String sql, List<Object> params) {
        return params == null? this.delete(sql): this.delete(sql, params.toArray());
    }

    @Override
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... params) {
        return jdbcTemplate.query(sql, rowMapper, params);
    }

    @Override
    public <T> List<T> queryForList(Class<T> clazz, String sql,  Object... params) {
        boolean isPrimitiveWrapper = ClassUtility.isPrimitiveWrapper(clazz);
        if(isPrimitiveWrapper) {
            return jdbcTemplate.queryForList(sql, clazz, params);
        } else{
            List<T> collection = new ArrayList<>();
            List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql, params);
            if(listMap == null) return collection;
            HashMap<String, FieldInfo> hashClassField = ClassUtility.getAllFields(null, clazz);
            for(Map<String, Object> map: listMap) {
                T model = ClassUtility.castMapToModel(map, clazz, hashClassField);
                collection.add(model);
            }
            return collection;
        }
    }

    @Override
    public <T> List<T> queryForList(Class<T> clazz, String sql, Sort sort, Object... params) {
        if(sort == null){
            return this.queryForList(clazz, sql, params);
        }
        List<T> collection = new ArrayList<>();
        String sortingClause = this.sortingClauseIfRequired(sort);
        String pagingSql = sql + sortingClause;
        List<Map<String, Object>> pagingMap = jdbcTemplate.queryForList(pagingSql, params);
        if(pagingMap == null || pagingMap.size() == 0) {
            return  collection;
        }
        HashMap<String, FieldInfo> hashClassField = ClassUtility.getAllFields(null, clazz);
        for(Map<String, Object> map: pagingMap) {
            T model = ClassUtility.castMapToModel(map, clazz, hashClassField);
            collection.add(model);
        }
        return collection;
    }

    @Override
    public <T> Page<T> queryForPaging(Class<T> clazz, String sql, Pageable pageable, Object... params) {
        if(pageable == null){
            List<T> list = this.queryForList(clazz, sql, params);
            return new PageImpl<T>(list);
        }
        List<T> collection = new ArrayList<>();

        //Calculate total elements
        String countingSql = this.countingClause(sql);
        Long totalElements = jdbcTemplate.queryForObject(countingSql, Long.class, params);
        if(totalElements == null || totalElements.longValue() == 0) {
            return new PageImpl<>(collection, pageable, collection.size());
        }

        String sortingClause = this.sortingClauseIfRequired(pageable.getSort());
        String limitClause = this.limitClause(pageable);
        String pagingSql = sql + sortingClause + limitClause;
        List<Map<String, Object>> pagingMap = jdbcTemplate.queryForList(pagingSql, params);
        if(pagingMap == null || pagingMap.size() == 0) {
            new PageImpl<>(collection, pageable, collection.size());
        }
        HashMap<String, FieldInfo> hashClassField = ClassUtility.getAllFields(null, clazz);
        for(Map<String, Object> map: pagingMap) {
            T model = ClassUtility.castMapToModel(map, clazz, hashClassField);
            collection.add(model);
        }
        return new PageImpl<>(collection, pageable, totalElements.longValue());
    }

    @Override
    public List<Map<String, Object>> queryForListMap(String sql, Object... params) {
        return jdbcTemplate.queryForList(sql, params);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
        T result = this.jdbcTemplate.queryForObject(sql, rowMapper, params);
        return result;
    }

    @Override
    public <T> T queryForObject(Class<T> clazz, String sql, Object... params) {
        boolean isPrimitiveWrapper = ClassUtility.isPrimitiveWrapper(clazz);
        if(isPrimitiveWrapper) {
            T result = this.jdbcTemplate.queryForObject(sql, clazz, params);
            return result;
        } else {
            List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql, params);
            if(listMap == null || listMap.size() == 0) return null;
            HashMap<String, FieldInfo> hashClassField = ClassUtility.getAllFields(null, clazz);
            Map<String, Object> firstMap = listMap.get(0);
            T model = ClassUtility.castMapToModel(firstMap, clazz, hashClassField);
            return model;
        }
    }

    @Override
    public JsonNode queryForJsonNode(String sql, Object... params) {
        Map<String, Object> map = this.queryForMap(sql, params);
        Map<String, String> jsonKeys = ClassUtility.buildJsonKeys(map);
        JsonNode result = ClassUtility.castMapToJsonNode(map, jsonKeys);
        return result;
    }

    @Override
    public List<JsonNode> queryForListJsonNode(String sql, Object... params) {
        List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql, params);
        List<JsonNode>  result = new ArrayList<>();
        if(listMap == null || listMap.size() == 0) return result;
        Map<String, String> jsonKeys = null;
        for (Map<String, Object> map: listMap){
            if(jsonKeys == null){
                jsonKeys = ClassUtility.buildJsonKeys(map);
            }
            JsonNode jsonNode = ClassUtility.castMapToJsonNode(map, jsonKeys);
            result.add(jsonNode);
        }
        return result;
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... params) {
        List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql, params);
        if(listMap == null || listMap.size() == 0) return null;
        return listMap.get(0);
    }

    @Override
    public String limitClause(Pageable page) {
        final int offset = page.getPageNumber() * page.getPageSize();
        return " LIMIT " + page.getPageSize() + " OFFSET " + offset;
    }

    @Override
    public String countingClause(String query) {
        return String.format("Select count(0) from (%s) alias", query);
    }

    @Override
    public String sortingClauseIfRequired(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return "";
        }
        StringBuilder orderByClause = new StringBuilder();
        orderByClause.append(" ORDER BY ");
        for(Iterator<Sort.Order> iterator = sort.iterator(); iterator.hasNext();) {
            final Sort.Order order = iterator.next();
            orderByClause.
                    append(order.getProperty()).
                    append(" ").
                    append(order.getDirection().toString());
            if (iterator.hasNext()) {
                orderByClause.append(SQL_COMMA);
            }
        }
        return orderByClause.toString();
    }



}
