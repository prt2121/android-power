/**
 * Created by pt2121 on 7/3/15.
 */
public class Actress {

    private Script script;

    private Actress(Script script) {
        this.script = script;
    }

    public static Actress with(Script script) {
        return new Actress(script);
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public Response respond(String word) {
        return script.queryAll(word);
    }

}
