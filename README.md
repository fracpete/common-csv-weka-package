# common-csv-weka-package

Weka package for loader and saver for common CSV formats, using the 
[Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) library.

Requires Weka 3.9.5+ or 3.9.x snapshots post revision 15656 (2020-11-29).

Supported formats:

* **DEFAULT** - Standard Comma Separated Value format, as for RFC4180 but allowing empty lines.
* **EXCEL** - The Microsoft Excel CSV format.
* **INFORMIX_UNLOAD** - Informix UNLOAD format used by the UNLOAD TO file_name operation.
* **INFORMIX_UNLOAD_CSV** - Informix CSV UNLOAD format used by the UNLOAD TO file_name operation (escaping is disabled.)
* **MYSQL** - The MySQL CSV format.
* **ORACLE** - Default Oracle format used by the SQL*Loader utility.
* **POSTGRESSQL_CSV** - Default PostgreSQL CSV format used by the COPY operation.
* **POSTGRESSQL_TEXT** - Default PostgreSQL text format used by the COPY operation.
* **RFC-4180** - The RFC-4180 format defined by RFC-4180.
* **TDF** - A tab delimited format.

## Releases

* [2020.11.29](https://github.com/fracpete/common-csv-weka-package/releases/download/v2020.11.29/common-csv-2020.11.29.zip)


## Maven

Use the following dependency in your `pom.xml`:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>common-csv-weka-package</artifactId>
      <version>2020.11.29</version>
      <type>jar</type>
      <exclusions>
        <exclusion>
          <groupId>nz.ac.waikato.cms.weka</groupId>
          <artifactId>weka-dev</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```


## How to use packages

For more information on how to install the package, see:

https://waikato.github.io/weka-wiki/packages/manager/


