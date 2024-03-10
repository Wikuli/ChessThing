package oma.grafiikka.chessthing;

import java.util.ArrayList;
import java.util.Iterator;

public class Trie {
    private TrieNode rootNode;

    public Trie(){
        rootNode = new TrieNode();
    }

    public TrieNode getRootNode(){
        return rootNode;
    }

    boolean isEmpty(){
        return rootNode == null;
    }

    boolean containsNode(String word){
        TrieNode cur = rootNode;

        for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            TrieNode node = cur.getChildren().get(c);
            if(node == null){
                return false;
            }
            cur = node;
        }
        return true;
    }

    public void addWord(String word, int id){
        TrieNode cur = rootNode;

        for (char c: word.toCharArray()){
            if(cur.isWord()){
                cur.setGameIDs(id);
            }
            cur = cur.getChildren().computeIfAbsent(c, l -> new TrieNode());
        }
        cur.setGameIDs(id);
        //Higher filter creation time for lower filter application time
        //If current isn't flagged as a word find every gameId from its children
        //and add them to the current node's GameIDsArr
        if(!cur.isWord()){
            ArrayList<Integer> temp = new ArrayList<>();
            for (Iterator<TrieNode> it = cur.getAllUncomputedChildren(); it.hasNext(); ) {
                TrieNode node = it.next();
                if(node.isWord()){
                    temp.addAll(node.getGameIDs());
                }
            }
            cur.setGameIDsArr(temp);
        }
        cur.setIsWord(true);
    }
}
