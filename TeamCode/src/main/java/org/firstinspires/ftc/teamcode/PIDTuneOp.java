//package org.firstinspires.ftc.teamcode;
//
//import com.acmerobotics.roadrunner.control.PIDCoefficients;
//import com.acmerobotics.roadrunner.control.PIDFController;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.firstinspires.ftc.teamcode.subsystems.Mecanum;
//import org.firstinspires.ftc.teamcode.subsystems.Winch;
//
//@TeleOp(name = Constants.OpModes.pidOp, group = Constants.OpModes.linearOp)
//public class PIDTuneOp extends LinearOpMode {
//
//    subsystems[] options = {subsystems.MECANUMY, subsystems.MECANUMX, subsystems.MECANUMR, subsystems.WINCH};
//    int currentOption = 0;
//
//    PID[] pid = {PID.P, PID.I, PID.D};
//    int currentPID = 0;
//
//    double p = 0;
//    double i = 0;
//    double d = 0;
//
//    double interval = 0.01;
//
//    double setPoint = 15;
//    double measurement = 0;
//    boolean isRunning = false;
//
//    int pidSwap = 0;
//    int systemSwap = 0;
//    int pidIncrement = 0;
//
//    Mecanum drive;
//    Winch winch;
//
//    PIDFController pidController = new PIDFController(new PIDCoefficients(p, i, d), 0);
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//
//        waitForStart();
//
//        drive = new Mecanum(hardwareMap);
//        winch = new Winch(hardwareMap);
//        while (opModeIsActive()) {
//
//            boolean change = false;
//            //Subsystem Selector
//            if (systemSwap == 0) {
//                if (gamepad1.left_bumper) {
//                    currentOption = (int) Utilities.clip(currentOption - 1, options.length - 1, 0);
//                    systemSwap++;
//                    change = true;
//                } else if (gamepad1.right_bumper) {
//                    currentOption = (int) Utilities.clip(currentOption + 1, options.length - 1, 0);
//                    systemSwap++;
//                    change = true;
//                }
//
//                if (change) {
//                    switch (options[currentOption]) {
//                        case MECANUMY:
//                            pidController = new PIDFController(Constants.MecanumConstants.yPID, 0);
//                            break;
//                        case MECANUMX:
//                            pidController = new PIDFController(Constants.MecanumConstants.xPID, 0);
//                            break;
//                        case MECANUMR:
//                            pidController = new PIDFController(Constants.MecanumConstants.yawPID, 0);
//                            break;
//                        case WINCH:
//                            pidController = new PIDFController(Constants.WinchConstants.winchPID, 0);
//                            break;
//                    }
//                }
//
//            } else if (!gamepad1.left_bumper && !gamepad1.right_bumper) {
//                systemSwap = 0;
//            }
//
//            //PID Selector
//            if (pidSwap == 0) {
//                if (gamepad1.dpad_left) {
//                    currentPID = (int) Utilities.clip(currentPID - 1, pid.length - 1, 0);
//                    pidSwap++;
//                } else if (gamepad1.dpad_right) {
//                    currentPID = (int) Utilities.clip(currentPID + 1, pid.length - 1, 0);
//                    pidSwap++;
//                }
//            } else if (!gamepad1.dpad_left && !gamepad1.dpad_right) {
//                pidSwap = 0;
//            }
//
//            //PID Selector
//            if (pidIncrement == 0) {
//                if (gamepad1.dpad_up) {
//
//                    switch (pid[currentPID]) {
//                        case P:
//                            p += interval;
//                            break;
//                        case I:
//                            i += interval;
//                            break;
//                        case D:
//                            d += interval;
//                            break;
//                    }
//                    pidIncrement++;
//
//                } else if (gamepad1.dpad_down) {
//                    switch (pid[currentPID]) {
//                        case P:
//                            p -= interval;
//                            break;
//                        case I:
//                            i -= interval;
//                            break;
//                        case D:
//                            d -= interval;
//                            break;
//                    }
//                    pidIncrement++;
//                }
//            } else if (!gamepad1.dpad_down && !gamepad1.dpad_up) {
//                pidIncrement = 0;
//            }
//
//            if (gamepad1.b) {
//                pidController = new PIDFController(new PIDCoefficients(p, i, d), 0);
//            }
//
//            if (gamepad1.a) {
//
//                isRunning = true;
//
//                periodic();
//
//                switch (options[currentOption]) {
//                    case MECANUMY:
//                        pidController.setTargetPosition(15);
//                        while (!Utilities.withinBounds(drive.getY(), pidController.getTargetPosition(), 0.5) && gamepad1.a) {
//                            measurement = drive.getY();
//                            drive.drive(pidController.update(drive.getY()), 0, 0, false);
//                            periodic();
//                        }
//                        break;
//                    case MECANUMX:
//                        pidController.setTargetPosition(15);
//                        while (!Utilities.withinBounds(drive.getX(), pidController.getTargetPosition(), 0.5) && gamepad1.a) {
//                            measurement = drive.getX();
//                            drive.drive(0, pidController.update(drive.getX()), 0, false);
//                            periodic();
//                        }
//                        break;
//                    case MECANUMR:
//                        pidController.setTargetPosition(30);
//                        while (!Utilities.withinBounds(drive.getYaw(), pidController.getTargetPosition(), 1.5) && gamepad1.a) {
//                            measurement = drive.getYaw();
//                            drive.drive(0, 0, pidController.update(drive.getYaw()), false);
//                            periodic();
//                        }
//                        break;
//                    case WINCH:
//                        pidController.setTargetPosition(1000);
//                        while (!Utilities.withinBounds(winch.getPosition(), pidController.getTargetPosition(), Constants.WinchConstants.tolerance)  && gamepad1.a) {
//                            measurement = winch.getPosition();
//                            winch.setPower(pidController.update(winch.getPosition()));
//                            telemetry.addData("update", pidController.update(winch.getPosition()));
//                            periodic();
//                        }
//                        break;
//                }
//
//            }
//
//            winch.setPower(0);
//            drive.drive(0,0,0,true);
//
//            isRunning = false;
//
//            periodic();
//        }
//
//    }
//
//    public void periodic() {
//        telemetry.addData("Current Subsystem: ", options[currentOption]);
//        telemetry.addData("Current Edit: ", pid[currentPID]);
//        telemetry.addData("P: ", p);
//        telemetry.addData("I ", i);
//        telemetry.addData("D ", d);
//        telemetry.addData("Setpoint ", setPoint);
//        telemetry.addData("Measurement", measurement);
//        telemetry.addData("Running ", isRunning);
//        telemetry.addData("at setpoint", Utilities.withinBounds(winch.getPosition(), pidController.getTargetPosition(), 1));
//
//        telemetry.update();
//    }
//
//    enum subsystems {
//        MECANUMY, MECANUMX, MECANUMR, WINCH;
//    }
//
//    enum PID {
//        P, I, D;
//    }
//
//}