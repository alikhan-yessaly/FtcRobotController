package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "QuantumDrive")
public class QuantumDrive extends LinearOpMode {
    Servo clawServo;
    Servo wristServo;
    Servo arm1Servo;
    Servo arm0Servo;
    Servo liftUpServo;
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("leftRear");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rightRear");
        DcMotor armLift1 = hardwareMap.dcMotor.get("lift1");
        DcMotor armLift2 = hardwareMap.dcMotor.get("lift2");

        clawServo = hardwareMap.servo.get("claw");
        wristServo = hardwareMap.servo.get("wrist");
        arm1Servo = hardwareMap.servo.get("arm1");
        arm0Servo = hardwareMap.servo.get("arm0");
        liftUpServo = hardwareMap.servo.get("liftUp");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        armLift1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armLift2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        armLift1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        armLift2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Main loop: run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // double y = gamepad1.left_stick_y;
            double y = Math.abs(gamepad1.left_stick_y) > deadzone ? gamepad1.left_stick_y : gamepad1.right_stick_y;
            double x = -gamepad1.right_stick_x * 1.1;
            double rx = -gamepad1.left_stick_x;
            // dn
            boolean liftUp = gamepad1.left_bumper;
            boolean liftDown = gamepad1.left_trigger > 0.1;
            boolean extendOut = gamepad1.right_bumper;
            boolean retractIn = gamepad1.right_trigger > 0.1;

            if (gamepad1.a) {
                // Extract the piece
                clawServo.setPosition(0.8);
            } else if (gamepad1.b) {
                // Intake the piece
                wristServo.setPosition(0.5);
                clawServo.setPosition(0);
            }else {
                clawServo.setPosition(0);
            }

            if (gamepad1.x) {
                // Up the wrist
                wristServo.setPosition(0.0);
            } else if (gamepad1.y) {
                // Down the wrist
                wristServo.setPosition(0.5);
            }

            if (liftUp) {
                // Get current position and set it as the target
                int currentPosition1 = armLift1.getCurrentPosition();
                armLift1.setTargetPosition(currentPosition1 + 100); // Move up by 50 ticks
                armLift1.setPower(0.8);
                int currentPosition2 = armLift2.getCurrentPosition();
                armLift2.setTargetPosition(currentPosition2 + 100); // Move up by 50 ticks
                armLift2.setPower(0.8);
            } else if (liftDown) {
                // Get current position and set it as the target
                int currentPosition1 = armLift1.getCurrentPosition();
                armLift1.setTargetPosition(currentPosition1 - 100); // Move down by 50 ticks
                armLift1.setPower(0.8);
                int currentPosition2 = armLift2.getCurrentPosition();
                armLift2.setTargetPosition(currentPosition2 - 100); // Move down by 50 ticks
                armLift2.setPower(0.8);
            } else {
                // Hold the current position when not driving and no button is pressed
                int currentPosition1 = armLift1.getCurrentPosition();
                armLift1.setTargetPosition(currentPosition1);
                armLift1.setPower(0.5); // A small power to hold the position
                // Hold the current position when not driving and no button is pressed
                int currentPosition2 = armLift2.getCurrentPosition();
                armLift2.setTargetPosition(currentPosition2);
                armLift2.setPower(0.5); // A small power to hold the position
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
