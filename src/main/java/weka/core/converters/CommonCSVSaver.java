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
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVRecordFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Writes to a destination that is in the specified CSV format.
 *
 * @author FracPete (fracpete at gmail dot com)
 * @see Saver
 */
public class CommonCSVSaver
  extends AbstractFileSaver
  implements BatchConverter, WeightedInstancesHandler {

  /** for serialization */
  private static final long serialVersionUID = -7226404765213522043L;

  /** the format. */
  protected int m_Format = CommonCsvFormats.DEFAULT;

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
    super.setOptions(options);

    String format = Utils.getOption('F', options);
    if (!format.isEmpty())
      setFormat(new SelectedTag(format, CommonCsvFormats.TAGS_FORMATS));
    else
      setFormat(new SelectedTag(CommonCsvFormats.DEFAULT, CommonCsvFormats.TAGS_FORMATS));

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

  /**
   * Writes a Batch of instances
   * 
   * @throws IOException throws IOException if saving in batch mode is not
   *           possible
   */
  @Override
  public void writeBatch() throws IOException {
    if (getInstances() == null) {
      throw new IOException("No instances to save");
    }

    if (getRetrieval() == INCREMENTAL) {
      throw new IOException("Batch and incremental saving cannot be mixed.");
    }

    setRetrieval(BATCH);
    setWriteMode(WRITE);

    CSVFormat format = CommonCsvFormats.getFormat(m_Format);
    CSVPrinter printer = format.print(getWriter());
    Instances data = getInstances();

    // header
    Map<String,Integer> map = new HashMap<String,Integer>();
    List<String> values = new ArrayList<String>();
    for (int i = 0; i < data.numAttributes(); i++) {
      map.put(data.attribute(i).name(), i);
      values.add(data.attribute(i).name());
    }
    CSVRecord header = CSVRecordFactory.newRecord(values.toArray(new String[0]), map);
    printer.printRecord(header);

    // data
    for (int n = 0; n < data.numInstances(); n++) {
      Instance inst = data.instance(n);
      values = new ArrayList<String>();
      for (int i = 0; i < inst.numAttributes(); i++) {
        if (!inst.isMissing(i)) {
          switch (inst.attribute(i).type()) {
	    case Attribute.NUMERIC:
	    case Attribute.DATE:
	      values.add("" + inst.value(i));
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
      CSVRecord rec = CSVRecordFactory.newRecord(values.toArray(new String[0]), map, null, n+1, -1);
      printer.printRecord(rec);
    }

    printer.flush();
    setWriteMode(WAIT);
    resetWriter();
    setWriteMode(CANCEL);
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
