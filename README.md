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

## Options

The loader:

```
Usage:
	CommonCSVLoader <file.csv | file.tsv | file.txt> [options]

Options:

-decimal <num>
	The maximum number of digits to print after the decimal
	place for numeric values (default: 6)
-F <DEFAULT|EXCEL|INFORMIX|INFORMIXCSV|MYSQL|ORACLE|POSTGRESQLCSV|POSTGRESQLTXT|RFC4180|TDF>
	The CSV format to use
	(default: DEFAULT)
-use-custom-field-separator
	Whether to use custom field separator
	(default: no)
-custom-field-separator <separator-char>
	The custom field separator
	(default: ,)
-use-custom-quote-character
	Whether to use custom quote character
	(default: no)
-custom-quote-character <quote-char>
	The custom quote character
	(default: ")
-no-header
	Whether there is no header row in the spreadsheet
	(default: assumes header row present)
-nominal <range>
	The attribute range to treat as nominal
	(default: none)
-nominal-label-spec <nominal label spec>
	Optional specification of legal labels for nominal
	attributes. May be specified multiple times.
	The spec contains two parts separated by a ":".
	The first part can be a range of attribute indexes or
	a comma-separated list off attruibute names;
	the second part is a comma-separated list of labels. E.g.:
	"1,2,4-6:red,green,blue" or "att1,att2:red,green,blue"
-string <range>
	The attribute range to treat as string
	(default: none)
-date <range>
	The attribute range to treat as date
	(default: none)
-date-format <format>
	The format to use for parsing the date attribute(s)
	see: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html
	(default: yyyy-MM-dd'T'HH:mm:ss)
-missing-value <string>
	The string to interpret as missing value
	(default: '')
```

The saver:

```
CommonCSVSaver options:

-i <the input file>
	The input file
-o <the output file>
	The output file
-F <DEFAULT|EXCEL|INFORMIX|INFORMIXCSV|MYSQL|ORACLE|POSTGRESQLCSV|POSTGRESQLTXT|RFC4180|TDF>
	The CSV format to use
	(default: DEFAULT)
-use-custom-field-separator
	Whether to use custom field separator
	(default: no)
-custom-field-separator <separator-char>
	The custom field separator
	(default: ,)
-use-custom-quote-character
	Whether to use custom quote character
	(default: no)
-custom-quote-character <quote-char>
	The custom quote character
	(default: ")
-use-custom-quote-mode
	Whether to use custom quote mode
	(default: no)
-custom-quote-mode <ALL|ALL_NON_NULL|MINIMAL|NON_NUMERIC|NONE>
	The custom quote mode
	(default: MINIMAL)
-use-custom-escape-character
	Whether to use custom escape character
	(default: no)
-custom-escape-character <escape-char>
	The custom escape character
	(default: )
-no-header
	Whether to suppress output of header row
	(default: outputs header)
```


## Releases

* [2020.12.30](https://github.com/fracpete/common-csv-weka-package/releases/download/v2020.12.30/common-csv-2020.12.30.zip)
* [2020.12.29](https://github.com/fracpete/common-csv-weka-package/releases/download/v2020.12.29/common-csv-2020.12.29.zip)
* [2020.11.29](https://github.com/fracpete/common-csv-weka-package/releases/download/v2020.11.29/common-csv-2020.11.29.zip)


## Maven

Use the following dependency in your `pom.xml`:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>common-csv-weka-package</artifactId>
      <version>2020.12.30</version>
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


