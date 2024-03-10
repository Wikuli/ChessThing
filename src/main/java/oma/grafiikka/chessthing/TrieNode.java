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

        public Iter(Collection<TrieNode> list){
            for (TrieNode trieNode : list) {
                stack.push(trieNode);
            }
        }


        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public TrieNode next() {
            //Add stuff to stack. Miksu send halp
            TrieNode ret = stack.pop();
            if(!ret.isWord()){
                ret.getChildren().forEach((key, value) -> stack.push(value));
            }
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

        @Override
        public void forEachRemaining(Consumer<? super TrieNode> action) {
            Iterator.super.forEachRemaining(action);
        }
    }
}
