package lab.access.sap.util;

import com.sap.conn.jco.*;
import lab.access.sap.JCoDataType;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class JCoUtil {

    private final static String dateFormat = "yyyyMMdd";
    private final static String timeFormat = "HHmmss";
    public static Object readValue(JCoField jCoField, JCoRecordMapper mapper, int jCoDataType) {
        String fieldName = jCoField.getName();
        Object value = null;
        if (mapper != null) {
            value = mapper.callValue(fieldName, jCoField.getValue(), jCoDataType);
        } else {
            value = jCoField.getValue();
        }

        if (value == null) {
            //log.debug("The field '{}' value is null", fieldName);
            return null;
        }
        int type = jCoField.getType();
        switch (type) {
            case JCoListMetaData.TYPE_DATE: {
                SimpleDateFormat sdf = new SimpleDateFormat(mapper.getDateFormat());
                return sdf.format(value);
            }
            case JCoListMetaData.TYPE_TIME: {
                SimpleDateFormat sdf = new SimpleDateFormat(mapper.getTimeFormat());
                return sdf.format(value);
            }
            case JCoListMetaData.TYPE_XSTRING: {
                byte[] byteArr = jCoField.getByteArray();
                StringBuilder sb = new StringBuilder();
                for (final byte b : byteArr)
                    sb.append(String.format("%02X", b & 0xff));
                return sb.toString();
            }
            default: {
                return String.valueOf(value);
            }
        }
    }

    public static Object readValue(JCoField jCoField, int jCoDataType) {
        String fieldName = jCoField.getName();
        Object value = null;
        value = jCoField.getValue();

        if (value == null) {
            //log.debug("The field '{}' value is null", fieldName);
            return null;
        }
        int type = jCoField.getType();
        switch (type) {
            case JCoListMetaData.TYPE_DATE: {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                return sdf.format(value);
            }
            case JCoListMetaData.TYPE_TIME: {
                SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
                return sdf.format(value);
            }
            case JCoListMetaData.TYPE_XSTRING: {
                byte[] byteArr = jCoField.getByteArray();
                StringBuilder sb = new StringBuilder();
                for (final byte b : byteArr)
                    sb.append(String.format("%02X", b & 0xff));
                return sb.toString();
            }
            default: {
                return String.valueOf(value);
            }
        }
    }

    public static boolean setValue(JCoRecord record, String recordName, Object value) {
        if (recordName == null) {
            return false;
        }

        if (value != null) {
            try {
                record.setValue(recordName, value);
                //log.debug("## Logging for test:\nrecord.setValue({}, {}); complete", recordName, value);
            } catch (JCoRuntimeException e) {
                String key = e.getKey();
                if (key.equals("JCO_ERROR_FIELD_NOT_FOUND")) {
                    log.debug("JCO_ERROR_FIELD_NOT_FOUND : field '{}' is not found in the record[{}]", recordName, record.getClass().getSimpleName());
                    return false;
                } else {
                    log.error("", e);
                    throw e;
                }
            }
            return true;
        }
        //log.info("## setValue value is null");
        return false;
    }

    public static Map<String, List<Map<String, Object>>> getJCoTables(JCoFunction function) {
        return getJCoTables(function, null);
    }

    public static Map<String, Object> getJCoImportParameters(JCoFunction function) {
        return getJCoImportParameters(function, null);
    }

    public static Map<String, Object> getJCoExportParameters(JCoFunction function) {
        return getJCoExportParameters(function, null);
    }

    public static Map<String, Map<String, Object>> getJCoImportStructures(JCoFunction function) {
        return getJCoImportStructures(function, null);
    }

    public static Map<String, Map<String, Object>> getJCoExportStructures(JCoFunction function) {
        return getJCoExportStructures(function, null);
    }

    public static Map<String, Object> getJCoChangingParameters(JCoFunction function) {
        return getJCoChangingParameters(function, null);
    }

    public static Map<String, List<Map<String, Object>>> getJCoTables(JCoFunction function, JCoRecordMapper mapper) {
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }

        JCoParameterList jCoTableList = function.getTableParameterList();
        if (jCoTableList == null) {
            return null;
        }
        Map<String, List<Map<String, Object>>> tableData = new HashMap<>();

        JCoFieldIterator jcoTableIter = jCoTableList.getFieldIterator();
        if (mapper != null) {
            while (jcoTableIter.hasNextField()) {
                JCoField jCoField = jcoTableIter.nextField();
                String tableName = jCoField.getName();
                String mappedTableName = mapper.mapTableName(tableName);
                List<Map<String, Object>> tableRows = new ArrayList<>();

                JCoTable jCoTable = jCoTableList.getTable(tableName);
                int rowCnt = jCoTable.getNumRows();
                for (int i = 0; i < rowCnt; i++) {
                    jCoTable.setRow(i);
                    Map<String, Object> row = new HashMap<>();
                    JCoFieldIterator iter = jCoTable.getFieldIterator();
                    while (iter.hasNextField()) {
                        JCoField field = iter.nextField();
                        String fieldName = field.getName();
                        String mappedFieldName = mapper.mapTableFieldName(tableName, fieldName);
                        Object value = readValue(field, mapper, JCoDataType.JCO_TABLE_ROWS);

                        if (mappedFieldName != null) {
                            row.put(mappedFieldName, value);
                        } else {
                            row.put(fieldName, value);
                        }
                    }
                    tableRows.add(row);
                }
                if (mappedTableName != null) {
                    tableData.put(mappedTableName, tableRows);
                } else {
                    tableData.put(tableName, tableRows);
                }
            }
        } else {
            while (jcoTableIter.hasNextField()) {
                JCoField jCoField = jcoTableIter.nextField();
                String tableName = jCoField.getName();
                List<Map<String, Object>> tableRows = new ArrayList<>();
                JCoTable jCoTable = jCoTableList.getTable(tableName);
                int rowCnt = jCoTable.getNumRows();
                for (int i = 0; i < rowCnt; i++) {
                    jCoTable.setRow(i);
                    Map<String, Object> row = new HashMap<>();
                    JCoFieldIterator iter = jCoTable.getFieldIterator();
                    while (iter.hasNextField()) {
                        JCoField field = iter.nextField();
                        String fieldName = field.getName();
                        Object value = readValue(field, JCoDataType.JCO_TABLE_ROWS);
                        row.put(fieldName, value);
                    }
                    tableRows.add(row);
                }
                tableData.put(tableName, tableRows);
            }
        }

        return tableData;
    }

    public static Map<String, Object> getJCoImportParameters(JCoFunction function, JCoRecordMapper mapper) {
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        Map<String, Object> importParameterData = new HashMap<>();
        JCoParameterList jCoParameterList = function.getImportParameterList();
        importParameterData = getJCoParameters(jCoParameterList, mapper, JCoDataType.JCO_IMPORT_PARAMETERS);
        return importParameterData;
    }

    public static Map<String, Object> getJCoExportParameters(JCoFunction function, JCoRecordMapper mapper) {
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        Map<String, Object> exportParameterData = new HashMap<>();
        JCoParameterList jCoParameterList = function.getExportParameterList();
        exportParameterData = getJCoParameters(jCoParameterList, mapper, JCoDataType.JCO_EXPORT_PARAMETERS);
        return exportParameterData;
    }

    public static Map<String, Object> getJCoParameters(JCoParameterList jCoParameterList, JCoRecordMapper mapper, int jCoDataType) {
        if (jCoParameterList == null) {
            return null;
        }
        Map<String, Object> parameterData = new HashMap<>();
        JCoParameterFieldIterator iter = jCoParameterList.getParameterFieldIterator();
        if (mapper != null) {
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                int jCoType = jCoField.getType();
                String fieldName = jCoField.getName();
                Object value = null;
                String mappedFieldName = null;
                if (JCoDataType.JCO_IMPORT_PARAMETERS == jCoDataType) {
                    if (jCoType == JCoMetaData.TYPE_STRUCTURE)
                        continue;
                    mappedFieldName = mapper.mapImportParameterName(fieldName);
                    value = readValue(jCoField, mapper, jCoDataType);
                } else if (JCoDataType.JCO_EXPORT_PARAMETERS == jCoDataType) {
                    if (jCoType == JCoMetaData.TYPE_STRUCTURE)
                        continue;
                    mappedFieldName = mapper.mapExportParameterName(fieldName);
                    value = readValue(jCoField, mapper, jCoDataType);
                } else if (JCoDataType.JCO_CHANGING_PARAMETERS == jCoDataType) {
                    mappedFieldName = mapper.mapChangingParameterName(fieldName);
                    if (jCoType == JCoMetaData.TYPE_STRUCTURE) {
                        JCoStructure structure = jCoParameterList.getStructure(fieldName);
                        value = getJCoStructureFields(structure, fieldName, mapper, JCoDataType.JCO_CHANGING_PARAMETER_STRUCTURE);
                    } else {
                        value = readValue(jCoField, mapper, jCoDataType);
                    }
                } else {
                    return null;
                }

                if (mappedFieldName != null) {
                    parameterData.put(mappedFieldName, value);
                } else {
                    parameterData.put(fieldName, value);
                }
            }
        } else {
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                int jCoType = jCoField.getType();
                String fieldName = jCoField.getName();
                Object value = null;
                if (JCoDataType.JCO_IMPORT_PARAMETERS == jCoDataType) {
                    if (jCoType == JCoMetaData.TYPE_STRUCTURE)
                        continue;
                    value = readValue(jCoField, jCoDataType);
                } else if (JCoDataType.JCO_EXPORT_PARAMETERS == jCoDataType) {
                    if (jCoType == JCoMetaData.TYPE_STRUCTURE)
                        continue;
                    value = readValue(jCoField, jCoDataType);
                } else if (JCoDataType.JCO_CHANGING_PARAMETERS == jCoDataType) {
                    if (jCoType == JCoMetaData.TYPE_STRUCTURE) {
                        JCoStructure structure = jCoParameterList.getStructure(fieldName);
                        value = getJCoStructureFields(structure, fieldName, mapper, JCoDataType.JCO_CHANGING_PARAMETER_STRUCTURE);
                    } else {
                        value = readValue(jCoField, jCoDataType);
                    }
                } else {
                    return null;
                }
                parameterData.put(fieldName, value);

            }
        }


        return parameterData;
    }

    public static Map<String, Object> getJCoStructureFields(JCoStructure jCoStructure, String structureName, JCoRecordMapper mapper, int jCoDataType) {
        if (jCoStructure == null) {
            return null;
        }
        Map<String, Object> structureFieldData = new HashMap<>();
        JCoFieldIterator iter = jCoStructure.getFieldIterator();
        if (mapper != null) {
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                String fieldName = jCoField.getName();

                String mappedFieldName = null;
                if (JCoDataType.JCO_IMPORT_STRUCTURES == jCoDataType) {
                    mappedFieldName = mapper.mapImportStructureFieldName(structureName, fieldName);
                } else if (JCoDataType.JCO_EXPORT_STRUCTURES == jCoDataType) {
                    mappedFieldName = mapper.mapExportStructureFieldName(structureName, fieldName);
                } else if (JCoDataType.JCO_CHANGING_PARAMETER_STRUCTURE == jCoDataType) {
                    mappedFieldName = mapper.mapChangingParameterFieldName(structureName, fieldName);
                } else {
                    return null;
                }
                Object value = readValue(jCoField, mapper, jCoDataType);
                //나중에 삭제
                //log.info("STRUCTURE Filed(fieldName={})(mappedFieldName={})(value={})", fieldName, mappedFieldName, value);
                if (mappedFieldName != null) {
                    structureFieldData.put(mappedFieldName, value);
                } else {
                    structureFieldData.put(fieldName, value);
                }
            }
        } else {
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                String fieldName = jCoField.getName();
                Object value = readValue(jCoField, jCoDataType);
                structureFieldData.put(fieldName, value);
            }
        }


        return structureFieldData;
    }

    public static Map<String, Map<String, Object>> getJCoImportStructures(JCoFunction function, JCoRecordMapper mapper) {
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        Map<String, Map<String, Object>> importStructureData = new HashMap<>();
        JCoParameterList jCoParameterList = function.getImportParameterList();
        if (jCoParameterList == null) {
            return null;
        }
        JCoFieldIterator iter = jCoParameterList.getParameterFieldIterator();
        while (iter.hasNextField()) {
            JCoField jCoField = iter.nextField();
            int jCoType = jCoField.getType();
            if (jCoType != JCoMetaData.TYPE_STRUCTURE)
                continue;
            String structureName = jCoField.getName();
            if (mapper != null) {
                String mappedStructureName = mapper.mapImportStructureName(structureName);
                JCoStructure structure = jCoParameterList.getStructure(structureName);
                Map<String, Object> fieldData = getJCoStructureFields(structure, structureName, mapper, JCoDataType.JCO_IMPORT_STRUCTURES);

                if (mappedStructureName != null) {
                    importStructureData.put(mappedStructureName, fieldData);
                } else {
                    importStructureData.put(structureName, fieldData);
                }
            } else {
                JCoStructure structure = jCoParameterList.getStructure(structureName);
                Map<String, Object> fieldData = getJCoStructureFields(structure, structureName, mapper, JCoDataType.JCO_IMPORT_STRUCTURES);
                importStructureData.put(structureName, fieldData);
            }

        }

        return importStructureData;
    }

    public static Map<String, Map<String, Object>> getJCoExportStructures(JCoFunction function, JCoRecordMapper mapper) {
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        Map<String, Map<String, Object>> exportStructureData = new HashMap<>();
        JCoParameterList jCoParameterList = function.getExportParameterList();
        if (jCoParameterList == null) {
            return null;
        }
        JCoFieldIterator iter = jCoParameterList.getParameterFieldIterator();
        while (iter.hasNextField()) {
            JCoField jCoField = iter.nextField();
            int jCoType = jCoField.getType();
            if (jCoType != JCoMetaData.TYPE_STRUCTURE)
                continue;
            String structureName = jCoField.getName();
            if (mapper != null) {
                String mappedStructureName = mapper.mapExportStructureName(structureName);
                JCoStructure structure = jCoParameterList.getStructure(structureName);
                Map<String, Object> fieldData = getJCoStructureFields(structure, structureName, mapper, JCoDataType.JCO_EXPORT_STRUCTURES);

                if (mappedStructureName != null) {
                    exportStructureData.put(mappedStructureName, fieldData);
                } else {
                    exportStructureData.put(structureName, fieldData);
                }
            } else {
                JCoStructure structure = jCoParameterList.getStructure(structureName);
                Map<String, Object> fieldData = getJCoStructureFields(structure, structureName, mapper, JCoDataType.JCO_EXPORT_STRUCTURES);
                exportStructureData.put(structureName, fieldData);
            }
        }

        return exportStructureData;
    }

    public static Map<String, Object> getJCoChangingParameters(JCoFunction function, JCoRecordMapper mapper) {
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        Map<String, Object> changingParameterData = new HashMap<>();
        JCoParameterList jCoParameterList = function.getChangingParameterList();
        changingParameterData = getJCoParameters(jCoParameterList, mapper, JCoDataType.JCO_CHANGING_PARAMETERS);
        return changingParameterData;
    }

    public static int setJCoTable(JCoFunction function, String tableName, List<Map<String, Object>> inputData, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        JCoParameterList jCoParameterList = function.getTableParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoParameterList is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }

        JCoTable jCoTable = null;
        if (tableName == null) {
            JCoParameterFieldIterator iter = jCoParameterList.getParameterFieldIterator();
            String tempTableName = null;
            while (iter.hasNextField()) {
                JCoField field = iter.nextField();

            }
        }

        try {
            jCoTable = jCoParameterList.getTable(tableName);
        } catch (JCoRuntimeException e) {
            String key = e.getKey();
            if (key.equals("JCO_ERROR_FIELD_NOT_FOUND")) {
                log.debug("JCO_FIELD_NOT_FOUND : table({}) is not found in the JCoTableParameterList", tableName);
                if (!ignoreFieldNotFound) {
                    throw e;
                }
                return 0;
            }
            log.error("", e);
            throw e;
        }
        for (Map<String, Object> row : inputData) {
            jCoTable.appendRow();
            JCoFieldIterator iter = jCoTable.getFieldIterator();
            boolean setMoreThanOnce = false;
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                String fieldName = jCoField.getName();
                Object value = row.get(fieldName);
                setMoreThanOnce = setValue(jCoTable, fieldName, value);
            }
            if (setMoreThanOnce) {
                ++cnt;
            }
        }
        jCoParameterList.setValue(tableName, jCoTable);

        return cnt;
    }

    public static int setJCoTable(JCoFunction function, String tableName, List<Map<String, Object>> inputData, Map<String, Object> addParameter, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        JCoParameterList jCoParameterList = function.getTableParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoParameterList is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }

        JCoTable jCoTable = null;
        /*if (tableName == null) {
            JCoParameterFieldIterator iter = jCoParameterList.getParameterFieldIterator();
            String tempTableName = null;
            while (iter.hasNextField()) {
                JCoField field = iter.nextField();
            }
        }*/

        try {
            jCoTable = jCoParameterList.getTable(tableName);
        } catch (JCoRuntimeException e) {
            String key = e.getKey();
            if (key.equals("JCO_ERROR_FIELD_NOT_FOUND")) {
                log.debug("JCO_FIELD_NOT_FOUND : table({}) is not found in the JCoTableParameterList", tableName);
                if (!ignoreFieldNotFound) {
                    throw e;
                }
                return 0;
            }
            log.error("", e);
            throw e;
        }
        for (Map<String, Object> row : inputData) {
            if (addParameter != null) {
                row.putAll(addParameter);
            }

            jCoTable.appendRow();
            JCoFieldIterator iter = jCoTable.getFieldIterator();
            boolean fieldValueSet = false;
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                String fieldName = jCoField.getName();
                //log.info("Table={}, Field name={}", tableName, fieldName);
                Object value = row.get(fieldName);
                if (setValue(jCoTable, fieldName, value)) {
                    fieldValueSet = true;
                }
            }
            if (fieldValueSet) {
                ++cnt;
            }
        }
        jCoParameterList.setValue(tableName, jCoTable);

        return cnt;
    }

    public static int setJCoTableFromFirstIndex(JCoFunction function, String tableName, List<Map<String, Object>> inputData, Map<String, Object> addParameter, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        JCoParameterList jCoParameterList = function.getTableParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoParameterList is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }

        JCoTable jCoTable = null;
        /*if (tableName == null) {
            JCoParameterFieldIterator iter = jCoParameterList.getParameterFieldIterator();
            String tempTableName = null;
            while (iter.hasNextField()) {
                JCoField field = iter.nextField();
            }
        }*/

        try {
            jCoTable = jCoParameterList.getTable(tableName);
        } catch (JCoRuntimeException e) {
            String key = e.getKey();
            if (key.equals("JCO_ERROR_FIELD_NOT_FOUND")) {
                log.debug("JCO_FIELD_NOT_FOUND : table({}) is not found in the JCoTableParameterList", tableName);
                if (!ignoreFieldNotFound) {
                    throw e;
                }
                return 0;
            }
            log.error("", e);
            throw e;
        }
        int idx = 1;
        boolean first = true;
        for (Map<String, Object> row : inputData) {
            if (addParameter != null) {
                row.putAll(addParameter);
            }
            if (first) {
                jCoTable.setRow(idx);
                first = false;
            } else {
                jCoTable.setRow(idx++);
            }

            JCoFieldIterator iter = jCoTable.getFieldIterator();
            boolean fieldValueSet = false;
            while (iter.hasNextField()) {
                JCoField jCoField = iter.nextField();
                String fieldName = jCoField.getName();
                //log.info("Table={}, Field name={}", tableName, fieldName);
                Object value = row.get(fieldName);
                if (setValue(jCoTable, fieldName, value)) {
                    fieldValueSet = true;
                }
            }
            if (fieldValueSet) {
                ++cnt;
            }
        }
        jCoParameterList.setValue(tableName, jCoTable);

        return cnt;
    }

    public static int setJCoParameter(JCoParameterList jCoParameterList, String parameterName, Object value, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoParameterList is null");
        }
        JCoField field = null;
        try {
            field = jCoParameterList.getField(parameterName);
        } catch (JCoRuntimeException e) {
            String key = e.getKey();
            if (key.equals("JCO_ERROR_FIELD_NOT_FOUND")) {
                log.debug("JCO_FIELD_NOT_FOUND : parameter({}) is not found in the JCoTableParameterList", parameterName);
                if (!ignoreFieldNotFound) {
                    throw e;
                }
                return 0;
            }
            log.error("", e);
            throw e;
        }
        if (field.getType() == JCoMetaData.TYPE_STRUCTURE)
            return 0;
        return setValue(jCoParameterList, parameterName, value) ? 1 : 0;
    }

    public static int setJCoImportParameters(JCoFunction function, Map<String, Object> inputData, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }
        JCoParameterList jCoParameterList = function.getImportParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoImportParameterList is null");
        }
        for (String name : inputData.keySet()) {
            cnt += setJCoParameter(jCoParameterList, name, inputData.get(name), ignoreFieldNotFound);
        }
        return cnt;
    }

    public static int setJCoExportParameters(JCoFunction function, Map<String, Object> inputData, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }
        JCoParameterList jCoParameterList = function.getExportParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoExportParameterList is null");
        }
        for (String name : inputData.keySet()) {
            cnt += setJCoParameter(jCoParameterList, name, inputData.get(name), ignoreFieldNotFound);
        }
        return cnt;
    }

    public static int setJCoStructure(JCoParameterList jCoParameterList, String structureName, Map<String, Object> inputData, boolean ignoreFiledNotFound) {
        int cnt = 0;
        JCoStructure jCoStructure = null;
        try {
            jCoStructure = jCoParameterList.getStructure(structureName);
        } catch (JCoRuntimeException e) {
            String key = e.getKey();
            if (key.equals("JCO_ERROR_FIELD_NOT_FOUND")) {
                log.debug("JCO_FIELD_NOT_FOUND : structure({}) is not found", structureName);
                if (!ignoreFiledNotFound) {
                    throw e;
                }
                return 0;
            }
            log.error("", e);
            throw e;
        }
        for (String name : inputData.keySet()) {
            boolean flag = setValue(jCoStructure, name, inputData.get(name));
            if (flag) {
                ++cnt;
            }
        }
        if (cnt > 0) {
            jCoParameterList.setValue(structureName, jCoStructure);
        }
        return cnt;
    }

    public static int setJCoImportStructure(JCoFunction function, String structureName, Map<String, Object> inputData, boolean ignoreFiledNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }
        JCoParameterList jCoParameterList = function.getImportParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoParameterList is null");
        }

        return setJCoStructure(jCoParameterList, structureName, inputData, ignoreFiledNotFound);
    }

    public static int setJCoExportStructure(JCoFunction function, String structureName, Map<String, Object> inputData, boolean ignoreFiledNotFound) {
        int cnt = 0;
        try {
            if (inputData == null || inputData.isEmpty()) {
                return 0;
            }
        } catch (NullPointerException ne) {
            return 0;
        }
        JCoParameterList jCoParameterList = function.getExportParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoParameterList is null");
        }

        return setJCoStructure(jCoParameterList, structureName, inputData, ignoreFiledNotFound);
    }

    public static int setJCoChangingParameter(JCoFunction function, Map<String, Object> inputData, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (function == null) {
            throw new NullPointerException("The JCoFunction is null");
        }
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }
        JCoParameterList jCoParameterList = function.getChangingParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoChangingParameterList is null");
        }
        for (String name : inputData.keySet()) {
            Object value = inputData.get(name);
            //Changing 파라미터의 value 가 structure 형태인 경우
            if (value instanceof Map) {
                cnt += setJCoChangingParameterStructure(function, name, (Map<String, Object>) value, ignoreFieldNotFound);
            } else {
                cnt += setJCoParameter(jCoParameterList, name, inputData.get(name), ignoreFieldNotFound);
            }
        }
        return cnt;
    }

    public static int setJCoChangingParameterStructure(JCoFunction function, String structureName, Map<String, Object> inputData, boolean ignoreFieldNotFound) {
        int cnt = 0;
        if (inputData == null && inputData.isEmpty()) {
            return 0;
        }
        JCoParameterList jCoParameterList = function.getChangingParameterList();
        if (jCoParameterList == null) {
            throw new NullPointerException("The JCoChangingParameterList is null");
        }

        return setJCoStructure(jCoParameterList, structureName, inputData, ignoreFieldNotFound);
    }
}
