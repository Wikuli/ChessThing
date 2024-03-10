package oma.grafiikka.chessthing;

import java.util.*;
import java.util.function.Consumer;

public class TrieNode {
    private HashMap<Character, TrieNode> children = new HashMap<>();
    private ArrayList<Integer> gameIDs = new ArrayList<>();
    private String content;
    private boolean isWord = false;

    public ArrayList<Integer> getGameIDs() {
        return gameIDs;
    }

    public HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    public void setContent(String s) {
        this.content = s;
    }

    public void setGameIDsArr(ArrayList<Integer> list) {
        this.gameIDs.addAll(list);
    }

    public void setGameIDs(int id) {
        this.getGameIDs().add(id);
    }

    public String getContent() {
        return this.content;
    }

    public void setIsWord(boolean v) {
        this.isWord = v;
    }

    public boolean isWord() {
        return isWord;
    }

    public Iterator<TrieNode> getAllUncomputedChildren() {
        return new Iter(getChildren().values());
    }




    private class Iter implements Iterator<TrieNode> {
        private Stack<TrieNode> stack = new Stack<>();

        //Initialise the stack with the first node's children from the Trie tree
        //First node is the one we are currently in so we don't need to push that to the stack
        public Iter(Collection<TrieNode> list){
            for (TrieNode trieNode : list) {
                stack.push(trieNode);
            }
        }

        //Check if stack is empty if it is every node has been checked
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        //Pop the top node from the stack and add it's children to the stack
        //Return the popped node to the for loop
        @Override
        public TrieNode next() {
            TrieNode ret = stack.pop();
            if(!ret.isWord()){
                ret.getChildren().forEach((key, value) -> stack.push(value));
            }
            return ret;
        }
    }
}
