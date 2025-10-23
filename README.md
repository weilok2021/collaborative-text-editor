# 🧑‍💻 Collaborative Text Editor (Java)

A **Java-based real-time collaborative text editor** that lets multiple users edit the same document simultaneously over a network.  
This project demonstrates key **Distributed Systems** concepts such as concurrency control, synchronization, and network communication using sockets.

---

## 🚀 Features

- 🧠 **Real-time Collaboration** — Multiple users can edit the same shared document together.
- 💬 **Live Updates** — Every keystroke is broadcast to all connected clients instantly.
- 🔗 **Client-Server Architecture** — A central server manages all connected clients and synchronizes document content.
- 💾 **Document Synchronization** — Ensures consistency of document data across all clients.
- 🖥️ **Simple GUI** — Built with Java Swing for a clean and responsive text editing interface.

---

## ⚙️ Workflow Overview

This project works through a **client-server model**:

1. **Server Setup**  
   - The server (`CollaborativeServer.java`) listens on a specific port (default: `5000`).  
   - It maintains the shared document content and a list of connected clients.

2. **Client Connection**  
   - Each client (`CollaborativeClient.java`) connects to the server using a socket connection.  
   - The client receives the current document content when it joins.

3. **Editing Process**  
   - When a user types or edits the text, the changes are sent to the server.  
   - The server broadcasts the update to all connected clients.  
   - Every client’s text area is refreshed to reflect the most recent version of the shared document.

4. **Real-Time Synchronization**  
   - All clients stay synchronized with minimal delay.  
   - The server ensures no client’s update is lost by handling all incoming messages in sequence.

---

## 🧩 Key Distributed System Concepts Demonstrated

| Concept | Description |
|----------|--------------|
| **Client-Server Architecture** | Demonstrates centralized coordination of multiple distributed clients. |
| **Concurrency & Synchronization** | Handles multiple users editing simultaneously without overwriting others’ updates. |
| **Socket Programming** | Uses Java sockets for persistent network connections and message broadcasting. |
| **State Consistency** | Maintains a consistent shared document state across all clients. |
| **Scalability** | Easily extendable to more clients or enhanced synchronization logic. |

---

## 🛠️ Getting Started
You need to have jdk installed on your machine to run this project. 
For my case, I use **jdk 21**. Then you can compile and run the programs.

### 1. Compile the files
```bash
javac CollaborativeServer.java
javac CollaborativeClient.java
```

### 2. Run the server
```bash
java CollaborativeServer
```

### 3. Run the clients
```bash
java CollaborativeClient
```
Each client will open a GUI window for editing, you can type random words in one window, and real time changes will also occur in another window!