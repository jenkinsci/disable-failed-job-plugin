package disableFailedJob.listener;

import hudson.Extension;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.io.IOException;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;

import disableFailedJob.disableFailedJob.DisableFailedJob;
import disableFailedJob.disableFailedJob.DisableFailedJobGlobal;

@SuppressWarnings("rawtypes")
@Extension
public class DisableFailedJobRunListener extends RunListener<Run> {

	private static final Logger LOGGER = Logger
			.getLogger(DisableFailedJobRunListener.class.getName());

	public DisableFailedJobRunListener() {

	}

	@SuppressWarnings("unchecked")
	public DisableFailedJobRunListener(Class targetType) {
		super(targetType);
	}

	@Override
	public void onCompleted(Run r, TaskListener listener) {
		DisableFailedJobGlobal.Descriptor descriptor = (DisableFailedJobGlobal.Descriptor) Jenkins
				.getInstance().getDescriptor(DisableFailedJobGlobal.class);
		if (descriptor != null) {
			if (StringUtils.isNotBlank(descriptor.getWhenDisable())) {
				if (r instanceof AbstractBuild) {
					AbstractBuild<?, ?> build = (AbstractBuild) r;
					// Check if the job already has disable Failed job post
					// build action
					if (build.getProject().getPublishersList()
							.get(DisableFailedJob.class) == null) {
						// Construct the publisher with the values from global
						// configuration
						DisableFailedJob disableFailedJob = new DisableFailedJob(
								descriptor.getWhenDisable(),
								descriptor.getDisableDescriptionUpdate(),
								new DisableFailedJob.OptionalBlock(descriptor.getFailureTimes()));
						try {
							// Call perform to run the check and disable if
							// required
							disableFailedJob.perform(build, null,
									(BuildListener) listener);
						} catch (InterruptedException e) {
							listener.getLogger().print(
									"Interrupted exception while running disable failed job: "
											+ e);
						} catch (IOException e) {
							listener.getLogger().print(
									"IO exception while running disable failed job: "
											+ e);
						}
					}
				}
			}
		}
	}

}
