/**
 * Created by pt2121 on 7/3/15.
 */
public interface IScript {

    Line whenHear(String word);

    Line whenHear(String[] words);

    Line whenHearKeyword(String keyword);

    Line whenHearKeywords(String[] keywords);

    void deleteWord(String word);

    String query(String word);

    String queryAll(String word);

    class Line {
        public final String[] words;
        private String line;
        private Script script;

        public Line(Script script, String keyword) {
            this.script = script;
            this.words = new String[1];
            words[0] = keyword;
        }

        public Line(Script script, String[] words) {
            this.script = script;
            this.words = words;
        }

        public Script say(String response) {
            this.line = response;
            return script;
        }

        public String respond() {
            return line;
        }
    }
}
