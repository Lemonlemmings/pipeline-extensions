package io.jenkins.plugins.ShellEcho;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import hudson.tasks.Shell;

import javax.annotation.Nonnull;
import java.io.IOException;

public class ShellEchoExecution extends SynchronousNonBlockingStepExecution<String> {

    private String message;

    public ShellEchoExecution(@Nonnull StepContext context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    protected String run() throws Exception {
        Launcher launcher = this.getContext().get(Launcher.class);
        FilePath ws = this.getContext().get(FilePath.class);
        EnvVars envVars = this.getContext().get(EnvVars.class);
        TaskListener listener = this.getContext().get(TaskListener.class);

        Shell shellCommand = new Shell("echo " + message);
        FilePath script = shellCommand.createScriptFile(ws);
        int r = 0;
        try {
            r = launcher.launch().cmds(shellCommand.buildCommandLine(script)).envs(envVars).stdout(listener).pwd(ws).start().join();
            if(r != 0) {
                // Works
            }
            else {
                // Doesn't work
            }
        }
        finally {
            script.delete();
        }

        return Integer.toString(r);
    }
}
