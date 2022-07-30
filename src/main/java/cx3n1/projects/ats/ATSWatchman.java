package cx3n1.projects.ats;

import cx3n1.projects.ats.interfaces.StaticObservable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.quartz.JobBuilder.newJob;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman extends StaticObservable {

    public static void startupSequence() throws Exception {
        ATSSettings.startupSequence();
        //notifyChange() ATSSettings.startupSequence() will notify everybody, so extra notification is useless
        //ATSLogic.mainLogic(); same this is called in ATSSettings.startupSequence()
    }

    public static void shutdownSequence() throws Exception {
        //could add some code here if needed...
        ATSSettings.saveSettingsIntoDataFile();
    }

}
