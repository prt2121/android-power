/**
 * Created by pt2121 on 7/4/15.
 */
public class Line {
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
