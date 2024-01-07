package me.gritter.librarysystem;

import java.time.LocalDateTime;

public class SimulatedDateTime {

    private static int daysDelta = 0;

    public static void nextDay() {
        daysDelta += 1;
    }

    public static LocalDateTime now() {
        return LocalDateTime.now().plusDays(daysDelta);
    }
}
