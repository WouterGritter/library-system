package me.gritter.librarysystem.cli;

import me.gritter.librarysystem.SimulatedDateTime;

public class CliTimeSystem implements CliSystem {
    @Override
    public boolean parseCommand(String[] parts) {
        if (parts.length < 1) {
            return false;
        }

        switch (parts[0]) {
            case "t":
                showTimeCommand();
                return true;
            case "a":
                advanceTimeCommand();
                return true;
            default:
                return false;
        }
    }

    private void showTimeCommand() {
        System.out.println("Current simulated date/time: " + SimulatedDateTime.now());
    }

    private void advanceTimeCommand() {
        SimulatedDateTime.nextDay();
        System.out.println("Simulated number of days increased. Current simulated date/time: " + SimulatedDateTime.now());
    }
}
