##About
**MDAG** (Minimalistic Directed Acyclic Graph) is a **Java library** capable of constructing character-sequence-storing, directed acyclic graphs of minimal size. 

The library is small, deceptively simple, fast, and powerful. It differs from other libraries capable of creating minimal directed acyclic graphs
(also known as **MA-FSA** (Minimal Acyclic Finite State Automata) or **DAWG**s (directed acyclic word graphs)) in the following ways:

- Graphs are constructed directly from input (instead of from a preliminarily constructed trie)
- Graphs can be constructed from unsorted input
- Graphs can be constructed from either files or collections
- Graphs can be modified on the fly (words can be added and/or removed from the represented lexicon)
- Graphs can be "simplified" in to an array for even more space-savings
- Out of the box convenience methods are provided for perusing the graph:

The code well structured, easy to follow, and extensively commented for the 
benefit of developers seeking to understand the data structure, as well as 
developers seeking to add homogeneous, functionality-extending code with ease.

The code has also been fully tested for correct functionality and performance.

##How to use

    MDAG myMDAG = new MDAG(new ArrayList<String>()); //Overriden constructor also accepts a file
    
    //Add a single String to the lexicon
    myMDAG.addString("str0");
    
    //Add a collection of Strings to the lexicon
    myMDAG.addStrings(Arrays.asList(new String[]{"str1", "str2", "str3"}));
    
    //Remove a String from the lexicon
    myMDAG.removeString("str0");
    
    //Deterine if the lexicon contains a given String (O(n) based on input)
    boolean doesContain = myMDAG.contains("str0"); //false
    
    //Get all Strings starting with "str1" (O(n) based on input)
    HashSet<String> startingWithSet = myMDAG.getStringsStartingWith("str1"); //{"str1"}

    //Get all String ending with "2" (O(n) based on dictionary)
    HashSet<String> endingWithSet = myMDAG.geStringsEndingWith("2"); //{"str2"}
    
    //Get all String containing "r3" (O(n) based on dictionary)
    HashSet<String> containingSet = myMDAG.getStringsWithSubstring("r3"); //{"str3"}
    
    //Get all Strings
    HashSet<String> entireSet = myMDAG.getAllStrings(); //{"str1", "str2", "str3"}
    
    //Simpify graph structure in to an array (further space reduction)
    myMDAG.simplify();
    
##Repo contents
- **src**: Contains source code for unit & integration tests as well as modified MDAG source code with exclusive debugging methods and permissive access modifiers on existing methods to facilitate testing
- **dist**: Contains test library and test suite jars
- **final**: Contains src and dist folders housing production-ready MDAG source and jar files respectively
- **words.txt**: Lexicon (/usr/share/dict/words)
- **words_unsorted.txt**: Shuffled lexicon (/usr/share/dict/words)

##Licensing and usage information

MDAG is licensed under the GNU General Public License (version 3). Licensing for proprietary software is available at a cost, inquire for more details. 

Informally, It'd be great to be notified of any derivatives or forks (or even better, issues or refactoring points that may inspire one)!

More informally, it'd **really** be great to be notified any uses in open-source, educational, or (if granted a license) commercial contexts.
Help me build my portfolio, if you found the library helpful it only takes an e-mail!

##Reference material

- Incremental Construction of Minimal Acyclic Finite-State Automata (2000) by Jan Daciuk , Stoyan Mihov , Bruce W. Watson , Richard E. Watson
  (http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.37.7600)
  (Presented algorithms used as bases for MDAG construction and manipulation algorithms)
  
- Programming Abstractions (Lecture 25)- Julie Zelinsky (Stanford University)
  (http://www.youtube.com/watch?v=TJ8SkcUSdbU)
  (Presented size reduction process used as basis for MDAG simplification algorithm)
