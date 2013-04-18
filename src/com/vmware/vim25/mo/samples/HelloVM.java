/*================================================================================
Copyright (c) 2008 VMware, Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

* Neither the name of VMware, Inc. nor the names of its contributors may be used
to endorse or promote products derived from this software without specific prior 
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL VMWARE, INC. OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
================================================================================*/

package com.vmware.vim25.mo.samples;

import java.net.InetAddress;
import java.net.URL;
import java.rmi.RemoteException;

import org.sblim.slp.internal.ua.SLEnumerationImpl;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import com.vmware.vim25.mo.samples.network.TurnOnFirewallPolicy;
import com.vmware.vim25.mo.samples.storage.QueryVirtualDisk;
import com.vmware.vim25.mo.samples.vm.CloneVM;
import com.vmware.vim25.mo.samples.vm.MigrateVM;
public class HelloVM 
{
	static VirtualMachine[] vm = new VirtualMachine[8];
	public static ManagedObjectReference findSnapshotInTree(
		      VirtualMachineSnapshotTree[] snapTree, String snapName)
	  {
	    for(int i=0; i <snapTree.length; i++) 
	    {
	      VirtualMachineSnapshotTree node = snapTree[i];
	      if(snapName.equals(node.getName()))
	      {
	        return node.getSnapshot();
	      } 
	      else 
	      {
	        VirtualMachineSnapshotTree[] childTree = 
	            node.getChildSnapshotList();
	        if(childTree!=null)
	        {
	          ManagedObjectReference mor = findSnapshotInTree(
	              childTree, snapName);
	          if(mor!=null)
	          {
	            return mor;
	          }
	        }
	      }
	    }
	    return null;
	  }
		
	public static ManagedObjectReference getSnapshotInTree(
		      VirtualMachine vm, String snapName)
	  {
	    if (vm == null || snapName == null) 
	    {
	      return null;
	    }

	    VirtualMachineSnapshotTree[] snapTree = 
	        vm.getSnapshot().getRootSnapshotList();
	    if(snapTree!=null)
	    {
	      ManagedObjectReference mor = findSnapshotInTree(
	          snapTree, snapName);
	      if(mor!=null)
	      {
	        return mor;
	      }
	    }
	    return null;
	  }
	
public static void getStatistic(VirtualMachine vm) throws InvalidProperty, RuntimeFault, RemoteException{
	System.out.println("Network Statistics: ");
	Network[] vmN = vm.getNetworks();
	for(int i=0; i<vmN.length;i++)
	{
		System.out.println("Network IP Address: "+vmN[i]);
		System.out.println("Network Name: "+vmN[i].getName());
		
	}
		System.out.println("Gathering stattistics for VM:");
		VirtualMachineSummary VMsum = vm.getSummary();
		VirtualMachineQuickStats vmqStats = VMsum.getQuickStats();
		System.out.println("Overall CPU Usage:" + vmqStats.overallCpuUsage);
		System.out.println("Guest Memory Usage: "+vmqStats.guestMemoryUsage);
		System.out.println("Guest Status: " + vmqStats.guestHeartbeatStatus);
		VirtualMachineRuntimeInfo vmRuntime = VMsum.getRuntime();
	    System.out.println("Max CPU Usage: "+ vmRuntime.maxCpuUsage);
	    System.out.println("Max Memory Usage: "+vmRuntime.maxMemoryUsage);
	}
	
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Connecting to the vCenter....");
		long start = System.currentTimeMillis();
		String link = "https://130.65.157.200/sdk";
		String username = "Administrator";
		String password = "12!@qwQW";
		URL url = new URL(link);
		ServiceInstance si = new ServiceInstance(url, username, password, true);
		long end = System.currentTimeMillis();
		System.out.println("Time taken to connect:" + (end-start));
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		if(mes==null || mes.length ==0)
		{
			return;
		}
		
		System.out.println("Total VMs detected: "+mes.length );
		PingExample p = new PingExample();
		String[] guestIP = new String[8];
		int i;
			System.out.println("Gathering Information about The VMs");	
		for(i=0; i<mes.length; i++)
		{
		
			vm[i] = (VirtualMachine) mes[i];
		
		System.out.println("---------For VM " + i + " ------------");
		VirtualMachineConfigInfo vminfo = vm[i].getConfig();
		VirtualMachineCapability vmc = vm[i].getCapability();
       		
		vm[i].getResourcePool();
		System.out.println("VM General Info:");
		System.out.println("Hello " + vm[i].getName());
		System.out.println("GuestOS: " + vminfo.getGuestFullName());
		System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());
		
		// get statistics info
		getStatistic(vm[i]);
		String ipAddress = "130.65.157.55";
	    InetAddress inet = InetAddress.getByName(ipAddress);

	    System.out.println("Sending Ping Request to HOST: " + ipAddress);
	    String hostReachable = inet.isReachable(5000) ? "Host is reachable" : "Host is NOT reachable";
	    System.out.println(hostReachable);
		//failOver(vm);
		GuestInfo vmgs = vm[i].getGuest();
		//System.out.println("VMGS : " + vmgs);
		
		
		guestIP[i] = vmgs.getIpAddress();
		System.out.println("Guest Ip Address: " + guestIP[i]);
		}
		
		String failedVM = p.ping(guestIP, vm, link);
		int j;
		for(j =0 ; i< guestIP.length; i++)
	    	   if(guestIP[j] == failedVM)
	    		   break;
		
		ManagedObjectReference mor = getSnapshotInTree(vm[j], "test");
		
		System.out.println("MOR : " + mor.val);
		
		//vmclone
		CloneVM cvm = new CloneVM();
		cvm.clone(link, "administrator", "12!@qwQW", vm[j].getName(), failedVM + "_clone", mor);
		
		
		System.out.println("Host ip: 130.65.157.55");
		
//		 String ipAddress = "130.65.157.55";
//		    InetAddress inet = InetAddress.getByName(ipAddress);
//
//		    System.out.println("Sending Ping Request to HOST: " + ipAddress);
//		    String hostReachable = inet.isReachable(5000) ? "Host is reachable" : "Host is NOT reachable";
//		    System.out.println(hostReachable);
//		    if(hostReachable=="Host is reachable")
//		    {
	    MigrateVM migrate = new MigrateVM();
	    migrate.migrate(link, "Administrator", "12!@qwQW", failedVM + "_clone", "130.65.157.55");
	    System.out.println("Hurreyy!!!");
	   // }
//		    else 
//		    {
//		    	System.out.println("Host is not reachable. Unable to Migrate!");
//		    }
		    
	    	
	   // failedVM = p.ping(guestIP, vm, link);
		
		si.getServerConnection().logout(); 
		
//		String vmname = "Roopak_Vm1";
//		long start = System.currentTimeMillis();
//		final URL url = new URL("https://130.65.157.103/sdk");
//		final String username = "administrator";
//		final String password = "12!@qwQW";
//		ServiceInstance si = new ServiceInstance(url, username, password , true);
//		long end = System.currentTimeMillis();
//		System.out.println("time taken:" + (end-start));
//		final Folder rootFolder = si.getRootFolder();
//		String name = rootFolder.getName();
//		System.out.println("root:" + name);
////		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
////		if(mes==null || mes.length ==0)
////		{
////			return;
////		}
//		final VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname ); 
//		
//		//final HostSystem hs = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("", name);
//		VirtualMachineConfigInfo vminfo = vm.getConfig();
//		VirtualMachineCapability vmc = vm.getCapability();
////		
//		
//		//CloneVM c = new CloneVM();
//		//c.clone("https://130.65.157.53/sdk", "administrator", "12!@qwQW", "Roopak_Vm1", "Roopak_Vm1_Clone");
//		
//		
//		
//		vm.getResourcePool();
//		// Vm Information
//		System.out.println("Hello " + vm.getName());
//		System.out.println("GuestOS: " + vminfo.getGuestFullName());
//		System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());
//		
//		// CPU Statistics
//		VirtualMachineRuntimeInfo vmri = vm.getRuntime();
//		System.out.println("CPU statistics: ");
//		System.out.println("Maximum CPU Usage: "+ vmri.getMaxCpuUsage().toString());
//		System.out.println("Boot time: "+ vmri.getBootTime());
//		System.out.println("Memory Usage: "+ vmri.getMaxMemoryUsage().toString());
//		System.out.println("Memory Overload: "+ vmri.getMemoryOverhead().toString());
//		
//		// IO Statistics
//		// Use performanceManager and queryPerf 
//	
//		// Network Statistics
//		System.out.println("Network Statistics: ");
//		Network[] vmN = vm.getNetworks();
//		for(int i=0; i<vmN.length;i++)
//		{
//			System.out.println("Network IP Address: "+vmN[i]);
//			System.out.println("Network Name: "+vmN[i].getName());
//			
//		}
//		
//		
//
//		// First thread to keep pinging the host 
//		 new Thread(
//	        		new Runnable()
//	                {
//	                 public void run()
//	                    {    
//	                	 try{
//	                		GuestInfo vmgs = vm.getGuest();
//	                		vmgs.getHostName();
//	 	       				PingExample p = new PingExample();
//		 	       				while(true){
//				       				 String x = p.ping(vmgs.getIpAddress());
//				       				 System.out.println("Ping Result: "+x);
//		       			                	}
//	                       }
//	                       catch(Exception e)
//	                       {
//	                    	   e.printStackTrace();
//	                        }
//	                       }
//	                }
//	        ).start();
//		
//		 
//		 
//		 // Third thread for failure scenario 
////		 	new Thread(
////		 			new Runnable() {
////						
////						@Override
////						public void run() {
////							try {
////								Thread.sleep(10000);
////							} catch (InterruptedException e) {
////								// TODO Auto-generated catch block
////								e.printStackTrace();
////							}
////							
////							
////						    try {
////						    	GuestInfo vmgs1 = vm.getGuest();
////								String hostname = vmgs1.getHostName();
////								//String url1 = vmgs1.getIpAddress();
////								String username1 = "root";
////								String password = "12!@qwQW";
////							 //   Folder rootFolder = si.getRootFolder();
////							    HostSystem host = null;
////								host = (HostSystem) new InventoryNavigator(
////								    rootFolder).searchManagedEntity("HostSystem", hostname);
////								 if(host==null)
////								    {
////								      System.out.println("Cannot find the host:" + hostname);
////								    //  si.getServerConnection().logout();
////								      return;
////								    }
////								 TurnOnFirewallPolicy firewall = new TurnOnFirewallPolicy();
////								 firewall.OnFirewall(url, username1, password, hostname, host);
////							} catch (Exception e1) {
////								// TODO Auto-generated catch block
////								e1.printStackTrace();
////							}
////						}
////					}).start();
//		 	
//		 	
//		 	// Second thread to do something 
////		 new Thread(
////	        		new Runnable()
////	                {
////	                 public void run()
////	                    {    
////	                	 try{
////	                		System.out.println("This is my second thread!");
////	       				try{
////                         Thread.sleep(2000); 
////                       }
////                    catch(Exception exp)
////                      {
////                     			exp.printStackTrace();
////                       }
////	       			 }
////	                       catch(Exception e)
////	                       {
////	                        e.printStackTrace();
////	                        }
////	                       }
////	                }
////	        ).start();
//		 
//		 System.out.println("This is my main thread!");
    }
}
