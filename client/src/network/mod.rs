use std::error::Error;
use std::io::prelude::{Read, Write};
use std::net::TcpStream;

#[derive(Debug)]
pub struct Connection {
    is_connected: bool,
    ip_address: String,
    port: u16,
    stream: Option<TcpStream>,
}

impl Connection {
    pub fn new(ip_address: String, port: u16) -> Self {
        Self {
            is_connected: false,
            port,
            ip_address,
            stream: None,
        }
    }

    pub fn try_connect(&mut self) -> Result<(), Box<dyn Error>> {
        if self.is_connected {
            return Ok(());
        };

        let ip = format!("{}:{}", self.ip_address, self.port);
        match TcpStream::connect(&ip) {
            Ok(stream) => {
                self.stream = Some(stream);
                self.is_connected = true;
                Ok(())
            }
            Err(e) => Err(Box::new(e)),
        }
    }

    pub fn read_stream(&mut self) -> Result<String, String> {
        if !self.is_connected {
            return Err("No Connection Available".to_string());
        }

        match &mut self.stream {
            Some(stream) => {
                let mut buffer = String::new();
                let mut tmp_buffer = [0; 1];
                loop {
                    match stream.read_exact(&mut tmp_buffer) {
                        Ok(_) => {
                            let char_read = String::from_utf8_lossy(&tmp_buffer[..]);
                            buffer.push_str(&char_read);
                            if buffer.ends_with("\n") {
                                // Found a newline, stop reading
                                return Ok(buffer);
                            }
                        }
                        Err(_) => return Ok(String::new()), // Assuming you want to return an empty string on error
                    }
                }
            }
            None => Err("Stream not defined".to_string()),
        }
    }

    pub fn write_stream(&mut self, msg: String) -> Result<(), String> {
        if !self.is_connected {
            return Err("No Connection Available".to_string());
        }

        match &mut self.stream {
            Some(stream) => match stream.write(msg.as_bytes()) {
                Ok(_) => Ok(()),
                Err(e) => Err(format!("Error writing to the stream: {}", e)),
            },
            None => Err("Stream not defined".to_string()),
        }
    }
}
