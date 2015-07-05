/**
 * Created by pt2121 on 7/3/15.
 */
public class Actress {

    // TODO: write test and make clean builder (Script -> ScriptBuilder)

    private Script script;

    public static Actress with(Script script) {
        return new Actress(script);
    }

    public void setScript(Script script) {
        this.script = script;
    }

    private Actress(Script script) {
        this.script = script;
    }

    public IScript.Response respond(String word) {
        return script.queryAll(word);
    }

    public static void main(String[] args) {
        Script script1 = Script.blank();
        Script script2 = Script.blank();

        Actress actress = Actress.with(script1);

        String[] companies = {"Google", "Apple", "Microsoft"};

        script1.whenHear("Hello").say("Hi")
                .whenHear("How are you?").say("I am fine.")
                .whenHear("I am so bored and sleepy.").say("Me too.")
                .whenHearKeyword("haha").say("LOL")
                .whenHear(companies).say("The tech giant?")
                .whenHearKeywords(companies).say("hmm tech giants?")
                .whenHearKeyword("borrow").say("how much?", () -> actress.setScript(script2))
                .ignoreCase(true)
                .build();

        script2.whenHearKeyword("dollars").say("sure")
                .ignoreCase(true)
                .build();

        System.out.println(actress.respond("hello").line);
        System.out.println(actress.respond("how are you?").line);
        System.out.println(actress.respond("google").line);
        System.out.println(actress.respond("apple").line);
        System.out.println(actress.respond("it's so funny. haha").line);
        System.out.println(actress.respond("Google vs Apple").line);

        IScript.Response response = actress.respond("borrow");
        System.out.println(response.line);
        response.callback.call();

        System.out.println(actress.respond("100 dollars").line);

    }

}
