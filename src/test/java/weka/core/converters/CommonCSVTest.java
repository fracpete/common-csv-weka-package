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
 * Copyright (C) 2020 FracPete
 */

package weka.core.converters;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;
import weka.core.Range;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.test.Regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Tests CommonCSVLoader/CommonCSVSaver. Run from the command line with:<p/>
 * java weka.core.converters.CommonCSVTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CommonCSVTest 
  extends AbstractFileConverterTest {

  /**
   * Constructs the <code>CommonCSVTest</code>.
   *
   * @param name the name of the test class
   */
  public CommonCSVTest(String name) { 
    super(name);  
    System.setProperty("weka.test.Regression.root", "src/test/resources");
    System.setProperty("weka.test.maventest", "true");
  }

  /**
   * returns the loader used in the tests
   * 
   * @return the configured loader
   */
  public AbstractLoader getLoader() {
    return new CommonCSVLoader();
  }

  protected void updateLoader(Instances data, Loader loader) {
    String nominal = "";
    for (int i = 0; i < data.numAttributes(); i++) {
      if (data.attribute(i).isNominal()) {
        if (nominal.length() > 0)
          nominal += ",";
        nominal += (i+1);
      }
    }
    ((CommonCSVLoader) loader).setNominalRange(new Range(nominal));
  }

  /**
   * returns the saver used in the tests
   * 
   * @return the configured saver
   */
  public AbstractSaver getSaver() {
    return new CommonCSVSaver();
  }

  /**
   * tests whether a URL can be loaded (via setURL(URL)).
   */
  public void testURLSourcedLoader() {
    updateLoader(m_Instances, m_Loader);
    super.testURLSourcedLoader();
  }

  /**
   * test the batch saving/loading (via setFile(File)).
   */
  public void testBatch() {
    updateLoader(m_Instances, m_Loader);
    super.testBatch();
  }

  /**
   * tests whether data can be loaded via setSource() with a file stream.
   */
  public void testLoaderWithStream() {
    updateLoader(m_Instances, m_Loader);
    super.testLoaderWithStream();
  }

  /**
   * Returns the files to use for the loader regression tests.
   *
   * @return		the files
   */
  protected String[] getLoaderRegressionFiles() {
    return new String[]{
      "weka/core/converters/iris_comma.csv",
      "weka/core/converters/iris_semicolon.csv",
      "weka/core/converters/iris_tab.csv",
      "weka/core/converters/ambiguous.csv",
      "weka/core/converters/mixed.csv",
    };
  }

  /**
   * Returns the loader setups to apply to the regression files.
   *
   * @return		the setups
   */
  protected CommonCSVLoader[] getLoaderRegressionSetups() {
    CommonCSVLoader[] result;

    result = new CommonCSVLoader[5];

    result[0] = new CommonCSVLoader();

    result[1] = new CommonCSVLoader();
    result[1].setUseCustomFieldSeparator(true);
    result[1].setCustomFieldSeparator(";");

    result[2] = new CommonCSVLoader();
    result[2].setFormat(new SelectedTag(CommonCsvFormats.TDF, CommonCsvFormats.TAGS_FORMATS));

    result[3] = new CommonCSVLoader();
    result[3].setMissingValue("?");
    result[3].setNominalRange(new Range("2,5"));
    result[3].setDateRange(new Range("7"));
    result[3].setDateFormat("yyyy-MM-dd");

    result[4] = new CommonCSVLoader();
    result[4].setUseCustomFieldSeparator(true);
    result[4].setCustomFieldSeparator(",");
    result[4].setNominalRange(new Range("2,5"));
    result[4].setStringRange(new Range("1,4"));
    result[4].setDateRange(new Range("last"));
    result[4].setDateFormat("yyyy-MM-dd");
    result[4].setMissingValue("?");

    return result;
  }

  /**
   * Runs a regression test -- this checks that the output of the tested object
   * matches that in a reference version. When this test is run without any
   * pre-existing reference output, the reference version is created.
   */
  public void testLoaderRegression() throws Exception {
    int i;
    Regression reg;
    Instances data;
    String[] files;
    CommonCSVLoader[] setups;
    InputStream in;

    reg = new Regression(CommonCSVLoader.class);
    files = getLoaderRegressionFiles();
    setups = getLoaderRegressionSetups();
    if (files.length != setups.length)
      fail("Number of files does not match setups: " + files.length + " != " + setups.length);

    for (i = 0; i < files.length; i++) {
      try {
        reg.println("--> " + (i+1));
        reg.println(Utils.toCommandLine(setups[i]));
        reg.println("");
        in = ClassLoader.getSystemResourceAsStream(files[i]);
        if (in == null)
          throw new IOException("Failed to load: " + files[i]);
        setups[i].setSource(in);
	data = setups[i].getDataSet();
        reg.println(data.toString());
        reg.println("");
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }

    try {
      String diff = reg.diff();
      if (diff == null) {
        System.err.println("Warning: No reference available, creating.");
      }
      else if (!diff.equals("")) {
        fail("Regression test failed. Difference:\n" + diff);
      }
    }
    catch (java.io.IOException ex) {
      fail("Problem during regression testing.\n" + ex);
    }
  }

  /**
   * Returns the files to use for the saver regression tests.
   *
   * @return		the files
   */
  protected String[] getSaverRegressionFiles() {
    return new String[]{
      "weka/core/converters/iris.arff",
      "weka/core/converters/iris.arff",
      "weka/core/converters/iris.arff",
      "weka/core/converters/mixed.arff",
    };
  }

  /**
   * Returns the output files (no path) to use for the saver regression tests.
   *
   * @return		the files
   */
  protected String[] getSaverRegressionOutput() {
    return new String[]{
      "iris.csv",
      "iris.csv",
      "iris.tsv",
      "mixed.csv",
    };
  }

  /**
   * Returns the saver setups to apply to the regression files.
   *
   * @return		the setups
   */
  protected CommonCSVSaver[] getSaverRegressionSetups() {
    CommonCSVSaver[] result;

    result = new CommonCSVSaver[4];

    result[0] = new CommonCSVSaver();

    result[1] = new CommonCSVSaver();
    result[1].setUseCustomFieldSeparator(true);
    result[1].setCustomFieldSeparator(";");

    result[2] = new CommonCSVSaver();
    result[2].setFormat(new SelectedTag(CommonCsvFormats.TDF, CommonCsvFormats.TAGS_FORMATS));

    result[3] = new CommonCSVSaver();
    result[3].setUseCustomQuoteMode(true);
    result[3].setCustomQuoteMode(new SelectedTag(CommonCsvQuoteModes.NON_NUMERIC, CommonCsvQuoteModes.TAGS_QUOTEMODES));

    return result;
  }

  /**
   * Reads the content of the specified file.
   *
   * @param file	the file to read
   * @return		the content
   * @throws IOException	if reading failed
   */
  protected String readFile(File file) throws IOException {
    BufferedReader breader;
    FileReader freader;
    StringBuilder result;
    String line;

    freader = new FileReader(file);
    breader = new BufferedReader(freader);
    result = new StringBuilder();
    while ((line = breader.readLine()) != null)
      result.append(line).append("\n");
    breader.close();
    freader.close();

    return result.toString();
  }

  /**
   * Runs a regression test -- this checks that the output of the tested object
   * matches that in a reference version. When this test is run without any
   * pre-existing reference output, the reference version is created.
   */
  public void testSaverRegression() throws Exception {
    int i;
    Regression reg;
    Instances data;
    String[] files;
    CommonCSVSaver[] setups;
    String[] outputs;
    InputStream in;
    File outFile;

    reg = new Regression(CommonCSVSaver.class);
    files = getSaverRegressionFiles();
    setups = getSaverRegressionSetups();
    outputs = getSaverRegressionOutput();
    if (files.length != setups.length)
      fail("Number of files does not match setups: " + files.length + " != " + setups.length);
    if (files.length != outputs.length)
      fail("Number of files does not match outputs: " + files.length + " != " + outputs.length);

    for (i = 0; i < files.length; i++) {
      try {
        reg.println("--> " + (i+1));
        reg.println(Utils.toCommandLine(setups[i]));
        reg.println("");
        in = ClassLoader.getSystemResourceAsStream(files[i]);
        if (in == null)
          throw new IOException("Failed to load: " + files[i]);
        outFile = new File(System.getProperty("java.io.tmpdir") + "/" + outputs[i]);
	data = DataSource.read(in);
        setups[i].setInstances(data);
        setups[i].setFile(outFile);
        setups[i].writeBatch();

        reg.println(readFile(outFile));
        reg.println("");
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }

    try {
      String diff = reg.diff();
      if (diff == null) {
        System.err.println("Warning: No reference available, creating.");
      }
      else if (!diff.equals("")) {
        fail("Regression test failed. Difference:\n" + diff);
      }
    }
    catch (java.io.IOException ex) {
      fail("Problem during regression testing.\n" + ex);
    }
  }

  /**
   * returns a test suite
   * 
   * @return the test suite
   */
  public static Test suite() {
    return new TestSuite(CommonCSVTest.class);
  }

  /**
   * for running the test from commandline
   * 
   * @param args the commandline arguments - ignored
   */
  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }
}

