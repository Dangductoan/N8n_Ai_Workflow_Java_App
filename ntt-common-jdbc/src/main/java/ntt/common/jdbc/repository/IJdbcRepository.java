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

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IJdbcRepository {
    Map<String, Object> insertForKeys(String sql, @Nullable Object... params) throws SQLException;

    String insert(String sql, @Nullable Object... params);

    @Transactional
    String dynamicInsert(String sql, @Nullable List<Object> params);

    int update(String sql, @Nullable Object... params);

    @Transactional
    int dynamicUpdate(String sql, @Nullable List<Object> params);

    int delete(String sql, @Nullable Object... params);

    @Transactional
    int dynamicDelete(String sql, @Nullable List<Object> params);

    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, @Nullable Object... params);

    <T> List<T> queryForList(Class<T> clazz, String sql, @Nullable Object... parameters);

    <T> List<T> queryForList(Class<T> clazz, String sql, Sort sort, @Nullable Object... params);

    <T> Page<T> queryForPaging(Class<T> clazz, String sql, Pageable pageable, @Nullable Object... params);

    List<Map<String, Object>> queryForListMap(String sql, @Nullable Object... params);

    <T> T queryForObject(String sql, RowMapper<T> rowMapper, @Nullable Object... params);

    <T> T queryForObject(Class<T> clazz, String sql, @Nullable Object... params);

    JsonNode queryForJsonNode(String sql, @Nullable Object... params);

    List<JsonNode> queryForListJsonNode(String sql, @Nullable Object... params);

    Map<String, Object> queryForMap(String sql, @Nullable Object... params);

    String limitClause(Pageable page);

    String countingClause(String query);

    String sortingClauseIfRequired(Sort sort);


}
