//--------------------------------------------------//
//                                                  //
//                ENTRADOX ROBOTICS                 //
//                                                  //
//--------------------------------------------------//

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "drive")
public class drive extends OpMode {
    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;

    DcMotor leg;
    DcMotor arm;

    CRServo mouth;
    Servo head;
    Servo foot;

    double controlPower = 0.7; // Initial gear

    // Variables to track servo positions
    double headPosition = 0.0;
    double footPosition = 0.0;

    boolean previousRightBumper = false;
    boolean previousLeftBumper = false;
    boolean previousRightTriggerPressed = false;

    @Override
    public void init() {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        leg = hardwareMap.dcMotor.get("leg"); // slide
        arm = hardwareMap.dcMotor.get("arm"); // arm

        mouth = hardwareMap.crservo.get("mouth"); // feeder
        head = hardwareMap.servo.get("head");   // turner
        foot = hardwareMap.servo.get("foot");   // dropper

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);

        leg.setDirection(DcMotor.Direction.FORWARD);
        arm.setDirection(DcMotor.Direction.FORWARD);

        // // Initialize servo positions
        headPosition = 0.5;
        footPosition = 0.5;

    }

    @Override
    public void loop() {
        double leftStickY = -gamepad1.left_stick_y;
        double leftStickX = gamepad1.left_stick_x;
        double rightStickX = gamepad1.right_stick_x;

        double frontLeftPower = leftStickY - leftStickX + rightStickX;
        double frontRightPower = leftStickY + leftStickX - rightStickX;
        double backLeftPower = leftStickY + leftStickX + rightStickX;
        double backRightPower = leftStickY - leftStickX - rightStickX;

        double maxPower = Math.max(
            Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
            Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
        );

        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower /= maxPower;
            backRightPower /= maxPower;
        }

        // Gear shifting using bumpers
        boolean currentRightBumper = gamepad1.right_bumper;
        boolean currentLeftBumper = gamepad1.left_bumper;

        // Increase controlPower when the right bumper is pressed
        if (currentRightBumper && !previousRightBumper) {
            controlPower += 0.2;
            if (controlPower > 2.0) {
                controlPower = 2.0; // Maximum limit
            }
        }

        // Decrease controlPower when the left bumper is pressed
        if (currentLeftBumper && !previousLeftBumper) {
            controlPower -= 0.2;
            if (controlPower < 0.1) {
                controlPower = 0.1; // Minimum limit
            }
        }

        // Reset gear when RT is pressed
        boolean currentRightTriggerPressed = gamepad1.right_trigger > 0.1;

        if (currentRightTriggerPressed && !previousRightTriggerPressed) {
            controlPower = 0.7; // Reset to default gear
        }

        // Update previous bumper and trigger states
        previousRightBumper = currentRightBumper;
        previousLeftBumper = currentLeftBumper;
        previousRightTriggerPressed = currentRightTriggerPressed;

        // Log the current gear
        telemetry.addData("⚙️ Current Gear", "%.2f", controlPower);

        // Set motor powers with the adjusted controlPower
        frontLeftMotor.setPower(frontLeftPower * controlPower);
        frontRightMotor.setPower(frontRightPower * controlPower);
        backLeftMotor.setPower(backLeftPower * controlPower);
        backRightMotor.setPower(backRightPower * controlPower);

        double leftStickY2 = gamepad2.left_stick_y;
        double rightStickY2 = gamepad2.right_stick_y;

        // Arm and leg control
        arm.setPower(leftStickY2 * 0.4);
        leg.setPower(rightStickY2 * 0.7);

        //servos
        if (gamepad2.x) {
            telemetry.addData("headpos", 0.9);
            head.setPosition(0.9);
        } else if (gamepad2.b) {
            head.setPosition(0.5);
            telemetry.addData("headpos", 0.5);
        }
        if (gamepad2.y) {
            foot.setPosition(0.7);
            telemetry.addData("footpos", 0.9);
        } else if (gamepad2.a) {
            foot.setPosition(0.5);
            telemetry.addData("footpos", 0.5);
        }

        if (gamepad2.dpad_up) {
            mouth.setPower(-3);
        }
        mouth.setPower(0);

        // // Telemetry for debugging
        // telemetry.addData("Mouth Position", "%.2f", mouthPosition);
        // telemetry.addData("Head Position", "%.2f", headPosition);
        // telemetry.addData("Foot Position", "%.2f", footPosition);
        // telemetry.update();
    }
}
