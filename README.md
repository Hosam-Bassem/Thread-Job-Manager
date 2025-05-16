# 🧵 Thread Job Manager

A thread-safe job scheduling system implemented in Java as part of the **Concurrency (F29OC)** module at Heriot-Watt University. This project simulates a multi-threaded environment where server threads are synchronized and grouped to execute specified jobs based on strict concurrency rules.

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

- `JobManager.java` – Core implementation
- `Tests.java` – Custom and example test cases
- `Main.java` – Entry point for test execution
- `Manager.java` – Provided interface (not modified)
- `JobRequest.java` – Defines job structure (not modified)

## 📚 Learning Outcomes

- Implementing thread-safe classes from scratch
- Deep understanding of Java concurrency primitives
- Managing complex thread interactions in a real-world simulation
- Writing meaningful commit histories for Git-based development
