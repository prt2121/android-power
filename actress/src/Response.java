/**
 * Created by pt2121 on 7/4/15.
 */
public class Response {
    String line;
    Callback callback;

    public Response(String line, Callback callback) {
        this.line = line;
        this.callback = callback;
    }
}
