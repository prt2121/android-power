package com.prt2121.actress;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

/**
 * Created by pt2121 on 7/4/15.
 */
public class ActressTest extends TestCase {

    Actress actress;

    @Before
    public void setUp() throws Exception {
        Script script1 = Script.blank();
        Script script2 = Script.blank();

        actress = Actress.with(script1);

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
    }

    public void testWhenHear() {
        assertEquals(actress.respond("hello").line, "Hi");
        assertEquals(actress.respond("how are you?").line, "I am fine.");
        assertEquals(actress.respond("google").line, "The tech giant?");
    }

    public void testWhenHearKeyword() {
        assertEquals(actress.respond("apple").line, "The tech giant?");
        assertEquals(actress.respond("it's so funny. haha").line, "LOL");
        assertEquals(actress.respond("Google vs Apple").line, "hmm tech giants?");
    }

    public void testCallback() {
        Response response = actress.respond("borrow");
        assertEquals(response.line, "how much?");
        response.callback.call();
        assertEquals(actress.respond("100 dollars").line, "sure");
    }

    @After
    public void tearDown() throws Exception {

    }
}