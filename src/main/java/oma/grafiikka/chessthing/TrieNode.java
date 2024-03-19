package oma.grafiikka.chessthing;

import java.util.*;

/**
 * Trien solmu
 */
public class TrieNode {
    /**
     * HashMap solmun lapsista
     */
    private HashMap<Character, TrieNode> children = new HashMap<>();
    /**
     * ArrayList solmun ja sen lasten pelien id:istä
     */
    private ArrayList<Integer> gameIDs = new ArrayList<>();
    /**
     * Solmun ja sen vanhempien sisältö, eli reitti solmuun. Pääasiassa debugaamista varten
     */
    private String content;
    /**
     * Onko solmu merkitty sanaksi eli avaukseksi
     */
    private boolean isWord = false;

    /**
     *
     * @return Solmun listan pelien id:istä
     */
    public ArrayList<Integer> getGameIDs() {
        return gameIDs;
    }

    /**
     *
     * @return Solmun lapset
     */
    public HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    /**
     * Debugaamista varten. Asettaa sanan tai sanan osan content-kenttään
     * @param s sana tai sanan osa
     */
    public void setContent(String s) {
        this.content = s;
    }

    /**
     * Asettaa listan pelien id:istä solmuun
     * @param list Pelien id:t
     */
    public void setGameIDsArr(ArrayList<Integer> list) {
        this.gameIDs.addAll(list);
    }

    /**
     * Lisää yksittäisen id:n listaan id:istä
     * @param id Pelin id
     */
    public void setGameIDs(int id) {
        this.getGameIDs().add(id);
    }

    /**
     * Debugaamista varten
     * @return Solmun kohdalla kertyneen sana eli reitin solmuun
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Asettaa solmuun tiedon siitä, onko se sana eli avaus
     * @param v boolean onko solmu avaus
     */
    public void setIsWord(boolean v) {
        this.isWord = v;
    }

    /**
     *
     * @return Kentän isWord, joka sisältää tiedon onko solmu sana
     */
    public boolean isWord() {
        return isWord;
    }

    /**
     * Kutsuu iteraattoria, jokä käy trietä läpi
     * @return Solmut joita ei ole käyty läpi aiemmin
     */
    public Iterator<TrieNode> getAllUncomputedChildren() {
        return new Iter(getChildren().values(), true);
    }

    /**
     * Kutsuu iteraattoria, joka käy trietä läpi
     * @return Ensimmäinen solmu jokaisesta branchista, joka on merkitty sanaksi
     */
    public Iterator<TrieNode> getAllWordChildren(){return new Iter(getChildren().values(), true);}


    /**
     * Iteraattori, jonka avulla trietä voidaan iteroida
     */
    private class Iter implements Iterator<TrieNode> {
        private Stack<TrieNode> stack = new Stack<>();
        /**
         * Etsitäänkö vain ensimmäinen solmu jokaisesta branchistä, joka on merkitty sanaksi
         */
        public Iter(Collection<TrieNode> list, boolean onlyWords){
            //this.onlyWords = onlyWords;
            for (TrieNode trieNode : list) {
                stack.push(trieNode);
            }
        }

        /**
         *
         * @return Onko stack tyhjä
         */
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        /**
         * Poppaa stackista solmun ja riippuen onlyWords kentästä lisää tai ignoraa
         * @return Stackista popatun solmun
         */
        @Override
        public TrieNode next() {
            TrieNode ret = stack.pop();
            //if (onlyWords){
               while(!ret.isWord()){
                   ret.getChildren().forEach((key, value) -> stack.push(value));
                   ret = stack.pop();
               }
            //}
            /*else if (!ret.isWord()){
                ret.getChildren().forEach((key, value) -> stack.push(value));
            }*/
            return ret;
        }
    }
}
