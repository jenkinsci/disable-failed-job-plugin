package disableFailedJob.disableFailedJob;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;

public class ParameterDefinision extends SimpleParameterDefinition{
	
	private static final long serialVersionUID = 1L;
	
	public static final String JOB_DISABLE_WHEN_ONLY_FAIRURE = "Only Failure";
	public static final String JOB_DISABLE_WHEN_FAIRURE_AND_UNSTABLE = "Failure And Unstable";
	public static final String JOB_DISABLE_WHEN_ONLY_UNSTABLE = "Only Unstable";
	
	
	@DataBoundConstructor
	public ParameterDefinision(String name) {
		super(name);
	}

	@Override
	public ParameterValue createValue(String arg0) {
		return null;
	}

	@Override
	public ParameterValue createValue(StaplerRequest arg0, JSONObject arg1) {
		return null;
	}
	
	public static List<String> getJobDisableTimes() {
		List<String> jobDisableTimes = new ArrayList<String>();
		jobDisableTimes.add(JOB_DISABLE_WHEN_ONLY_FAIRURE);
		jobDisableTimes.add(JOB_DISABLE_WHEN_FAIRURE_AND_UNSTABLE);
		jobDisableTimes.add(JOB_DISABLE_WHEN_ONLY_UNSTABLE);
		return jobDisableTimes;
	}
}
