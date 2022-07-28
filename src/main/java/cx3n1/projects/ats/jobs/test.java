package cx3n1.projects.ats.jobs;

import cx3n1.projects.ats.ATSSettings;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class test implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ATSSettings.progress.set((ATSSettings.progress.getValue() + 0.01)%1);
    }
}
