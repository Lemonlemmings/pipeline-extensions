package io.jenkins.plugins;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.tasks.Shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ShellExecution {
    public static String ExecuteShell(String script, Launcher launcher, FilePath workingDir, EnvVars envVars) throws Exception {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        Shell shellCommand = new Shell(script);
        FilePath scriptFile = shellCommand.createScriptFile(workingDir);
        int returnValue = launcher.launch().cmds(shellCommand.buildCommandLine(scriptFile)).envs(envVars).pwd(workingDir).stdout(stdout).start().join();
        if(returnValue != 0) {
            throw new Exception("Non-zero return value: " + returnValue);
        }
        return new String(stdout.toByteArray());
    }
}
