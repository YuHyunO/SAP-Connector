# SAP Connector

SAP JCo(Java Connector) 연결 지원 모듈

## 목차

- [개요](#개요)
- [기술 스택 및 요구사항](#기술-스택-및-요구사항)
- [설치 및 설정](#설치-및-설정)
- [프로젝트 구조](#프로젝트-구조)
- [사용 예제](#사용-예제)
- [주요 API 문서](#주요-api-문서)
- [매퍼 XML 설정](#매퍼-xml-설정)
- [라이센스](#라이센스)

## 개요

SAP Connector는 SAP 시스템과의 통합을 위한 Java 라이브러리입니다. SAP JCo(Java Connector) 3.1.9를 기반으로 하며, SAP RFC Function 호출, Destination 관리, JCo Server 설정 등의 기능을 제공합니다.

### 주요 기능

- **클라이언트 연결 관리**: SAP Destination 생성 및 관리
- **서버 설정**: JCo Server 생성 및 설정
- **유틸리티 함수**: JCo Function, Table, Parameter, Structure 처리 유틸리티
- **필드 매핑**: XML 기반 필드/테이블 이름 매핑 지원
- **데이터 변환**: 날짜, 시간, 바이너리 데이터 자동 변환

### 사용 사례

- SAP RFC Function 호출
- SAP 시스템과의 데이터 동기화
- SAP JCo Server 구현
- SAP 데이터 매핑 및 변환

## 기술 스택 및 요구사항

### 필수 요구사항

- **Java**: JDK 8 이상
- **Maven**: 3.x 이상
- **SAP JCo**: 3.1.9 (라이브러리 파일 필요)

### 주요 의존성

- **SLF4J API**: 1.7.12
- **Log4j2**: 2.17.x (API, Core, SLF4J 구현)
- **Lombok**: 1.18.30
- **SAP JCo 3**: 3.1.9 (시스템 의존성)

## 설치 및 설정

### 1. Maven 의존성 추가

프로젝트의 `pom.xml`에 다음 의존성을 추가하세요:

```xml
<dependency>
    <groupId>lab.access</groupId>
    <artifactId>SAP-Connector</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. SAP JCo 라이브러리 설정

SAP JCo 3.1.9 라이브러리는 Maven 중앙 저장소에서 제공되지 않으므로, 수동으로 설정해야 합니다:

1. SAP JCo 3.1.9 JAR 파일을 프로젝트의 `lib` 디렉토리에 배치합니다.
2. `pom.xml`에 시스템 의존성으로 추가합니다:

```xml
<dependency>
    <groupId>sapjco3</groupId>
    <artifactId>sapjco3</artifactId>
    <version>3.1.9</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/sapjco3.jar</systemPath>
</dependency>
```

**참고**: SAP JCo 라이브러리는 SAP에서 라이센스가 필요하며, 공식 SAP 웹사이트에서 다운로드할 수 있습니다.

### 3. 네이티브 라이브러리 설정

SAP JCo는 플랫폼별 네이티브 라이브러리(Windows: `sapjco3.dll`, Linux: `libsapjco3.so`)가 필요합니다. 이 파일들을 시스템 PATH 또는 Java 라이브러리 경로에 배치해야 합니다.

### 4. 빌드

```bash
mvn clean install
```

## 프로젝트 구조

```
src/main/java/lab/access/sap/
├── client/                    # JCo 클라이언트 연결 관리
│   ├── JCoDestinationAccessor.java
│   ├── SAPJCoDestinationFactory.java
│   ├── SAPJCoDestinationProvider.java
│   └── ...
├── server/                    # JCo 서버 설정 및 관리
│   ├── JCoServerPropsAccessor.java
│   ├── SAPJCoServerFactory.java
│   ├── SAPJCoServerProvider.java
│   └── ...
├── util/                      # 유틸리티 클래스
│   ├── JCoUtil.java
│   ├── JCoRecordMapper.java
│   ├── JCoRecordMapperFactory.java
│   └── ...
└── JCoDataType.java          # JCo 데이터 타입 상수

src/main/resources/
└── JCO_Mapper.xml            # 매퍼 XML 예제
```

### 패키지 설명

- **`lab.access.sap.client`**: SAP 클라이언트 연결 및 Destination 관리
- **`lab.access.sap.server`**: SAP JCo Server 설정 및 관리
- **`lab.access.sap.util`**: JCo Function 처리 유틸리티 및 매퍼
- **`lab.access.sap`**: 공통 데이터 타입 및 상수

## 사용 예제

### 클라이언트 연결 예제

#### 1. Destination Factory 사용

```java
import lab.access.sap.client.SAPJCoDestinationFactory;
import lab.access.sap.client.SAPJCoDestinationProvider;
import lab.access.sap.client.JCoDestinationAccessor;
import java.util.Properties;

// Destination Factory 생성
SAPJCoDestinationFactory factory = new SAPJCoDestinationFactory();
factory.setClient_ashost("sap-server.example.com");
factory.setClient_sysnr("00");
factory.setClient_client("100");
factory.setClient_user("USERNAME");
factory.setClient_passwd("PASSWORD");
factory.setClient_lang("EN");

// Properties로 변환
Properties props = factory.toProperties();

// Destination Accessor 구현
JCoDestinationAccessor accessor = new JCoDestinationAccessor() {
    @Override
    public Properties getDesitinationProperties(String name) {
        return props;
    }
};

// Provider 등록
SAPJCoDestinationProvider provider = new SAPJCoDestinationProvider();
provider.setDestinationAccessor(accessor);
provider.register();
```

#### 2. Destination을 사용한 Function 호출

```java
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import lab.access.sap.util.JCoUtil;

// Destination 가져오기
JCoDestination destination = JCoDestinationManager.getDestination("DESTINATION_NAME");

// Function 가져오기
JCoFunction function = destination.getRepository().getFunction("RFC_FUNCTION_NAME");

// Import Parameter 설정
Map<String, Object> importParams = new HashMap<>();
importParams.put("PARAM1", "VALUE1");
importParams.put("PARAM2", "VALUE2");
JCoUtil.setJCoImportParameters(function, importParams, false);

// Function 실행
function.execute(destination);

// Export Parameter 읽기
Map<String, Object> exportParams = JCoUtil.getJCoExportParameters(function);
```

### 서버 설정 예제

```java
import lab.access.sap.server.SAPJCoServerFactory;
import lab.access.sap.server.SAPJCoServerProvider;
import lab.access.sap.server.JCoServerPropsAccessor;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFactory;
import java.util.Properties;

// Server Factory 생성
SAPJCoServerFactory serverFactory = new SAPJCoServerFactory();
serverFactory.setServer_gwhost("gateway-host");
serverFactory.setServer_gwserv("3300");
serverFactory.setServer_progid("PROGRAM_ID");
serverFactory.setServer_connection_count("5");

// Properties로 변환
Properties serverProps = serverFactory.toProperties();

// Server Properties Accessor 구현
JCoServerPropsAccessor accessor = new JCoServerPropsAccessor() {
    @Override
    public Properties getServerProperties(String name) {
        return serverProps;
    }
};

// Provider 등록
SAPJCoServerProvider provider = new SAPJCoServerProvider();
provider.setServerPropsAccessor(accessor);
provider.register();

// Server 생성 및 시작
JCoServer server = JCoServerFactory.getServer("SERVER_NAME");
server.start();
```

### 유틸리티 사용 예제

#### 1. Table 데이터 처리

```java
import lab.access.sap.util.JCoUtil;
import java.util.List;
import java.util.Map;

// Table 데이터 읽기
Map<String, List<Map<String, Object>>> tables = JCoUtil.getJCoTables(function);

// Table 데이터 설정
List<Map<String, Object>> tableData = new ArrayList<>();
Map<String, Object> row1 = new HashMap<>();
row1.put("FIELD1", "VALUE1");
row1.put("FIELD2", "VALUE2");
tableData.add(row1);

JCoUtil.setJCoTable(function, "TABLE_NAME", tableData, false);
```

#### 2. Structure 데이터 처리

```java
// Import Structure 읽기
Map<String, Map<String, Object>> importStructures = 
    JCoUtil.getJCoImportStructures(function);

// Export Structure 읽기
Map<String, Map<String, Object>> exportStructures = 
    JCoUtil.getJCoExportStructures(function);

// Structure 설정
Map<String, Object> structureData = new HashMap<>();
structureData.put("FIELD1", "VALUE1");
JCoUtil.setJCoImportStructure(function, "STRUCTURE_NAME", structureData, false);
```

#### 3. Mapper를 사용한 필드 매핑

```java
import lab.access.sap.util.JCoRecordMapper;
import lab.access.sap.util.JCoRecordMapperFactory;
import java.util.Arrays;

// Mapper 파일 로드
JCoRecordMapperFactory mapperFactory = new JCoRecordMapperFactory();
mapperFactory.setMapperFileLocation(
    Arrays.asList("src/main/resources/JCO_Mapper.xml")
);

// Mapper 가져오기
JCoRecordMapper mapper = JCoRecordMapperFactory.getMapper("MyMapper1");

// Mapper를 사용하여 데이터 읽기
Map<String, Object> exportParams = 
    JCoUtil.getJCoExportParameters(function, mapper);
Map<String, List<Map<String, Object>>> tables = 
    JCoUtil.getJCoTables(function, mapper);
```

## 주요 API 문서

### Client 패키지

#### `JCoDestinationAccessor`

Destination 속성에 접근하기 위한 인터페이스입니다.

```java
public interface JCoDestinationAccessor {
    Properties getDesitinationProperties(String name);
}
```

#### `SAPJCoDestinationFactory`

Destination 속성을 설정하고 Properties로 변환하는 팩토리 클래스입니다.

**주요 메서드:**
- `toProperties()`: 설정된 속성을 Properties 객체로 변환

**주요 속성:**
- `client_ashost`: SAP 서버 호스트명
- `client_sysnr`: SAP 시스템 번호
- `client_client`: SAP 클라이언트 번호
- `client_user`: 사용자명
- `client_passwd`: 비밀번호
- `client_lang`: 언어 코드
- `destination_pool_capacity`: 연결 풀 용량
- `destination_peak_limit`: 최대 연결 수

#### `SAPJCoDestinationProvider`

Destination Data Provider를 구현한 클래스입니다.

**주요 메서드:**
- `register()`: Provider를 JCo Environment에 등록
- `setDestinationAccessor(JCoDestinationAccessor)`: Accessor 설정
- `getInstance()`: 싱글톤 인스턴스 반환

### Server 패키지

#### `SAPJCoServerFactory`

JCo Server 속성을 설정하는 팩토리 클래스입니다.

**주요 메서드:**
- `toProperties()`: 설정된 속성을 Properties 객체로 변환

**주요 속성:**
- `server_gwhost`: Gateway 호스트명
- `server_gwserv`: Gateway 서비스 포트
- `server_progid`: 프로그램 ID
- `server_connection_count`: 연결 수
- `server_worker_thread_count`: 워커 스레드 수

#### `SAPJCoServerProvider`

Server Data Provider를 구현한 클래스입니다.

**주요 메서드:**
- `register()`: Provider를 JCo Environment에 등록
- `setServerPropsAccessor(JCoServerPropsAccessor)`: Accessor 설정
- `getInstance()`: 싱글톤 인스턴스 반환

### Util 패키지

#### `JCoUtil`

JCo Function 처리 유틸리티 클래스입니다.

**주요 메서드:**

**데이터 읽기:**
- `getJCoTables(JCoFunction)`: Table 파라미터 읽기
- `getJCoImportParameters(JCoFunction)`: Import 파라미터 읽기
- `getJCoExportParameters(JCoFunction)`: Export 파라미터 읽기
- `getJCoImportStructures(JCoFunction)`: Import Structure 읽기
- `getJCoExportStructures(JCoFunction)`: Export Structure 읽기
- `getJCoChangingParameters(JCoFunction)`: Changing 파라미터 읽기

**데이터 설정:**
- `setJCoTable(JCoFunction, String, List<Map<String, Object>>, boolean)`: Table 설정
- `setJCoImportParameters(JCoFunction, Map<String, Object>, boolean)`: Import 파라미터 설정
- `setJCoExportParameters(JCoFunction, Map<String, Object>, boolean)`: Export 파라미터 설정
- `setJCoImportStructure(JCoFunction, String, Map<String, Object>, boolean)`: Import Structure 설정
- `setJCoExportStructure(JCoFunction, String, Map<String, Object>, boolean)`: Export Structure 설정
- `setJCoChangingParameter(JCoFunction, Map<String, Object>, boolean)`: Changing 파라미터 설정

#### `JCoRecordMapper`

필드 및 테이블 이름 매핑을 관리하는 클래스입니다.

**주요 메서드:**
- `mapTableName(String)`: 테이블 이름 매핑
- `mapTableFieldName(String, String)`: 테이블 필드 이름 매핑
- `mapImportParameterName(String)`: Import 파라미터 이름 매핑
- `mapExportParameterName(String)`: Export 파라미터 이름 매핑
- `mapImportStructureName(String)`: Import Structure 이름 매핑
- `mapExportStructureName(String)`: Export Structure 이름 매핑

#### `JCoRecordMapperFactory`

XML 파일에서 Mapper를 생성하는 팩토리 클래스입니다.

**주요 메서드:**
- `setMapperFileLocation(List<String>)`: Mapper XML 파일 경로 설정
- `getMapper(String)`: ID로 Mapper 가져오기

## 매퍼 XML 설정

매퍼 XML 파일을 사용하여 SAP 필드 이름을 애플리케이션 필드 이름으로 매핑할 수 있습니다.

### 기본 구조

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcoMappers>
    <jcoMapper id="MyMapper1">
        <!-- 테이블 매핑 -->
        <tables>
            <name from="SAP_TABLE" to="APP_TABLE"/>
            <table name="SAP_TABLE">
                <field from="SAP_FIELD1" to="APP_FIELD1"/>
                <field from="SAP_FIELD2" to="APP_FIELD2"/>
            </table>
        </tables>

        <!-- 파라미터 매핑 -->
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

        <!-- Structure 매핑 -->
        <structures>
            <import>
                <structure name="SAP_STRUCTURE">
                    <field from="SAP_FIELD1" to="APP_FIELD1"/>
                </structure>
            </import>
        </structures>

        <!-- 날짜/시간 포맷 설정 -->
        <format type="date" value="yyyyMMdd"/>
        <format type="time" value="HHmmss"/>
    </jcoMapper>
</jcoMappers>
```

### 조건부 매핑 (When 절)

성공/실패/기본값에 따라 다른 매핑을 적용할 수 있습니다:

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

### 사용 예제

```java
// Mapper 파일 로드
JCoRecordMapperFactory mapperFactory = new JCoRecordMapperFactory();
mapperFactory.setMapperFileLocation(
    Arrays.asList("src/main/resources/JCO_Mapper.xml")
);

// Mapper 가져오기
JCoRecordMapper mapper = JCoRecordMapperFactory.getMapper("MyMapper1");

// Mapper를 사용하여 데이터 읽기
Map<String, Object> params = 
    JCoUtil.getJCoExportParameters(function, mapper);
```

## 트러블슈팅

### JCo로 가져온 데이터가 잘리는 경우

JCo를 통해 SAP에서 데이터를 가져올 때 데이터가 잘리는 현상이 발생하는 경우, SAP 시스템의 통신 타입 설정을 확인해야 합니다.

**해결 방법:**

1. SAP GUI에 로그인합니다.
2. 트랜잭션 코드 `SM59` (RFC Destinations)를 실행합니다.
3. 해당 RFC Destination을 선택하고 **MDMP & Unicode** 탭으로 이동합니다.
4. **Communication Type with Target System** 항목을 확인합니다.
   - 이 설정이 올바르게 구성되지 않은 경우 데이터 전송 시 문자 인코딩 문제로 인해 데이터가 잘릴 수 있습니다.
   - Unicode 시스템과의 통신인 경우 적절한 Unicode 설정이 필요합니다.
5. 필요시 SAP 시스템 관리자에게 올바른 통신 타입 설정을 요청하세요.


## 라이센스

이 프로젝트는 MIT License 하에 배포됩니다. 자유롭게 사용, 수정, 배포할 수 있습니다.

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

**주의**: SAP JCo 라이브러리는 SAP의 라이센스 정책을 따릅니다. 이 프로젝트의 라이센스는 SAP JCo 라이브러리 자체에는 적용되지 않으며, 사용 시 SAP의 라이센스 약관을 확인하시기 바랍니다.

## 기여

이슈 리포트나 기능 제안은 GitHub Issues를 통해 제출해 주세요.

## 참고 자료

이 프로젝트는 다음 SAP JCo 공식 문서를 참고하여 개발되었습니다:

- `jco_30_documentation_en.pdf`! - SAP Java Connector Release 3.0 문서
- `jco_31_documentation.pdf` - SAP Java Connector Release 3.1 문서
