import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pt2121 on 7/3/15.
 */
public class Script implements IScript {

    private Map<String, String> wordMap = new HashMap<String, String>();

    private Map<String, String> keywordMap = new HashMap<String, String>();

    private List<Line> lines = new ArrayList<Line>();

    private List<Line> keywordLines = new ArrayList<Line>();

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
                String respond = line.respond().toLowerCase();
                for (String word : line.words) {
                    wordMap.put(word.toLowerCase(), respond);
                }
            }
            for (Line line : keywordLines) {
                String respond = line.respond().toLowerCase();
                for (String keyword : line.words) {
                    keywordMap.put(keyword.toLowerCase(), respond);
                }
            }
        } else {
            for (Line line : lines) {
                String respond = line.respond();
                for (String word : line.words) {
                    wordMap.put(word, respond);
                }
            }
            for (Line line : keywordLines) {
                String respond = line.respond();
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
    public String query(String text) {
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
    public String queryAll(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text must not be null or empty.");
        }
        text = ignoreCase ? text.toLowerCase() : text;
        if (wordMap.containsKey(text)) {
            return wordMap.get(text);
        }
        for (Map.Entry<String, String> entry : keywordMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (text.contains(key))
                return value;
        }
        return null;
    }

}
