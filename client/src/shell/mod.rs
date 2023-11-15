use std::process::{Command, Output};

pub fn execute_command(cmd: &str) -> Result<String, String> {
    #[cfg(unix)]
    {
        let output: Output = match Command::new("sh").arg("-c").arg(cmd).output() {
            Ok(output) => output,
            Err(err) => return Err(format!("Failed to execute command: {}", err)),
        };

        let result = if output.status.success() {
            String::from_utf8(output.stdout)
        } else {
            String::from_utf8(output.stderr)
        };

        result.map_err(|err| format!("Failed to convert command output to string: {}", err))
    }

    #[cfg(windows)]
    {
        let output: Output = match Command::new("cmd").arg("/C").arg(cmd).output() {
            Ok(output) => output,
            Err(err) => return Err(format!("Failed to execute command: {}", err)),
        };

        let result = if output.status.success() {
            String::from_utf8(output.stdout)
        } else {
            String::from_utf8(output.stderr)
        };

        result.map_err(|err| format!("Failed to convert command output to string: {}", err))
    }
}
