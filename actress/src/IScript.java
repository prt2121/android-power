/**
 * Created by pt2121 on 7/3/15.
 */
public interface IScript {

    Line whenHear(String word);

    Line whenHear(String[] words);

    Line whenHearKeyword(String keyword);

    Line whenHearKeywords(String[] keywords);

    void deleteWord(String word);

    Response query(String word);

    Response queryAll(String word);

}
