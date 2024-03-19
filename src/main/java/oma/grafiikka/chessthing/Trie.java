package oma.grafiikka.chessthing;

import java.util.ArrayList;
import java.util.Iterator;

public class Trie {
    private TrieNode rootNode;

    public Trie(){
        rootNode = new TrieNode();
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
        if(!cur.isWord() && !isWord){
            ArrayList<Integer> temp = new ArrayList<>();
            for (Iterator<TrieNode> it = cur.getAllWordChildren(); it.hasNext();) {
                TrieNode node = it.next();
                temp.addAll(node.getGameIDs());

            }
            cur.setGameIDsArr(temp);
        }
        cur.setIsWord(true);
    }
}
