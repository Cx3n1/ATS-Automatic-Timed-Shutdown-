package cx3n1.projects.ats.jobs;

import cx3n1.projects.ats.ATSLogic;
import cx3n1.projects.ats.ATSWatchman;
import cx3n1.projects.ats.Alerts;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ShutdownJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ATSLogic.shutDownAfterGivenMinutes(ATSWatchman.LOADED_PRESET.getWarningTime());
            ATSWatchman.killShutdownScheduler();
        } catch (Exception e) {
            Alerts.error("Couldn't shutdown system!");
            e.printStackTrace();
        }

    }

}
