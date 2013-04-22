/**
 * MDAG is a Java library capable of constructing character-sequence-storing,
 * directed acyclic graphs of minimal size. 
 * 
 *  Copyright (C) 2012 Kevin Lawson <Klawson88@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (version 3) as 
 * published by the Free Software Foundation. Licensing for proprietary 
 * software is available at a cost, inquire for more details. 
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.BoxOfC.MDAGTest;

import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin
 */
public class Main 
{


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestNG test = new TestNG();
        test.setTestClasses(new Class[]{DAWGNodeTest.class, DAWGTest.class});
        test.run();
       
    }
}