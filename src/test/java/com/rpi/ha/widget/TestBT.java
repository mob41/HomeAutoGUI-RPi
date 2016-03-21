package com.rpi.ha.widget;

//
// a Client to request Date/Time from server
// connection: RFCOMM
//

import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;


public class TestBT
{
	static Object lock= new Object();
	
  public static void main(String args[]) throws BluetoothStateException
  {


 try{
            // 1
            LocalDevice localDevice = LocalDevice.getLocalDevice();

            // 2
            DiscoveryAgent agent = localDevice.getDiscoveryAgent();
            
            // 3
            agent.startInquiry(DiscoveryAgent.GIAC, new MyDiscoveryListener());

            try {
                synchronized(lock){
                    lock.wait();
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Device Inquiry Completed. ");
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
 
  }
  
  
  
}


