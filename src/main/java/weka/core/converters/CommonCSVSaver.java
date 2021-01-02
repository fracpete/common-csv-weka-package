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
 * CommonCSVSaver.java
 * Copyright (C) 2019-2020 FracPete
 *
 */

package weka.core.converters;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Writes to a destination that is in the specified CSV format.
 *
 * @author FracPete (fracpete at gmail dot com)
 * @see Saver
 */
public class CommonCSVSaver
  extends AbstractFileSaver
  implements BatchConverter, IncrementalConverter, WeightedInstancesHandler {

  /** for serialization */
  private static final long serialVersionUID = -7226404765213522043L;

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
  protected String m_CustomQuoteCharacter = DEFAULT_CUSTOM_QUOTE_CHARACTER;

  /** whether to use a custom quote mode. */
  protected boolean m_UseCustomQuoteMode= false;

  /** the default custom quote mode. */
  public final static int DEFAULT_CUSTOM_QUOTE_MODE = CommonCsvQuoteModes.MINIMAL;

  /** the custom quote mode to use. */
  protected int m_CustomQuoteMode = DEFAULT_CUSTOM_QUOTE_MODE;

  /** whether to use a custom escape character. */
  protected boolean m_UseCustomEscapeCharacter = false;

  /** the default escape character. */
  public final static String DEFAULT_CUSTOM_ESCAPE_CHARACTER = "";

  /** the custom escape character to use. */
  protected String m_CustomEscapeCharacter = DEFAULT_CUSTOM_ESCAPE_CHARACTER;

  /** whether the file has no header row. */
  protected boolean m_NoHeader = false;

  /** generates the CSV. */
  protected transient CSVPrinter m_Printer;

  /**
   * Constructor
   */
  public CommonCSVSaver() {
    resetOptions();
  }

  /**
   * Returns a string describing this Saver
   *
   * @return a description of the Saver suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return "Writes to a destination that is in the specified CSV format.";
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
   * Sets whether to use the custom quote character.
   *
   * @param value	true if to use
   */
  public void setUseCustomQuoteMode(boolean value) {
    m_UseCustomQuoteMode = value;
  }

  /**
   * Returns whether to use the custom quote mode.
   *
   * @return		true if to use
   */
  public boolean getUseCustomQuoteMode() {
    return m_UseCustomQuoteMode;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String useCustomQuoteModeTipText() {
    return "If enabled, makes use of the supplied quote mode.";
  }

  /**
   * Sets the custom quote mode.
   *
   * @param value	the mode
   */
  public void setCustomQuoteMode(SelectedTag value) {
    if (value.getTags() == CommonCsvQuoteModes.TAGS_QUOTEMODES)
      m_CustomQuoteMode = value.getSelectedTag().getID();
  }

  /**
   * Returns the custom quote mode.
   *
   * @return		the mode
   */
  public SelectedTag getCustomQuoteMode() {
    return new SelectedTag(m_CustomQuoteMode, CommonCsvQuoteModes.TAGS_QUOTEMODES);
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String customQuoteModeTipText() {
    return "The quote mode, when using custom one is enabled.";
  }

  /**
   * Sets whether to use the custom escape character.
   *
   * @param value	true if to use
   */
  public void setUseCustomEscapeCharacter(boolean value) {
    m_UseCustomEscapeCharacter = value;
  }

  /**
   * Returns whether to use the custom escape character.
   *
   * @return		true if to use
   */
  public boolean getUseCustomEscapeCharacter() {
    return m_UseCustomEscapeCharacter;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String useCustomEscapeCharacterTipText() {
    return "If enabled, makes use of the supplied escape character.";
  }

  /**
   * Sets the custom escape character.
   *
   * @param value	the character
   */
  public void setCustomEscapeCharacter(String value) {
    m_CustomEscapeCharacter = value;
  }

  /**
   * Returns the custom escape character.
   *
   * @return		the character
   */
  public String getCustomEscapeCharacter() {
    return m_CustomEscapeCharacter;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String customEscapeCharacterTipText() {
    return "The escape character, when using custom one is enabled.";
  }

  /**
   * Sets whether to suppress the header.
   *
   * @param value	true if no header row
   */
  public void setNoHeader(boolean value) {
    m_NoHeader = value;
  }

  /**
   * Returns whether to suppress the header.
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
    return "If enabled, suppresses header output.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();
    Enumeration enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

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

    result.addElement(new Option("\tWhether to use custom quote mode\n"
      + "\t(default: no)",
      "use-custom-quote-mode", 0, "-use-custom-quote-mode"));

    result.addElement(new Option("\tThe custom quote mode\n"
      + "\t(default: MINIMAL)",
      "custom-quote-mode", 1, "-custom-quote-mode " + Tag.toOptionList(CommonCsvQuoteModes.TAGS_QUOTEMODES)));

    result.addElement(new Option("\tWhether to use custom escape character\n"
      + "\t(default: no)",
      "use-custom-escape-character", 0, "-use-custom-escape-character"));

    result.addElement(new Option("\tThe custom escape character\n"
      + "\t(default: " + DEFAULT_CUSTOM_ESCAPE_CHARACTER + ")",
      "custom-escape-character", 1, "-custom-escape-character <escape-char>"));

    result.addElement(new Option("\tWhether to suppress output of header row\n"
      + "\t(default: outputs header)",
      "no-header", 0, "-no-header"));

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
    String tmp;

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

    tmp = Utils.getOption("custom-quote-character", options);
    if (!tmp.isEmpty() && (tmp.length() == 1))
      setCustomQuoteCharacter(tmp);
    else
      setCustomQuoteCharacter(DEFAULT_CUSTOM_QUOTE_CHARACTER);

    setUseCustomQuoteMode(Utils.getFlag("use-custom-quote-mode", options));

    tmp = Utils.getOption("custom-quote-mode", options);
    if (!tmp.isEmpty())
      setCustomQuoteMode(new SelectedTag(tmp, CommonCsvQuoteModes.TAGS_QUOTEMODES));
    else
      setCustomQuoteMode(new SelectedTag(CommonCsvQuoteModes.MINIMAL, CommonCsvQuoteModes.TAGS_QUOTEMODES));

    setUseCustomEscapeCharacter(Utils.getFlag("use-custom-escape-character", options));

    tmp = Utils.getOption("custom-escape-character", options);
    setCustomEscapeCharacter(tmp);

    setNoHeader(Utils.getFlag("no-header", options));

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the object.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

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

    if (getUseCustomQuoteMode()) {
      result.add("-use-custom-quote-mode");
      result.add("-custom-quote-mode");
      result.add(getCustomQuoteMode().getSelectedTag().getIDStr());
    }

    if (getUseCustomEscapeCharacter()) {
      result.add("-use-custom-escape-character");
      result.add("-custom-escape-character");
      result.add(getCustomEscapeCharacter());
    }

    if (getNoHeader())
      result.add("-no-header");

    return result.toArray(new String[0]);
  }

  /**
   * Returns a description of the file type.
   *
   * @return a short file description
   */
  @Override
  public String getFileDescription() {
    return new CommonCSVLoader().getFileDescription();
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new CommonCSVLoader().getFileExtensions();
  }

  /**
   * Resets the Saver
   */
  @Override
  public void resetOptions() {
    super.resetOptions();
    m_Format = CommonCsvFormats.DEFAULT;
    setFileExtension(CommonCSVLoader.FILE_EXTENSION);
  }

  /**
   * Returns the Capabilities of this saver.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /** Sets the writer to null. */
  public void resetWriter() {
    super.resetWriter();
    m_Printer = null;
  }

  /**
   * Initializes the CSV output.
   *
   * @throws IOException	if initialization fails
   */
  protected void initPrinter() throws IOException {
    CSVFormat 		format;

    format = CommonCsvFormats.getFormat(m_Format);
    if (m_Format != CommonCsvFormats.TDF) {
      if (m_UseCustomFieldSeparator)
	format = format.withDelimiter(m_CustomFieldSeparator.charAt(0));
    }
    if (m_UseCustomQuoteCharacter)
      format = format.withQuote(m_CustomQuoteCharacter.charAt(0));
    if (m_UseCustomQuoteMode)
      format = format.withQuoteMode(CommonCsvQuoteModes.getQuoteMode(m_CustomQuoteMode));
    if (m_UseCustomEscapeCharacter && m_CustomEscapeCharacter.length() == 1)
      format = format.withEscape(m_CustomEscapeCharacter.charAt(0));
    if (getWriter() != null)
      m_Printer = format.print(getWriter());
    else
      m_Printer = format.print(System.out);
  }

  /**
   * Outputs the header.
   *
   * @param data	the data to base the header on
   * @throws IOException	if writing fails
   */
  protected void writeHeader(Instances data) throws IOException {
    List<Object> 	values;
    int			i;

    values = new ArrayList<Object>();
    for (i = 0; i < data.numAttributes(); i++)
      values.add(data.attribute(i).name());
    if (!m_NoHeader)
      m_Printer.printRecord(values);
  }

  /**
   * Writes a CSV row.
   *
   * @param inst	the row to output
   * @throws IOException	if writing fails
   */
  protected void writeRow(Instance inst) throws IOException {
    List<Object> 	values;
    int			i;

    values = new ArrayList<Object>();
    for (i = 0; i < inst.numAttributes(); i++) {
      if (!inst.isMissing(i)) {
	switch (inst.attribute(i).type()) {
	  case Attribute.NUMERIC:
	  case Attribute.DATE:
	    values.add(inst.value(i));
	    break;
	  case Attribute.NOMINAL:
	  case Attribute.STRING:
	    values.add(inst.stringValue(i));
	    break;
	  case Attribute.RELATIONAL:
	    values.add(inst.relationalValue(i).toString());
	    break;
	}
      }
    }
    m_Printer.printRecord(values);
  }

  /**
   * Writes a Batch of instances
   *
   * @throws IOException throws IOException if saving in batch mode is not
   *           possible
   */
  @Override
  public void writeBatch() throws IOException {
    Instances 		data;
    int			n;

    if (getInstances() == null)
      throw new IOException("No instances to save");

    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Batch and incremental saving cannot be mixed.");

    setRetrieval(BATCH);
    setWriteMode(WRITE);

    initPrinter();
    data = getInstances();

    // header
    writeHeader(data);

    // data
    for (n = 0; n < data.numInstances(); n++)
      writeRow(data.instance(n));

    m_Printer.flush();
    m_Printer.close();
    setWriteMode(WAIT);
    resetWriter();
    setWriteMode(CANCEL);
  }

  /**
   * Saves an instances incrementally. Structure has to be set by using the
   * setStructure() method or setInstances() method.
   *
   * @param inst the instance to save
   * @throws IOException throws IOEXception if an instance cannot be saved
   *           incrementally.
   */
  @Override
  public void writeIncremental(Instance inst) throws IOException {
    int 	writeMode;
    Instances 	structure;

    writeMode = getWriteMode();
    structure = getInstances();

    if (getRetrieval() == BATCH || getRetrieval() == NONE)
      throw new IOException("Batch and incremental saving cannot be mixed.");
    initPrinter();

    if (writeMode == WAIT) {
      if (structure == null) {
	setWriteMode(CANCEL);
	if (inst != null)
	  System.err.println("Structure(Header Information) has to be set in advance");
      }
      else {
	setWriteMode(STRUCTURE_READY);
      }
      writeMode = getWriteMode();
    }
    if (writeMode == CANCEL) {
      if (m_Printer != null) {
	m_Printer.flush();
	m_Printer.close();
      }
      cancel();
    }
    if (writeMode == STRUCTURE_READY) {
      setWriteMode(WRITE);
      writeHeader(structure);
      writeMode = getWriteMode();
    }
    if (writeMode == WRITE) {
      if (structure == null)
	throw new IOException("No instances information available.");
      if (inst != null) {
	writeRow(inst);
	m_Printer.flush();
      }
      else {
	// close
	if (m_Printer != null) {
	  m_Printer.flush();
	  m_Printer.close();
	}
	m_incrementalCounter = 0;
	resetStructure();
	m_Printer = null;
	resetWriter();
      }
    }
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method.
   *
   * @param args should contain the options of a Saver.
   */
  public static void main(String[] args) {
    runFileSaver(new CommonCSVSaver(), args);
  }
}
