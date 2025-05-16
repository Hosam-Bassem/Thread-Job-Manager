

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        EXAMPLE TESTS
//        Provided as examples ONLY
//
//        NOTE: 1. When these tests are run with the original JobManager.java file they 
//                 will fail to produce the correct output (i.e. that described in the tests below).
//				   As the JobManager supplied has no code in its .serverLogin() method, calls to this 
//                 method will return immediately (thereby 'releasing' ALL server threads to continue).
//              2. You must write your own tests here to make sure that your JobManager.java meets the UR
//              3. You may use any Java SE17 libraries or code in THIS Tests.java file but the only concurrent, 
//				   thread safe classes you may use in JobManager.java are ReentrantLock and its Condition variables.
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// v001 11/10/2024




import java.util.concurrent.ConcurrentLinkedQueue;

public class Tests {
	//Declare global list of events to log ServerThread completions in:
	ConcurrentLinkedQueue<String> events;	//"wait-free" FIFO queue
	String threadName = Thread.currentThread().getName();
	
	public void testUR1() {
		// Initialize event queue to log thread activities
		events = new ConcurrentLinkedQueue<>();
		JobManager manager = new JobManager();
			        
		// Log the start of server threads
		events.add(threadName + ": Starting 5 ComputeServer threads");
		
		// Start 5 ComputeServer threads, each calling serverLogin()
		for (int i = 0; i < 5; i++) {
			new ServerThread(manager, "ComputeServer", i).start();
		}
			        
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        
		// Create a job request that requires 2 ComputeServer instances
		JobRequest job01 = new JobRequest("job01");
		job01.put("ComputeServer", 2);
		events.add(threadName + ": Calling specifyJob(" + job01.toString() + ")");
		
		// Submit the job request to JobManager
		manager.specifyJob(job01);
			        
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		
		System.out.println("\n=== UR1 Test Output ===");
		for (String event : events) {
			System.out.println(event);
		}
	}
			
	public void exampleUR2Test (){
		// This test 
		//   1. starts four ServerThreads of type = "ComputeServer" and one of type "StorageServer"
		//           Each of these threads calls: manager.serverLogin(type, ID);
		//   2. main thread sleeps to allow ServerThreads to start and run
		//   3. One job is specified ("job01") that requires two ComputeServers and one StorageServer
		//           jobName=job01, job={ComputeServer=2, StorageServer=1}
		//   4. Your JobManager should now release two ComputeServers and one StorageServer to run
		//   5. A ConcurrentLinkedQueue 'events' is used to collect ServerThreads events in a non-blocking way.
		//   6. The printout should be of the following form:
		//		Thread Releases:
		//				Thread-0: server_type=ComputeServer, job=job01, ID=100 -- released by jobManager.
		//				Thread-4: server_type=StorageServer, job=job01, ID=100 -- released by jobManager.
		//				Thread-1: server_type=ComputeServer, job=job01, ID=100 -- released by jobManager.
		//      Note that 
		//          a) the above lines can be in any order.
		//          b) value of ID does not matter in this example as there is only one job 
		//          c) the names of the threads ('Thread-0' etc. can change)



		events = new ConcurrentLinkedQueue<String>(); //We are using this 
		JobManager manager = new JobManager();	
		
		//Start four "ComputeServer"s and one "StorageServer":
		events.add(threadName + ": starting 4 ComputeServers and 1 StorageServer:" ); //"wait-free" FIFO queue
		for (int i=0; i < 4; i++) (new ServerThread(manager, "ComputeServer", 100)).start();
		(new ServerThread(manager, "StorageServer", 100)).start();
		
		//Sleep main to allow ServerThreads to execute:
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} 

		//Create job request for two "ComputeServer"s and one "StorageServer":		
		JobRequest job01 = new JobRequest("job01");
		job01.put("ComputeServer", 2);
		job01.put("StorageServer", 1);	
		events.add(threadName +": calling specifyJob(" + job01.toString() + ")");		
		manager.specifyJob(job01);		

		//Sleep main to allow ServerThreads to complete:
		events.add(threadName + ": Sleeping main to allow Servers time to be released");
		events.add(threadName + ": Expect 2 ComputeServer and 1 StorageServer to be released:");
		
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} 

		//Now print out the event log:
		System.out.println("Event log:");
		for (String event : events) System.out.println(event);		
	}
	
	public void testUR3() {
		// Initialize event queue to log thread activities
		events = new ConcurrentLinkedQueue<>();
		JobManager manager = new JobManager();
			        
		// Log the start of UR3 test execution
		events.add(threadName + ": Starting testUR3");
		
		// Specify jobs first (before any servers log in)	    
		// Create a job request (job01) that requires 2 ComputeServers
		JobRequest job01 = new JobRequest("job01");
		job01.put("ComputeServer", 2);
		events.add(threadName + ": Calling specifyJob(" + job01.toString() + ")");
		manager.specifyJob(job01);
			    
		// Create another job request (job02) that requires 1 StorageServer
		JobRequest job02 = new JobRequest("job02");
		job02.put("StorageServer", 1);
		events.add(threadName + ": Calling specifyJob(" + job02.toString() + ")");
		manager.specifyJob(job02);
			    
		// Start servers after jobs are queued
		for (int i = 0; i < 2; i++) new ServerThread(manager, "ComputeServer", i).start();
		
		// Start 1 StorageServer thread that will call serverLogin()
		new ServerThread(manager, "StorageServer", 1).start();
			        
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			    
		System.out.println("\n=== UR3 Test Output ===");
		for (String event : events) System.out.println(event);
	}
	
	public void testUR4() {
		// Initialize event queue to log thread activities
		events = new ConcurrentLinkedQueue<>();
		JobManager manager = new JobManager();
		
		events.add(threadName + ": Starting testUR4");
		
		// Specify and start servers alternately
		JobRequest job01 = new JobRequest("job01");
		job01.put("ComputeServer", 2);
		events.add(threadName + ": Calling specifyJob(" + job01.toString() + ")");
		manager.specifyJob(job01);
		
		// Start 1 ComputeServer thread, which calls serverLogin()
		new ServerThread(manager, "ComputeServer", 1).start();
		
		// Create another job request (job02) that requires 1 StorageServer
		JobRequest job02 = new JobRequest("job02");
		job02.put("StorageServer", 1);
		events.add(threadName + ": Calling specifyJob(" + job02.toString() + ")");
		manager.specifyJob(job02);
		
		// Start another ComputeServer thread
		new ServerThread(manager, "ComputeServer", 2).start();
		
		// Start 1 StorageServer thread
		new ServerThread(manager, "StorageServer", 1).start();
		
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		
		System.out.println("\n=== UR4 Test Output ===");
		for (String event : events) System.out.println(event);
	}
	
	public void testUR5() {
		events = new ConcurrentLinkedQueue<>();
		JobManager manager = new JobManager();
		
		events.add(threadName + ": Starting testUR5");
		
		// Start servers first
		new ServerThread(manager, "ComputeServer", 1).start();
		new ServerThread(manager, "StorageServer", 1).start();
		
		// Specify jobs
		JobRequest job01 = new JobRequest("job01");
		job01.put("ComputeServer", 1);
		events.add(threadName + ": Calling specifyJob(" + job01.toString() + ")");
		manager.specifyJob(job01);
		
		JobRequest job02 = new JobRequest("job02");
		job02.put("StorageServer", 1);
		events.add(threadName + ": Calling specifyJob(" + job02.toString() + ")");
		manager.specifyJob(job02);
		
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		
		System.out.println("\n=== UR5 Test Output ===");
		for (String event : events) System.out.println(event);
	}  

	public void exampleUR6test (){
		// This test 
		//   1. starts five ServerThreads of type = "ComputeServer" and ID = 0 to 4
		//           Each of these threads calls: manager.serverLogin(type, ID);
		//   2. main thread sleeps to allow ComputeServers to start and run
		//   3. job01 is specified to require two ComputeServers
		//   4. main sleeps to allow job01 to release the two ComputeServers with the highest IDs (4 & 3)
		//   4. job02 is specified again to require two ComputeServers
		//   5. main sleeps to allow job02 to release the two ComputeServers with the next highest IDs (2 & 1)
		//   6. The printout should be of the following form:
		//			...
		//			Thread-4: server_type=ComputeServer, job=job01, ID=4 -- released by jobManager.
		//			Thread-3: server_type=ComputeServer, job=job01, ID=3 -- released by jobManager.
		//			...
		//			Thread-1: server_type=ComputeServer, job=job02, ID=1 -- released by jobManager.
		//			Thread-2: server_type=ComputeServer, job=job02, ID=2 -- released by jobManager.
		//	    Note that:
		//         job1's ComputeServers must have IDs of 3 & 4, but we do not know which will be released first (ID 3 or 4).
		//         Similarly, while job2's ComputeServers must have IDs of 1 & 2, we also do not know which will be released first.
		//         So:
		//			...
		//			Thread-3: server_type=ComputeServer, job=job01, ID=3 -- released by jobManager.
		//			Thread-4: server_type=ComputeServer, job=job01, ID=4 -- released by jobManager.
		//			...
		//			Thread-2: server_type=ComputeServer, job=job02, ID=2 -- released by jobManager.
		//			Thread-1: server_type=ComputeServer, job=job02, ID=1 -- released by jobManager.
		//		
		//		   would also be a correct console output.
		//	       Note that the names of the threads ('Thread-0' etc. can change)	   
		//		
		events = new ConcurrentLinkedQueue<String>(); //"wait-free" FIFO queue
		JobManager manager = new JobManager();	
		
		JobRequest job01 = new JobRequest("job01");
		job01.put("ComputeServer", 2);		
		JobRequest job02 = new JobRequest("job02");
		job02.put("ComputeServer", 2);	
		
		//Start 5 server threads:
		events.add(threadName + ": starting 5 ComputeServers, ID=[0, 1, 2, 3, 4]" );
		for (int i=0; i < 5; i++) (new ServerThread(manager, "ComputeServer", i)).start();
		//Sleep main to allow server threads to execute:
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} //sleep 1
		events.add(threadName +": threads started, now specifying job1." );
		
		//Specify jobs:	
		events.add(threadName +": calling specifyJob(" + job01.toString() + ")");
		events.add(threadName +": expect two ComputeServers 'job01' [ID=3&4] to be released:");		
		manager.specifyJob(job01);
		//Sleep main to allow job1 server threads to complete:
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} //sleep 2
		events.add(threadName +": job1 specified, now specifying job2." );
		events.add(threadName +": calling specifyJob(" + job02.toString() + ")");
		events.add(threadName +": expect two ComputeServers 'job02' [ID=1&2] to be released:");	
		manager.specifyJob(job02);	
		//Sleep main to allow job2 server threads to complete:
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} //sleep 3

		System.out.println("Event log:");
		for (String event : events) System.out.println(event);		
	}

	private class ServerThread extends Thread {
		JobManager manager;
		String type;
		int ID = 100;
		String threadName;
		ServerThread (JobManager manager, String type, int ID){
			this.manager = manager;
			this.type = type;
			this.ID = ID;
		};
		public void run(){
			this.threadName = Thread.currentThread().getName();
			events.add(threadName +": started & calling serverLogin(" + type + ", ID=" + ID + ")" );
			String job = manager.serverLogin(type, ID);
	        events.add(threadName +": server_type=" + type + ", job=" + job + ", ID=" + ID + " -- released by jobManager." );
		};	
	};
	

}