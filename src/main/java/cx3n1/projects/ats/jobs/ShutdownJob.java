package cx3n1.projects.ats.jobs;

import cx3n1.projects.ats.ATSLogic;
import cx3n1.projects.ats.ATSSettings;
import cx3n1.projects.ats.ATSWatchman;
import cx3n1.projects.ats.utilities.Alerts;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ShutdownJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ATSLogic.shutDownAfterGivenMinutes(ATSSettings.getLoadedWarningTime());
        } catch (Exception e) {
            Alerts.error("Couldn't shutdown system!");
            e.printStackTrace();
        }

    }

}
