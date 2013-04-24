/**
 * MDAG is a Java library capable of constructing character-sequence-storing,
 * directed acyclic graphs of minimal size. 
 * 
 *  Copyright (C) 2012 Kevin Lawson <Klawson88@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BoxOfC.MDAGTest;

import com.BoxOfC.MDAG.MDAGNode;
import com.BoxOfC.MDAG.MDAG;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin
 */
public class DAWGTest 
{
    ArrayList<String> wordArrayList;
    ReferenceMDAG dawgA;
    MDAG dawg1;
    MDAG dawg2;
    
    
    @BeforeSuite
    public void initializer() throws FileNotFoundException, IOException
    {
        wordArrayList = new ArrayList<String>(100000);
        BufferedReader breader = new BufferedReader(new FileReader(new File("C:\\Users\\Kevin\\Documents\\NetBeansProjects\\MDAGTest\\words.txt")));
        String currentWord;
        
        while((currentWord = breader.readLine()) != null)
            wordArrayList.add(currentWord);
 
        
        //dawgA = new ReferenceMDAG(wordArrayList);
        
        dawg1 = new MDAG(wordArrayList);
        
        Collections.shuffle(wordArrayList);
        dawg2 = new MDAG(wordArrayList);

        dawg2.simplify();
        
    }
    
    
    @Test(enabled = false, groups = {"dawgTypeB"})
    public void dawgBGetMinimizationIndexTest()
    {
       assert dawg1.calculateMinimizationProcessingStartIndex("", "") == -1;
       assert dawg1.calculateMinimizationProcessingStartIndex("abcd", "efg") == 0;
       assert dawg1.calculateMinimizationProcessingStartIndex("abcd", "ab") == 2;
       assert dawg1.calculateMinimizationProcessingStartIndex("abcd", "abcd") == -1;
       assert dawg1.calculateMinimizationProcessingStartIndex("abcd", "abd") == 2;
       assert dawg1.calculateMinimizationProcessingStartIndex("abcd", "abcr") == 3;
       assert dawg1.calculateMinimizationProcessingStartIndex("abcd", "") == 0;
       assert dawg1.calculateMinimizationProcessingStartIndex("", "abcd") == -1;
    }
    
    public void insertString(String str, MDAGNode currentNode)
    {
        int numberOfChars = str.length();
        for(int i = 0; i < numberOfChars; i++)
        {
            currentNode = currentNode.addOutgoingTransition(str.charAt(i), i == numberOfChars - 1);
        }
    }
    
    @Test(enabled = false, groups = {"dawgTypeB"})
    public void dawgBGetLongestStoredSubsequenceTest()
    {  
        assert(dawg1.determineLongestPrefixInMDAG("do").equals("do"));
        assert(dawg1.determineLongestPrefixInMDAG("doggy").equals("doggy"));
        assert(dawg1.determineLongestPrefixInMDAG("c").equals("c"));
        assert(dawg1.determineLongestPrefixInMDAG("catsing").equals("cats"));
        assert(dawg1.determineLongestPrefixInMDAG("brolic").equals("bro"));
        assert(dawg1.determineLongestPrefixInMDAG("1234").equals(""));
        assert(dawg1.determineLongestPrefixInMDAG("Czechoslovakians").equals("Czechoslovakians"));
    }
    
    @Test(enabled = false, groups = {"dawgTypeB"})
    public void dawgBGetTransitionPathFirstConfluenceNodeDataTest()
    {
        assert(dawg1.getTransitionPathFirstConfluenceNodeData((MDAGNode)dawg1.getSourceNode(), "caution").get("confluenceNode") != null);
        assert(dawg1.getTransitionPathFirstConfluenceNodeData((MDAGNode)dawg1.getSourceNode(), "abated").get("confluenceNode") != null);
        assert(dawg1.getTransitionPathFirstConfluenceNodeData((MDAGNode)dawg1.getSourceNode(), "watching").get("confluenceNode") != null);
    }
    
    
    @Test(enabled = false, groups = {"dawgTypeB"})
    public void dawgBBuildTest()
    {
        for(String currentWord : wordArrayList)
        {
            assert dawg1.contains(currentWord) : "dawg1 does not contain " + currentWord;
            assert dawg2.contains(currentWord) : "dawg2 does not contain " + currentWord;
        }
            
    }
    
    @DataProvider(name = "removeWordDP")
    public Object[][] removeWordDataProvider()
    {
        int numberOfRuns = 20;
        Object[][] parameterObjectDoubleArray = new Object[numberOfRuns][];
        
        int wordArrayListSize = wordArrayList.size();        
        for(int i = 0; i < numberOfRuns; i++, wordArrayListSize--)
        {
            Object[] currentParameterArray = new Object[1];
            currentParameterArray[0] = (int)(Math.random() * wordArrayListSize);
            parameterObjectDoubleArray[i] = currentParameterArray;
        }
        
        return parameterObjectDoubleArray;
    }
    
    @Test(dataProvider = "removeWordDP", groups = {"dawgTypeB"})
    public void removeWordTest(Integer wordIndex)
    {
        String toBeRemovedWord = wordArrayList.get(wordIndex);
        
        MDAG testDAWG = new MDAG(wordArrayList);
        testDAWG.removeString(wordArrayList.get(wordIndex));
        
        wordArrayList.remove((int)wordIndex);
        MDAG controlTestDAWG = new MDAG(wordArrayList);
        
        assert testDAWG.getNodeCount() == controlTestDAWG.getNodeCount() : "Removed word: " + toBeRemovedWord;
        assert testDAWG.getEquivalenceClassCount() == controlTestDAWG.getEquivalenceClassCount() : "Removed word: " + toBeRemovedWord;
        assert testDAWG.getTransitionCount() == controlTestDAWG.getTransitionCount() : "Removed word: " + toBeRemovedWord;
    }
    
    @DataProvider(name = "removeWord2DP")
    public Object[][] removeWord2DataProvider()
    {
        int numberOfRuns = 20;
        int intervalSize = 20;
        
        Object[][] parameterObjectDoubleArray = new Object[numberOfRuns][];

        int wordArrayListSize = wordArrayList.size();
        for(int i = 0; i < numberOfRuns; i++, wordArrayListSize -= intervalSize)
        {
            int intervalBoundaryIndex1 = (int)(Math.random() * wordArrayListSize);
            int intervalBoundaryIndex2;
            
            if(intervalBoundaryIndex1 + intervalSize >= wordArrayListSize)
                intervalBoundaryIndex2 = intervalBoundaryIndex1 - intervalSize;
            else
                intervalBoundaryIndex2 = intervalBoundaryIndex1 + intervalSize;
            
            Object[] currentParameterArray = new Object[2];
            currentParameterArray[0] = Math.min(intervalBoundaryIndex1, intervalBoundaryIndex2);
            currentParameterArray[1] = Math.max(intervalBoundaryIndex1, intervalBoundaryIndex2);
            parameterObjectDoubleArray[i] = currentParameterArray;
        }
        
        return parameterObjectDoubleArray;
    }
    
    
    @Test(dataProvider = "removeWord2DP", groups = {"dawgTypeB"})
    public void removeWord2(Integer intervalBegin, Integer onePastIntervalEnd)
    {
        MDAG testDAWG = new MDAG(wordArrayList);
        
        int intervalSize = onePastIntervalEnd - intervalBegin;
        for(int i = 0; i < intervalSize; i++)
            testDAWG.removeString(wordArrayList.get(intervalBegin.intValue() + i));
        
        for(int i = 0; i < intervalSize; i++)
            wordArrayList.remove(intervalBegin.intValue());
        
        MDAG controlTestDAWG = new MDAG(wordArrayList);
        
        assert testDAWG.getNodeCount() == controlTestDAWG.getNodeCount();
        assert testDAWG.getEquivalenceClassCount() == controlTestDAWG.getEquivalenceClassCount();
        assert testDAWG.getTransitionCount() == controlTestDAWG.getTransitionCount();
    }
    
    

    
    
    @Test
    public void getAllWordsTest()
    {
        HashSet<String> wordHashSet1 = dawg1.getAllStrings();
        HashSet<String> wordHashSet2 = dawg2.getAllStrings();
        assert wordHashSet1.containsAll(wordArrayList);
        assert wordHashSet2.containsAll(wordArrayList);
        
    }
    
    
    @Test
    public void containsTest()
    {
        for(int i = 0; i < 100; i++)
        {
            assert dawg1.contains(wordArrayList.get(i));
            assert dawg2.contains(wordArrayList.get(i));
        }
    }
    
    @DataProvider(name = "searchDP")
    public Object[][] searchDataProvider() 
    {
        return new Object[][]{
                    {"ang"},
                    {"iter"},
                    {"con"},
                    {"pro"},
                    {"nan"},
                    {"ing"},
                    {"inter"},
                    {"ton"},
                    {"tion"},
                };
    }
    
    
    @Test(dataProvider = "searchDP")
    public void getAllWordsWithPrefixTest(String prefixStr)
    {
        HashSet<String> controlSet = new HashSet<String>();
        
        for(String str : wordArrayList)
        {
            if(str.startsWith(prefixStr))
                controlSet.add(str);
        }
        
        assert dawg1.getStringsStartingWith(prefixStr).equals(controlSet);
        assert dawg2.getStringsStartingWith(prefixStr).equals(controlSet);
    }
    
    
    @Test(dataProvider = "searchDP")
    public void getStringsWithSubstringTest(String substringStr)
    {
        HashSet<String> controlSet = new HashSet<String>();
        
        for(String str : wordArrayList)
        {
            if(str.contains(substringStr))
                controlSet.add(str);
        }
        
        assert dawg1.getStringsWithSubstring(substringStr).equals(controlSet);
        assert dawg2.getStringsWithSubstring(substringStr).equals(controlSet);
    }
    
    
    @Test(dataProvider = "searchDP")
    public void getStringsEndingWithTest(String suffixStr)
    {
        HashSet<String> controlSet = new HashSet<String>();
        
        for(String str : wordArrayList)
        {
            if(str.endsWith(suffixStr))
                controlSet.add(str);
        }
        
        assert dawg1.getStringsEndingWith(suffixStr).equals(controlSet);
        assert dawg2.getStringsEndingWith(suffixStr).equals(controlSet);
    }
}
