package io.jenkins.plugins.GitStep;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.ShellExecution;
import jenkins.plugins.git.GitStep;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;

public class GitCommitStepExecute extends SynchronousNonBlockingStepExecution<String> {

    private transient TaskListener listener;
    private transient Launcher launcher;
    private transient Computer computer;
    private transient FilePath workspace;
    private transient EnvVars envVars;
    private transient Run run;

    private String repoUrl;
    private String checkoutBranch;
    private String commitBranch;
    private String credentialsId;

    private String localDir;
    private String remoteDir;
    private String message;

    private String tag;
    private String tagMessage;


    protected GitCommitStepExecute(@Nonnull StepContext context) {
        super(context);
    }

    @Override
    protected String run() throws Exception {
        this.listener = this.getContext().get(TaskListener.class);
        this.launcher = this.getContext().get(Launcher.class);
        this.computer = this.getContext().get(Computer.class);
        this.workspace = this.getContext().get(FilePath.class);
        this.envVars = this.getContext().get(EnvVars.class);
        this.run = this.getContext().get(Run.class);

        // Create the checkout directory
        FilePath checkoutDir = this.workspace.child("git");
        if(checkoutDir.exists()) {
            // Delete anything that exists there already
            checkoutDir.deleteContents();
        }
        else {
            // Create the folder if it doesn't exist
            checkoutDir.mkdirs();
        }

        String username = "";
        String password = "";

        // Change the repo URL so it has the username & password in it
        int index = this.repoUrl.indexOf("//") + 2;
        this.repoUrl = this.repoUrl.substring(0, index) + username + ":" + password +"@" + this.repoUrl.substring(index);

        // Set no SSL verification
        envVars.put("GIT_SSL_NO_VERIFY", "true");

        // Checkout git
        GitStep gitStep = new GitStep(this.repoUrl);
        gitStep.setBranch(this.checkoutBranch);
        gitStep.setCredentialsId(this.credentialsId);
        gitStep.checkout(this.run, checkoutDir, this.listener, this.launcher);

        // Set the commit branch up
        String output = ShellExecution.ExecuteShell("git checkout -B " + this.commitBranch, this.launcher, checkoutDir, this.envVars);
        this.listener.getLogger().println(output);

        // Copy new files into place
        FilePath localDirFilePath = new FilePath(computer.getChannel(), this.localDir);
        FilePath remoteDirFilePath = checkoutDir.child(remoteDir);
        if(!remoteDirFilePath.exists()) {
            remoteDirFilePath.mkdirs();
        }
        localDirFilePath.copyRecursiveTo(remoteDirFilePath);

        // Add all the new files into place
        output = ShellExecution.ExecuteShell("git add --all", this.launcher, checkoutDir, this.envVars);
        this.listener.getLogger().println(output);

        // Commit the new files
        output = ShellExecution.ExecuteShell("git commit -m \"" + this.message + "\"", this.launcher, checkoutDir, this.envVars);
        this.listener.getLogger().println(output);

        // Tag the release
        if(this.tag != null) {
            output = ShellExecution.ExecuteShell("git tag -a " + this.tag + " -m \"" + this.tagMessage + "\"", this.launcher, checkoutDir, this.envVars);
            this.listener.getLogger().println(output);
        }

        // Push the release
        output = ShellExecution.ExecuteShell("git push -u origin " + this.commitBranch + " --repo " + this.repoUrl + " --follow-tags", this.launcher, checkoutDir, this.envVars);
        this.listener.getLogger().println(output);

        // Delete the checkout
        checkoutDir.deleteContents();
        envVars.remove("GIT_SSL_NO_VERIFY");

        // Return commit hash?
        return null;
    }
}
