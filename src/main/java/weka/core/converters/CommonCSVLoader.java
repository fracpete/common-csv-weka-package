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
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Loader
 */
public class CommonCSVLoader
  extends AbstractFileLoader
  implements BatchConverter, URLSourcedLoader, OptionHandler {

  /** for serialization */
  private static final long serialVersionUID = 3764533621135196582L;

  /** the default file extension */
  public static String FILE_EXTENSION = ".csv";

  /** the text file extension */
  public static String FILE_EXTENSION_TEXT = ".txt";

  /** the tab-delimited file extension */
  public static String FILE_EXTENSION_TSV = ".tsv";

  /** the format. */
  protected int m_Format = CommonCsvFormats.DEFAULT;

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
    return "Reads files in common CSV formats.";
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
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();

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
    String format = Utils.getOption('F', options);
    if (!format.isEmpty())
      setFormat(new SelectedTag(format, CommonCsvFormats.TAGS_FORMATS));
    else
      setFormat(new SelectedTag(CommonCsvFormats.DEFAULT, CommonCsvFormats.TAGS_FORMATS));

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
   * Determines and returns (if possible) the structure (internally the 
   * header) of the data set as an empty set of instances.
   *
   * @return 			the structure of the data set as an empty set 
   * 				of Instances
   * @throws IOException        if an error occurs
   */
  public Instances getStructure() throws IOException {
    if (m_sourceReader == null)
      throw new IOException("No source has been specified");

    if (m_structure == null) {
      try {
        CSVFormat format = CommonCsvFormats.getFormat(m_Format);
        CSVParser parser = format.withFirstRecordAsHeader().parse(m_sourceReader);
        Map<String,Integer> header = parser.getHeaderMap();
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < header.size(); i++)
          names.add("");
        for (String name: header.keySet())
          names.set(header.get(name), name);
        List<CSVRecord> records = parser.getRecords();
        int numRows = records.size();
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
	if (numRows == 0)
	  throw new IOException("No rows in CSV file?");
	boolean[] numeric = new boolean[records.get(0).size()];
	for (int i = 0; i < numeric.length; i++)
	  numeric[i] = true;
	if (numRows == 1) {
	  // header
	  for (int i = 0; i < names.size(); i++)
	    atts.add(new Attribute(names.get(i)));
	  // no data
	  m_Data = new Instances("CSV", atts, 0);
	  m_structure = new Instances(m_Data, 0);
	}
	else {
	  // check data
	  for (int n = 0; n < records.size(); n++) {
	    boolean noNumeric = true;
	    for (int i = 0; i < numeric.length && i < records.get(n).size(); i++) {
	      if (numeric[i]) {
	        noNumeric = false;
	        String cell = records.get(n).get(i);
	        if (cell.isEmpty())
	          continue;
	        if (!isNumeric(cell))
	          numeric[i] = false;
	      }
	    }
	    // all columns contain string values, no need to further investigate
	    if (noNumeric)
	      break;
	  }
	  // header
	  for (int i = 0; i < names.size(); i++) {
	    if (numeric[i])
	      atts.add(new Attribute(names.get(i)));
	    else
	      atts.add(new Attribute(names.get(i), (List<String>) null));
	  }
	  m_Data = new Instances("CSV", atts, records.size());
	  // data
	  for (int n = 0; n < records.size(); n++) {
	    double[] values = new double[numeric.length];
	    for (int i = 0; i < numeric.length && i < records.get(n).size(); i++) {
	      String cell = records.get(n).get(i);
	      if (cell.isEmpty())
	        values[i] = Utils.missingValue();
	      else if (numeric[i])
	        values[i] = Double.parseDouble(cell);
	      else
	        values[i] = m_Data.attribute(i).addStringValue(cell);
	      m_Data.add(new DenseInstance(1.0, values));
	    }
	  }
	  m_structure = new Instances(m_Data, 0);
	}
      }
      catch (IOException ioe) {
	// just re-throw it
	throw ioe;
      }
      catch (Exception e) {
	throw new RuntimeException(e);
      }
    }

    return new Instances(m_structure, 0);
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
