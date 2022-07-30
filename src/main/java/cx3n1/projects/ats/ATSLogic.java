package cx3n1.projects.ats;

import cx3n1.projects.ats.data.Preset;
import cx3n1.projects.ats.jobs.ShutdownJob;
import cx3n1.projects.ats.utilities.Alerts;
import org.apache.commons.lang3.SystemUtils;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ATSLogic {

    public static void mainLogic() throws Exception {
        Preset currentPreset = ATSSettings.getLoadedPreset();

        if(!currentPreset.checkIfGivenDayIsChecked(LocalDate.now().getDayOfWeek()))
            return;

        if(LocalTime.now().isBefore(currentPreset.getTimeOfShutdown())){
            scheduleShutdownWaiter(currentPreset);
        }
    }

    public static void shutDownAfterGivenMinutes(int minutes) throws Exception {
        String shutdownCommand = null;

        if(SystemUtils.IS_OS_AIX)
            shutdownCommand = "shutdown -Fh " + minutes;
        else if(SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC|| SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_NET_BSD || SystemUtils.IS_OS_OPEN_BSD || SystemUtils.IS_OS_UNIX)
            shutdownCommand = "shutdown -h " + minutes;
        else if(SystemUtils.IS_OS_HP_UX)
            shutdownCommand = "shutdown -hy " + minutes;
        else if(SystemUtils.IS_OS_IRIX)
            shutdownCommand = "shutdown -y -g " + minutes;
        else if(SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS)
            shutdownCommand = "shutdown -y -i5 -g" + minutes;
        else if(SystemUtils.IS_OS_WINDOWS)
            shutdownCommand = "shutdown.exe /s /t " + minutes*60; //Windows takes seconds as delay
        else{
            Alerts.error("We are sorry, but currently ATS doesn't support your operating system!");
            return;
        }

        ATSWatchman.shutdownSequence();
        Runtime.getRuntime().exec(shutdownCommand);
    }

    private static void scheduleShutdownWaiter(Preset currentPreset) throws SchedulerException {
        JobKey jobKey = new JobKey("shutdownWaiter");

        JobDetail job = newJob(ShutdownJob.class)
                .withIdentity(jobKey)
                .build();

        long epochTimeOfShutdown = currentPreset.getTimeOfShutdown().toEpochSecond(LocalDate.now(), ATSSettings.ZONE_OFFSET);
        Date dateOfShutdown = new Date((epochTimeOfShutdown - (long) currentPreset.getWarningTime() * 60) * 1000);

        Trigger trigger = newTrigger()
                .withIdentity("shutdownTrigger")
                .startAt(dateOfShutdown)
                .build();

        ATSSettings.removeJobFromScheduler(jobKey);
        ATSSettings.scheduleJobOnScheduler(job, trigger);
    }
}
