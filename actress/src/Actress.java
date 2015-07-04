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
        return script.queryAll(word);
    }

    public static void main(String[] args) {

        String[] companies = {"Google", "Apple", "Microsoft"};

        Script script = Script.blank()
                .whenHear("Hello").say("Hi")
                .whenHear("How are you?").say("I am fine.")
                .whenHear("I am so bored and sleepy.").say("Me too.")
                .whenHearKeyword("haha").say("LOL")
                .whenHear(companies).say("The tech giant?")
                .whenHearKeywords(companies).say("hmm tech giants?")
                .ignoreCase(true)
                .build();

        Actress actress = Actress.with(script);
        System.out.println(actress.respond("hello"));
        System.out.println(actress.respond("how are you?"));
        System.out.println(actress.respond("google"));
        System.out.println(actress.respond("apple"));
        System.out.println(actress.respond("it's so funny. haha"));
        System.out.println(actress.respond("Google vs Apple"));
    }

}
