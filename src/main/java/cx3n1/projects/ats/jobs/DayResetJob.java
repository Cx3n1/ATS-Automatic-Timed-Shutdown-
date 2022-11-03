package cx3n1.projects.ats.jobs;

import cx3n1.projects.ats.ATSWatchman;
import cx3n1.projects.ats.utilities.Alerts;
import javafx.application.Platform;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DayResetJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //TODO: this could be without resetting whole thing
        try {
            System.out.println("Initiated Day Reset");

            Thread.sleep(1000);

            //this is really inelegant solution but what gives this is java motherfu*ker! (or I'm really stupid <- probably this)
            Platform.runLater(() -> {
                try {
                    ATSWatchman.startupSequence();
                } catch (Exception e) {
                    Alerts.error("Couldn't do end of the day system reinitialization!");
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            Alerts.error("Couldn't do end of the day system reinitialization!");
            e.printStackTrace();
        }
    }
}
