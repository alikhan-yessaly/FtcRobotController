package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "QuantumDrive")
public class QuantumDrive extends LinearOpMode {
    Servo intakeServo;
    Servo wristServo;
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("leftRear");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rightRear");
        DcMotor armExtend = hardwareMap.dcMotor.get("extension");
        DcMotor armLift = hardwareMap.dcMotor.get("lift");

        intakeServo = hardwareMap.servo.get("intake");
        wristServo = hardwareMap.servo.get("wrist");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        armExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        waitForStart();

        if (isStopRequested()) return;

        double rampUpRate = 0.1; // Adjust this value for faster/slower ramp-up
        double frontLeftPower = 0;
        double backLeftPower = 0;
        double frontRightPower = 0;
        double backRightPower = 0;
        double frontLeftTargetPower;
        double backLeftTargetPower;
        double frontRightTargetPower;
        double backRightTargetPower;

        double deadzone = 0.1; // Adjust this value for the deadzone of the joysticks

        // armLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armExtend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armExtend.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set initial position to 0
        int initialPosition = armLift.getCurrentPosition();
        int drivePosition = initialPosition + 300;
        wristServo.setPosition(0.1);
        // Main loop: run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // double y = gamepad1.left_stick_y;
            double y = Math.abs(gamepad1.left_stick_y) > deadzone ? gamepad1.left_stick_y : gamepad1.right_stick_y;
            double x = -gamepad1.right_stick_x * 1.1;
            double rx = -gamepad1.left_stick_x;
            // d
            boolean liftUp = gamepad1.left_bumper;
            boolean liftDown = gamepad1.left_trigger > 0.1;
            boolean extendOut = gamepad1.right_bumper;
            boolean retractIn = gamepad1.right_trigger > 0.1;

            if (gamepad1.a) {
                // Extract the piece
                intakeServo.setPosition(1);
            } else if (gamepad1.b) {
                // Intake the piece
                armLift.setTargetPosition(initialPosition);
                armLift.setPower(0.8);
                wristServo.setPosition(0.9);
                intakeServo.setPosition(0);
            }else {
                intakeServo.setPosition(0.5);
            }
            if (gamepad1.x) {
                // Up the wrist
                wristServo.setPosition(0.1);
            } else if (gamepad1.y) {
                // Down the wrist
                wristServo.setPosition(0.9);
            }
            boolean isDriving = !gamepad1.b && (Math.abs(gamepad1.left_stick_y) > deadzone ||
                    Math.abs(gamepad1.left_stick_x) > deadzone ||
                    Math.abs(gamepad1.right_stick_x) > deadzone);

            if (liftUp) {
                // Get current position and set it as the target
                int currentPosition = armLift.getCurrentPosition();
                armLift.setTargetPosition(currentPosition + 100); // Move up by 50 ticks
                armLift.setPower(0.8);
            } else if (liftDown) {
                // Get current position and set it as the target
                int currentPosition = armLift.getCurrentPosition();
                armLift.setTargetPosition(currentPosition - 100); // Move down by 50 ticks
                armLift.setPower(0.8);
            } else if (isDriving) {
                // Go to drive position when driving
                if (armLift.getCurrentPosition() < drivePosition) {
                    armLift.setTargetPosition(drivePosition);
                    armLift.setPower(0.8);
                }
            } else {
                // Hold the current position when not driving and no button is pressed
                int currentPosition = armLift.getCurrentPosition();
                armLift.setTargetPosition(currentPosition);
                armLift.setPower(0.8); // A small power to hold the position
            }

            if (extendOut) {
                // Get current position and set it as the target
                int currentPosition = armExtend.getCurrentPosition();
                armExtend.setTargetPosition(currentPosition + 500); // Move out by 10 ticks
                armExtend.setPower(0.8);
            } else if (retractIn) {
                // Get current position and set it as the target
                int currentPosition = armExtend.getCurrentPosition();
                armExtend.setTargetPosition(currentPosition - 500); // Move in by 10 ticks
                if (gamepad1.b){
                    armExtend.setPower(1);
                }else {
                    armExtend.setPower(0.5);
                }
            } else {
                // Hold the current position when no button is pressed
                int currentPosition = armExtend.getCurrentPosition();
                armExtend.setTargetPosition(currentPosition);
                armExtend.setPower(0.8); // A small power to hold the position
            }

            if (Math.abs(y) < deadzone && Math.abs(x) < deadzone && Math.abs(rx) < deadzone) {
                // Set target powers to 0 to stop motors immediately

                frontLeftTargetPower = 0;
                backLeftTargetPower = 0;
                frontRightTargetPower = 0;
                backRightTargetPower = 0;
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
            } else {
                // Calculate target powers based on gamepad input
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
                frontLeftTargetPower = 1.1*(y + x + rx) / denominator;
                backLeftTargetPower = (y - x + rx) / denominator;
                frontRightTargetPower = 1.1*(y - x - rx) / denominator;
                backRightTargetPower = (y + x - rx) / denominator;
            }

            // Ramp up motor powers towards target powers
            frontLeftPower += rampUpRate * (frontLeftTargetPower - frontLeftPower);
            backLeftPower += rampUpRate * (backLeftTargetPower - backLeftPower);
            frontRightPower += rampUpRate * (frontRightTargetPower - frontRightPower);
            backRightPower += rampUpRate * (backRightTargetPower - backRightPower);

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
        }
    }
}
