/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description : Create common Sql Generator
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

package ntt.common.jdbc.service;

import ntt.common.jdbc.model.KeyValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.Nullable;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

public interface IPostgreSqlGenerator {
    void setSchema(String schema);

    String createCountQuery(String tableName) throws Exception;

    String createCountDetailQuery(String tableName, String fkName, String fkValue) throws Exception;

    String createSelectQuery(String tableName, int pageIndex, int pageSize,
                             @Nullable String order,
                             @Nullable String filter,
                             @Nullable String fkName,
                             @Nullable String fkValue) throws Exception;

    KeyValue<String, List<Object>> createDeleteQuery(String tableName, JsonNode row) throws Exception;

    KeyValue<String, List<Object>> createInsertQuery(String tableName, JsonNode resource, @Nullable Hashtable fxResult) throws Exception;

    KeyValue<String, List<Object>> createUpdateQuery(String tableName, JsonNode row, JsonNode originalRow, @Nullable Hashtable fxResult) throws Exception;

    List<String> getPrimaryKeys(String schema, String tableName) throws SQLException;

    Hashtable getTableStructure(String tableName);

    Boolean checkTableHasIdentity(String tableName);
}
