package com.rpi.ha.speech;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammarManager;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

public class VoiceRegThread {
	
	public static void main(String[] args) throws IOException{
		try {
            URL url;
            if (args.length > 0) {
                url = new File(args[0]).toURI().toURL();
            } else {
                url = VoiceRegThread.class.getResource("voiceCommand.config.xml");
            }

            System.out.println("Loading...");

            ConfigurationManager cm = new ConfigurationManager(url);

	    Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
	    Microphone microphone = (Microphone) cm.lookup("microphone");

            /* allocate the resource necessary for the recognizer */
            recognizer.allocate();

            /* the microphone will keep recording until the program exits */
	    if (microphone.startRecording()) {

		System.out.println
		    ("Say: (Good morning | Hello) " +
                     "( Bhiksha | Evandro | Paul | Philip | Rita | Will )");

		while (true) {
		    System.out.println
			("Start speaking. Press Ctrl-C to quit.\n");

                    /*
                     * This method will return when the end of speech
                     * is reached. Note that the endpointer will determine
                     * the end of speech.
                     */ 
		    Result result = recognizer.recognize();
		    
		    if (result != null) {
			String resultText = result.getBestFinalResultNoFiller();
			if (resultText.isEmpty()){
				System.out.println("You said nothing.");
			} else {
				System.out.println("You said: [" + resultText + "]\n");
				if (resultText.contains("turn")){
					if (resultText.contains("light")){
						if (resultText.contains("on")){
							Speak.speak("Okay, turning on the light now.");
						} else {
							Speak.speak("Okay, turning off the light now.");
						}
					} else if (resultText.contains("tv")) {
						if (resultText.contains("on")){
							Speak.speak("Okay, turning on the tv now.");
						} else {
							Speak.speak("Okay, turning off the tv now.");
						}
					}
				} else if (resultText.contains("time")){
					Calendar cal = Calendar.getInstance();
					String timetext = "";
					int hour = cal.get(Calendar.HOUR);
					switch (cal.get(Calendar.AM_PM)){
					case Calendar.AM:
						timetext = "in the morning";
						break;
					case Calendar.PM:
						if (hour >= 9){
							timetext = "in the midnight";
						} else if (hour >= 5){
							timetext = "in the evening";
						} else if (hour >= 1){
							timetext = "in the afternoon";
						} else if (hour >= 0){
							timetext = "in the noon";
						}
					}
					Speak.speak("The time is: " + hour + ":" + cal.get(Calendar.MINUTE) + timetext);
				} else if (resultText.contains("home")){
					Speak.speak("Okay, running home scene");
				} else if (resultText.contains("who") || resultText.contains("your name")){
					Speak.speak("I am Voice. Your Home automation assistant.");
				} else if (resultText.contains("shut up")){
					Speak.speak("You can shut me up by cutting down the power.");
				} else if (resultText.contains("voice")){
					//Do nothing
				} else {
					Speak.speak("I cannot do something like \"" + resultText + "\". Try other commands.");
				}
				microphone.clear();
			}
		    } else {
			System.out.println("I can't hear what you said.\n");
		    }
		}
	    } else {
		System.out.println("Cannot start microphone.");
		recognizer.deallocate();
		System.exit(1);
	    }
        } catch (IOException e) {
            System.err.println("Problem when loading HelloWorld: " + e);
            e.printStackTrace();
        } catch (PropertyException e) {
            System.err.println("Problem configuring HelloWorld: " + e);
            e.printStackTrace();
        }
	}
}
