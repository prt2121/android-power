import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pt2121 on 7/3/15.
 */
public class Script implements IScript {

    private Map<String, String> map = new HashMap<String, String>();

    private List<Line> lines = new ArrayList<Line>();

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
                map.put(line.keyword.toLowerCase(), line.respond().toLowerCase());
            }
        } else {
            for (Line line : lines) {
                map.put(line.keyword, line.respond());
            }
        }
        return this;
    }

    @Override
    public Line whenHear(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("keyword must not be null or empty.");
        }
        Line line = new Line(this, keyword);
        lines.add(line);
        return line;
    }

    @Override
    public Line whenHear(String[] keyword) {
        return null;
    }

    @Override
    public void deleteKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("keyword must not be null or empty.");
        }
        if (map.containsKey(keyword))
            map.remove(keyword);
    }

    @Override
    public String query(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("keyword must not be null or empty.");
        }
        if (map.containsKey(keyword))
            return map.get(keyword);
        else
            return null;
    }

    @Override
    public String queryContain(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("keyword must not be null or empty.");
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.contains(keyword))
                return value;
        }
        return null;
    }

    @Override
    public String queryContain(String[] keywords) {
        if (keywords == null || keywords.length == 0) {
            throw new IllegalArgumentException("keywords must not be null or empty.");
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            boolean contain = true;
            for (String keyword : keywords) {
                if (!key.contains(keyword)) {
                    contain = false;
                }
            }
            if (contain) {
                return value;
            }
        }
        return null;
    }

}
