/**
 * Created by pt2121 on 7/3/15.
 */
public interface IScript {

    Line whenHear(String keyword);

    Line whenHear(String[] keyword);

    void deleteKeyword(String keyword);

    String query(String keyword);

    String queryContain(String keyword);

    String queryContain(String[] keywords);

    class Line {
        public final String keyword;
        private String line;
        private Script script;

        public Line(Script script, String keyword) {
            this.script = script;
            this.keyword = keyword;
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
