# HomeAutoSys (Home Automation System) [![Build Status](https://travis-ci.org/mob41/HomeAutoSys.svg?branch=master)](https://travis-ci.org/mob41/HomeAutoSys)
Home Automation System designed for Home Automation. My goal is to program this system to be a professional automated controller.

##What is this used for?
Home automation is a system controlled by computers/modules which automate your things. For example, when you are so tried one day, you can just enter the house. The system will help you to turn on the lights automatically. And you can sleep on the sofa and relax. Once you leave the house, the system will turn off all the lights and home appliances.
The system is totally customized, you can edit your favourite scene and actions, even internal settings and internal terminal. The system will also include a web server, which allows you to turn off/on appliances on the web easily.

##Home automation cost lots of money and time!
Yep, lots of home automation projects on the web cost about US$500-6000. They also need a box to store the electric devices/home automation devices in it. They also need to pull wires around the house! It is so troublesome!

But I suggested to use wireless technology to control the things around the house. After designing, I used a normal wall switch to be the case of the wireless controller. I wired up a pull up resistor to the switch to the Arduino. The wireless module ESP8266 helped me a lot. And also wired up a relay module with it. After programming, it become a wireless relay module controller! It works well and I calculated the amps and the load. I sealed the low voltage part to prevent 220v directly touch the 5v part. The Raspberry Pi with the Touch screen acts a GUI to control them. It works well too! It only cost about US$55!

## Notes before building
You have to code before build.

1. Generate the 4 variable by calling the Main Class from HashKey.

```
    public static void main(String[] args) throws Exception{
    	//Change this to anything that you can read (AuthKey Password)
    	String authkey = "aUnHashedAuthKey";
    	//Change this to anything that you can read (Salt's password)
    	String saltpwd = "aPasswordForSalt";
    	String authkeysalt = getRandomSalt();
    	String saltpwdsalt = getRandomSalt();
    	String enauthkey = encrypt(authkey, authkeysalt);
    	String ensaltpwd = encrypt(saltpwd, saltpwdsalt);
    	System.out.println("Encrypting for AuthKey and Salt is completed.");
    	System.out.println("Put it into the code in the HashKey class\n");
    	System.out.println("Variable 's' (AuthKeyHash): " + enauthkey);
    	System.out.println("Variable 'h' (SaltPwdHash): " + ensaltpwd);
    	System.out.println("Variable 'y' (AuthKeySalt): " + authkeysalt);
    	System.out.println("Variable 'v' (SaltPwdSalt): " + saltpwdsalt);
    }
```

2. Put the generated hashes and salts into the code in the class HashKey

```
    /***
     * AuthKey Hash
     * <br>
     * You have to generate one with the salt and put it into the code, as it must be final.
     */
    private static final String s = "";
    /***
     * Salt Password Hash
     * <br>
     * You have to generate one with the salt and put it into the code, as it must be final.
     */
    private static final String h = "";
    /***
     * AuthKey Salt Encoded
     * <br>
     * You have to generate one with the hash and put it into the code, as it must be final.
     */
    protected static final String y = "";
    /***
     * Salt Password Salt
     * <br>
     * You have to generate one with the hash and put it into the code, as it must be final.
     */
    private static final String v = "";
    
```

The steps above are required for security reasons.

3. (Optional) Register (a account for only API accessing) at [http://www.pushbullet.com/](http://www.pushbullet.com) for messaging / sending notifications to users. And get your API key to put into the code.
```
public class PBClient {
	/***
	 * Your Pushbullet's Account API Key
	 * <br>
	 * I recommend you to create a account for API accessing 
	 */
	protected static final String apikey = "";
```

4. (Optional) Generate a salt for the PushbulletServer (PBServer class) by calling the Main Class in the PBServer class. And put the generated variable into the code.
```
	/***
	 * A salt for internal encrypting.
	 * <br>
	 * Generate it before build! Invoke PBServer.main and put into the code!
	 * <br><br>
	 * There are two layer of encrypting.
	 * <br>
	 * First Layer: From the file <br>
	 * Second Layer: From the code
	 */
	private static final String salt = "";
	
	public static void main(String[] args){
		String salt = HashKey.getRandomSalt();
		System.out.println("Salt is successfully generated for PushbulletServer Internal Encrypting.");
		System.out.println("Put it into the code in the PBServer class\n");
		System.out.println("Variable 'salt': " + salt);
	}
```

5. (Optional) Install HomeDashboard (in HTML5) to Apache2 / Web Servers. Changing the ```srvip``` from the ```homeautosys_opensources.js``` code. And put the website url to the code in the ```Conf``` class.
```
public class Conf {
/*
 * Default Config for GUI
 *
 * Read Config: String moduleIP = #com.rpi.homeautogui.#Config.module1ip
 * Edit Config: #com.rpi.homeautogui.Config.#module1ip = 1.1.1.1;
 * 
 * ## - No need to add Package if you are in the same package.
 * 
 */
	
	/***
	 * The server that is running HomeDashboard's WebURL
	 */
	public static final String thisServerHomeDashboardURL = "http://www.example.com/";
```

## Stage
- [x] Preparing, designing
- [x] Designing Javax Swing
- [x] Programming Arduino ESP8266 Controller (HoAuModule_Prg.ino)
- [x] Programming HomeAutoGUI-RPi
- [x] Release Development Preview 0.2
- [x] Setup Jetty and programming GUI
- [x] Programming Jetty / Internal WAR
- [x] Install HomeDashboard
- [x] Socket / Function Connections between Java and Web App
- [ ] Light Control (Missing DNA-Kit from Broadlink. Awaiting...)
- [ ] Final Test
