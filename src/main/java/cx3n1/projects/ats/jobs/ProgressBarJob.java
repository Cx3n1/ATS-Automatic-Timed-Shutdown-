package cx3n1.projects.ats.jobs;

import cx3n1.projects.ats.ATSSettings;
import cx3n1.projects.ats.controllers.MainViewController;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ProgressBarJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        MainViewController.ProgressBarController.updateProgress();
    }
}
