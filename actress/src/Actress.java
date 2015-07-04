/**
 * Created by pt2121 on 7/3/15.
 */
public class Actress {

    private final Script script;

    public static Actress with(Script script) {
        return new Actress(script);
    }

    private Actress(Script script) {
        this.script = script;
    }

    public String respond(String word) {
        return script.query(word);
    }

    public String respondContain(String keyword) {
        return script.queryContain(keyword);
    }

    public String respondContain(String[] keywords) {
        return script.queryContain(keywords);
    }

    public static void main(String[] args) {

        Script script = Script.blank()
                .whenHear("Hello").say("Hi")
                .whenHear("How are you?").say("I am fine.")
                .whenHear("I am so bored and sleepy.").say("Me too.")
                .ignoreCase(true)
                .build();

        Actress actress = Actress.with(script);
        String[] keywords = {"bored", "sleepy"};
        System.out.println(actress.respond("hello"));
        System.out.println(actress.respond("how are you?"));
        System.out.println(actress.respondContain("you"));
        System.out.println(actress.respondContain(keywords));
    }

}
