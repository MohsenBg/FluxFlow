# FluxFlow

FluxFlow is a powerful and flexible application designed for seamless communication and remote execution between client and server environments.

## Overview

FluxFlow operates on a client-server architecture, with a Rust-based client and a Java-based server. The client retrieves data from the server using specific message sizes and executes it in the shell. The server, equipped with a custom command line and GUI, awaits connections at a specified port until a client establishes a connection. Once connected, users can send commands and perform remote tasks on both Windows and UNIX-based systems.

## Features

- **Cross-Platform Compatibility:** FluxFlow supports remote execution on both Windows and UNIX-based systems, providing a versatile solution for various environments.

- **Custom Command Line:** The server side boasts a custom command line interface, enhancing user experience and control over remote tasks.

- **GUI Interface:** The server includes a text-based GUI in the console, making it accessible and user-friendly.

- **Single Connection Support:** The current version supports one connection at a time, ensuring focused and efficient communication between client and server.

## Getting Started

### Prerequisites

- Rust: [Installation Guide](https://www.rust-lang.org/tools/install)
- Java: [Download Java](https://www.oracle.com/java/technologies/javase-downloads.html)

### Installation

1. Clone the FluxFlow repository.

    ```bash
    git clone https://github.com/your-username/FluxFlow.git
    ```

2. Navigate to the `client` and `server` directories and follow the installation instructions provided.

### Usage

1. Start the FluxFlow server:

    ```bash
    java -jar bmt.jar
    ```

2. Run the FluxFlow client:

UnixBase:

    ```bash
    ./client
    ```
Windows:

    ```cmd
    ./client.exe
    ```


3. Follow on-screen instructions to establish a connection and start remote tasks.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

