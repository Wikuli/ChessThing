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

    protected TrieNode findNode(String word){
        TrieNode cur = rootNode;

        for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            TrieNode node = cur.getChildren().get(c);
            if(node == null){
                return null;
            }
            cur = node;
        }
        return cur;
    }
    public void addWord(String word, int id){
        TrieNode cur = rootNode;
        boolean isWord = false;
        String content = "";
        for (char c: word.toCharArray()){
            if(cur.isWord()){
                cur.setGameIDs(id + 1);
                isWord = true;
            }
            content += c;
            cur = cur.getChildren().computeIfAbsent(c, l -> new TrieNode());
            cur.setContent(content);
        }
        cur.setGameIDs(id + 1);
        //Higher filter creation time for lower filter application time
        //If current isn't flagged as a word find every gameId from its children
        //and add them to the current node's GameIDsArr
        if(!cur.isWord() && !isWord){
            ArrayList<Integer> temp = new ArrayList<>();
            //Loop through all of the nodes under the current node using the iterator
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
