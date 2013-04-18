package com.vmware.vim25.mo.samples;


import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.tempuri.* ;

import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.samples.vm.VMSnapshot;

public class PingExample
{
	boolean f1 = false;
	
	
   
    public String ping( String[] ip, VirtualMachine[] vm, String link ) throws Exception
    {
//       System.out.println( "Ping Host: " + host ) ;
//       Service service = new Service();
//       ServiceSoap port = service.getServiceSoap(); 
//      
//       String result = port.pingHost( host ) ;
//       System.out.println( "Ping Result: " + result ) ;
    	int count=0;
    	boolean[] flags = new boolean[10];
    	long timer = 0;
    	int[] a = new int[10];
       for(int i =0 ; i< ip.length; i++)
    	   if(ip[i] != null){
    		   a[count++] = i;
    		   System.out.println(" IP address of VM : " + i + " IP addr: " + ip[i]);
    		   flags[i] = false;
    	   }
    		   
       System.out.println("count : " + count);
     while(true){
    	 long start = System.currentTimeMillis();
       
    	 for(int i=0; i<count; i++){
    		   InetAddress inet = InetAddress.getByName(ip[a[i]]);
    		   System.out.println("VM IP address : " + i);
    	       System.out.println("Sending Ping Request to " + ip[a[i]]);
    	       boolean flag = inet.isReachable(10000) ? true : false;
    	       Thread.sleep(1000);
    	       long end = System.currentTimeMillis();
    	       timer = timer + (end-start);
    	       System.out.println("Time elapsed: "+timer);
    	       System.out.println("Ping result: " + flag);
    	       if(flag){
    	    	//   if(!f1){
    	    		   System.out.println("inside if");
    	    		  
    	    		   if(timer>10000)
    	    		   {
    	    			   System.out.println("30 minutes up. Create a snapshot!");
    	    		   VMSnapshot snapshot = new VMSnapshot();
    	    		   // removing previous snapshot
    	    		  // snapshot.snapshot(link, "administrator", "12!@qwQW", vm[a[i]].getName(), "remove");
    	    		   System.out.println("Removed the previous snap shot");
    	    		   snapshot.snapshot(link, "administrator", "12!@qwQW", vm[a[i]].getName(), "create");
    	    		   System.out.println("snapshot created");
    	    		   System.out.println("Reseting timer to 0!");
    	    		   timer=0;
    	    		   }
    	   		
    	    	//   }   	   
    	       }
    	       else{
    	    	   if(flags[i]==false)
    	    	   {
    	    		   flags[i] = true;
    	    		   String state = vm[a[i]].getRuntime().getPowerState().toString();
    	    		   VMPowerStateAlarm alarm = new VMPowerStateAlarm();
    	    		   if( state == "poweredOff")
    	    		   {
    	    			   alarm.alarm(link, "administrator", "12!@qwQW", vm[a[i]].getName(), alarm + "i");
    	    			   System.out.println("Alarm created on " + vm[a[i]].getName() + " Virtual Machine");
    	    		   }
    	    		   else
    	    		   {
    	    			   return ip[a[i]];
    	    		   }
    	    	   }
    	       }
    	    	   
    	 }
    	 f1 = true;
       
     }

    }
}
