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

import com.BoxOfC.MDAG.MDAGNode;
import java.util.TreeMap;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin
 */
@Test(enabled = false)
public class DAWGNodeTest 
{
    
    public void addOutgoingTransitionTest()
    {
        MDAGNode node1 = new MDAGNode(false);
        node1.addOutgoingTransition('a', true);
        node1.addOutgoingTransition('b', false);
        node1.addOutgoingTransition('c', false);
        
        TreeMap<Character, MDAGNode> outgoingTransitionTreeMap = node1.getOutgoingTransitions();
        
        assert outgoingTransitionTreeMap.size() == 3;
        assert outgoingTransitionTreeMap.get('a').isAcceptNode() == true;
        assert outgoingTransitionTreeMap.get('b').isAcceptNode() == false;
        assert outgoingTransitionTreeMap.get('b').isAcceptNode() == false;
    }
    
    
    public void cloneTest()
    {
        MDAGNode node1 = new MDAGNode(false);
        node1.addOutgoingTransition('a', false);
        node1.addOutgoingTransition('b', true);
        MDAGNode cloneNode1 = node1.clone();
        
        MDAGNode node2 = new MDAGNode(true);
        node2.addOutgoingTransition('c', false);
        node2.addOutgoingTransition('d', true);
        MDAGNode cloneNode2 = node2.clone();
        
        assert node1 != cloneNode1;
        assert node1.getIncomingTransitionCount() == cloneNode1.getIncomingTransitionCount();
        assert node1.isAcceptNode() == cloneNode1.isAcceptNode();
        assert node1.getOutgoingTransitions().equals(cloneNode1.getOutgoingTransitions());
        
        assert node2 != cloneNode2;
        assert node2.getIncomingTransitionCount() == cloneNode2.getIncomingTransitionCount();
        assert node2.isAcceptNode() == cloneNode2.isAcceptNode();
        assert node2.getOutgoingTransitions().equals(cloneNode2.getOutgoingTransitions());
    }
    

    public void transitionTest1()
    {
        MDAGNode node1= new MDAGNode(false);
        MDAGNode currentNode = node1;
        
        char[] alphabet = {'a', 'b', 'c','d', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};
        
        for(int i = 0; i < alphabet.length; i++)
            currentNode = currentNode.addOutgoingTransition(alphabet[i], i % 2 == 0);
        
        String alphaStr = new String(alphabet);
        
        assert node1.transition(alphaStr) != null;
    }
    
    public void reassignOutgoingTransitionTest()
    {
        MDAGNode node1 = new MDAGNode(false);
        node1.addOutgoingTransition('a', true);
        node1.addOutgoingTransition('b', false);
        node1.addOutgoingTransition('c', true);
        node1.addOutgoingTransition('d', false);
        
        MDAGNode node2 = new MDAGNode(true);
        node1.reassignOutgoingTransition('a', node1.transition('a'), node2);
        
        MDAGNode node3 = new MDAGNode(false);
        node1.reassignOutgoingTransition('b', node1.transition('b'), node3);
        
        MDAGNode node4 = new MDAGNode(false);
        node1.reassignOutgoingTransition('c', node1.transition('c'), node4);
        
        MDAGNode node5 = new MDAGNode(true);
        node1.reassignOutgoingTransition('d', node1.transition('d'), node5);
        
        assert node1.transition('a') == node2;
        assert node2.getIncomingTransitionCount() == 1;
        
        assert node1.transition('b') == node3;
        assert node3.getIncomingTransitionCount() == 1;
        
        assert node1.transition('c') == node4;
        assert node4.getIncomingTransitionCount() == 1;
        
        assert node1.transition('d') == node5;
        assert node5.getIncomingTransitionCount() == 1;
    }
    
    public void cloneTest2()
    {
        MDAGNode node1 = new MDAGNode(false);
        
        MDAGNode node2 = node1.addOutgoingTransition('\0', false);
        node2.addOutgoingTransition('a', false);
        node2.addOutgoingTransition('b', false);
        node2.addOutgoingTransition('c', false);
        
        MDAGNode node3 = node2.clone(node1, '\0');
        
        assert node2 != node3;
        assert node2.hasOutgoingTransition('a') && node3.hasOutgoingTransition('a');
        assert node2.hasOutgoingTransition('b') && node3.hasOutgoingTransition('b');
        assert node2.hasOutgoingTransition('c') && node3.hasOutgoingTransition('c');
        
        assert node1.getOutgoingTransitions().size() == 1;
        assert node3.getIncomingTransitionCount() == 1;
        assert node2.getIncomingTransitionCount() == 0;
    }
    
    public void equalsTest()
    {
        MDAGNode node1= new MDAGNode(false);
        MDAGNode node2 = new MDAGNode(false);
        
        MDAGNode node3 = new MDAGNode(true);
        MDAGNode node4 = new MDAGNode(true);
        
        MDAGNode currentNode1 = node1;
        MDAGNode currentNode2 = node2;

        char[] alphabet = {'a', 'b', 'c','d', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        
        for(int i = 0; i < alphabet.length; i++)
        {
           currentNode1 = currentNode1.addOutgoingTransition(alphabet[i], i % 2 == 0); 
           currentNode2 = currentNode2.addOutgoingTransition(alphabet[i], i % 2 == 0); 
        }
            
        assert node1.equals(node2);
        assert node3.equals(node4);
        
        assert !node1.equals(node3);
        assert !node2.equals(node4);
    }
    
    public void hashTest()
    {
        MDAGNode node1= new MDAGNode(false);
        MDAGNode node2 = new MDAGNode(false);
        
        MDAGNode node3 = new MDAGNode(true);
        MDAGNode node4 = new MDAGNode(true);
        
        MDAGNode currentNode1 = node1;
        MDAGNode currentNode2 = node2;

        char[] alphabet = {'a', 'b', 'c','d', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        
        for(int i = 0; i < alphabet.length; i++)
        {
           currentNode1 = currentNode1.addOutgoingTransition(alphabet[i], i % 2 == 0); 
           currentNode2 = currentNode2.addOutgoingTransition(alphabet[i], i % 2 == 0); 
        }
        
        assert node1.hashCode() == node2.hashCode();
        assert node3.hashCode() == node4.hashCode();
        assert node1.hashCode() != node3.hashCode();
        
    }
}
