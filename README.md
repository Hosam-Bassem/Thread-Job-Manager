# 🧵 Thread Job Manager

A thread-safe job scheduling system implemented in Java. This project simulates a multi-threaded environment where server threads are grouped and synchronized to execute jobs using concurrency control mechanisms like ReentrantLock, Condition, and FIFO queues.

## 💡 Overview

The `JobManager` is designed to coordinate multiple server threads using **Extrinsic Monitors** (`ReentrantLock` and `Condition`) to ensure safe and efficient execution of jobs. Each job specifies the required server types and quantities, and the `JobManager` releases exactly the matching group of threads once all conditions are met.

Key principles:
- **Thread-safety** without using thread-safe collections or polling
- **FIFO job scheduling**
- **Reverse ID server ordering** (for advanced requirements)
- Strict compliance with concurrency constraints

## 🚀 Features

- Accepts job specifications dynamically
- Manages mixed server types
- Blocks threads until job conditions are met
- Returns job names to servers upon assignment
- Prioritizes jobs in FIFO order
- Releases servers in **reverse ID order** when required

## ⚙️ Technologies

- Java SE 17
- `ReentrantLock` & `Condition` (Extrinsic Monitors)

## 🧪 Testing

Includes a suite of custom test cases in `Tests.java` to verify:
- Basic job assignment
- Mixed job and server logins
- Correct synchronization and blocking behavior
- FIFO scheduling and reverse ID release

## 📁 Structure

- `JobManager.java` – Handles thread synchronization, job queuing, and server assignment logic.  
- `Tests.java` – Contains unit tests and simulation cases for verifying system behavior.  
- `Main.java` – Launches the program and runs the defined test scenarios.  
- `Manager.java` – Defines the job manager interface and expected method signatures.  
- `JobRequest.java` – Represents the structure and attributes of individual job requests.  

## 📚 Learning Outcomes

- Implementing thread-safe classes from scratch
- Deep understanding of Java concurrency primitives
- Managing complex thread interactions in a real-world simulation
