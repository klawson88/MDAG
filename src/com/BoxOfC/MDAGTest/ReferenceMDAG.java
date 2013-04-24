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
import com.BoxOfC.MDAG.SimpleMDAGNode;
import com.BoxOfC.MDAG.SimpleMDAGNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;



/**
 *
 * @author Kevin
 */
public class ReferenceMDAG
{
    int nextNodeID = 0;
    
    private MDAGNode sourceNode = new MDAGNode(false, nextNodeID++);
    private HashMap<MDAGNode, MDAGNode> equivalenceClassMDAGNodeHashMap = new HashMap<MDAGNode, MDAGNode>();
    private final SimpleMDAGNode[] mdagDataArray;
    
    private final long constructionDurationMilliseconds;
    private int transitionCount = 0;
    private int nodeCount = -1;
    
    
    
    
    public ReferenceMDAG(File dataFile) throws IOException
    {
        long begin = System.nanoTime();
        
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        String previousWord = "";
        String currentWord;

        while((currentWord = br.readLine()) != null)
        {
            
            String prefixString = getPrefix(currentWord, previousWord);
            String suffixString = currentWord.substring(prefixString.length());

            MDAGNode prefixLastNode = sourceNode.transition(prefixString);
            
            if(prefixLastNode.hasOutgoingTransitions())
                replaceOrRegister(prefixLastNode);
            
            addString(prefixLastNode, suffixString);
            previousWord = currentWord;
        }
        
        replaceOrRegister(sourceNode);
        
        long end = System.nanoTime();
        constructionDurationMilliseconds = (end - begin);
        
        mdagDataArray = buildMDAGDataArray();
    }
    
    public ReferenceMDAG(Collection<String> wordCollection)
    {
        long begin = System.nanoTime();
        
        String previousWord= "";
        
        for(String word : wordCollection)
        {
            String prefixString = getPrefix(word, previousWord);
            String suffixString = word.substring(prefixString.length());
            
            MDAGNode prefixLastNode = sourceNode.transition(prefixString);
            
            if(prefixLastNode.hasOutgoingTransitions())
                replaceOrRegister(prefixLastNode);
            
            addString(prefixLastNode, suffixString);
            previousWord = word;
        }
        
        replaceOrRegister(sourceNode);
        
        long end = System.nanoTime();
        constructionDurationMilliseconds = (end - begin);
        
        mdagDataArray = buildMDAGDataArray();
    }


    private String getPrefix(String str1, String str2)
    {
        int i = 0;
        int shortestStrLength = Math.min(str1.length(), str2.length());
        for(; i < shortestStrLength && str1.charAt(i) == str2.charAt(i); i++){};
        
        return str1.substring(0, i);
    }
   
    
    
    private void replaceOrRegister(MDAGNode originNode)
    {
        Entry<Character,MDAGNode> lastTransitionKeyValuePair = originNode.getLastTransition();
        MDAGNode lastTransitionTargetNode =  lastTransitionKeyValuePair.getValue();

        if(lastTransitionKeyValuePair.getValue().hasOutgoingTransitions())
             replaceOrRegister(lastTransitionTargetNode);

        if(equivalenceClassMDAGNodeHashMap.containsKey(lastTransitionTargetNode))
        {
            transitionCount -= lastTransitionTargetNode.getOutgoingTransitionCount();
            originNode.reassignOutgoingTransition(lastTransitionKeyValuePair.getKey(), lastTransitionTargetNode, equivalenceClassMDAGNodeHashMap.get(lastTransitionTargetNode));
        }         
        else
            equivalenceClassMDAGNodeHashMap.put(lastTransitionTargetNode, lastTransitionTargetNode);
    }
    
    private void addString(MDAGNode node, String str)
    {
        MDAGNode activeNode = node;
        
        int charCount = str.length();
        for(int i = 0; i < charCount; i++, transitionCount++)
        {
            boolean isLastChar = (i == charCount - 1);
            activeNode = activeNode.addOutgoingTransition(str.charAt(i), isLastChar, nextNodeID++);
        }
    }


    
    private int buildMDAGConnectionSet(MDAGNode node, SimpleMDAGNode[] mdagDataArray, int onePastLastCreatedConnectionSetIndex)
    {   
        int pivotIndex = onePastLastCreatedConnectionSetIndex;
        node.setTransitionSetBeginIndex(pivotIndex);
          
        
        onePastLastCreatedConnectionSetIndex += node.getOutgoingTransitionCount();
        
        TreeMap<Character, MDAGNode> transitionTreeMap = node.getOutgoingTransitions();

        for(Entry<Character, MDAGNode> transitionKeyValuePair : transitionTreeMap.entrySet())
        {
            char transitionLabelChar = transitionKeyValuePair.getKey();
            MDAGNode transitionTargetNode = transitionKeyValuePair.getValue();
            mdagDataArray[pivotIndex] = new SimpleMDAGNode(transitionLabelChar, transitionTargetNode.isAcceptNode(), transitionTargetNode.getOutgoingTransitionCount());
            
            if(transitionTargetNode.getTransitionSetBeginIndex() == -1)
                onePastLastCreatedConnectionSetIndex = buildMDAGConnectionSet(transitionTargetNode, mdagDataArray, onePastLastCreatedConnectionSetIndex);
            
            mdagDataArray[pivotIndex++].setTransitionSetBeginIndex(transitionTargetNode.getTransitionSetBeginIndex());
        }
        
        return onePastLastCreatedConnectionSetIndex;
    }
    
    
    private SimpleMDAGNode[] buildMDAGDataArray()
    {
        SimpleMDAGNode[] dataArray = new SimpleMDAGNode[transitionCount];
        buildMDAGConnectionSet(sourceNode, dataArray, 0);
        
        return dataArray;
    }
    
    
    
    
    private ArrayList<String> getWordsWithPrefix(String prefix, String necessarySubstring, boolean prefixNecessary, boolean suffixNecessary, int connectionSetStartBeginIndex, int onePastConnectionSetEndIndex)
    {
        ArrayList<String> wordArrayList = new ArrayList<String>();
        
        for(int i = connectionSetStartBeginIndex; i < onePastConnectionSetEndIndex; i++)
        {
            SimpleMDAGNode currentNode = mdagDataArray[i];
            int nextConnectionSetBeginIndex = currentNode.getTransitionSetBeginIndex();
            int outgoingTransitionCount = currentNode.getOutgoingTransitionSetSize();
            
            String newPrefix = prefix + currentNode.getLetter();
            
            boolean meetsSubstringCondition = (prefixNecessary ? newPrefix.startsWith(necessarySubstring)
                                                : (suffixNecessary ? newPrefix.endsWith(necessarySubstring) : newPrefix.contains(necessarySubstring)));
            
            if(currentNode.isAcceptNode() && meetsSubstringCondition) wordArrayList.add(newPrefix);
            
            if(nextConnectionSetBeginIndex != -1)
                wordArrayList.addAll(getWordsWithPrefix(newPrefix, necessarySubstring, prefixNecessary, suffixNecessary, nextConnectionSetBeginIndex, nextConnectionSetBeginIndex + outgoingTransitionCount));
        }
        
        Collections.sort(wordArrayList);
        return wordArrayList;
    }
    
    public ArrayList<String> getAllWords()
    {
        return getWordsWithPrefix("", "", false, false, 0, sourceNode.getOutgoingTransitionCount());
    }
    
    public ArrayList<String> getWordsWithSubstring(String str)
    {
        return getWordsWithPrefix("", str.toLowerCase(), false, false, 0,  sourceNode.getOutgoingTransitionCount());
    }
    
    
    private int getStringEndIndex(String str, boolean fullWordFlag)
    {
        int wordEndIndex = -1;
        
        int currentTransitionSetBegin = 0;
        int onePastCurrentTransitionSetEnd = sourceNode.getOutgoingTransitionCount();
        int currentIndex;
        
        int numberOfChars = str.length();
        
        for(int i = 0; i < numberOfChars; i++)
        {
            char currentChar = str.charAt(i);
            boolean isLastChar = (i == (numberOfChars - 1));
            
            for(currentIndex = currentTransitionSetBegin; currentIndex < onePastCurrentTransitionSetEnd; currentIndex++)
            {
                SimpleMDAGNode currentNode = mdagDataArray[currentIndex];
                if(currentNode.getLetter() == currentChar)
                {
                    if(!isLastChar)
                    {
                        currentTransitionSetBegin = currentNode.getTransitionSetBeginIndex();
                        onePastCurrentTransitionSetEnd = currentTransitionSetBegin + currentNode.getOutgoingTransitionSetSize();
                    }
                    else
                        wordEndIndex = (!fullWordFlag || currentNode.isAcceptNode() ? currentIndex : -1);
                    
                    break;
                }
            }
            
            if(currentIndex == onePastCurrentTransitionSetEnd)
            {
                wordEndIndex = -1;
                break;
            }
        }
        
        return wordEndIndex;
    }
    
    public ArrayList<String> getWordsStartingWith(String prefix)
    {
        prefix = prefix.toLowerCase();
        ArrayList<String> wordArrayList = new ArrayList<String>();
        int prefixEndNodeIndex = getStringEndIndex(prefix, false);
        
        if(prefixEndNodeIndex != -1)
        {
            SimpleMDAGNode prefixEndMDAGNode = mdagDataArray[prefixEndNodeIndex];
            int connectionSetBeginIndex = prefixEndMDAGNode.getTransitionSetBeginIndex();
            int connectionSetSize = prefixEndMDAGNode.getOutgoingTransitionSetSize();
            
            wordArrayList = getWordsWithPrefix("", "", false, false, connectionSetBeginIndex , connectionSetBeginIndex + connectionSetSize);
            
            int numberOfSuffixes = wordArrayList.size();
            
            for(int i = 0; i < numberOfSuffixes; i++)
                wordArrayList.set(i, prefix + wordArrayList.get(i));
        }

        return wordArrayList;
    }
    
    public ArrayList<String> getWordsEndingWith(String suffix)
    {
        return getWordsWithPrefix("", suffix.toLowerCase(), false, true, 0, sourceNode.getOutgoingTransitionCount());
    }
    

    
    
    public boolean contains(String str)
    {
        return (getStringEndIndex(str.toLowerCase(), true) != -1);
    }
    
    
    public long getConstructionTimeMilliseconds()
    {
        return constructionDurationMilliseconds;
    }

    
    private int countNodes(MDAGNode originNode, HashSet<Integer> nodeIDHashSet)
    {
        if(originNode != sourceNode) nodeIDHashSet.add(originNode.id);
        
        TreeMap<Character, MDAGNode> transitionTreeMap = originNode.getOutgoingTransitions();
        
        for(Entry<Character, MDAGNode> transitionKeyValuePair : transitionTreeMap.entrySet()) 
            countNodes(transitionKeyValuePair.getValue(), nodeIDHashSet);

        return nodeIDHashSet.size();
    }
    
    public int getNodeCount()
    {
        if(nodeCount == -1)  nodeCount = countNodes(sourceNode, new HashSet<Integer>());
        return nodeCount;
    }
    
    public int getEquivalenceClassCount()
    {
        return equivalenceClassMDAGNodeHashMap.size();
    }
    
    public int getTransitionCount()
    {
        return transitionCount;
    }

    
}
