package lab.access.sap.util;

import lab.access.sap.JCoDataType;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Data
public class JCoRecordMapper {
    private String dateFormat = "yyyyMMdd";
    private String timeFormat = "HHmmss";
    private Map<String, String> tableNameMap;
    private Map<String, String> paramTableNameMap;
    private Map<String, Map<String, String>> tableFieldMap;
    private Map<String, String> importParameterNameMap;
    private Map<String, String> exportParameterNameMap;
    private Map<String, String> importStructureNameMap;
    private Map<String, Map<String, String>> importStructureFieldMap;
    private Map<String, String> exportStructureNameMap;
    private Map<String, Map<String, String>> exportStructureFieldMap;
    private Map<String, String> changingParameterNameMap;
    private Map<String, Map<String, String>> changingParameterFieldMap;
    private Map<String, JCoCallableValueMapper> callableValueMapper;
    private Map<String, List<String>> whenFieldNameMap;


    public Object callValue(String fieldName, Object value, int jCoDataType) {
        Object result = null;
        if (callableValueMapper == null) {
            return value;
        }
        JCoCallableValueMapper jCoCallableValueMapper = callableValueMapper.get(jCoDataType + "$" + fieldName);
        if (jCoCallableValueMapper == null) {
            return value;
        }
        result = jCoCallableValueMapper.mapValue(value);

        if (result == null) {
            return value;
        }
        return result;
    }

    public String mapTableName(String tableName) {
        try {
            return tableNameMap.get(tableName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getParamTableName(String tableName) {
        try {
            return paramTableNameMap.get(tableName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, Map<String, String>> getTableMapWhenSuccess() {
        List<String> tableNames = getTableNamesWhenSuccess();
        if (tableNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String table : tableNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getTableFieldNamesWhenSuccess(table);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getTableFieldValueWhenSuccess(table, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(table, fieldMap);
            }
            return map;

        }
        return null;
    }

    public Map<String, Map<String, String>> getTableMapWhenFailed() {
        List<String> tableNames = getTableNamesWhenFailed();
        if (tableNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String table : tableNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getTableFieldNamesWhenFailed(table);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getTableFieldValueWhenFailed(table, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(table, fieldMap);
            }
            return map;

        }
        return null;
    }

    public Map<String, Map<String, String>> getTableMapWhenDefault() {
        List<String> tableNames = getTableNamesWhenDefault();
        if (tableNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String table : tableNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getTableFieldNamesWhenDefault(table);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getTableFieldValueWhenDefault(table, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(table, fieldMap);
            }
            return map;

        }
        return null;
    }

    public List<String> getTableNamesWhenSuccess() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "T");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getTableNamesWhenFailed() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "T");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getTableNamesWhenDefault() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "T");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapTableFieldName(String tableName, String fieldName) {
        try {
            return tableFieldMap.get(tableName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getTableFieldNamesWhenSuccess(String tableName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "T$" + tableName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getTableFieldNamesWhenFailed(String tableName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "T$" + tableName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getTableFieldNamesWhenDefault(String tableName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "T$" + tableName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getTableFieldValueWhenSuccess(String tableName, String fieldName) {
        try {
            return tableFieldMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + tableName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getTableFieldValueWhenFailed(String tableName, String fieldName) {
        try {
            return tableFieldMap.get(JCoRecordMapperFactory.FAILED_PREFIX + tableName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getTableFieldValueWhenDefault(String tableName, String fieldName) {
        try {
            return tableFieldMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + tableName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapImportParameterName(String parameterName) {
        try {
            return importParameterNameMap.get(parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, String> getImportParameterMapWhenSuccess() {
        List<String> parameterNames = getImportParameterNamesWhenSuccess();
        if (parameterNames != null) {
            Map<String, String> map = new HashMap<>();
            for (String parameter : parameterNames) {
                String value = getImportParameterValueWhenSuccess(parameter);
                if (value != null) {
                    map.put(parameter, value);
                }
            }
            return map;
        }
        return null;
    }

    public Map<String, String> getImportParameterMapWhenFaield() {
        List<String> parameterNames = getImportParameterNamesWhenFailed();
        if (parameterNames != null) {
            Map<String, String> map = new HashMap<>();
            for (String parameter : parameterNames) {
                String value = getImportParameterValueWhenFailed(parameter);
                if (value != null) {
                    map.put(parameter, value);
                }
            }
            return map;
        }
        return null;
    }

    public Map<String, String> getImportParameterMapWhenDefault() {
        List<String> parameterNames = getImportParameterNamesWhenDefault();
        if (parameterNames != null) {
            Map<String, String> map = new HashMap<>();
            for (String parameter : parameterNames) {
                String value = getImportParameterValueWhenDefault(parameter);
                if (value != null) {
                    map.put(parameter, value);
                }
            }
            return map;
        }
        return null;
    }

    public List<String> getImportParameterNamesWhenSuccess() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "IP");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportParameterNamesWhenFailed() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "IP");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportParameterNamesWhenDefault() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "IP");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getImportParameterValueWhenSuccess(String parameterName) {
        try {
            return importParameterNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getImportParameterValueWhenFailed(String parameterName) {
        try {
            return importParameterNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getImportParameterValueWhenDefault(String parameterName) {
        try {
            return importParameterNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapExportParameterName(String parameterName) {
        try {
            return exportParameterNameMap.get(parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, String> getExportParameterMapWhenSuccess() {
        List<String> parameterNames = getExportParameterNamesWhenSuccess();
        if (parameterNames != null) {
            Map<String, String> map = new HashMap<>();
            for (String parameter : parameterNames) {
                String value = getExportParameterValueWhenSuccess(parameter);
                if (value != null) {
                    map.put(parameter, value);
                }
            }
            return map;
        }
        return null;
    }

    public Map<String, String> getExportParameterMapWhenFaield() {
        List<String> parameterNames = getExportParameterNamesWhenFailed();
        if (parameterNames != null) {
            Map<String, String> map = new HashMap<>();
            for (String parameter : parameterNames) {
                String value = getExportParameterValueWhenFailed(parameter);
                if (value != null) {
                    map.put(parameter, value);
                }
            }
            return map;
        }
        return null;
    }

    public Map<String, String> getExportParameterMapWhenDefault() {
        List<String> parameterNames = getExportParameterNamesWhenDefault();
        if (parameterNames != null) {
            Map<String, String> map = new HashMap<>();
            for (String parameter : parameterNames) {
                String value = getExportParameterValueWhenDefault(parameter);
                if (value != null) {
                    map.put(parameter, value);
                }
            }
            return map;
        }
        return null;
    }


    public List<String> getExportParameterNamesWhenSuccess() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "EP");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportParameterNamesWhenFailed() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "EP");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportParameterNamesWhenDefault() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "EP");
        } catch (NullPointerException ne) {
            return null;
        }
    }


    public String getExportParameterValueWhenSuccess(String parameterName) {
        try {
            return exportParameterNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getExportParameterValueWhenFailed(String parameterName) {
        try {
            return exportParameterNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getExportParameterValueWhenDefault(String parameterName) {
        try {
            return exportParameterNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapImportStructureName(String structureName) {
        try {
            return importStructureNameMap.get(structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, Map<String, String>> getImportStructureMapWhenSuccess() {
        List<String> structureNames = getImportStructureNamesWhenSuccess();
        if (structureNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String structure : structureNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getImportStructureFieldNamesWhenSuccess(structure);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getImportStructureFieldValueWhenSuccess(structure, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(structure, fieldMap);
            }
            return map;
        }
        return null;
    }

    public Map<String, Map<String, String>> getImportStructureMapWhenFailed() {
        List<String> structureNames = getImportStructureNamesWhenFailed();
        if (structureNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String structure : structureNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getImportStructureFieldNamesWhenFailed(structure);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getImportStructureFieldValueWhenFailed(structure, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(structure, fieldMap);
            }
            return map;
        }
        return null;
    }

    public Map<String, Map<String, String>> getImportStructureMapWhenDefault() {
        List<String> structureNames = getImportStructureNamesWhenDefault();
        if (structureNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String structure : structureNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getImportStructureFieldNamesWhenDefault(structure);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getImportStructureFieldValueWhenDefault(structure, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(structure, fieldMap);
            }
            return map;
        }
        return null;
    }

    public List<String> getImportStructureNamesWhenSuccess() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "IS");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportStructureNamesWhenFailed() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "IS");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportStructureNamesWhenDefault() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "IS");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapImportStructureFieldName(String structureName, String fieldName) {
        try {
            return importStructureFieldMap.get(structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportStructureFieldNamesWhenSuccess(String structureName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "IS$" + structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportStructureFieldNamesWhenFailed(String structureName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "IS$" + structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getImportStructureFieldNamesWhenDefault(String structureName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "IS$" + structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getImportStructureFieldValueWhenSuccess(String structureName, String fieldName) {
        try {
            return importStructureFieldMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getImportStructureFieldValueWhenFailed(String structureName, String fieldName) {
        try {
            return importStructureFieldMap.get(JCoRecordMapperFactory.FAILED_PREFIX + structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getImportStructureFieldValueWhenDefault(String structureName, String fieldName) {
        try {
            return importStructureFieldMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapExportStructureName(String structureName) {
        try {
            return exportStructureNameMap.get(structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, Map<String, String>> getExportStructureMapWhenSuccess() {
        List<String> structureNames = getExportStructureNamesWhenSuccess();
        if (structureNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String structure : structureNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getExportStructureFieldNamesWhenSuccess(structure);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getExportStructureFieldValueWhenSuccess(structure, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(structure, fieldMap);
            }
            return map;
        }
        return null;
    }

    public Map<String, Map<String, String>> getExportStructureMapWhenFailed() {
        List<String> structureNames = getExportStructureNamesWhenFailed();
        if (structureNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String structure : structureNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getExportStructureFieldNamesWhenFailed(structure);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getExportStructureFieldValueWhenFailed(structure, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(structure, fieldMap);
            }
            return map;
        }
        return null;
    }

    public Map<String, Map<String, String>> getExportStructureMapWhenDefault() {
        List<String> structureNames = getExportStructureNamesWhenDefault();
        if (structureNames != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (String structure : structureNames) {
                Map<String, String> fieldMap = new HashMap<>();
                List<String> fieldNames = getExportStructureFieldNamesWhenDefault(structure);
                if (fieldNames != null) {
                    for (String field : fieldNames) {
                        String value = getExportStructureFieldValueWhenDefault(structure, field);
                        if (value != null) {
                            fieldMap.put(field, value);
                        }
                    }
                }
                map.put(structure, fieldMap);
            }
            return map;
        }
        return null;
    }

    public List<String> getExportStructureNamesWhenSuccess() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "ES");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportStructureNamesWhenFailed() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "ES");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportStructureNamesWhenDefault() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "ES");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapExportStructureFieldName(String structureName, String fieldName) {
        try {
            return exportStructureFieldMap.get(structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportStructureFieldNamesWhenSuccess(String structureName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "ES$" + structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportStructureFieldNamesWhenFailed(String structureName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "ES$" + structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getExportStructureFieldNamesWhenDefault(String structureName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "ES$" + structureName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getExportStructureFieldValueWhenSuccess(String structureName, String fieldName) {
        try {
            return exportStructureFieldMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getExportStructureFieldValueWhenFailed(String structureName, String fieldName) {
        try {
            return exportStructureFieldMap.get(JCoRecordMapperFactory.FAILED_PREFIX + structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String getExportStructureFieldValueWhenDefault(String structureName, String fieldName) {
        try {
            return exportStructureFieldMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + structureName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapChangingParameterName(String parameterName) {
        try {
            return changingParameterNameMap.get(parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, Object> getChangingParameterMapWhenSuccess() {
        List<String> parameterNames = getChangingParameterNamesWhenSuccess();
        if (parameterNames != null) {
            Map<String, Object> map = new HashMap<>();
            for (String parameter : parameterNames) {
                Object value = getChangingParameterValueWhenSuccess(parameter);
                if (value != null) {
                    map.put(parameter, value);
                } else {
                    Map<String, String> fieldMap = getChangingParameterFieldMapWhenSuccess(parameter);
                    if (fieldMap != null) {
                        map.put(parameter, fieldMap);
                    }
                }
            }
            return map;
        }
        return null;
    }

    public Map<String, Object> getChangingParameterMapWhenFailed() {
        List<String> parameterNames = getChangingParameterNamesWhenFailed();
        if (parameterNames != null) {
            Map<String, Object> map = new HashMap<>();
            for (String parameter : parameterNames) {
                Object value = getChangingParameterValueWhenFailed(parameter);
                if (value != null) {
                    map.put(parameter, value);
                } else {
                    Map<String, String> fieldMap = getChangingParameterFieldMapWhenFailed(parameter);
                    if (fieldMap != null) {
                        map.put(parameter, fieldMap);
                    }
                }
            }
            return map;
        }
        return null;
    }

    public Map<String, Object> getChangingParameterMapWhenDefault() {
        List<String> parameterNames = getChangingParameterNamesWhenFailed();
        if (parameterNames != null) {
            Map<String, Object> map = new HashMap<>();
            for (String parameter : parameterNames) {
                Object value = getChangingParameterValueWhenFailed(parameter);
                if (value != null) {
                    map.put(parameter, value);
                } else {
                    Map<String, String> fieldMap = getChangingParameterFieldMapWhenFailed(parameter);
                    if (fieldMap != null) {
                        map.put(parameter, fieldMap);
                    }
                }
            }
            return map;
        }
        return null;
    }

    public List<String> getChangingParameterNamesWhenSuccess() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "C");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getChangingParameterNamesWhenFailed() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "C");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getChangingParameterNamesWhenDefault() {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "C");
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getChangingParameterFieldNamesWhenSuccess(String parameterName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + "C$" + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getChangingParameterFieldNamesWhenFailed(String parameterName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + "C$" + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public List<String> getChangingParameterFieldNamesWhenDefault(String parameterName) {
        try {
            return whenFieldNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + "C$" + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Object getChangingParameterValueWhenSuccess(String parameterName) {
        try {
            return changingParameterNameMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Object getChangingParameterValueWhenFailed(String parameterName) {
        try {
            return changingParameterNameMap.get(JCoRecordMapperFactory.FAILED_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Object getChangingParameterValueWhenDefault(String parameterName) {
        try {
            return changingParameterNameMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapChangingParameterFieldName(String parameterName, String fieldName) {
        try {
            return changingParameterFieldMap.get(parameterName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapChangingParameterFieldValueWhenSuccess(String parameterName, String fieldName) {
        try {
            return changingParameterFieldMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + parameterName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapChangingParameterFieldValueWhenFailed(String parameterName, String fieldName) {
        try {
            return changingParameterFieldMap.get(JCoRecordMapperFactory.FAILED_PREFIX + parameterName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public String mapChangingParameterFieldValueWhenDefault(String parameterName, String fieldName) {
        try {
            return changingParameterFieldMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + parameterName).get(fieldName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, String> getChangingParameterFieldMapWhenSuccess(String parameterName) {
        try {
            return changingParameterFieldMap.get(JCoRecordMapperFactory.SUCCESS_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, String> getChangingParameterFieldMapWhenFailed(String parameterName) {
        try {
            return changingParameterFieldMap.get(JCoRecordMapperFactory.FAILED_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map<String, String> getChangingParameterFieldMapWhenDefault(String parameterName) {
        try {
            return changingParameterFieldMap.get(JCoRecordMapperFactory.DEFAULT_PREFIX + parameterName);
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public void addTableNameMap(Map<String, String> tableNameMap) {
        if (this.tableNameMap == null) {
            this.tableNameMap = new HashMap<>();
        }
        this.tableNameMap.putAll(tableNameMap);
    }

    public void addParamTableName(String tableName, String paramTableName) {
        if (this.paramTableNameMap == null) {
            this.paramTableNameMap = new HashMap<>();
        }
        this.paramTableNameMap.put(tableName, paramTableName);
    }

    public void addTableNameWhen(String tableName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "T");
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(tableName);
            whenFieldNameMap.put(prefix + "T", nameList);
        } else {
            nameList.add(tableName);
        }
    }

    public void addTableFieldMap(Map<String, Map<String, String>> tableFieldMap) {
        if (this.tableFieldMap == null) {
            this.tableFieldMap = new HashMap<>();
        }
        this.tableFieldMap.putAll(tableFieldMap);
    }

    public void addTableFieldNameWhen(String tableName, String fieldName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "T$" + tableName);
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(fieldName);
            whenFieldNameMap.put(prefix + "T$" + tableName, nameList);
        } else {
            nameList.add(fieldName);
        }
    }

    public void addImportParameterNameMap(Map<String, String> importParameterNameMap) {
        if (this.importParameterNameMap == null) {
            this.importParameterNameMap = new HashMap<>();
        }
        this.importParameterNameMap.putAll(importParameterNameMap);
    }

    public void addImportParameterNameWhen(String parameterName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "IP");
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(parameterName);
            whenFieldNameMap.put(prefix + "IP", nameList);
        } else {
            nameList.add(parameterName);
        }
    }

    public void addExportParameterNameMap(Map<String, String> exportParameterNameMap) {
        if (this.exportParameterNameMap == null) {
            this.exportParameterNameMap = new HashMap<>();
        }
        this.exportParameterNameMap.putAll(exportParameterNameMap);
    }

    public void addExportParameterNameWhen(String parameterName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "EP");
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(parameterName);
            whenFieldNameMap.put(prefix + "EP", nameList);
        } else {
            nameList.add(parameterName);
        }
    }

    public void addImportStructureNameMap(Map<String, String> importStructureNameMap) {
        if (this.importStructureNameMap == null) {
            this.importStructureNameMap = new HashMap<>();
        }
        this.importStructureNameMap.putAll(importStructureNameMap);
    }

    public void addImportStructureNameWhen(String structureName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "IS");
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(structureName);
            whenFieldNameMap.put(prefix + "IS", nameList);
        } else {
            nameList.add(structureName);
        }
    }

    public void addImportStructureFieldMap(Map<String, Map<String, String>> importStructureFieldMap) {
        if (this.importStructureFieldMap == null) {
            this.importStructureFieldMap = new HashMap<>();
        }
        this.importStructureFieldMap.putAll(importStructureFieldMap);
    }

    public void addImportStructureFieldNameWhen(String structureName, String fieldName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "IS$" + structureName);
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(fieldName);
            whenFieldNameMap.put(prefix + "IS$" + structureName, nameList);
        } else {
            nameList.add(fieldName);
        }
    }

    public void addExportStructureNameMap(Map<String, String> exportStructureNameMap) {
        if (this.exportStructureNameMap == null) {
            this.exportStructureNameMap = new HashMap<>();
        }
        this.exportStructureNameMap.putAll(exportStructureNameMap);
    }

    public void addExportStructureNameWhen(String structureName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "ES");
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(structureName);
            whenFieldNameMap.put(prefix + "ES", nameList);
        } else {
            nameList.add(structureName);
        }
    }

    public void addExportStructureFieldMap(Map<String, Map<String, String>> exportStructureFieldMap) {
        if (this.exportStructureFieldMap == null) {
            this.exportStructureFieldMap = new HashMap<>();
        }
        this.exportStructureFieldMap.putAll(exportStructureFieldMap);
    }

    public void addExportStructureFieldNameWhen(String structureName, String fieldName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "ES$" + structureName);
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(fieldName);
            whenFieldNameMap.put(prefix + "ES$" + structureName, nameList);
        } else {
            nameList.add(fieldName);
        }
    }

    public void addChangingParameterNameMap(Map<String, String> changingParameterNameMap) {
        if (this.changingParameterNameMap == null) {
            this.changingParameterNameMap = new HashMap<>();
        }
        this.changingParameterNameMap.putAll(changingParameterNameMap);
    }

    public void addChangingParameterNameWhen(String parameterName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "C");
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(parameterName);
            whenFieldNameMap.put(prefix + "C", nameList);
        } else {
            nameList.add(parameterName);
        }
    }

    public void addChangingParameterFieldMap(Map<String, Map<String, String>> changingParameterFieldMap) {
        if (this.changingParameterFieldMap == null) {
            this.changingParameterFieldMap = new HashMap<>();
        }
        this.changingParameterFieldMap.putAll(changingParameterFieldMap);
    }

    public void addChangingParameterFieldNameWhen(String parameterName, String fieldName, String prefix) {
        if (whenFieldNameMap == null) {
            whenFieldNameMap = new HashMap<>();
        }
        List<String> nameList = whenFieldNameMap.get(prefix + "C$" + parameterName);
        if (nameList == null) {
            nameList = new ArrayList<>();
            nameList.add(fieldName);
            whenFieldNameMap.put(prefix + "C$" + parameterName, nameList);
        } else {
            nameList.add(fieldName);
        }
    }

    public void addCallableValueMapper(String fieldName, JCoCallableValueMapper valueMapper, int jCoType) {
        if (callableValueMapper == null) {
            callableValueMapper = new HashMap<>();
        }
        String prefix = prefix = jCoType + "$";
        if (jCoType == JCoDataType.JCO_TABLE_ROWS) {
        } else if (jCoType == JCoDataType.JCO_IMPORT_PARAMETERS) {
        } else if (jCoType == JCoDataType.JCO_EXPORT_PARAMETERS) {
        } else if (jCoType == JCoDataType.JCO_CHANGING_PARAMETERS) {
        } else if (jCoType == JCoDataType.JCO_IMPORT_STRUCTURES) {
        } else if (jCoType == JCoDataType.JCO_EXPORT_STRUCTURES) {
        } else if (jCoType == JCoDataType.JCO_CHANGING_PARAMETER_STRUCTURE) {
        } else {
            throw new IllegalStateException("Not supported JCo data type. '" + jCoType +"'");
        }
        callableValueMapper.put(prefix + fieldName, valueMapper);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JCoRecordMapper{");
        sb.append("\n-dateFormat='").append(dateFormat).append('\'');
        sb.append("\n-timeFormat='").append(timeFormat).append('\'');
        sb.append("\n-tableNameMap=").append(tableNameMap);
        sb.append("\n-tableFieldMap=").append(tableFieldMap);
        sb.append("\n-importParameterNameMap=").append(importParameterNameMap);
        sb.append("\n-exportParameterNameMap=").append(exportParameterNameMap);
        sb.append("\n-importStructureNameMap=").append(importStructureNameMap);
        sb.append("\n-importStructureFieldMap=").append(importStructureFieldMap);
        sb.append("\n-exportStructureNameMap=").append(exportStructureNameMap);
        sb.append("\n-exportStructureFieldMap=").append(exportStructureFieldMap);
        sb.append("\n-changingParameterNameMap=").append(changingParameterNameMap);
        sb.append("\n-changingParameterFieldMap=").append(changingParameterFieldMap);
        sb.append("\n-callableValueMapper=").append(callableValueMapper);
        sb.append("\n}");
        return sb.toString();
    }
}
