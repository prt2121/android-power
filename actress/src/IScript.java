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

    interface Callback {
        void call();
    }

    class Response {
        String line;
        Callback callback;

        public Response(String line, Callback callback) {
            this.line = line;
            this.callback = callback;
        }
    }

    class Line {
        public final String[] words;
        private Response response;
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

        public Script say(String string) {
            this.response = new Response(string, null);
            return script;
        }

        public Script say(String string, Callback callback) {
            this.response = new Response(string, callback);
            return script;
        }

        public Response respond() {
            return response;
        }
    }
}
