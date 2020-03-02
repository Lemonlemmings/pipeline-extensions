package io.jenkins.plugins.GitStep;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import io.blueocean.rest.pipeline.editor.BlueOceanStepDescriptor;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.HashSet;
import java.util.Set;

public class GitCommitStep extends Step {

    @DataBoundConstructor
    public GitCommitStep() {

    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new GitCommitStepExecute(stepContext);
    }

    @Extension
    public static class DescriptorImpl extends BlueOceanStepDescriptor {

        public DescriptorImpl() {
            super();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> contextUsed = new HashSet<>();
            contextUsed.add(FilePath.class);
            contextUsed.add(EnvVars.class);
            return contextUsed;
        }

        @Override public String getFunctionName() {
            return "gitCommit";
        }

        @Override public String getDisplayName() {
            return "Git Commit";
        }
    }
}
