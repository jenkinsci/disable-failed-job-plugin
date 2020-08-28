package disableFailedJob.disableFailedJob;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

public class DisableFailedJobGlobal extends Builder {

	@Extension
	public static class Descriptor extends BuildStepDescriptor<Builder> {

		private String whenDisable;
		private String failureTimes;
		private String disableDescriptionUpdate;

		public Descriptor() {
			load();
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			whenDisable = failureTimes = disableDescriptionUpdate = null;
			JSONObject disableJobsJson = json.getJSONObject("disableJobs");
			if (disableJobsJson != null && !disableJobsJson.isNullObject()
					&& !disableJobsJson.isEmpty()) {
				whenDisable = disableJobsJson.getString("whenDisable");
				failureTimes = disableJobsJson.getString("failureTimes");
				disableDescriptionUpdate = disableJobsJson.getString("disableDescriptionUpdate");
			}
			save();
			return true;
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> arg0) {
			return false;
		}

		@Override
		public String getDisplayName() {
			return "Disable Failed Job Plugin";
		}

		public boolean toDisableFailedJobs() {
			return StringUtils.isNotBlank(whenDisable);
		}

		public String getWhenDisable() {
			return whenDisable;
		}

		public String getFailureTimes() {
			return failureTimes;
		}

		public String getDisableDescriptionUpdate() { return disableDescriptionUpdate; }

	}
}