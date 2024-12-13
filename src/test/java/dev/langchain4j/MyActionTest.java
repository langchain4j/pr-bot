package dev.langchain4j;

import java.net.MalformedURLException;
import java.net.URL;

import static dev.langchain4j.MyAction.getContents;

class MyActionTest {

    public static void main(String[] args) throws MalformedURLException {
        String url = "https://patch-diff.githubusercontent.com/raw/langchain4j/langchain4j-embeddings/pull/70.diff";
        String diff = getContents(new URL(url));
        System.out.println(diff);
    }
}