package io.jenkins.plugins.ShellEcho;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import io.blueocean.rest.pipeline.editor.BlueOceanStepDescriptor;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.HashSet;
import java.util.Set;

public class ShellEchoStep extends Step {

    private final String message;
    private boolean test;

    @DataBoundConstructor
    public ShellEchoStep(String message) {
        this.message = message;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new ShellEchoExecution(stepContext, message);
    }

    public boolean isTest() {
        return test;
    }

    @DataBoundSetter
    public void setTest(boolean test) {
        this.test = test;
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
            return "shEcho";
        }

        @Override public String getDisplayName() {
            return "Shell Echo";
        }
    }
}
