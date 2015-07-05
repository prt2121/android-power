package com.prt2121.actress;

/**
 * Created by pt2121 on 7/3/15.
 */
public interface IScript {

    Script.Line whenHear(String word);

    Script.Line whenHear(String[] words);

    Script.Line whenHearKeyword(String keyword);

    Script.Line whenHearKeywords(String[] keywords);

    void deleteWord(String word);

    Response query(String word);

    Response queryAll(String word);

}
