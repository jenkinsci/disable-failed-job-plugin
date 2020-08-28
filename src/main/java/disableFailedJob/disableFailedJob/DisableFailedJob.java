package disableFailedJob.disableFailedJob;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

public class DisableFailedJob extends Publisher {

	private final String whenDisable;
	private final String failureTimes;
	private final String optionalBlockChecked;

	private static final String FAILURE_DISCRIPTION = "This job has been disabled by 'Disable Failed Job Plugin' due to consecutive failures. ";
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	@DataBoundConstructor
	public DisableFailedJob(String whenDisable, OptionalBlock optionalBlock) {
		this.whenDisable = whenDisable;
		if (optionalBlock != null) {
			this.failureTimes = optionalBlock.failureTimes;
			optionalBlockChecked = "true";
		} else {
			failureTimes = null;
			optionalBlockChecked = "false";
		}
	}

	public String getWhenDisable() {
		return whenDisable;
	}

	public String getFailureTimes() {
		return failureTimes;
	}

	public String getOptionalBlockChecked() {
		return optionalBlockChecked;
	}

	@Override
	public Descriptor<Publisher> getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		int threshold = 0;
		if (failureTimes != null) {
			threshold = Integer.parseInt(failureTimes);
		}
		int lastSuccessBuildNumber = 0;
		if (build.getPreviousSuccessfulBuild() != null) {
			lastSuccessBuildNumber = build.getPreviousSuccessfulBuild()
					.getNumber();
		}
		int lastNotFaildBuildNumber = 0;
		if (build.getPreviousNotFailedBuild() != null) {
			lastNotFaildBuildNumber = build.getPreviousNotFailedBuild()
					.getNumber();
		}
		int lastFaildBuildNumber = 0;
		if (build.getPreviousCompletedBuild() != null) {
			lastFaildBuildNumber = build.getPreviousCompletedBuild()
					.getNumber();
		}

		int faildBuildCount = build.getNumber() - lastNotFaildBuildNumber;
		int notSuccessBuildCount = build.getNumber() - lastSuccessBuildNumber;
		int notUnstableBuildCount;
		if (lastSuccessBuildNumber < lastFaildBuildNumber) {
			notUnstableBuildCount = build.getNumber() - lastFaildBuildNumber;
		} else {
			notUnstableBuildCount = build.getNumber() - lastSuccessBuildNumber;
		}

		if (whenDisable
				.equals(ParameterDefinision.JOB_DISABLE_WHEN_ONLY_FAIRURE)) {
			if (build.getResult() == Result.FAILURE) {
				if (optionalBlockChecked.equals("true")
						&& faildBuildCount < threshold) {
					return false;
				}
				disableJob(build, listener);
			}
		} else if (whenDisable
				.equals(ParameterDefinision.JOB_DISABLE_WHEN_FAIRURE_AND_UNSTABLE)) {
			if (build.getResult() == Result.FAILURE
					|| build.getResult() == Result.UNSTABLE) {
				if (optionalBlockChecked.equals("true")
						&& notSuccessBuildCount < threshold) {
					return false;
				}
				disableJob(build, listener);
			}
		} else if (whenDisable
				.equals(ParameterDefinision.JOB_DISABLE_WHEN_ONLY_UNSTABLE)) {
			if (build.getResult() == Result.UNSTABLE) {
				if (optionalBlockChecked.equals("true")
						&& notUnstableBuildCount < threshold) {
					return false;
				}
				disableJob(build, listener);
			}
		} else {
			return false;
		}

		return true;
	}

	private void disableJob(AbstractBuild<?, ?> build, BuildListener listener)
			throws IOException {
		build.getProject().disable();
		String description = build.getProject().getDescription();
		if (description == null) {
			description = new String();
		}
		if (!description.contains(FAILURE_DISCRIPTION)) {
			description = description.concat(LINE_SEPARATOR
					+ FAILURE_DISCRIPTION);
		}
		build.getProject().setDescription(description);
		listener.getLogger()
				.println("'Disable Failed Job Plugin' Disabled Job");
	}

	@Extension
	public static final class DescriptorImpl extends Descriptor<Publisher> {

		private String whenDisable;
		private String failureTimes;
		private String optionalBlockChecked;

		public DescriptorImpl() {
			super(DisableFailedJob.class);
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		public String getDisplayName() {
			return "When Failed To Build, Disable Job";
		}

		public String whenDisable() {
			return whenDisable;
		}

		public String failureTimes() {
			return failureTimes;
		}

		public String optionalBlockChecked() {
			return optionalBlockChecked;
		}

	}

	public static class OptionalBlock {
		public String failureTimes;

		@DataBoundConstructor
		public OptionalBlock(String failureTimes) {
			this.failureTimes = failureTimes;
		}
	}
}
