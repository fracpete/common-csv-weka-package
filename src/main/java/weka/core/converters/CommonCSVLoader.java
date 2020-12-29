/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * CommonCSVLoader.java
 * Copyright (C) 2019-2020 FracPete
 *
 */

package weka.core.converters;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Reads files in common CSV formats.
 * For tab-delimited files, choose TDF as format.
 * For other formats, you can specify a custom field separator.
 *
 * @author FracPete (fracpete at gmail dot com)
 * @see Loader
 */
public class CommonCSVLoader
  extends AbstractFileLoader
  implements BatchConverter, URLSourcedLoader, OptionHandler {

  /** for serialization */
  private static final long serialVersionUID = 3764533621135196582L;

  /**
   * The types of attributes.
   */
  public enum AttributeType {
    NUMERIC,
    NOMINAL,
    STRING,
    DATE,
  }

  /** the default file extension */
  public final static String FILE_EXTENSION = ".csv";

  /** the text file extension */
  public final static String FILE_EXTENSION_TEXT = ".txt";

  /** the tab-delimited file extension */
  public final static String FILE_EXTENSION_TSV = ".tsv";

  /** the format. */
  protected int m_Format = CommonCsvFormats.DEFAULT;

  /** whether to use a custom field separator. */
  protected boolean m_UseCustomFieldSeparator = false;

  /** the default field separator. */
  public final static String DEFAULT_CUSTOM_FIELD_SEPARATOR = ",";

  /** the custom field separator to use. */
  protected String m_CustomFieldSeparator = DEFAULT_CUSTOM_FIELD_SEPARATOR;

  /** whether to use a custom quote character. */
  protected boolean m_UseCustomQuoteCharacter = false;

  /** the default quote character. */
  public final static String DEFAULT_CUSTOM_QUOTE_CHARACTER = "\"";

  /** the custom quote character to use. */
  protected String m_CustomQuoteCharacter = DEFAULT_CUSTOM_FIELD_SEPARATOR;
  
  /** whether the file has no header row. */
  protected boolean m_NoHeader = false;

  /** the default custom header. */
  public final static String DEFAULT_CUSTOM_HEADER = "";

  /** the custom header. */
  protected String m_CustomHeader = DEFAULT_CUSTOM_HEADER;

  /** the range of nominal attributes. */
  protected Range m_NominalRange = new Range();

  /** The user-supplied legal nominal values - each entry in the list is a spec */
  protected List<String> m_nominalLabelSpecs = new ArrayList<String>();

  /** the range of string attributes. */
  protected Range m_StringRange = new Range();

  /** the range of date attributes. */
  protected Range m_DateRange = new Range();

  /** the default date format. */
  public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /** the date format. */
  protected String m_DateFormat = DEFAULT_DATE_FORMAT;

  /** the default missing value. */
  public final static String DEFAULT_MISSING_VALUE = "";

  /** the missing value. */
  protected String m_MissingValue = DEFAULT_MISSING_VALUE;

  /** the url */
  protected String m_URL = "http://";

  /** The reader for the source file. */
  protected transient Reader m_sourceReader = null;

  /** the data that has been read. */
  protected Instances m_Data;

  /**
   * Returns a string describing this Loader
   * 
   * @return 		a description of the Loader suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Reads files in common CSV formats.\n"
      + "For tab-delimited files, choose TDF as format.\n"
      + "For other formats, you can specify a custom field separator.";
  }

  /**
   * Get the file extension used for libsvm files
   *
   * @return 		the file extension
   */
  public String getFileExtension() {
    return FILE_EXTENSION;
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  public String[] getFileExtensions() {
    return new String[]{FILE_EXTENSION, FILE_EXTENSION_TSV, FILE_EXTENSION_TEXT};
  }

  /**
   * Returns a description of the file type.
   *
   * @return 		a short file description
   */
  public String getFileDescription() {
    return "Common CSV files";
  }

  /**
   * Sets the format to use.
   *
   * @param value	the format
   */
  public void setFormat(SelectedTag value) {
    if (value.getTags() == CommonCsvFormats.TAGS_FORMATS)
      m_Format = value.getSelectedTag().getID();
  }

  /**
   * Returns the format in use.
   *
   * @return		the format
   */
  public SelectedTag getFormat() {
    return new SelectedTag(m_Format, CommonCsvFormats.TAGS_FORMATS);
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String formatTipText() {
    return "The format of the CSV file.";
  }

  /**
   * Sets whether to use the custom field separator.
   *
   * @param value	true if to use
   */
  public void setUseCustomFieldSeparator(boolean value) {
    m_UseCustomFieldSeparator = value;
  }

  /**
   * Returns whether to use the custom field separator.
   *
   * @return		true if to use
   */
  public boolean getUseCustomFieldSeparator() {
    return m_UseCustomFieldSeparator;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String useCustomFieldSeparatorTipText() {
    return "If enabled, makes use of the supplied field separator.";
  }

  /**
   * Sets the custom field separator.
   *
   * @param value	the separator
   */
  public void setCustomFieldSeparator(String value) {
    if (value.length() == 1)
      m_CustomFieldSeparator = value;
    else
      System.err.println("Field separator must be 1 character long!");
  }

  /**
   * Returns the custom field separator.
   *
   * @return		the separator
   */
  public String getCustomFieldSeparator() {
    return m_CustomFieldSeparator;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String customFieldSeparatorTipText() {
    return "The field separator, when using custom one is enabled.";
  }

  /**
   * Sets whether to use the custom quote character.
   *
   * @param value	true if to use
   */
  public void setUseCustomQuoteCharacter(boolean value) {
    m_UseCustomQuoteCharacter = value;
  }

  /**
   * Returns whether to use the custom quote character.
   *
   * @return		true if to use
   */
  public boolean getUseCustomQuoteCharacter() {
    return m_UseCustomQuoteCharacter;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String useCustomQuoteCharacterTipText() {
    return "If enabled, makes use of the supplied quote character.";
  }

  /**
   * Sets the custom quote character.
   *
   * @param value	the character
   */
  public void setCustomQuoteCharacter(String value) {
    if (value.length() == 1)
      m_CustomQuoteCharacter = value;
    else
      System.err.println("Quote character must be 1 character long!");
  }

  /**
   * Returns the custom quote character.
   *
   * @return		the character
   */
  public String getCustomQuoteCharacter() {
    return m_CustomQuoteCharacter;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String customQuoteCharacterTipText() {
    return "The quote character, when using custom one is enabled.";
  }

  /**
   * Sets whether there is no header row present.
   *
   * @param value	true if no header row
   */
  public void setNoHeader(boolean value) {
    m_NoHeader = value;
  }

  /**
   * Returns whether there is no header row present.
   *
   * @return		true if no header row
   */
  public boolean getNoHeader() {
    return m_NoHeader;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String noHeaderTipText() {
    return "If enabled, assumes no header row in the spradsheet.";
  }

  /**
   * Sets the custom header.
   *
   * @param value	the column names (comma-separated)
   */
  public void setCustomHeader(String value) {
    m_CustomHeader = value;
  }

  /**
   * Returns the custom header.
   *
   * @return		the column names (comma-separated)
   */
  public String getCustomHeader() {
    return m_CustomHeader;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String customHeaderTipText() {
    return "The comma-separated list of column names, ignored if empty.";
  }

  /**
   * Sets the range of attributes to treat as nominal.
   *
   * @param value	the range
   */
  public void setNominalRange(Range value) {
    m_NominalRange = value;
  }

  /**
   * Returns the range of attributes to treat as nominal.
   *
   * @return		the range
   */
  public Range getNominalRange() {
    return m_NominalRange;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String nominalRangeTipText() {
    return "The range of attributes to treat as nominal.";
  }

  /**
   * Set label specifications for nominal attributes.
   *
   * @param specs an array of label specifications
   */
  public void setNominalLabelSpecs(Object[] specs) {
    m_nominalLabelSpecs.clear();
    for (Object spec : specs)
      m_nominalLabelSpecs.add(spec.toString());
  }

  /**
   * Get label specifications for nominal attributes.
   *
   * @return an array of label specifications
   */
  public Object[] getNominalLabelSpecs() {
    return m_nominalLabelSpecs.toArray(new String[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String nominalLabelSpecsTipText() {
    return "Optional specification of legal labels for nominal "
      + "attributes. May be specified multiple times. "
      + "Batch mode can determine this "
      + "automatically (and so can incremental mode if "
      + "the first in memory buffer load of instances "
      + "contains an example of each legal value). The "
      + "spec contains two parts separated by a \":\". The "
      + "first part can be a range of attribute indexes or "
      + "a comma-separated list off attruibute names; the "
      + "second part is a comma-separated list of labels. E.g "
      + "\"1,2,4-6:red,green,blue\" or \"att1,att2:red,green,blue\"";
  }

  /**
   * Sets the range of attributes to treat as string.
   *
   * @param value	the range
   */
  public void setStringRange(Range value) {
    m_StringRange = value;
  }

  /**
   * Returns the range of attributes to treat as string.
   *
   * @return		the range
   */
  public Range getStringRange() {
    return m_StringRange;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String stringRangeTipText() {
    return "The range of attributes to treat as string.";
  }

  /**
   * Sets the range of attributes to treat as date.
   *
   * @param value	the range
   */
  public void setDateRange(Range value) {
    m_DateRange = value;
  }

  /**
   * Returns the range of attributes to treat as date.
   *
   * @return		the range
   */
  public Range getDateRange() {
    return m_DateRange;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String dateRangeTipText() {
    return "The range of attributes to treat as date.";
  }

  /**
   * Sets the format for parsing the date attribute(s).
   *
   * @param value	the format
   */
  public void setDateFormat(String value) {
    try {
      new SimpleDateFormat(value);
      m_DateFormat = value;
    }
    catch (Exception e) {
      System.err.println("Failed to parse date format: " + value);
      e.printStackTrace();
    }
  }

  /**
   * Returns the format for parsing the date attribute(s).
   *
   * @return		the format
   */
  public String getDateFormat() {
    return m_DateFormat;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String dateFormatTipText() {
    return "The format for parsing the date attribute(s).";
  }

  /**
   * Sets the missing value.
   *
   * @param value	the missing value
   */
  public void setMissingValue(String value) {
    m_MissingValue= value;
  }

  /**
   * Returns the missing value.
   *
   * @return		the missing value
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String missingValueTipText() {
    return "The string to interpret as missing value.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();

    result.addElement(new Option("\tThe CSV format to use\n"
      + "\t(default: DEFAULT)",
      "F", 1, "-F " + Tag.toOptionList(CommonCsvFormats.TAGS_FORMATS)));

    result.addElement(new Option("\tWhether to use custom field separator\n"
      + "\t(default: no)",
      "use-custom-field-separator", 0, "-use-custom-field-separator"));

    result.addElement(new Option("\tThe custom field separator\n"
      + "\t(default: " + DEFAULT_CUSTOM_FIELD_SEPARATOR + ")",
      "custom-field-separator", 1, "-custom-field-separator <separator-char>"));

    result.addElement(new Option("\tWhether to use custom quote character\n"
      + "\t(default: no)",
      "use-custom-quote-character", 0, "-use-custom-quote-character"));

    result.addElement(new Option("\tThe custom quote character\n"
      + "\t(default: " + DEFAULT_CUSTOM_QUOTE_CHARACTER + ")",
      "custom-quote-character", 1, "-custom-quote-character <quote-char>"));

    result.addElement(new Option("\tWhether there is no header row in the spreadsheet\n"
      + "\t(default: assumes header row present)",
      "no-header", 0, "-no-header"));

    result.addElement(new Option("\tThe attribute range to treat as nominal\n"
      + "\t(default: none)",
      "nominal", 1, "-nominal <range>"));

    result.add(new Option("\tOptional specification of legal labels for nominal\n"
      + "\tattributes. May be specified multiple times.\n"
      + "\tThe spec contains two parts separated by a \":\".\n"
      + "\tThe first part can be a range of attribute indexes or\n"
      + "\ta comma-separated list off attruibute names;\n"
      + "\tthe second part is a comma-separated list of labels. E.g.:\n"
      + "\t\"1,2,4-6:red,green,blue\" or \"att1,att2:red,green,blue\"",
      "nominal-label-spec", 1, "-nominal-label-spec <nominal label spec>"));

    result.addElement(new Option("\tThe attribute range to treat as string\n"
      + "\t(default: none)",
      "string", 1, "-string <range>"));

    result.addElement(new Option("\tThe attribute range to treat as date\n"
      + "\t(default: none)",
      "date", 1, "-date <range>"));

    result.addElement(new Option("\tThe format to use for parsing the date attribute(s)\n"
      + "\tsee: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html\n"
      + "\t(default: " + DEFAULT_DATE_FORMAT + ")",
      "date-format", 1, "-date-format <format>"));

    result.addElement(new Option("\tThe string to interpret as missing value\n"
      + "\t(default: '" + DEFAULT_MISSING_VALUE + "')",
      "missing-value", 1, "-missing-value <string>"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmp;

    tmp = Utils.getOption('F', options);
    if (!tmp.isEmpty())
      setFormat(new SelectedTag(tmp, CommonCsvFormats.TAGS_FORMATS));
    else
      setFormat(new SelectedTag(CommonCsvFormats.DEFAULT, CommonCsvFormats.TAGS_FORMATS));

    setUseCustomFieldSeparator(Utils.getFlag("use-custom-field-separator", options));

    tmp = Utils.getOption("custom-field-separator", options);
    if (!tmp.isEmpty() && (tmp.length() == 1))
      setCustomFieldSeparator(tmp);
    else
      setCustomFieldSeparator(DEFAULT_CUSTOM_FIELD_SEPARATOR);

    setUseCustomQuoteCharacter(Utils.getFlag("use-custom-quote-character", options));

    setNoHeader(Utils.getFlag("no-header", options));

    tmp = Utils.getOption("custom-header", options);
    if (!tmp.isEmpty())
      setCustomHeader(tmp);
    else
      setCustomHeader(DEFAULT_CUSTOM_HEADER);

    tmp = Utils.getOption("custom-quote-character", options);
    if (!tmp.isEmpty() && (tmp.length() == 1))
      setCustomQuoteCharacter(tmp);
    else
      setCustomQuoteCharacter(DEFAULT_CUSTOM_QUOTE_CHARACTER);

    tmp = Utils.getOption("nominal", options);
    if (!tmp.isEmpty())
      setNominalRange(new Range(tmp));
    else
      setNominalRange(new Range());

    m_nominalLabelSpecs.clear();
    while (true) {
      tmp = Utils.getOption("nominal-label-spec", options);
      if (tmp.isEmpty())
        break;
      m_nominalLabelSpecs.add(tmp);
    }

    tmp = Utils.getOption("string", options);
    if (!tmp.isEmpty())
      setStringRange(new Range(tmp));
    else
      setStringRange(new Range());

    tmp = Utils.getOption("date", options);
    if (!tmp.isEmpty())
      setDateRange(new Range(tmp));
    else
      setDateRange(new Range());

    tmp = Utils.getOption("date-format", options);
    if (!tmp.isEmpty())
      setDateFormat(tmp);
    else
      setDateFormat(DEFAULT_DATE_FORMAT);

    tmp = Utils.getOption("missing-value", options);
    if (!tmp.isEmpty())
      setMissingValue(tmp);
    else
      setMissingValue(DEFAULT_MISSING_VALUE);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the Apriori object.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add("-F");
    result.add(getFormat().getSelectedTag().getIDStr());

    if (getUseCustomFieldSeparator()) {
      result.add("-use-custom-field-separator");
      result.add("-custom-field-separator");
      result.add(getCustomFieldSeparator());
    }

    if (getUseCustomQuoteCharacter()) {
      result.add("-use-custom-quote-character");
      result.add("-custom-quote-character");
      result.add(getCustomQuoteCharacter());
    }

    if (getNoHeader())
      result.add("-no-header");

    if (!getCustomHeader().isEmpty()) {
      result.add("-custom-header");
      result.add(getCustomHeader());
    }

    if (!getNominalRange().getRanges().isEmpty()) {
      result.add("-nominal");
      result.add(getNominalRange().getRanges());

      for (String spec : m_nominalLabelSpecs) {
	result.add("-nominal-label-spec");
	result.add(spec);
      }
    }

    if (!getStringRange().getRanges().isEmpty()) {
      result.add("-string");
      result.add(getStringRange().getRanges());
    }

    if (!getDateRange().getRanges().isEmpty()) {
      result.add("-date");
      result.add(getDateRange().getRanges());
      result.add("-date-format");
      result.add(getDateFormat());
    }

    result.add("-missing-value");
    result.add(getMissingValue());

    return result.toArray(new String[0]);
  }

  /**
   * Resets the Loader ready to read a new data set
   * 
   * @throws IOException        if something goes wrong
   */
  public void reset() throws IOException {
    m_structure = null;
    m_Data      = null;

    setRetrieval(NONE);
    
    if (m_File != null)
      setFile(new File(m_File));
    else if ((m_URL != null) && !m_URL.equals("http://"))
      setURL(m_URL);
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied File object.
   *
   * @param file 		the source file.
   * @throws IOException        if an error occurs
   */
  public void setSource(File file) throws IOException {
    m_structure = null;
    m_Data      = null;

    setRetrieval(NONE);

    if (file == null)
      throw new IOException("Source file object is null!");

    try {
      if (file.getName().endsWith(FILE_EXTENSION_COMPRESSED))
	setSource(new GZIPInputStream(new FileInputStream(file)));
      else
	setSource(new FileInputStream(file));
    }
    catch (FileNotFoundException ex) {
      throw new IOException("File not found");
    }
    
    m_sourceFile = file;
    m_File       = file.getAbsolutePath();
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied url.
   *
   * @param url 	the source url.
   * @throws IOException        if an error occurs
   */
  public void setSource(URL url) throws IOException {
    m_structure = null;
    m_Data      = null;

    setRetrieval(NONE);
    
    setSource(url.openStream());

    m_URL = url.toString();
  }
  
  /**
   * Set the url to load from
   *
   * @param url 		the url to load from
   * @throws IOException        if the url can't be set.
   */
  public void setURL(String url) throws IOException {
    m_URL = url;
    setSource(new URL(url));
  }

  /**
   * Return the current url
   *
   * @return the current url
   */
  public String retrieveURL() {
    return m_URL;
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied InputStream.
   *
   * @param in 			the source InputStream.
   * @throws IOException        if initialization of reader fails.
   */
  public void setSource(InputStream in) throws IOException {
    m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
    m_URL  = "http://";

    m_sourceReader = new BufferedReader(new InputStreamReader(in));
    m_Data         = null;
  }

  /**
   * Checks whether the string is numeric.
   *
   * @param s		the string to test
   * @return		true if numeric
   */
  protected boolean isNumeric(String s) {
    try {
      Double.parseDouble(s);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the list of custom column names.
   *
   * @param max 	the maximum number of columns, -1 just use the custom header ones
   * @return		the column names
   */
  protected List<String> customColumnNames(int max) {
    List<String> 	result;
    int			i;
    int			start;

    result = new ArrayList<String>();
    if (!m_CustomHeader.isEmpty())
      result.addAll(Arrays.asList(m_CustomHeader.split(",")));

    if (max > -1) {
      start = result.size();
      for (i = start; i < max; i++)
	result.add("att-" + (i + 1));
    }

    return result;
  }

  /**
   * Initializes the m_Data and m_structure member variables.
   *
   * @param atts	the attribute definitions to use
   * @param capacity 	the number of rows to reserve in m_Data
   */
  protected void initInstances(ArrayList<Attribute> atts, int capacity) {
    String	relation;

    if (m_sourceFile != null)
      relation = m_sourceFile.getName();
    else
      relation = "CommonCSV";
    m_Data = new Instances(relation, atts, capacity);
    m_structure = new Instances(m_Data, 0);
  }

  /**
   * Determines and returns (if possible) the structure (internally the 
   * header) of the data set as an empty set of instances.
   * If not yet read, also reads the full dataset into m_Data.
   *
   * @return 			the structure of the data set as an empty set 
   * 				of Instances
   * @throws IOException        if an error occurs
   */
  public Instances getStructure() throws IOException {
    CSVFormat 			format;
    CSVParser 			parser;
    List<String> 		names;
    Set<String>			namesSet;
    String			nameTmp;
    Map<String,Integer>		attIndex;
    int 			i;
    int				n;
    List<CSVRecord> 		records;
    int 			numRows;
    ArrayList<Attribute> 	atts;
    AttributeType[]		types;
    boolean 			noNumeric;
    String 			cell;
    double[] 			values;
    boolean			hasNominalValues;
    Map<Integer,Collection<String>>	nominalValues;
    Map<Integer,Boolean>	sortLabels;
    List<String>		labels;
    String[]			parts;
    Range			specRange;
    String[]			attList;
    int				firstDataRow;

    if (m_sourceReader == null)
      throw new IOException("No source has been specified");

    if (m_structure != null)
      return new Instances(m_structure, 0);

    try {
      // parse
      format = CommonCsvFormats.getFormat(m_Format);
      if (m_Format != CommonCsvFormats.TDF) {
	if (m_UseCustomFieldSeparator)
	  format = format.withDelimiter(m_CustomFieldSeparator.charAt(0));
      }
      if (m_UseCustomQuoteCharacter)
	format = format.withEscape(m_CustomQuoteCharacter.charAt(0));
      format.withAllowMissingColumnNames();
      parser = format.parse(m_sourceReader);
      records = parser.getRecords();
      numRows = records.size();
      firstDataRow = m_NoHeader ? 0 : 1;
      atts = new ArrayList<Attribute>();
      if (numRows == 0) {
	if (m_NoHeader) {
	  names = customColumnNames(-1);
	  if (names.size() == 0)
	    throw new IOException("No rows in CSV file and no custom header defined!");
	  for (i = 0; i < names.size(); i++)
	    atts.add(new Attribute(names.get(i)));
	  initInstances(atts, 0);
	  return new Instances(m_structure, 0);
	}
	else {
	  throw new IOException("No rows in CSV file!");
	}
      }
      if (numRows == 1) {
        if (!m_NoHeader) {
          for (i = 0; i < records.get(0).size(); i++)
	    atts.add(new Attribute(records.get(0).get(i)));
	  initInstances(atts, 0);
	  return new Instances(m_structure, 0);
	}
      }

      // init header names
      names = new ArrayList<String>();
      if (m_NoHeader) {
        names.addAll(customColumnNames(records.get(0).size()));
      }
      else {
        names.addAll(customColumnNames(-1));
        for (i = names.size(); i < records.get(0).size(); i++)
          names.add(records.get(0).get(i));
      }

      // disambiguate names if necessary
      namesSet = new HashSet<String>();
      for (i = 0; i < names.size(); i++) {
	nameTmp = names.get(i);
	if (!nameTmp.isEmpty() && !namesSet.contains(nameTmp)) {
	  namesSet.add(nameTmp);
	  continue;
	}

	n = 1;
	while (nameTmp.isEmpty() || namesSet.contains(nameTmp)) {
	  n++;
	  if (names.get(i).isEmpty())
	    nameTmp = "att-" + n;
	  else
	    nameTmp = names.get(i) + "-" + n;
	}
	System.err.println("Column #" + i + " required disambiguating: '" + names.get(i) + "' -> '" + nameTmp + "'");
	namesSet.add(nameTmp);
	names.set(i, nameTmp);
      }

      // generate name index for label specs
      attIndex = new HashMap<String, Integer>();
      for (String name: names)
	attIndex.put(name, attIndex.size());

      // init types
      types = new AttributeType[records.get(0).size()];
      m_NominalRange.setUpper(types.length - 1);
      m_StringRange.setUpper(types.length - 1);
      m_DateRange.setUpper(types.length - 1);
      hasNominalValues = false;
      nominalValues = new HashMap<Integer, Collection<String>>();
      sortLabels = new HashMap<Integer, Boolean>();
      for (i = 0; i < types.length; i++) {
	types[i] = AttributeType.NUMERIC;
	if (m_NominalRange.isInRange(i)) {
	  types[i] = AttributeType.NOMINAL;
	  hasNominalValues = true;
	}
	else if (m_StringRange.isInRange(i)) {
	  types[i] = AttributeType.STRING;
	}
	else if (m_DateRange.isInRange(i)) {
	  types[i] = AttributeType.DATE;
	}
      }

      if ((numRows == 1) && !m_NoHeader) {
	for (i = 0; i < names.size(); i++)
	  atts.add(new Attribute(names.get(i)));
	initInstances(atts, 0);
	return new Instances(m_structure, 0);
      }

      // check numeric columns
      for (n = firstDataRow; n < records.size(); n++) {
	noNumeric = true;
	for (i = 0; i < types.length && i < records.get(n).size(); i++) {
	  if (types[i] == AttributeType.NUMERIC) {
	    noNumeric = false;
	    cell = records.get(n).get(i);
	    if (cell.equals(m_MissingValue))
	      continue;
	    if (!isNumeric(cell))
	      types[i] = AttributeType.STRING;
	  }
	}
	// all columns contain string values, no need to further investigate
	if (noNumeric)
	  break;
      }

      // collect nominal values
      if (hasNominalValues) {
	for (n = firstDataRow; n < records.size(); n++) {
	  for (i = 0; i < types.length && i < records.get(n).size(); i++) {
	    if (types[i] == AttributeType.NOMINAL) {
	      if (!nominalValues.containsKey(i)) {
		nominalValues.put(i, new HashSet<String>());
		sortLabels.put(i, true);  // label specs override this
	      }
	      cell = records.get(n).get(i);
	      if (cell.equals(m_MissingValue))
		continue;
	      nominalValues.get(i).add(cell);
	    }
	  }
	}
      }

      // label specs
      for (String labelSpec: m_nominalLabelSpecs) {
	parts = labelSpec.split(":");
	if (parts.length != 2)
	  throw new IllegalStateException("Invalid label specification (required: 'indices/names:list,of,labels'): " + labelSpec);
	labels = new ArrayList<String>(Arrays.asList(parts[1].split(",")));
	try {
	  specRange = new Range(parts[0]);
	  specRange.setUpper(types.length + 1);
	  for (int index: specRange.getSelection()) {
	    nominalValues.put(index, labels);
	    sortLabels.put(index, false);
	    types[index] = AttributeType.NOMINAL;
	  }
	}
	catch (Exception e) {
	  attList = parts[0].split(",");
	  for (String name: attList) {
	    i = attIndex.get(name);
	    nominalValues.put(i, labels);
	    sortLabels.put(i, false);
	    types[i] = AttributeType.NOMINAL;
	  }
	}
      }

      // header
      for (i = 0; i < names.size(); i++) {
	switch (types[i]) {
	  case NUMERIC:
	    atts.add(new Attribute(names.get(i)));
	    break;
	  case NOMINAL:
	    labels = new ArrayList<String>(nominalValues.get(i));
	    if (sortLabels.get(i))
	      Collections.sort(labels);
	    atts.add(new Attribute(names.get(i), labels));
	    break;
	  case STRING:
	    atts.add(new Attribute(names.get(i), (List<String>) null));
	    break;
	  case DATE:
	    atts.add(new Attribute(names.get(i), m_DateFormat));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled attribute type: " + types[i]);
	}
      }
      initInstances(atts, records.size());

      // data
      for (n = firstDataRow; n < records.size(); n++) {
	values = new double[types.length];
	for (i = 0; i < types.length && i < records.get(n).size(); i++) {
	  cell = records.get(n).get(i);
	  if (cell.equals(m_MissingValue)) {
	    values[i] = Utils.missingValue();
	  }
	  else {
	    switch (types[i]) {
	      case NUMERIC:
		values[i] = Double.parseDouble(cell);
		break;
	      case NOMINAL:
		values[i] = m_Data.attribute(i).indexOfValue(cell);
		break;
	      case STRING:
		values[i] = m_Data.attribute(i).addStringValue(cell);
		break;
	      case DATE:
		values[i] = m_Data.attribute(i).parseDate(cell);
		break;
	      default:
		throw new IllegalStateException("Unhandled attribute type: " + types[i]);
	    }
	  }
	}
	m_Data.add(new DenseInstance(1.0, values));
      }

      return new Instances(m_structure, 0);
    }
    catch (IOException ioe) {
      // just re-throw it
      throw ioe;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Return the full data set. If the structure hasn't yet been determined
   * by a call to getStructure then method should do so before processing
   * the rest of the data set.
   *
   * @return 			the structure of the data set as an empty 
   * 				set of Instances
   * @throws IOException        if there is no source or parsing fails
   */
  public Instances getDataSet() throws IOException {
    if (m_sourceReader == null)
      throw new IOException("No source has been specified");
    
    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes");

    setRetrieval(BATCH);
    if (m_structure == null)
      getStructure();

    try {
      // close the stream
      m_sourceReader.close();
    }
    catch (Exception ex) {
      // ignored
    }

    return m_Data;
  }

  /**
   * CommonCSVLoader is unable to process a data set incrementally.
   *
   * @param structure		ignored
   * @return 			never returns without throwing an exception
   * @throws IOException        always. CommonCSVLoader is unable to process a
   * 				data set incrementally.
   */
  public Instance getNextInstance(Instances structure) throws IOException {
    throw new IOException("CommonCSVLoader can't read data sets incrementally.");
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method.
   *
   * @param args 	should contain the name of an input file.
   */
  public static void main(String[] args) {
    runFileLoader(new CommonCSVLoader(), args);
  }
}
