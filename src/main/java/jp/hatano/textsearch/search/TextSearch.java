package jp.hatano.textsearch.search;

public class TextSearch {
    public static boolean search(String content, String searchText) {
        return content.contains(searchText);
    }
}