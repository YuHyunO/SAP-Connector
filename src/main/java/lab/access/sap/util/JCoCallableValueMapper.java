package lab.access.sap.util;

import lab.access.sap.JCoDataType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class JCoCallableValueMapper {
    private String method;
    private String field;
    private boolean table;
    private boolean structure;
    private boolean changing;
    private boolean parameter;
    private String tableName;
    private String structureName;
    private boolean whenImport;
    private boolean whenExport;
    private String from;
    private String to;
    private String begin;
    private int locationType;

    public Object mapValue(Object value) {
        Map<Integer, String> args = new HashMap<>();
        args.put(CallableMappingExecutor.FROM, from);
        args.put(CallableMappingExecutor.TO, to);
        args.put(CallableMappingExecutor.BEGIN, begin);
        return CallableMappingExecutor.mapValue(method, value, args);
    }

    public void setLocation(String location) {
        location = location.replace(" ", "");
        if (location.startsWith("table.")) {
            int idx = location.indexOf("table.");
            if (idx != -1) {
                table = true;
                tableName = location.substring("table.".length());
                locationType = JCoDataType.JCO_TABLE_ROWS;
            } else {
                throw new IllegalStateException("The value of attribute 'location' in 'format' element is invalid");
            }
        } else if (location.startsWith("parameter.")) {
            if (location.equals("parameter.import")) {
                whenImport = true;
                parameter = true;
                locationType = JCoDataType.JCO_IMPORT_PARAMETERS;
            } else if (location.equals("parameter.export")) {
                whenExport = true;
                parameter = true;
                locationType = JCoDataType.JCO_EXPORT_PARAMETERS;
            } else if (location.startsWith("parameter.changing")){
                int idx = location.indexOf("parameter.changing.");
                if (idx != -1) {
                    changing = true;
                    structureName = location.substring("parameter.changing.".length());
                    locationType = JCoDataType.JCO_CHANGING_PARAMETER_STRUCTURE;
                } else if (location.equals("parameter.changing")){
                    changing = true;
                    locationType = JCoDataType.JCO_CHANGING_PARAMETERS;
                } else {
                    throw new IllegalStateException("The value of attribute 'location' in 'format' element is invalid");
                }
            } else {
                throw new IllegalStateException("The value of attribute 'location' in 'format' element is invalid");
            }
        } else if (location.startsWith("structure.")) {
            int idx = -1;

            idx = location.indexOf("structure.import.");
            if (idx != -1) {
                whenImport = true;
                structure = true;
                structureName = location.substring("structure.import.".length());
                locationType = JCoDataType.JCO_IMPORT_STRUCTURES;
                return;
            }

            idx = location.indexOf("structure.export.");
            if (idx != -1) {
                whenExport = true;
                structure = true;
                structureName = location.substring("structure.export.".length());
                locationType = JCoDataType.JCO_EXPORT_STRUCTURES;
                return;
            }

            throw new IllegalStateException("The value of attribute 'location' in 'format' element is invalid");
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JCoCallableValueMapper{");
        sb.append("\n-method='").append(method).append('\'');
        sb.append("\n-field='").append(field).append('\'');
        sb.append("\n-table=").append(table);
        sb.append("\n-structure=").append(structure);
        sb.append("\n-changing=").append(changing);
        sb.append("\n-parameter=").append(parameter);
        sb.append("\n-tableName='").append(tableName).append('\'');
        sb.append("\n-structureName='").append(structureName).append('\'');
        sb.append("\n-whenImport=").append(whenImport);
        sb.append("\n-whenExport=").append(whenExport);
        sb.append("\n-from='").append(from).append('\'');
        sb.append("\n-to='").append(to).append('\'');
        sb.append("\n-begin='").append(begin).append('\'');
        sb.append("\n-locationType=").append(locationType);
        sb.append("\n}");
        return sb.toString();
    }
}
