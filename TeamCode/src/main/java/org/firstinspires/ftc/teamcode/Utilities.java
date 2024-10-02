package org.firstinspires.ftc.teamcode;

public class Utilities {

    public static boolean withinBounds(double input, double target, double tolerance) {
        return (input < target + tolerance) && (input > target - tolerance);
    }

    public static double clip(double input, double max, double min) {
        return Math.max(Math.min(input, max), min);
    }

}
