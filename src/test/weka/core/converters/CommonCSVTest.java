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
  }

  /**
   * returns the loader used in the tests
   * 
   * @return the configured loader
   */
  public AbstractLoader getLoader() {
    return new CommonCSVLoader();
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
   * returns a test suite
   * 
   * @return the test suite
   */
  public static Test suite() {
    return new TestSuite(ArffTest.class);
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

