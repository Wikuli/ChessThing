package oma.grafiikka.chessthing;

import java.util.*;

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

    //Returns children that aren't marked as a word
    //If a node containing isWord == true terminate branch
    public Iterator<TrieNode> getAllUncomputedChildren() {
        return new Iter(getChildren().values(), false);
    }

    //Returns the first children from each branch where isWord == true
    public Iterator<TrieNode> getAllWordChildren(){return new Iter(getChildren().values(), true);}



    private class Iter implements Iterator<TrieNode> {
        private Stack<TrieNode> stack = new Stack<>();
        private boolean onlyWords;
        //Initialise the stack with the first node's children from the Trie tree
        public Iter(Collection<TrieNode> list, boolean onlyWords){
            this.onlyWords = onlyWords;
            for (TrieNode trieNode : list) {
                stack.push(trieNode);
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        //Pop the top node from the stack and add it's children to the stack
        //If onlyWords is set to true skip all nodes marked as !word
        //When a word node is found its' children aren't added to the stack
        //If onlyWords is set to false return all nodes
        @Override
        public TrieNode next() {
            TrieNode ret = stack.pop();
            if (onlyWords){
               while(!ret.isWord()){
                   ret.getChildren().forEach((key, value) -> stack.push(value));
                   ret = stack.pop();
               }
            }
            else if (!ret.isWord()){
                ret.getChildren().forEach((key, value) -> stack.push(value));
            }
            return ret;
        }
    }
}
