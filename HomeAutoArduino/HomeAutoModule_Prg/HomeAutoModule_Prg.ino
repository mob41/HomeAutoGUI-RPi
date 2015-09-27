#include <ESP8266.h>

#define _ledpin     13
#define _panelled 5
#define _switchpin  3
#define _relaypin 4

//*-- Module Number
#define modNo "001"

//*-- Hardware Serial
#define _baudrate 9600

//*-- IoT Information
#define SSID "MaTt"
#define PASS "matthewlaw"
#define IP "192.168.168.115"
#define PORT 7979
ESP8266 wifi(Serial);
void setup() {
    Serial.begin(_baudrate);
    Serial.println("AT");
    delay(5000);
    if(Serial.find("OK"))
    {
        if(connectWiFi())
        {
          startServer();
        }
        else
        {
          
        }
    }
    
    pinMode( _ledpin, OUTPUT);
    pinMode(_relaypin, OUTPUT);
    pinMode(_switchpin, INPUT);
    digitalWrite( _ledpin, LOW );
}

int switchStatus = 0;

void loop() {
  int lastRec = 0;
  int val = 0;
  workoklight();
  while (0 < 1)
  {
    val = digitalRead(_switchpin);
    if (val != lastRec)
    {
      if (val == 1)
      {
        digitalWrite(_relaypin, 1);
        updateStatus(1);
        lastRec = 1;
      }
      else
      {
        digitalWrite(_relaypin, 0);
        updateStatus(0);
        lastRec = 0;
      }
    }
    if (Serial.find("getSwitchStatus"))
    {
      updateStatus(lastRec);
    } 
    else if (Serial.find("switchOn"))
    {
      digitalWrite(_relaypin, 1);
      updateStatus(1);
    }
    else if (Serial.find("switchOff"))
    {
      digitalWrite(_relaypin, 0);
      updateStatus(0);
    }
  }
  
}

void updateStatus(int sstatus)
{
  String message = "";
  String StStatus = "";
  if (sstatus == 1)
  {
    String StStatus = "on";
  }
  else
  {
    String StStatus = "off";
  }
  if(wifi.createTCP(2, IP, PORT)){
    delay(500);
    String stringmessage = "postSwitchStatus";
    stringmessage += modNo;
    stringmessage += sstatus;
    const char* message = stringmessage.c_str();
    if(wifi.send(2, (const uint8_t*)message, strlen(message)))
    {
      workoklight();
      wifi.releaseTCP(2);
    }
    else
    {
      errorlight();
    }
  }
  else{
    errorlight();
  }
}

boolean connectWiFi()
{
  if (wifi.setOprToStation())
  {
    if (wifi.joinAP(SSID, PASS))
    {
      return true; 
    }
    else
    {
      return false;
    }
  }
  else
  {
    return false;
  }
}

boolean startServer()
{
  if(wifi.enableMUX())
  {
    if(wifi.startTCPServer(7979))
    {
      wifi.setTCPServerTimeout(10);
      return true;
    }
    else
    {
      return false;
    }
  }
  else
  {
    return false;
  }
}

void workoklight(){
  digitalWrite(13, 1);
  delay(250);
  digitalWrite(13, 0);
  delay(250);
  digitalWrite(13, 1);
  delay(250);
  digitalWrite(13, 0);
  delay(250);
}

void errorlight(){
  digitalWrite(13, 1);
  delay(1000);
  digitalWrite(13, 0);
}

