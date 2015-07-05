import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pt2121 on 7/3/15.
 */
public class Script implements IScript {

    private Map<String, Response> wordMap = new HashMap<>();

    private Map<String, Response> keywordMap = new HashMap<>();

    private List<Line> lines = new ArrayList<>();

    private List<Line> keywordLines = new ArrayList<>();

    private boolean ignoreCase;

    public static Script blank() {
        return new Script();
    }

    public Script ignoreCase(boolean ignore) {
        ignoreCase = ignore;
        return this;
    }

    public Script build() {
        if (ignoreCase) {
            for (Line line : lines) {
                Response respond = line.respond();
                for (String word : line.words) {
                    wordMap.put(word.toLowerCase(), respond);
                }
            }
            for (Line line : keywordLines) {
                Response respond = line.respond();
                for (String keyword : line.words) {
                    keywordMap.put(keyword.toLowerCase(), respond);
                }
            }
        } else {
            for (Line line : lines) {
                Response respond = line.respond();
                for (String word : line.words) {
                    wordMap.put(word, respond);
                }
            }
            for (Line line : keywordLines) {
                Response respond = line.respond();
                for (String keyword : line.words) {
                    keywordMap.put(keyword, respond);
                }
            }
        }
        return this;
    }

    @Override
    public Line whenHear(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("word must not be null or empty.");
        }
        Line line = new Line(this, word);
        lines.add(line);
        return line;
    }

    @Override
    public Line whenHear(String[] words) {
        if (words == null || words.length == 0) {
            throw new IllegalArgumentException("words must not be null or empty.");
        }
        Line line = new Line(this, words);
        lines.add(line);
        return line;
    }

    @Override
    public Line whenHearKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("word must not be null or empty.");
        }
        Line line = new Line(this, keyword);
        keywordLines.add(line);
        return line;
    }

    @Override
    public Line whenHearKeywords(String[] keywords) {
        if (keywords == null || keywords.length == 0) {
            throw new IllegalArgumentException("word must not be null or empty.");
        }
        Line line = new Line(this, keywords);
        keywordLines.add(line);
        return line;
    }

    @Override
    public void deleteWord(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("word must not be null or empty.");
        }
        if (wordMap.containsKey(word))
            wordMap.remove(word);
    }

    @Override
    public Response query(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text must not be null or empty.");
        }
        text = ignoreCase ? text.toLowerCase() : text;
        if (wordMap.containsKey(text))
            return wordMap.get(text);
        else
            return null;
    }

    @Override
    public Response queryAll(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text must not be null or empty.");
        }
        text = ignoreCase ? text.toLowerCase() : text;
        if (wordMap.containsKey(text)) {
            return wordMap.get(text);
        }
        for (Map.Entry<String, Response> entry : keywordMap.entrySet()) {
            String key = entry.getKey();
            Response value = entry.getValue();
            if (text.contains(key))
                return value;
        }
        return null;
    }

}
