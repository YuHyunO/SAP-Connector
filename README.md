# SAP Connector

SAP JCo (Java Connector) connection support module

## Table of Contents

- [Overview](#overview)
- [Technology Stack and Requirements](#technology-stack-and-requirements)
- [Installation and Setup](#installation-and-setup)
- [Project Structure](#project-structure)
- [Usage Examples](#usage-examples)
- [API Documentation](#api-documentation)
- [Mapper XML Configuration](#mapper-xml-configuration)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Overview

SAP Connector is a Java library for integrating with SAP systems. Based on SAP JCo (Java Connector) 3.1.9, it provides functionality for SAP RFC Function calls, Destination management, JCo Server configuration, and more.

### Key Features

- **Client Connection Management**: Create and manage SAP Destinations
- **Server Configuration**: Create and configure JCo Servers
- **Utility Functions**: Utilities for processing JCo Functions, Tables, Parameters, and Structures
- **Field Mapping**: XML-based field/table name mapping support
- **Data Conversion**: Automatic conversion of date, time, and binary data

### Use Cases

- SAP RFC Function calls
- Data synchronization with SAP systems
- SAP JCo Server implementation
- SAP data mapping and transformation

## Technology Stack and Requirements

### Prerequisites

- **Java**: JDK 8 or higher
- **Maven**: 3.x or higher
- **SAP JCo**: 3.1.9 (library file required)

### Key Dependencies

- **SLF4J API**: 1.7.12
- **Log4j2**: 2.17.x (API, Core, SLF4J implementation)
- **Lombok**: 1.18.30
- **SAP JCo 3**: 3.1.9 (system dependency)

## Installation and Setup

### 1. Add Maven Dependency

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>lab.access</groupId>
    <artifactId>SAP-Connector</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. SAP JCo Library Setup

SAP JCo 3.1.9 library is not available in Maven Central Repository, so it must be configured manually:

1. Place the SAP JCo 3.1.9 JAR file in your project's `lib` directory.
2. Add it as a system dependency in `pom.xml`:

```xml
<dependency>
    <groupId>sapjco3</groupId>
    <artifactId>sapjco3</artifactId>
    <version>3.1.9</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/sapjco3.jar</systemPath>
</dependency>
```

**Note**: SAP JCo library requires a license from SAP and can be downloaded from the official SAP website.

### 3. Native Library Setup

SAP JCo requires platform-specific native libraries (Windows: `sapjco3.dll`, Linux: `libsapjco3.so`). These files must be placed in the system PATH or Java library path.

### 4. Build

```bash
mvn clean install
```

## Project Structure

```
src/main/java/lab/access/sap/
├── client/                    # JCo client connection management
│   ├── JCoDestinationAccessor.java
│   ├── SAPJCoDestinationFactory.java
│   ├── SAPJCoDestinationProvider.java
│   └── ...
├── server/                    # JCo server configuration and management
│   ├── JCoServerPropsAccessor.java
│   ├── SAPJCoServerFactory.java
│   ├── SAPJCoServerProvider.java
│   └── ...
├── util/                      # Utility classes
│   ├── JCoUtil.java
│   ├── JCoRecordMapper.java
│   ├── JCoRecordMapperFactory.java
│   └── ...
└── JCoDataType.java          # JCo data type constants

src/main/resources/
└── JCO_Mapper.xml            # Mapper XML example
```

### Package Description

- **`lab.access.sap.client`**: SAP client connection and Destination management
- **`lab.access.sap.server`**: SAP JCo Server configuration and management
- **`lab.access.sap.util`**: JCo Function processing utilities and mappers
- **`lab.access.sap`**: Common data types and constants

## Usage Examples

### Client Connection Examples

#### 1. Spring Framework Configuration (Recommended)

When using Spring Framework, you can define beans in an XML configuration file as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">
<beans>
    <!-- SAP JCo Destination setting -->
    <bean class="lab.access.sap.client.SAPJCoDestinationProvider" init-method="register">
        <property name="destinationAccessor">
            <bean class="lab.access.sap.client.SingleInMemoryDestinationAccessor">
                <property name="destination" ref="SAP_DESTINATION"/>
            </bean>
        </property>
    </bean>
    
    <bean class="lab.access.sap.client.SAPJCoDestinationFactory" id="SAP_DESTINATION">
        <property name="client_ashost" value="sap-server.example.com"/>
        <property name="client_client" value="100"/>
        <property name="client_lang" value="KO"/>
        <property name="client_user" value="USERNAME"/>
        <property name="client_passwd" value="PASSWORD"/>
        <property name="destination_peak_limit" value="10"/>
        <property name="destination_pool_capacity" value="50"/>
        <property name="client_mshost" value="sap-server.example.com"/>
        <property name="client_msserv" value="3600"/>
        <property name="client_r3name" value="SYSTEM_NAME"/>
        <property name="client_group" value="GROUP_NAME"/>
    </bean>
</beans>
```

**Note**: 
- `SingleInMemoryDestinationAccessor` is an Accessor that stores a single Destination in memory.
- Use `init-method="register"` to automatically register the Provider when the Spring container initializes.

#### 2. Direct Java Code Configuration

If you are not using Spring Framework, you can configure it directly in Java code as follows:

```java
import lab.access.sap.client.SAPJCoDestinationFactory;
import lab.access.sap.client.SAPJCoDestinationProvider;
import lab.access.sap.client.SingleInMemoryDestinationAccessor;

// Create Destination Factory
SAPJCoDestinationFactory factory = new SAPJCoDestinationFactory();
factory.setClient_ashost("sap-server.example.com");
factory.setClient_client("100");
factory.setClient_lang("KO");
factory.setClient_user("USERNAME");
factory.setClient_passwd("PASSWORD");
factory.setDestination_peak_limit("10");
factory.setDestination_pool_capacity("50");
factory.setClient_mshost("sap-server.example.com");
factory.setClient_msserv("3600");
factory.setClient_r3name("SYSTEM_NAME");
factory.setClient_group("GROUP_NAME");

// Use SingleInMemoryDestinationAccessor
SingleInMemoryDestinationAccessor accessor = new SingleInMemoryDestinationAccessor();
accessor.setDestination(factory);

// Register Provider
SAPJCoDestinationProvider provider = new SAPJCoDestinationProvider();
provider.setDestinationAccessor(accessor);
provider.register();
```

#### 2. Function Call Using Destination

```java
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import lab.access.sap.util.JCoUtil;

// Get Destination
JCoDestination destination = JCoDestinationManager.getDestination("DESTINATION_NAME");

// Get Function
JCoFunction function = destination.getRepository().getFunction("RFC_FUNCTION_NAME");

// Set Import Parameters
Map<String, Object> importParams = new HashMap<>();
importParams.put("PARAM1", "VALUE1");
importParams.put("PARAM2", "VALUE2");
JCoUtil.setJCoImportParameters(function, importParams, false);

// Execute Function
function.execute(destination);

// Read Export Parameters
Map<String, Object> exportParams = JCoUtil.getJCoExportParameters(function);
```

### Server Configuration Example

```java
import lab.access.sap.server.SAPJCoServerFactory;
import lab.access.sap.server.SAPJCoServerProvider;
import lab.access.sap.server.JCoServerPropsAccessor;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFactory;
import java.util.Properties;

// Create Server Factory
SAPJCoServerFactory serverFactory = new SAPJCoServerFactory();
serverFactory.setServer_gwhost("gateway-host");
serverFactory.setServer_gwserv("3300");
serverFactory.setServer_progid("PROGRAM_ID");
serverFactory.setServer_connection_count("5");

// Convert to Properties
Properties serverProps = serverFactory.toProperties();

// Implement Server Properties Accessor
JCoServerPropsAccessor accessor = new JCoServerPropsAccessor() {
    @Override
    public Properties getServerProperties(String name) {
        return serverProps;
    }
};

// Register Provider
SAPJCoServerProvider provider = new SAPJCoServerProvider();
provider.setServerPropsAccessor(accessor);
provider.register();

// Create and Start Server
JCoServer server = JCoServerFactory.getServer("SERVER_NAME");
server.start();
```

### Utility Usage Examples

#### 1. Table Data Processing

```java
import lab.access.sap.util.JCoUtil;
import java.util.List;
import java.util.Map;

// Read Table Data
Map<String, List<Map<String, Object>>> tables = JCoUtil.getJCoTables(function);

// Set Table Data
List<Map<String, Object>> tableData = new ArrayList<>();
Map<String, Object> row1 = new HashMap<>();
row1.put("FIELD1", "VALUE1");
row1.put("FIELD2", "VALUE2");
tableData.add(row1);

JCoUtil.setJCoTable(function, "TABLE_NAME", tableData, false);
```

#### 2. Structure Data Processing

```java
// Read Import Structure
Map<String, Map<String, Object>> importStructures = 
    JCoUtil.getJCoImportStructures(function);

// Read Export Structure
Map<String, Map<String, Object>> exportStructures = 
    JCoUtil.getJCoExportStructures(function);

// Set Structure
Map<String, Object> structureData = new HashMap<>();
structureData.put("FIELD1", "VALUE1");
JCoUtil.setJCoImportStructure(function, "STRUCTURE_NAME", structureData, false);
```

#### 3. Field Mapping Using Mapper

```java
import lab.access.sap.util.JCoRecordMapper;
import lab.access.sap.util.JCoRecordMapperFactory;
import java.util.Arrays;

// Load Mapper File
JCoRecordMapperFactory mapperFactory = new JCoRecordMapperFactory();
mapperFactory.setMapperFileLocation(
    Arrays.asList("src/main/resources/JCO_Mapper.xml")
);

// Get Mapper
JCoRecordMapper mapper = JCoRecordMapperFactory.getMapper("MyMapper1");

// Read Data Using Mapper
Map<String, Object> exportParams = 
    JCoUtil.getJCoExportParameters(function, mapper);
Map<String, List<Map<String, Object>>> tables = 
    JCoUtil.getJCoTables(function, mapper);
```

## API Documentation

### Client Package

#### `JCoDestinationAccessor`

Interface for accessing Destination properties.

```java
public interface JCoDestinationAccessor {
    Properties getDesitinationProperties(String name);
}
```

#### `SAPJCoDestinationFactory`

Factory class for setting Destination properties and converting them to Properties.

**Key Methods:**
- `toProperties()`: Converts configured properties to Properties object

**Key Properties:**
- `client_ashost`: SAP server hostname
- `client_sysnr`: SAP system number
- `client_client`: SAP client number
- `client_user`: Username
- `client_passwd`: Password
- `client_lang`: Language code
- `destination_pool_capacity`: Connection pool capacity
- `destination_peak_limit`: Maximum number of connections

#### `SAPJCoDestinationProvider`

Class implementing Destination Data Provider.

**Key Methods:**
- `register()`: Registers Provider to JCo Environment
- `setDestinationAccessor(JCoDestinationAccessor)`: Sets Accessor
- `getInstance()`: Returns singleton instance

### Server Package

#### `SAPJCoServerFactory`

Factory class for setting JCo Server properties.

**Key Methods:**
- `toProperties()`: Converts configured properties to Properties object

**Key Properties:**
- `server_gwhost`: Gateway hostname
- `server_gwserv`: Gateway service port
- `server_progid`: Program ID
- `server_connection_count`: Number of connections
- `server_worker_thread_count`: Number of worker threads

#### `SAPJCoServerProvider`

Class implementing Server Data Provider.

**Key Methods:**
- `register()`: Registers Provider to JCo Environment
- `setServerPropsAccessor(JCoServerPropsAccessor)`: Sets Accessor
- `getInstance()`: Returns singleton instance

### Util Package

#### `JCoUtil`

Utility class for processing JCo Functions.

**Key Methods:**

**Data Reading:**
- `getJCoTables(JCoFunction)`: Read Table parameters
- `getJCoImportParameters(JCoFunction)`: Read Import parameters
- `getJCoExportParameters(JCoFunction)`: Read Export parameters
- `getJCoImportStructures(JCoFunction)`: Read Import Structures
- `getJCoExportStructures(JCoFunction)`: Read Export Structures
- `getJCoChangingParameters(JCoFunction)`: Read Changing parameters

**Data Setting:**
- `setJCoTable(JCoFunction, String, List<Map<String, Object>>, boolean)`: Set Table
- `setJCoImportParameters(JCoFunction, Map<String, Object>, boolean)`: Set Import parameters
- `setJCoExportParameters(JCoFunction, Map<String, Object>, boolean)`: Set Export parameters
- `setJCoImportStructure(JCoFunction, String, Map<String, Object>, boolean)`: Set Import Structure
- `setJCoExportStructure(JCoFunction, String, Map<String, Object>, boolean)`: Set Export Structure
- `setJCoChangingParameter(JCoFunction, Map<String, Object>, boolean)`: Set Changing parameter

#### `JCoRecordMapper`

Class for managing field and table name mappings.

**Key Methods:**
- `mapTableName(String)`: Map table name
- `mapTableFieldName(String, String)`: Map table field name
- `mapImportParameterName(String)`: Map Import parameter name
- `mapExportParameterName(String)`: Map Export parameter name
- `mapImportStructureName(String)`: Map Import Structure name
- `mapExportStructureName(String)`: Map Export Structure name

#### `JCoRecordMapperFactory`

Factory class for creating Mappers from XML files.

**Key Methods:**
- `setMapperFileLocation(List<String>)`: Set Mapper XML file path
- `getMapper(String)`: Get Mapper by ID

## Mapper XML Configuration

Mapper XML files can be used to map SAP field names to application field names.

### Basic Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcoMappers>
    <jcoMapper id="MyMapper1">
        <!-- Table mapping -->
        <tables>
            <name from="SAP_TABLE" to="APP_TABLE"/>
            <table name="SAP_TABLE">
                <field from="SAP_FIELD1" to="APP_FIELD1"/>
                <field from="SAP_FIELD2" to="APP_FIELD2"/>
            </table>
        </tables>

        <!-- Parameter mapping -->
        <parameters>
            <import>
                <field from="SAP_PARAM1" to="APP_PARAM1"/>
            </import>
            <export>
                <field from="SAP_PARAM2" to="APP_PARAM2"/>
            </export>
            <changing>
                <field from="SAP_PARAM3" to="APP_PARAM3"/>
            </changing>
        </parameters>

        <!-- Structure mapping -->
        <structures>
            <import>
                <structure name="SAP_STRUCTURE">
                    <field from="SAP_FIELD1" to="APP_FIELD1"/>
                </structure>
            </import>
        </structures>

        <!-- Date/Time format settings -->
        <format type="date" value="yyyyMMdd"/>
        <format type="time" value="HHmmss"/>
    </jcoMapper>
</jcoMappers>
```

### Conditional Mapping (When Clause)

Different mappings can be applied based on success/failure/default values:

```xml
<jcoMapper id="MyMapper1">
    <when if="success">
        <tables>
            <table name="RESULT_TABLE">
                <field name="STATUS" set="SUCCESS"/>
            </table>
        </tables>
    </when>
    <when if="failed">
        <tables>
            <table name="RESULT_TABLE">
                <field name="STATUS" set="FAILED"/>
            </table>
        </tables>
    </when>
</jcoMapper>
```

### Usage Example

```java
// Load Mapper File
JCoRecordMapperFactory mapperFactory = new JCoRecordMapperFactory();
mapperFactory.setMapperFileLocation(
    Arrays.asList("src/main/resources/JCO_Mapper.xml")
);

// Get Mapper
JCoRecordMapper mapper = JCoRecordMapperFactory.getMapper("MyMapper1");

// Read Data Using Mapper
Map<String, Object> params = 
    JCoUtil.getJCoExportParameters(function, mapper);
```

## Troubleshooting

### Data Truncation When Retrieving Data via JCo

When data retrieved from SAP via JCo is truncated, you need to check the communication type settings in the SAP system.

**Solution:**

1. Log in to SAP GUI.
2. Execute transaction code `SM59` (RFC Destinations).
3. Select the RFC Destination and navigate to the **MDMP & Unicode** tab.
4. Check the **Communication Type with Target System** field.
   - If this setting is not configured correctly, data may be truncated due to character encoding issues during data transmission.
   - For communication with Unicode systems, appropriate Unicode settings are required.
5. If necessary, request the SAP system administrator to configure the correct communication type.

## License

This project is distributed under the MIT License. You are free to use, modify, and distribute it.

### MIT License

```
MIT License

Copyright (c) 2024 Yuhyun O

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

**Note**: The SAP JCo library follows SAP's license policy. This project's license does not apply to the SAP JCo library itself. Please check SAP's license terms when using it.

## Contributing

Please submit issue reports or feature suggestions through GitHub Issues.

## References

This project was developed with reference to the following official SAP JCo documentation:

- `jco_30_documentation_en.pdf` - SAP Java Connector Release 3.0 Documentation
- `jco_31_documentation.pdf` - SAP Java Connector Release 3.1 Documentation

### Additional References

- [SAP JCo Official Documentation](https://help.sap.com/viewer/product/SAP_NETWEAVER_AS_ABAP/7.5/en-US)
- [SAP JCo Download](https://support.sap.com/en/my-support/software-downloads.html)

