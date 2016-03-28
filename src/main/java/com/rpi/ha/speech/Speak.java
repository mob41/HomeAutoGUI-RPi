package com.rpi.ha.speech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Speak {
	
	public static void main(String[] args){
		speak("Warning, the bus 2X is arriving the bus stop in 5 minutes");
	}
	
	public static void speak(String msg){
		speak(msg, "kevin16");
	}
	
	public static void speak(String msg, String voicetype){
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice voice = voiceManager.getVoice(voicetype);
		
		voice.allocate();
		voice.speak(msg);
		voice.deallocate();
	}
}
