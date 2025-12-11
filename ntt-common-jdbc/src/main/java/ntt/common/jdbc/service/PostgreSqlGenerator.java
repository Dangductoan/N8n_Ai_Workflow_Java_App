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

import ntt.common.jdbc.model.ColumnInfo;
import ntt.common.jdbc.model.KeyValue;
import ntt.common.jdbc.repository.IJdbcRepository;
import ntt.common.utility.JacksonUtility;
import ntt.common.utility.StringUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import com.github.benmanes.caffeine.cache.Cache;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.Date;

@Setter
@Getter
@Component
public class PostgreSqlGenerator implements IPostgreSqlGenerator {
    public static final String SQL_COMMA = ", ";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String schema = "public";

    private Boolean sqlSnakeCase = true;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IJdbcRepository jdbcRepository;

    @Autowired
    private Cache<String, Object> cache;


    private String normalizeSnakeCase(String input){
        String output = this.sqlSnakeCase? StringUtility.toSnakeCase(input): input;
        return output;
    }

    @Override
    public String createCountQuery(String tbName) throws Exception {
        String tableName = StringUtility.toSnakeCase(tbName);
        return String.format("Select count(0) from %s", tableName);
    }

    @Override
    public String createCountDetailQuery(String tbName, String fkName, String fkValue) throws Exception {
        String tableName = this.normalizeSnakeCase(tbName);
        String foreignKeyName = this.normalizeSnakeCase(fkName);
        var hash = getTableStructure(tableName);
        if (!hash.containsKey(foreignKeyName)) {
            String error = String.format("Does not exits ForeignKey: %1$s in the table: %2$s", foreignKeyName, tableName);
            throw new Exception(error);
        }
        var fkColumn = (ColumnInfo) hash.get(foreignKeyName);
        var foreignKeyValue = this.formatSqlValue(fkColumn.dataType, fkValue);
        return String.format("Select count(0) from %1$s where %2$s=%3$s", tableName, fkName, foreignKeyValue);
    }

    @Override
    public String createSelectQuery(String tbName, int pageIndex, int pageSize,
                                    @Nullable String order,
                                    @Nullable String filter,
                                    @Nullable String fkName,
                                    @Nullable String fkValue) throws Exception {
        if (StringUtils.isEmpty(tbName)) {
            throw new Exception("TableName is not defined in map configuration.");
        }
        if (StringUtils.isEmpty(order)) {
            order = "asc";
        }
        String tableName = this.normalizeSnakeCase(tbName);
        String foreignKeyName = this.normalizeSnakeCase(fkName);
        int offset = pageIndex * pageSize;
        var columns = StringUtils.EMPTY;
        var builder = new StringBuilder();

        Hashtable hash = getTableStructure(tableName);
        var primaryKeyList = getPrimaryKeys(this.schema, tableName);
        var primaryKey = primaryKeyList.get(0);
        var separator = StringUtils.EMPTY;
        Set<Map.Entry<String, ColumnInfo>> entrySet = hash.entrySet();
        for (Map.Entry<String, ColumnInfo> entry : entrySet) {
            String key = entry.getKey().toString();
            builder.append(String.format("%1$s%2$s", separator, key));
            separator = ",";
        }

        columns = builder.toString();
        var query = String.format("Select %1$s from %2$s ", columns, tableName);
        if (!StringUtils.isEmpty(fkName)) //IsDetail
        {
            if (!hash.containsKey(foreignKeyName)) {
                var error = String.format("Does not exits ForeignKey: %1$s in the table: %2$s", foreignKeyName, tableName);
                throw new Exception(error);
            }
            var fkColumn = (ColumnInfo) hash.get(foreignKeyName);
            var foreignKeyValue = this.formatSqlValue(fkColumn.dataType, fkValue);
            query = String.format("%1$s where %2$s=%3$s", query, foreignKeyName, foreignKeyValue);
        }

        var pagingQuery = String.format("%1$s Order By %2$s Offset %3$s Rows Fetch Next %4$s Rows Only", query, primaryKey, offset, pageSize);
        return pagingQuery;
    }

    @Override
    public KeyValue<String, List<Object>> createDeleteQuery(String tbName, JsonNode row) throws Exception {
        if (StringUtils.isEmpty(tbName)) {
            throw new Exception("TableName is not defined in map configuration.");
        }
        KeyValue<String, List<Object>> emptyKV = new KeyValue<>(StringUtils.EMPTY, null);
        List<Object> params = new ArrayList<>();
        String tableName = this.normalizeSnakeCase(tbName);
        Hashtable hashRec = new Hashtable();
        Set<String> keys = JacksonUtility.getAllJsonNodeFieldNames(row);
        if (keys == null || keys.size() == 0) return emptyKV;
        for (String key : keys) {
            String propertyName = key.toLowerCase();
            String propertyValue = JacksonUtility.getPropertyValueAsString(row, key);
            if (!hashRec.containsKey(propertyName)) {
                hashRec.put(propertyName, propertyValue);
            }
        }

        List<String> primaryKeyList = getPrimaryKeys(this.schema, tableName);
        String whereCondition = StringUtils.EMPTY;
        for (String primaryKey : primaryKeyList) {
            String primaryValue = hashRec.get(primaryKey).toString();
            String combineOperator = StringUtils.isEmpty(whereCondition) ? "" : "And";
            whereCondition += String.format(" %1$s %2$s= ? ", combineOperator, primaryKey);
            params.add(primaryValue);
        }
        String query = String.format("DELETE FROM %1$s WHERE %2$s", tableName, whereCondition);
        return new KeyValue<>(query, params);
    }

    @Override
    public KeyValue<String, List<Object>> createInsertQuery(String tbName, JsonNode row, @Nullable Hashtable fxResult) throws Exception {
        if (StringUtils.isEmpty(tbName)) {
            throw new Exception("TableName is not defined in configuration.");
        }
        KeyValue<String, List<Object>> emptyKV = new KeyValue<>(StringUtils.EMPTY, null);
        String tableName = this.normalizeSnakeCase(tbName);
        if (row == null) return emptyKV;
        Set<String> keys = JacksonUtility.getAllJsonNodeFieldNames(row);
        if (keys == null || keys.size() == 0) return emptyKV;
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Hashtable hashProperty = new Hashtable();
        List<Object> params = new ArrayList<>();

        for (String key : keys) {
            String propertyName = this.normalizeSnakeCase(key);
            String propertyValue = JacksonUtility.getPropertyValueAsString(row, key);
            if (!hashProperty.containsKey(propertyName)) {
                hashProperty.put(propertyName, propertyValue);
            } else {
                //Override new value if saved value is empty
                String savedValue = (String)hashProperty.get(propertyName);
                if(StringUtils.isEmpty(savedValue)){
                    hashProperty.put(propertyName, propertyValue);
                }

            }
        }

        List<String> primaryKeyList = getPrimaryKeys(this.schema, tableName);
        String primaryKey = primaryKeyList.get(0);
        Hashtable hashStructure = getTableStructure(tableName);
        String separator = "";
        //Make sure hashProperty has primaryKey if user does not pass it
        if(!hashProperty.containsKey(primaryKey)){
            hashProperty.put(primaryKey, "");
        }

        Set<Map.Entry<String, ColumnInfo>> entrySetStructure = hashStructure.entrySet();
        for (Map.Entry<String, ColumnInfo> entry : entrySetStructure) {
            var entity = (ColumnInfo) entry.getValue();
            //---<<Build column collection>>---
            var columnName = entity.columnName;
            //---<<Get out the value>>---
            Object value = null;
            if (hashProperty.containsKey(columnName)) {
                String propertyValue = (String) hashProperty.get(columnName);
                if (fxResult != null && fxResult.containsKey(columnName)) {
                    value = fxResult.get(columnName);
                    value = this.formatSqlValue(entity.dataType, value);
                } else {
                    value = parseSqlValue(entity.dataType, propertyValue);
                }
                //Normal table has only one key
                if (primaryKeyList.size() == 1 && columnName.equalsIgnoreCase(primaryKey)) {
                    String keyValue = value == null ? "": value.toString();
                    //Override the temporary Id
                    if(StringUtils.isEmpty(keyValue)) {
                        value = UUID.randomUUID().toString();
                    } else if (keyValue.startsWith("_")){
                        value = keyValue.substring(1);
                    }
                }
            }

            if (value == null || StringUtils.isEmpty(value.toString())) {
                continue;
            }
            columns.append(separator);
            columns.append(columnName);

            values.append(separator);
            values.append("?");

            params.add(value);
            separator = ",";
        }
        String sqlColumns = columns.toString();
        String sqlValues = values.toString();

        String query = String.format("INSERT INTO %1$s (%2$s) VALUES (%3$s)", tableName, sqlColumns, sqlValues);
        return new KeyValue<>(query, params);
    }

    @Override
    public KeyValue<String, List<Object>> createUpdateQuery(String tbName, JsonNode row, JsonNode originalRow, @Nullable Hashtable fxResult) throws Exception {
        if (StringUtils.isEmpty(tbName)) {
            throw new Exception("TableName is not defined in map configuration.");
        }
        KeyValue<String, List<Object>> emptyKV = new KeyValue<>(StringUtils.EMPTY, null);
        String tableName = this.normalizeSnakeCase(tbName);
        if (row == null || originalRow == null) return emptyKV;
        Set<String> keys = JacksonUtility.getAllJsonNodeFieldNames(row);
        if (keys == null || keys.size() == 0) return emptyKV;

        List<Object> params = new ArrayList<>();
        Hashtable hashField = new Hashtable();
        List<String> primaryKeyList = getPrimaryKeys(this.schema, tableName);
        String primaryKey = primaryKeyList.get(0);
        Hashtable hashTableStructure = getTableStructure(tableName);
        StringBuilder commandBuilder = new StringBuilder();
        String separator = "";
        for (String fieldName : keys) {
            String columnName = this.normalizeSnakeCase(fieldName);
            if(columnName.startsWith("_")) continue;

            String fieldValue = JacksonUtility.getPropertyValueAsString(row, fieldName);
            //Save to hashPair to use later
            if (!hashField.containsKey(columnName)) {
                hashField.put(columnName, fieldValue);
            }
            if (primaryKeyList.size() == 1 && columnName.equalsIgnoreCase(primaryKey)) {
                continue;
            }
            if (!hashTableStructure.containsKey(columnName)) continue;
            ColumnInfo entity = (ColumnInfo) hashTableStructure.get(columnName);
            Object value = null;
            if (fxResult != null && fxResult.containsKey(columnName)) {
                value = fxResult.get(columnName);
                value = this.formatSqlValue(entity.dataType, value);
            } else {
                value = this.parseSqlValue(entity.dataType, fieldValue);
            }
            String strCommand = String.format("%s = ? ", columnName);
            commandBuilder.append(separator);
            commandBuilder.append(strCommand);
            params.add(value);

            separator = ",";

        }
        String whereCondition = StringUtils.EMPTY;
        for (String pk : primaryKeyList) {
            ColumnInfo pkColumn = (ColumnInfo) hashTableStructure.get(pk);
            String primaryValue = hashField.get(pk).toString();
            String combineOperator = StringUtils.isEmpty(whereCondition) ? "" : "And";
            whereCondition += String.format(" %1$s %2$s= ? ", combineOperator, pk);
            params.add(primaryValue);
        }

        String setCommand = commandBuilder.toString();
        String query = String.format("UPDATE %1$s SET %2$s WHERE %3$s", tableName, setCommand, whereCondition);
        return new KeyValue<>(query, params);

    }


    private Object parseSqlValue(String dataType, String value) {
        if (dataType.equals("int") || dataType.equals("integer")) {
            if(StringUtils.isEmpty(value)) return null;
            Integer result = Ints.tryParse(value);
            return result == null ? 0 : result;
        } else if (dataType.equals("bit")) {
            return "1".equals(value) ? 1 : 0;
        } else if (dataType.equals("boolean")){
            if(StringUtils.isEmpty(value)) return null;
            return "true".equals(value)? true : false;
        } else if (dataType.equals("long")) {
            if(StringUtils.isEmpty(value)) return null;
            Long result = Longs.tryParse(value);
            return result == null ? 0 : result;
        } else if (dataType.equals("double")) {
            if(StringUtils.isEmpty(value)) return null;
            Double result = Doubles.tryParse(value);
            return result == null ? 0 : result;
        } else if (dataType.contains("unique") || dataType.equals("uuid")) {
            if (StringUtils.isEmpty(value)) return UUID.fromString("00000000-0000-0000-0000-000000000000");
            return UUID.fromString(value);
        } else if (dataType.equals("date")){
            try {
                TemporalAccessor ta = DateTimeFormatter.ISO_DATE_TIME.parse(value);
                Instant i = Instant.from(ta);
                Date date = Date.from(i);
                return date;
            } catch (Exception ex) {
                return null;
            }
        } else if (dataType.contains("timestamp")){ //datetime in postgres
            try{
                TemporalAccessor ta = DateTimeFormatter.ISO_DATE_TIME.parse(value);
                Instant i = Instant.from(ta);
                Date date = Date.from(i);
                return Timestamp.from(date.toInstant());
            } catch (Exception ex){
               return null;
           }
        }
        else if (dataType.contains("time")) { //may be "time without time zone"
            try {
                TemporalAccessor ta = DateTimeFormatter.ISO_DATE_TIME.parse(value);
                Instant i = Instant.from(ta);
                Date date = Date.from(i);
                return date;
            } catch (Exception ex) {
                return null;
            }
        } else {
            //To avoid SSX
            if (value != null && value.contains("<script>")) {
                value = StringEscapeUtils.escapeHtml(value);
            }
            return String.format("%s", value);
        }
    }

    private String formatSqlValue(String type, Object value) {
        if (StringUtils.isEmpty(type) || value == null) return StringUtils.EMPTY;
        String result = value.toString();
        if (type.contains("char") ||
                type.contains("guid") ||
                type.contains("text") ||
                type.contains("unique") ||
                type.contains("uuid")) {
            //Encode HTML
            if (result.contains("<script>")) {
                result = StringEscapeUtils.escapeHtml(result);
            }
            result = String.format("'%s'", result);
        }
        return result;
    }

    @Override
    public Boolean checkTableHasIdentity(String tbName) {
        String tableName = this.normalizeSnakeCase(tbName);
        String key = String.format("checkTableHasIdentity.%s", tableName);
        Object savedData = cache.getIfPresent(key);
        if(savedData == null) {
            String commandText = "select count(0) from INFORMATION_SCHEMA.table_constraints " +
                    "where constraint_type = 'PRIMARY KEY' and TABLE_NAME = ?";
            Boolean hashId = this.jdbcRepository.queryForObject(Boolean.class, commandText, tableName);
            Boolean result = hashId == null ? false : hashId.booleanValue();
            cache.put(key, result);
            return result;
        } else {
            return (Boolean) savedData;
        }
    }

    @Override
    public List<String> getPrimaryKeys(String schema, String tbName) throws SQLException {
        String tableName = this.normalizeSnakeCase(tbName);
        String key = String.format("getPrimaryKeys.%s", tableName);
        Object savedData = cache.getIfPresent(key);
        if(savedData == null) {
            DataSource dataSource = this.jdbcTemplate.getDataSource();
            try (Connection connection = dataSource.getConnection()) {
                String jdbcCatalog = connection.getCatalog();
                ResultSet resultSet = connection
                        .getMetaData()
                        .getPrimaryKeys(jdbcCatalog, schema, tableName);
                List<String> pkColumns = new ArrayList<>();
                while (resultSet.next()) {
                    pkColumns.add(resultSet.getString("COLUMN_NAME"));
                }
                cache.put(key, pkColumns);
                return pkColumns;
            }
        } else {
            return (List<String>) savedData;
        }
    }

    @Override
    public Hashtable getTableStructure(String tbName) {
        String tableName = this.normalizeSnakeCase(tbName);
        String key = String.format("getTableStructure.%s", tableName);
        Object savedData = cache.getIfPresent(key);
        if(savedData == null) {
            Hashtable hash = new Hashtable();
            String commandText = "select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE " +
                    "from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ?";
            List<ColumnInfo> list = this.jdbcRepository.queryForList(ColumnInfo.class, commandText, tableName);
            if (list == null) return hash;
            for (ColumnInfo item : list) {
                if (!hash.containsKey(item.columnName)) {
                    hash.put(item.columnName, item);
                }
            }
            cache.put(key,hash);
            return hash;
        } else{
            return (Hashtable) savedData;
        }
    }


    public Boolean isSafetySelectingQuery(String query) throws Exception {
        var queryParts = query.toLowerCase().split(" ");
        for (String element : queryParts) {
            if (StringUtils.isEmpty(element)) continue;
            if (element.contains("delete") ||
                    element.contains("update") ||
                    element.contains("insert") ||
                    element.contains("truncate")) {
                return false;
            }
        }
        return true;
    }


}
