package cx3n1.projects.ats.jobs;

import cx3n1.projects.ats.ATSWatchman;
import cx3n1.projects.ats.utilities.Alerts;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DayResetJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            //TODO: this could be without resetting whole thing
            ATSWatchman.startupSequence();
        } catch (Exception e) {
            Alerts.error("Couldn't do end of the day system reinitialization!");
            e.printStackTrace();
        }
    }
}
