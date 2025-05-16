// v001 11/10/2024

	//	To implement the required concurrent functionality, your JobManager must use two Extrinsic Monitor classes:
	//			java.util.concurrent.locks.Condition;
	//			java.util.concurrent.locks.ReentrantLock;
	//	Note that you must not use the signalAll() method (as this creates inefficient polling activity).
	//
	//	No other thread-safe,  synchronised or scheduling classes or methods may be used. In particular:
	//	•	The keyword synchronized, and other classes from the package java.util.concurrent must be not be used. 
	//	•	Thread.Sleep() and any other methods that affect thread scheduling must not be used.
	//	•	“Busy waiting” techniques, such as spinlocks, must not be used. 
	//	Other non-thread-safe classes from SE17 may be used, e.g. LinkedLists, HashMaps and ArrayLists 
 	//	(these are unsynchronised and therefore not thread-safe).

    //See the Coursework spec for full list of constraints marking penalties

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class JobManager implements Manager {
	
	// Lock to ensure thread-safe operations
	private final ReentrantLock lock = new ReentrantLock();
	
	// Stores available servers categorized by type (e.g., "ComputeServer", "StorageServer")
	private final HashMap<String, ArrayList<WorkerThread>> serverRegistry = new HashMap<>();
	
	// Storing job requests
    private final LinkedList<JobRequest> jobQueue = new LinkedList<>();

	@Override
	public void specifyJob(JobRequest job) {
		lock.lock();
		try {
			jobQueue.add(job); // Add job to the queue 
			handleJobAssignments(); // Attempt to assign servers
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String serverLogin(String type, int ID) {
	    lock.lock();
        try {
        	// Create a new WorkerThread representing the server
            WorkerThread serverNode = new WorkerThread(type, ID);
            
            // Register the server under its type if not already present
            serverRegistry.putIfAbsent(type, new ArrayList<>()); 
            serverRegistry.get(type).add(serverNode);

            // Attempt to assign a job if possible
            handleJobAssignments();
            
            // Wait until this server is assigned a job
            while (!serverNode.isAllocated()) serverNode.awaitTask();
            
            // Return the job name assigned to this server
            return serverNode.getTaskName();
        } finally {
        	lock.unlock();
        }
	}

	
	
	//==================================== PRIVATE METHODS & CLASSES  ===============================================
	
	 /**
     * Iterates through the job queue and assigns jobs to available servers.
     * Removes a job from the queue if all required servers are available.
     */
	private void handleJobAssignments() {
		for (int index = 0; index < jobQueue.size(); index++) {
			if (isAssignable(jobQueue.get(index))) {
				allocateResources(jobQueue.get(index));
				jobQueue.remove(index);
				index = -1;
			}
		}
	}
	
	 /**
     * Checks whether a job can be fully assigned based on available servers.
     * 
     * @param job The job request to check.
     * @return true if enough servers exist to fulfill the job request, false otherwise.
     */
	private boolean isAssignable(JobRequest job) {
		for (Map.Entry<String, Integer> requirement : job.entrySet()) {
			String category = requirement.getKey();
			int requiredCount = requirement.getValue();
			
			// If the required server type is missing or insufficient, the job cannot be assigned
			if (!serverRegistry.containsKey(category) || serverRegistry.get(category).size() < requiredCount) return false; 
		}
		return true; // The job can be assigned
	}
	
	/**
     * Allocates resources for a job by distributing the required servers.
     * 
     * @param job The job to be assigned servers.
     */
	private void allocateResources(JobRequest job) {
		for (Map.Entry<String, Integer> requirement : job.entrySet()) {
			distributeServers(job.jobName, requirement.getKey(), requirement.getValue());
		}
	}
	
	  /**
     * Assigns the specified number of servers to a job.
     * The highest ID servers are selected first (as required by UR6).
     * 
     * @param jobTitle The name of the job to which servers are assigned.
     * @param category The type of server needed (e.g., "ComputeServer").
     * @param count The number of servers required.
     */
	private void distributeServers(String jobTitle, String category, int count) {
		ArrayList<WorkerThread> availableNodes = serverRegistry.get(category);
		
		// Sort servers by ID in descending order (highest ID assigned first)
		availableNodes.sort(Comparator.comparingInt(WorkerThread::getID).reversed());
		
		// Assign the required number of servers
		for (int i = 0; i < count; i++) {
			WorkerThread assignedServer = availableNodes.remove(0);
			assignedServer.allocateJob(jobTitle);
		}
	}
	
	// Your inner classes and private methods here
	
	/**
     * Represents a server that registers itself with the JobManager and waits for job assignments.
     */
	private class WorkerThread {
		private final String category;
	    private final int serverID;
	    private String jobTask = "";
	    private boolean assigned = false;
	    private final Condition taskCondition = lock.newCondition(); 

	    /**
         * Constructor for WorkerThread.
         * 
         * @param category The type of server.
         * @param serverID The server ID.
         */
	    public WorkerThread(String category, int serverID) {
	        this.category = category;
	        this.serverID = serverID;
	    }

	    public String getTaskName() {
	        return jobTask;
	    }

	    public int getID() {
	        return serverID;
	    }

	    public boolean isAllocated() {
	        return assigned;
	    }
	    
	    /**
         * Assigns a job to the server and signals the waiting thread.
         * 
         * @param task The job name.
         */
	    public void allocateJob(String task) {
	        this.jobTask = task;
	        this.assigned = true;
	        lock.lock();
	        try {
	            taskCondition.signal();
	        } finally {
	            lock.unlock();
	        }
	    }
	    
	    /**
         * Puts the server thread into a waiting state until a job is assigned.
         */
	    public void awaitTask() {
	        lock.lock();
	        try {
	            taskCondition.await();
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        } finally {
	            lock.unlock();
	        }
	    }

	}

}
