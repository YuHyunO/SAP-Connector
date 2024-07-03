package lab.access.sap.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JCoRecordMapperFactory {
    private static Map<String, JCoRecordMapper> mapper = new HashMap<>();
    private final String JCO_MAPPERS = "jcoMappers";
    private final String JCO_MAPPER = "jcoMapper";
    private final String ID = "id";
    private final String NAME = "name";
    private final String FROM = "from";
    private final String TO = "to";
    private final String WHEN = "when";
    private final String IF = "if";
    private final String PARAM_TABLE_NAME = "paramTableName";
    private final String SUCCESS = "success";
    private final String FAILED = "failed";
    private final String DEFAULT = "default";
    private final String SET = "set";
    private final String TABLES = "tables";
    private final String TABLE = "table";
    private final String FIELD = "field";
    private final String PARAMETERS = "parameters";
    private final String IMPORT = "import";
    private final String EXPORT = "export";
    private final String CHANGING = "changing";
    private final String STRUCTURES = "structures";
    private final String STRUCTURE = "structure";
    public static final String SUCCESS_PREFIX = "S$";
    public static final String FAILED_PREFIX = "F$";
    public static final String DEFAULT_PREFIX = "D$";
    public final String FORMAT = "format";
    public final String TYPE = "type";
    public final String VALUE = "value";
    public final String METHOD = "method";
    public final String CALL = "call";
    public final String BEGIN = "begin";;

    public static JCoRecordMapper getMapper(String id) {
        return mapper.get(id);
    }

    public static String parseNamePlaceHolder(String fieldName) {
        if (fieldName != null) {
            if (fieldName.startsWith("#{") && fieldName.endsWith("}")) {
                return fieldName.substring(2, fieldName.length() - 1).trim();
            }
        }
        return null;
    }

    public static String[] getPlaceHolderFieldLocation(String fieldName) {
        if (fieldName != null) {
            return fieldName.split("[.]");
        }
        return null;
    }

    public void setMapperFileLocation(List<String> mapperFileLocation) throws IOException, XMLStreamException{
        for (String path : mapperFileLocation) {
            parse(path);
            log.info("Loaded mapper file:{}", path);
        }
    }

    private void parse(String mapperFilePath) throws IOException, XMLStreamException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileReader(mapperFilePath));

        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartDocument()) {
                nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement ste = nextEvent.asStartElement();
                    String elementName = ste.getName().getLocalPart();
                    String mapperStartIndicator = elementName;
                    if (!JCO_MAPPERS.equals(mapperStartIndicator)) {
                        log.error("Invalid mapper file. The document must be start with <jcoMappers>");
                        throw new IllegalStateException("Invalid mapper file. The document must be start with <jcoMappers>");
                    }
                } else {
                    log.error("Invalid mapper file. The document must be start with <jcoMappers>");
                    throw new IllegalStateException("Invalid mapper file. The document must be start with <jcoMappers>");
                }
                continue;
            }

            if (nextEvent.isStartElement()) {
                StartElement ste = nextEvent.asStartElement();
                String elementName = ste.getName().getLocalPart();
                if (JCO_MAPPER.equals(elementName)) {
                    String id = null;
                    JCoRecordMapper jCoMapper = new JCoRecordMapper();
                    Attribute idAttr = (ste.getAttributeByName(new QName(ID)));
                    if (idAttr == null) {
                        throw new IllegalStateException("The mapper id is not declared");
                    }
                    id = idAttr.getValue();
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ete = nextEvent.asEndElement();
                            String innerEleName = ete.getName().getLocalPart();
                            if (JCO_MAPPER.equals(innerEleName)) {
                                mapper.put(id, jCoMapper);
                                break;
                            }
                        }

                        if (nextEvent.isStartElement()) {
                            StartElement innerSte = nextEvent.asStartElement();
                            String innerEleName = innerSte.getName().getLocalPart();
                            if (TABLES.equals(innerEleName)) {
                                setTableMapper(reader, jCoMapper, false, "");
                            } else if (PARAMETERS.equals(innerEleName)) {
                                setParameterMapper(reader, jCoMapper, false, "");
                            } else if (STRUCTURES.equals(innerEleName)) {
                                setStructureMapper(reader, jCoMapper, false, "");
                            } else if (WHEN.equals(innerEleName)) {
                                Attribute ifAttr = innerSte.getAttributeByName(new QName(IF));
                                if (ifAttr == null) {
                                    throw new IllegalStateException("when element must contain the attribute 'if'");
                                }
                                String ifType = ifAttr.getValue().toLowerCase();
                                if (SUCCESS.equals(ifType)) {
                                    setWhenMapper(reader, jCoMapper, SUCCESS_PREFIX);
                                } else if (FAILED.equals(ifType)) {
                                    setWhenMapper(reader, jCoMapper, FAILED_PREFIX);
                                } else if (DEFAULT.equals(ifType)) {
                                    setWhenMapper(reader, jCoMapper, DEFAULT_PREFIX);
                                } else {
                                    throw new IllegalStateException("when if type '" + ifType + "' is not supported");
                                }
                            } else if (FORMAT.equals(innerEleName)) {
                                setFormatMapper(nextEvent, jCoMapper);
                            }
                        }

                    }
                }
            }

        }
    }

    private void setTableMapper(XMLEventReader reader, JCoRecordMapper jCoMapper, boolean when, String prefix) throws XMLStreamException {
        Map<String, String> tableNameMap = new HashMap<>();
        Map<String, String> paramTableNameMap = new HashMap<>();
        Map<String, Map<String, String>> tableFieldMap = new HashMap<>();
        while (true) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isEndElement()) {
                EndElement ene = nextEvent.asEndElement();
                if (TABLES.equals(ene.getName().getLocalPart())) {
                    if (!tableNameMap.isEmpty()) {
                        jCoMapper.addTableNameMap(tableNameMap);
                    }
                    if (!tableFieldMap.isEmpty()) {
                        jCoMapper.addTableFieldMap(tableFieldMap);
                    }
                    break;
                }
                continue;
            }
            if (nextEvent.isStartElement()) {
                StartElement ste = nextEvent.asStartElement();
                String elementName = ste.getName().getLocalPart();
                if (NAME.equals(elementName)) {
                    String[] attr = null;
                    if (!when) {
                        attr = getFromTo(ste);
                    } else {
                        attr = getNameSet(ste);
                    }
                    tableNameMap.put(prefix + attr[0], attr[1]);
                }
                if (TABLE.equals(elementName)) {
                    Attribute tableNameAttr = ste.getAttributeByName(new QName(NAME));
                    if (tableNameAttr == null) {
                        throw new IllegalStateException("The 'name' attribute in table tag is null");
                    }
                    String tableName = tableNameAttr.getValue();
                    if (when) {
                        Attribute paramTableNameAttr = ste.getAttributeByName(new QName(PARAM_TABLE_NAME));
                        if (paramTableNameAttr != null) {
                            jCoMapper.addParamTableName(tableName, paramTableNameAttr.getValue());
                        }
                        if (SUCCESS_PREFIX.equals(prefix)) {
                            jCoMapper.addTableNameWhen(tableName, prefix);
                        } else if (FAILED_PREFIX.equals(prefix)) {
                            jCoMapper.addTableNameWhen(tableName, prefix);
                        } else if (DEFAULT_PREFIX.equals(prefix)) {
                            jCoMapper.addTableNameWhen(tableName, prefix);
                        }
                    }
                    Map<String, String> fieldMap = new HashMap<>();
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ene = nextEvent.asEndElement();
                            if (TABLE.equals(ene.getName().getLocalPart())) {
                                break;
                            }
                        }
                        if (nextEvent.isStartElement()) {
                            ste = nextEvent.asStartElement();
                            if (FIELD.equals(ste.getName().getLocalPart())) {
                                String[] attr = null;
                                if (!when) {
                                    attr = getFromTo(ste);
                                } else {
                                    attr = getNameSet(ste);
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addTableFieldNameWhen(tableName, attr[0], prefix);
                                    } else if (FAILED_PREFIX.equals(prefix)) {
                                        jCoMapper.addTableFieldNameWhen(tableName, attr[0], prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addTableFieldNameWhen(tableName, attr[0], prefix);
                                    }
                                }
                                fieldMap.put(attr[0], attr[1]);
                            }
                        }
                    }
                    tableFieldMap.put(prefix + tableName, fieldMap);
                }
            }

        }
    }

    private void setParameterMapper(XMLEventReader reader, JCoRecordMapper jCoMapper, boolean when, String prefix) throws XMLStreamException {
        Map<String, String> importParameterNameMap = new HashMap<>();
        Map<String, String> exportParameterNameMap = new HashMap<>();
        Map<String, String> changingParameterNameMap = new HashMap<>();
        Map<String, Map<String, String>> changingParameterFieldMap = new HashMap<>();

        while (true) {
            XMLEvent nextEvent = reader.nextEvent();

            if (nextEvent.isEndElement()) {
                EndElement ene = nextEvent.asEndElement();
                String elementName = ene.getName().getLocalPart();
                if (PARAMETERS.equals(elementName)) {
                    if (!importParameterNameMap.isEmpty()) {
                        jCoMapper.addImportParameterNameMap(importParameterNameMap);
                    }
                    if (!exportParameterNameMap.isEmpty()) {
                        jCoMapper.addExportParameterNameMap(exportParameterNameMap);
                    }
                    if (!changingParameterNameMap.isEmpty()) {
                        jCoMapper.addChangingParameterNameMap(changingParameterNameMap);
                    }
                    if (!changingParameterFieldMap.isEmpty()) {
                        jCoMapper.addChangingParameterFieldMap(changingParameterFieldMap);
                    }
                    break;
                }
                continue;
            }

            if (nextEvent.isStartElement()) {
                StartElement ste = nextEvent.asStartElement();
                String elementName = ste.getName().getLocalPart();
                if (IMPORT.equals(elementName)) {
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ene = nextEvent.asEndElement();
                            if (IMPORT.equals(ene.getName().getLocalPart())) {
                                break;
                            }
                            continue;
                        }
                        if (nextEvent.isStartElement()) {
                            StartElement innerSte = nextEvent.asStartElement();
                            String innerEleName = innerSte.getName().getLocalPart();
                            if (FIELD.equals(innerEleName)) {
                                String[] attr = null;
                                if (!when) {
                                    attr = getFromTo(innerSte);
                                } else {
                                    attr = getNameSet(innerSte);
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addImportParameterNameWhen(attr[0], prefix);
                                    } else if ((FAILED_PREFIX.equals(prefix))) {
                                        jCoMapper.addImportParameterNameWhen(attr[0], prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addImportParameterNameWhen(attr[0], prefix);
                                    }
                                }
                                importParameterNameMap.put(prefix + attr[0], attr[1]);
                            }
                        }
                    }
                }
                if (EXPORT.equals(elementName)) {
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ene = nextEvent.asEndElement();
                            if (EXPORT.equals(ene.getName().getLocalPart())) {
                                break;
                            }
                            continue;
                        }
                        if (nextEvent.isStartElement()) {
                            StartElement innerSte = nextEvent.asStartElement();
                            String innerEleName = innerSte.getName().getLocalPart();
                            if (FIELD.equals(innerEleName)) {
                                String[] attr = null;
                                if (!when) {
                                    attr = getFromTo(innerSte);
                                } else {
                                    attr = getNameSet(innerSte);
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addExportParameterNameWhen(attr[0], prefix);
                                    } else if ((FAILED_PREFIX.equals(prefix))) {
                                        jCoMapper.addExportParameterNameWhen(attr[0], prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addExportParameterNameWhen(attr[0], prefix);
                                    }
                                }
                                exportParameterNameMap.put(prefix + attr[0], attr[1]);
                            }
                        }
                    }
                }
                if (CHANGING.equals(elementName)) {
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ene = nextEvent.asEndElement();
                            if (CHANGING.equals(ene.getName().getLocalPart())) {
                                break;
                            }
                            continue;
                        }
                        if (nextEvent.isStartElement()) {
                            StartElement innerSte = nextEvent.asStartElement();
                            String innerEleName = innerSte.getName().getLocalPart();
                            if (FIELD.equals(innerEleName)) {
                                String[] attr = null;
                                if (!when) {
                                    attr = getFromTo(innerSte);
                                } else {
                                    attr = getNameSet(innerSte);
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addChangingParameterNameWhen(attr[0], prefix);
                                    } else if ((FAILED_PREFIX.equals(prefix))) {
                                        jCoMapper.addChangingParameterNameWhen(attr[0], prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addChangingParameterNameWhen(attr[0], prefix);
                                    }
                                }
                                changingParameterNameMap.put(prefix + attr[0], attr[1]);
                            }
                            if (STRUCTURE.equals(innerEleName)) {
                                Attribute structureNameAttr = innerSte.getAttributeByName(new QName(NAME));
                                if (structureNameAttr == null) {
                                    throw new IllegalStateException("structure name attribute is null");
                                }
                                String strucutureName = structureNameAttr.getValue();
                                if (when) {
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addChangingParameterNameWhen(strucutureName, prefix);
                                    } else if (FAILED_PREFIX.equals(prefix)) {
                                        jCoMapper.addChangingParameterNameWhen(strucutureName, prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addChangingParameterNameWhen(strucutureName, prefix);
                                    }
                                }
                                Map<String, String> fieldMap = new HashMap<>();
                                while (true) {
                                    nextEvent = reader.nextEvent();
                                    if (nextEvent.isEndElement()) {
                                        EndElement ene = nextEvent.asEndElement();
                                        if (STRUCTURE.equals(ene.getName().getLocalPart())) {
                                            break;
                                        }
                                        continue;
                                    }
                                    if (nextEvent.isStartElement()) {
                                        StartElement innerSte2 = nextEvent.asStartElement();
                                        String innerEleName2 = innerSte2.getName().getLocalPart();
                                        if (FIELD.equals(innerEleName2)) {
                                            String[] attr = null;
                                            if (!when) {
                                                attr = getFromTo(innerSte2);
                                            } else {
                                                attr = getNameSet(innerSte2);
                                                if (SUCCESS_PREFIX.equals(prefix)) {
                                                    jCoMapper.addChangingParameterFieldNameWhen(strucutureName, attr[0], prefix);
                                                } else if ((FAILED_PREFIX.equals(prefix))) {
                                                    jCoMapper.addChangingParameterFieldNameWhen(strucutureName, attr[0], prefix);
                                                } else if (DEFAULT_PREFIX.equals(prefix)) {
                                                    jCoMapper.addChangingParameterFieldNameWhen(strucutureName, attr[0], prefix);
                                                }
                                            }
                                            fieldMap.put(attr[0], attr[1]);
                                        }
                                    }
                                }
                                changingParameterFieldMap.put(prefix + strucutureName, fieldMap);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setStructureMapper(XMLEventReader reader, JCoRecordMapper jCoMapper, boolean when, String prefix) throws XMLStreamException {
        Map<String, String> importStructureNameMap = new HashMap<>();
        Map<String, Map<String, String>> importStructureFieldMap = new HashMap<>();
        Map<String, String> exportStructureNameMap = new HashMap<>();
        Map<String, Map<String, String>> exportStructureFieldMap = new HashMap<>();

        while (true) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isEndElement()) {
                EndElement ene = nextEvent.asEndElement();
                if (STRUCTURES.equals(ene.getName().getLocalPart())) {
                    if (!importStructureNameMap.isEmpty()) {
                        jCoMapper.addImportStructureNameMap(importStructureNameMap);
                    }
                    if (!importStructureFieldMap.isEmpty()) {
                        jCoMapper.addImportStructureFieldMap(importStructureFieldMap);
                    }
                    if (!exportStructureNameMap.isEmpty()) {
                        jCoMapper.addExportStructureNameMap(exportStructureNameMap);
                    }
                    if (!exportStructureFieldMap.isEmpty()) {
                        jCoMapper.addExportStructureFieldMap(exportStructureFieldMap);
                    }
                    break;
                }
                continue;
            }

            if (nextEvent.isStartElement()) {
                StartElement ste = nextEvent.asStartElement();
                String elementName = ste.getName().getLocalPart();

                if (IMPORT.equals(elementName)) {
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ene = nextEvent.asEndElement();
                            if (IMPORT.equals(ene.getName().getLocalPart())) {
                                break;
                            }
                            continue;
                        }

                        if (nextEvent.isStartElement()) {
                            StartElement innerSte = nextEvent.asStartElement();
                            String innerEleName = innerSte.getName().getLocalPart();
                            if (FIELD.equals(innerEleName)) {
                                String[] attr = null;
                                if (!when) {
                                    attr = getFromTo(innerSte);
                                } else {
                                    attr = getNameSet(innerSte);
                                }
                                importStructureNameMap.put(prefix + attr[0], attr[1]);
                            }
                            if (STRUCTURE.equals(innerEleName)) {
                                Attribute structureNameAttr = innerSte.getAttributeByName(new QName(NAME));
                                if (structureNameAttr == null) {
                                    throw new IllegalStateException("structure name attribute is null");
                                }
                                String strucutureName = structureNameAttr.getValue();
                                if (when) {
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addImportStructureNameWhen(strucutureName, prefix);
                                    } else if (FAILED_PREFIX.equals(prefix)) {
                                        jCoMapper.addImportStructureNameWhen(strucutureName, prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addImportStructureNameWhen(strucutureName, prefix);
                                    }
                                }
                                Map<String, String> fieldMap = new HashMap<>();
                                while (true) {
                                    nextEvent = reader.nextEvent();
                                    if (nextEvent.isEndElement()) {
                                        EndElement ene = nextEvent.asEndElement();
                                        if (STRUCTURE.equals(ene.getName().getLocalPart())) {
                                            break;
                                        }
                                        continue;
                                    }
                                    if (nextEvent.isStartElement()) {
                                        StartElement innerSte2 = nextEvent.asStartElement();
                                        String innerEleName2 = innerSte2.getName().getLocalPart();
                                        if (FIELD.equals(innerEleName2)) {
                                            String[] attr = null;
                                            if (!when) {
                                                attr = getFromTo(innerSte2);
                                            } else {
                                                attr = getNameSet(innerSte2);
                                                if (SUCCESS_PREFIX.equals(prefix)) {
                                                    jCoMapper.addImportStructureFieldNameWhen(strucutureName, attr[0], prefix);
                                                } else if (FAILED_PREFIX.equals(prefix)) {
                                                    jCoMapper.addImportStructureFieldNameWhen(strucutureName, attr[0], prefix);
                                                } else if (DEFAULT_PREFIX.equals(prefix)) {
                                                    jCoMapper.addImportStructureFieldNameWhen(strucutureName, attr[0], prefix);
                                                }
                                            }
                                            fieldMap.put(attr[0], attr[1]);
                                        }
                                    }
                                }
                                importStructureFieldMap.put(prefix + strucutureName, fieldMap);
                            }

                        }
                    }
                }

                if (EXPORT.equals(elementName)) {
                    while (true) {
                        nextEvent = reader.nextEvent();
                        if (nextEvent.isEndElement()) {
                            EndElement ene = nextEvent.asEndElement();
                            if (EXPORT.equals(ene.getName().getLocalPart())) {
                                break;
                            }
                            continue;
                        }

                        if (nextEvent.isStartElement()) {
                            StartElement innerSte = nextEvent.asStartElement();
                            String innerEleName = innerSte.getName().getLocalPart();
                            if (FIELD.equals(innerEleName)) {
                                String[] attr = null;
                                if (!when) {
                                    attr = getFromTo(innerSte);
                                } else {
                                    attr = getNameSet(innerSte);
                                }
                                exportStructureNameMap.put(prefix + attr[0], attr[1]);
                            }
                            if (STRUCTURE.equals(innerEleName)) {
                                Attribute structureNameAttr = innerSte.getAttributeByName(new QName(NAME));
                                if (structureNameAttr == null) {
                                    throw new IllegalStateException("structure name attribute is null");
                                }
                                String strucutureName = structureNameAttr.getValue();
                                if (when) {
                                    if (SUCCESS_PREFIX.equals(prefix)) {
                                        jCoMapper.addExportStructureNameWhen(strucutureName, prefix);
                                    } else if (FAILED_PREFIX.equals(prefix)) {
                                        jCoMapper.addExportStructureNameWhen(strucutureName, prefix);
                                    } else if (DEFAULT_PREFIX.equals(prefix)) {
                                        jCoMapper.addExportStructureNameWhen(strucutureName, prefix);
                                    }
                                }
                                Map<String, String> fieldMap = new HashMap<>();
                                while (true) {
                                    nextEvent = reader.nextEvent();
                                    if (nextEvent.isEndElement()) {
                                        EndElement ene = nextEvent.asEndElement();
                                        if (STRUCTURE.equals(ene.getName().getLocalPart())) {
                                            break;
                                        }
                                        continue;
                                    }
                                    if (nextEvent.isStartElement()) {
                                        StartElement innerSte2 = nextEvent.asStartElement();
                                        String innerEleName2 = innerSte2.getName().getLocalPart();
                                        if (FIELD.equals(innerEleName2)) {
                                            String[] attr = null;
                                            if (!when) {
                                                attr = getFromTo(innerSte2);
                                            } else {
                                                attr = getNameSet(innerSte2);
                                                if (SUCCESS_PREFIX.equals(prefix)) {
                                                    jCoMapper.addExportStructureFieldNameWhen(strucutureName, attr[0], prefix);
                                                } else if (FAILED_PREFIX.equals(prefix)) {
                                                    jCoMapper.addExportStructureFieldNameWhen(strucutureName, attr[0], prefix);
                                                } else if (DEFAULT_PREFIX.equals(prefix)) {
                                                    jCoMapper.addExportStructureFieldNameWhen(strucutureName, attr[0], prefix);
                                                }
                                            }
                                            fieldMap.put(attr[0], attr[1]);
                                        }
                                    }
                                }
                                exportStructureFieldMap.put(prefix + strucutureName, fieldMap);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setWhenMapper(XMLEventReader reader, JCoRecordMapper jCoMapper, String prefix) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isEndElement()) {
                EndElement ene = nextEvent.asEndElement();
                if (WHEN.equals(ene.getName().getLocalPart())) {
                    break;
                }
                continue;
            }

            if (nextEvent.isStartElement()) {
                StartElement ste = nextEvent.asStartElement();
                String innerEleName = ste.getName().getLocalPart();
                if (TABLES.equals(innerEleName)) {
                    setTableMapper(reader, jCoMapper, true, prefix);
                } else if (PARAMETERS.equals(innerEleName)) {
                    setParameterMapper(reader, jCoMapper, true, prefix);
                } else if (STRUCTURES.equals(innerEleName)) {
                    setStructureMapper(reader, jCoMapper, true, prefix);
                }
            }
        }
    }

    private void setFormatMapper(XMLEvent nextEvent, JCoRecordMapper jCoMapper) throws XMLStreamException {
        if (nextEvent.isStartElement()) {
            StartElement ste = nextEvent.asStartElement();
            Attribute typeAttr = ste.getAttributeByName(new QName(TYPE));
            if (typeAttr == null) {
                throw new IllegalStateException("format element must contain the attribute 'type'");
            }
            String type = typeAttr.getValue().toLowerCase();
            Attribute valueAttr = ste.getAttributeByName(new QName(VALUE));
            Attribute methodAttr = ste.getAttributeByName(new QName(METHOD));
            Attribute fieldAttr = ste.getAttributeByName(new QName(FIELD));
            Attribute locationAttr = ste.getAttributeByName(new QName("location"));
            Attribute fromAttr = ste.getAttributeByName(new QName(FROM));
            Attribute toAttr = ste.getAttributeByName(new QName(TO));
            Attribute beginAttr = ste.getAttributeByName(new QName(BEGIN));

            if ("date".equals(type)) {
                String value = valueAttr.getValue();
                jCoMapper.setDateFormat(value);
            }
            if ("time".equals(type)) {
                String value = valueAttr.getValue();
                jCoMapper.setTimeFormat(value);
            }
            if (CALL.equals(type)) {
                if (methodAttr == null) {
                    new IllegalStateException("The format element's type is 'call', but the 'method' attribute is not declared");
                }
                if (fieldAttr == null) {
                    new IllegalStateException("The format element's type is 'call', but the 'field' attribute is not declared");
                }
                String fieldName = fieldAttr.getValue();
                if (locationAttr == null) {
                    new IllegalStateException("The format element's type is 'call', but the 'location' attribute is not declared");
                }
                JCoCallableValueMapper valueMapper = new JCoCallableValueMapper();
                valueMapper.setMethod(methodAttr.getValue());
                valueMapper.setField(fieldAttr.getValue());
                valueMapper.setLocation(locationAttr.getValue());
                if (fromAttr != null) {
                    valueMapper.setFrom(fromAttr.getValue());
                }
                if (toAttr != null) {
                    log.info("TO>>{}", toAttr.getValue());
                    valueMapper.setTo(toAttr.getValue());
                }
                if (beginAttr != null) {
                    valueMapper.setBegin(beginAttr.getValue());
                }
                jCoMapper.addCallableValueMapper(fieldName, valueMapper, valueMapper.getLocationType());
            }

        }
    }

    private String[] getFromTo(StartElement ste) throws IllegalStateException{
        String[] fromTo = new String[2];
        Attribute fromAttr = ste.getAttributeByName(new QName(FROM));
        Attribute toAttr = ste.getAttributeByName(new QName(TO));
        if (fromAttr == null || toAttr == null) {
            throw new IllegalStateException("The element attribute is invalid. 'from' or 'to' is null");
        }
        fromTo[0] = fromAttr.getValue();
        fromTo[1] = toAttr.getValue();
        return fromTo;
    }

    private String[] getNameSet(StartElement ste) throws IllegalStateException{
        String[] nameSet = new String[2];
        Attribute nameAttr = ste.getAttributeByName(new QName(NAME));
        Attribute setAttr = ste.getAttributeByName(new QName(SET));
        if (nameAttr == null || setAttr == null) {
            throw new IllegalStateException("The element attribute is invalid. 'name' or 'set' is null");
        }
        nameSet[0] = nameAttr.getValue();
        nameSet[1] = setAttr.getValue();
        return nameSet;
    }

    private String[] getTypeValue(StartElement ste) throws IllegalStateException{
        String[] typeValue = new String[2];
        Attribute typeAttr = ste.getAttributeByName(new QName(TYPE));
        Attribute valueAttr = ste.getAttributeByName(new QName(VALUE));
        if (typeAttr == null || valueAttr == null) {
            throw new IllegalStateException("The element attribute is invalid. 'type' or 'value' is null");
        }
        typeValue[0] = typeAttr.getValue();
        typeValue[1] = valueAttr.getValue();
        return typeValue;
    }
}
