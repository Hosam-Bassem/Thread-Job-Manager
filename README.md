# F29OC-2024-25-CW1

# Stub project

This project contains the stub files for your project.

# IMPORTANT
**You MUST fork this project into your remote repository BEFORE cloning it to your local disc space.**




Note that the Tests file contains two example tests - these tests will produce incorrect output if run without editing JobManager.java.

If run without edits you should get:

```
UR2 EXAMPLE TEST:
Event log:
main: starting 4 ComputeServers and 1 StorageServer:
Thread-0: started & calling serverLogin(ComputeServer, ID=100)
Thread-2: started & calling serverLogin(ComputeServer, ID=100)
Thread-3: started & calling serverLogin(ComputeServer, ID=100)
Thread-4: started & calling serverLogin(StorageServer, ID=100)
Thread-1: started & calling serverLogin(ComputeServer, ID=100)
Thread-2: server_type=ComputeServer, job=, ID=100 -- released by jobManager.
Thread-1: server_type=ComputeServer, job=, ID=100 -- released by jobManager.
Thread-4: server_type=StorageServer, job=, ID=100 -- released by jobManager.
Thread-0: server_type=ComputeServer, job=, ID=100 -- released by jobManager.
Thread-3: server_type=ComputeServer, job=, ID=100 -- released by jobManager.
main: calling specifyJob(jobName=job01, job={ComputeServer=2, StorageServer=1})
main: Sleeping main to allow Servers time to be released
main: Expect 2 ComputeServer and 1 StorageServer to be released:


UR6 EXAMPLE TEST:
Event log:
main: starting 5 ComputeServers, ID=[0, 1, 2, 3, 4]
Thread-5: started & calling serverLogin(ComputeServer, ID=0)
Thread-5: server_type=ComputeServer, job=, ID=0 -- released by jobManager.
Thread-6: started & calling serverLogin(ComputeServer, ID=1)
Thread-6: server_type=ComputeServer, job=, ID=1 -- released by jobManager.
Thread-7: started & calling serverLogin(ComputeServer, ID=2)
Thread-7: server_type=ComputeServer, job=, ID=2 -- released by jobManager.
Thread-8: started & calling serverLogin(ComputeServer, ID=3)
Thread-8: server_type=ComputeServer, job=, ID=3 -- released by jobManager.
Thread-9: started & calling serverLogin(ComputeServer, ID=4)
Thread-9: server_type=ComputeServer, job=, ID=4 -- released by jobManager.
main: threads started, now specifying job1.
main: calling specifyJob(jobName=job01, job={ComputeServer=2})
main: expect two ComputeServers 'job01' [ID=3&4] to be released:
main: job1 specified, now specifying job2.
main: calling specifyJob(jobName=job02, job={ComputeServer=2})
main: expect two ComputeServers 'job02' [ID=1&2] to be released:
```
the above is not correct as all server threads have been immediately released.




