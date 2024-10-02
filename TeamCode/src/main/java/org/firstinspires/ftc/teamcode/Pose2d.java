package org.firstinspires.ftc.teamcode;

public class Pose2d {

    private double x, y, yaw;

    public Pose2d(double x, double y, double yaw) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getYaw() {
        return yaw;
    }

    public void setX(double newX) {
        x = newX;
    }

    public void setY(double newY) {
        y = newY;
    }

    public void setYaw(double newYaw) {
        yaw = newYaw;
    }

    public void updatePose(Pose2d newPose) {
        setY(newPose.getY());
        setX(newPose.getX());
        setYaw(newPose.getYaw());
    }

    public static boolean atPose(Pose2d currentPose, Pose2d targetPose, double coordTolerance, double yawTolerance) {
        boolean xCheck = Utilities.withinBounds(currentPose.getX(), targetPose.getX(), coordTolerance);
        boolean yCheck = Utilities.withinBounds(currentPose.getY(), targetPose.getY(), coordTolerance);
        boolean yawCheck = Utilities.withinBounds(currentPose.getYaw(), targetPose.getYaw(), yawTolerance);

        return xCheck && yCheck && yawCheck;
    }

}
