package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Ian on 11/2/2017.
 */

@Autonomous(name="CaluperAuto", group="LinearOpMode")
//@Disabled
public class CaluperAuto extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();

    public DcMotor lfDrive;     // Left Front Drive
    public DcMotor rfDrive;     // Left Back Drive
    public DcMotor lbDrive;     // Right Front Drive
    public DcMotor rbDrive;     // Right Back Drive
    public DcMotor liftMotor;   // Lift Glyph Grabber

    public Servo lServoArm;
    public Servo rServoArm;
    public Servo jewelArm;

    ColorSensor sensorColor;

    private double lServoArmInit = .73;
    private double rServoArmInit = .1;
    private double lServoArmGrasp = .43;
    private double rServoArmGrasp = .50;
    private double speedFactor = .5;
    private int controlMode = 1;

    boolean blue = false;
    boolean red  = false;
    boolean rightStone = false;
    boolean leftStone  = false;
    boolean AutoFinished = false;

    public void drive(double leftPower, double rightPower) {    // Turns on motors. The reason
        lfDrive.setPower(leftPower);                            // there are two powers is so the
        lbDrive.setPower(leftPower);                            // robot may be allowed to curve.
        rfDrive.setPower(leftPower);
        rbDrive.setPower(leftPower);
    }

    public void driveStop() {
        lfDrive.setPower(0);
        lbDrive.setPower(0);
        rfDrive.setPower(0);
        rbDrive.setPower(0);
    }

    public void bumpRedJewel() throws InterruptedException {
        jewelArm.setPosition(1);
        sleep(3000);
        jewelArm.setPosition(.5);
        if(sensorColor.red() > sensorColor.blue()) {
            drive(0.5, 0.5);
            sleep(600);
            driveStop();
        } else if(sensorColor.blue() > sensorColor.red()) {
            drive(0.5, 0.5);
            sleep(600);
            driveStop();
        }
        jewelArm.setPosition(0);
        sleep(3000);
        jewelArm.setPosition(.5);
    }

    public void bumpBlueJewel() {
        jewelArm.setPosition(1);
        sleep(3000);
        jewelArm.setPosition(.5);
        if(sensorColor.blue() > sensorColor.red()) {
            drive(0.5, 0.5);
            sleep(600);
            driveStop();
        } else if(sensorColor.red() > sensorColor.blue()) {
            drive(0.5, 0.5);
            sleep(600);
            driveStop();
        }
        jewelArm.setPosition(0);
        sleep(3000);
        jewelArm.setPosition(.5);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // ===================================INIT===================================
        lfDrive = hardwareMap.dcMotor.get("lfDrive"); //Left front drive, Hub 1, port 2
        lfDrive.setPower(0);
        lfDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lbDrive = hardwareMap.dcMotor.get("lbDrive"); //Left back drive, Hub 1, port 3
        lbDrive.setPower(0);
        lbDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rfDrive = hardwareMap.dcMotor.get("rfDrive"); //Right front drive, Hub 1, port 1
        rfDrive.setPower(0);
        rfDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rbDrive = hardwareMap.dcMotor.get("rbDrive"); //Right back drive, Hub 1, port 0
        rbDrive.setPower(0);
        rbDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        liftMotor = hardwareMap.dcMotor.get("liftMotor"); //Lift motor, Hub 2, port 3
        liftMotor.setPower(0);

        lServoArm = hardwareMap.servo.get("lServoArm"); //Left servo arm, Hub 1, port 2
        lServoArm.setPosition(lServoArmInit);

        rServoArm = hardwareMap.servo.get("rServoArm"); //Right servo arm, Hub 2, port 1
        rServoArm.setPosition(rServoArmInit);

        jewelArm = hardwareMap.servo.get("ja"); //Jewel Arm, Hub 1, Port 3
        jewelArm.setPosition(.5);

        sensorColor = hardwareMap.get(ColorSensor.class, "sensor_color_distance");

        // =======================BEGIN SELECTION============================================
        telemetry.addData("Selection", "X for Blue, B for Red");
        telemetry.update();
        while(!blue && !red) {
            if(gamepad1.x) {
                blue = true;
            } else if(gamepad1.b) {
                red = true;
            }
        }
        sleep(500);
        telemetry.addData("Selection", "X for Left, B for Right");
        telemetry.update();
        while(!leftStone && !rightStone) {
            if(gamepad1.x) {
                leftStone = true;
            } else if(gamepad1.b) {
                rightStone = true;
            }
        }

        if(blue) {
            telemetry.addData("Team", "Blue");
        } else if(red) {
            telemetry.addData("Team", "Red");
        }

        if(leftStone) {                         // The selection here is based on the field edge
            telemetry.addData("Stone", "Left"); // without any cryptoboxes is on your right when you're blue.
        } else if(rightStone) {                 // If you're red, it's on your left.
            telemetry.addData("Stone", "Right");
        }

        telemetry.update();
        waitForStart();
        timer.reset();
        // =======================================AUTONOMOUS==============================
        while(timer.seconds() < 30 || !AutoFinished) {
            if(blue) {
                if(leftStone) {
                    // Ian's Auto
                    AutoFinished = true;
                } else if(rightStone) {
                    // Auto
                    AutoFinished = true;
                }
            } else if (red) {
                if(leftStone) {
                    // Auto
                    AutoFinished = true;
                } else if(rightStone) {
                    // Auto
                    AutoFinished = true;
                }
            }
        }
        driveStop();                                                   // Stop robot
        while(timer.seconds() < 30) {                         // Robot waits for Auto time to be up.
            sleep(50);
            telemetry.addData("Auto finished early!", timer.seconds());
            telemetry.update();
        }
        timer.reset();
        // =========================================TRANSITION===============================
        while(timer.seconds() <= 8 || !gamepad1.a) {
            telemetry.addData("Auto finished!", timer.seconds());
            telemetry.addData("At 8s, Teleop will begin", "Override by pressing A"); // Just in case
            telemetry.update();                                        // timer won't let robot move
            sleep(50);
        }
        timer.reset();
        //=================================TELEOP=========================================
        while(opModeIsActive()) {
            if(gamepad1.right_bumper) {
                speedFactor = 1;
            } else {
                speedFactor = .5;
            }

            if(gamepad1.a) {            // classic
                controlMode = 1;
            } else if(gamepad1.y) {     // differential lock
                controlMode = 2;
            }

            switch(controlMode) {       // apply power from joysticks to drive train based on control mode
                case 1:
                    lfDrive.setPower((-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor); //exponential scale algorithm
                    lbDrive.setPower((-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor); //tank drive
                    rfDrive.setPower((gamepad1.right_stick_y * Math.abs(gamepad1.right_stick_y)) * speedFactor);
                    rbDrive.setPower((gamepad1.right_stick_y * Math.abs(gamepad1.right_stick_y)) * speedFactor);
                    break;
                case 2:
                    lfDrive.setPower((-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor); //exponential scale algorithm
                    lbDrive.setPower((-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor); //tank drive
                    rfDrive.setPower((gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor);
                    rbDrive.setPower((gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor);
                    break;
                default:
                    lfDrive.setPower((-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor); //exponential scale algorithm
                    lbDrive.setPower((-gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y)) * speedFactor); //tank drive
                    rfDrive.setPower((gamepad1.right_stick_y * Math.abs(gamepad1.right_stick_y)) * speedFactor);
                    rbDrive.setPower((gamepad1.right_stick_y * Math.abs(gamepad1.right_stick_y)) * speedFactor);
                    break;
            }



            if (gamepad2.dpad_up) {
                liftMotor.setPower(1);
            }
            else if (gamepad2.dpad_down) {
                liftMotor.setPower(-1);
            }
            else {
                liftMotor.setPower(0);
            }

            if (gamepad2.dpad_up) {
                liftMotor.setPower(1);
            }
            else if (gamepad2.dpad_down) {
                liftMotor.setPower(-1);
            }
            else {
                liftMotor.setPower(0);
            }
            if (gamepad1.b) { //hitting the "b" button on Gamepad 2 will cause the two servos to grasp the glyph
                lServoArm.setPosition(lServoArmGrasp);
                rServoArm.setPosition(rServoArmGrasp);
            }
            if (gamepad1.x) { //hitting the "x" button on Gamepad 2 will cause the two servos to return to their original position
                lServoArm.setPosition(lServoArmInit);
                rServoArm.setPosition(rServoArmInit);
            }

            if (gamepad2.b) { //hitting the "b" button on Gamepad 2 will cause the two servos to grasp the glyph
                lServoArm.setPosition(lServoArmGrasp);
                rServoArm.setPosition(rServoArmGrasp);
            }
            if (gamepad2.x) { //hitting the "x" button on Gamepad 2 will cause the two servos to return to their original position
                lServoArm.setPosition(lServoArmInit);
                rServoArm.setPosition(rServoArmInit);
            }
            if (gamepad2.y) { //hitting the "y" button on Gamepad 2 will cause the jewel arm to drop
                jewelArm.setPosition(1);
            } else {
                jewelArm.setPosition(.5);
            }

            if (gamepad2.a) { //hitting the "a" button on Gamepad 2 will cause the jewel arm to lift
                jewelArm.setPosition(0);
            } else {
                jewelArm.setPosition(.5);
            }
        }           // End of Teleop
    }
}
