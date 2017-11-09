package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by jrahm on 6/14/17.
 */

@TeleOp (name="caluper_teleop",group="Opmode")

public class caluper_teleop extends OpMode {

    public DcMotor lfDrive, rfDrive, lbDrive, rbDrive, liftMotor;  //Left front drive, right front drive, left back drive, right back drive.
    public Servo lServoArm, rServoArm, jewelArm;            //lServoArm in port 5, rServoArm in port 4

    private double lServoArmInit = .73;                     //Glyph arms will initialize in the open position.
    private double rServoArmInit = .1;
    private double lServoArmGrasp = .43;                    //After testing, these positions were optimal for grasping the glyphs.
    private double rServoArmGrasp = .50;
    private double lServoArmAlmostGrasp = .50;
    private double rServoArmAlmostGrasp = .43;

    boolean rightBackwardBrake = false;                      //These four variables initiate an abrupt stop (see below).
    boolean rightForwardBrake = false;
    boolean leftBackwardBrake = false;
    boolean leftForwardBrake = false;


    private double speedFactor = .5;
    private int controlMode = 1;

    ElapsedTime timer = new ElapsedTime();

    public void init () {
        lfDrive = hardwareMap.dcMotor.get("lfDrive");       //Left front drive, Hub 1, port 2
        lfDrive.setPower(0);

        lbDrive = hardwareMap.dcMotor.get("lbDrive");       //Left back drive, Hub 1, port 3
        lbDrive.setPower(0);

        rfDrive = hardwareMap.dcMotor.get("rfDrive");       //Right front drive, Hub 1, port 1
        rfDrive.setPower(0);

        rbDrive = hardwareMap.dcMotor.get("rbDrive");       //Right back drive, Hub 1, port 0
        rbDrive.setPower(0);

        liftMotor = hardwareMap.dcMotor.get("liftMotor");   //Lift motor, Hub 2, port 3
        liftMotor.setPower(0);

        lServoArm = hardwareMap.servo.get("lServoArm");     //Left servo arm, Hub 1, port 2
        lServoArm.setPosition(lServoArmInit);

        rServoArm = hardwareMap.servo.get("rServoArm");     //Right servo arm, Hub 2, port 1
        rServoArm.setPosition(rServoArmInit);

        jewelArm = hardwareMap.servo.get("jewelArm");       //Jewel Arm, Hub 2, port 3
        jewelArm.setPosition(.5);
    }

    public void loop() {
        if(gamepad1.right_bumper) {
            speedFactor = 1;
        } else {
            speedFactor = .5;
        }
        if(gamepad1.a) {            //Hitting the "a" button will allow the drive train to run at full capacity.
            controlMode = 1;
        }
        else if(gamepad1.y) {     //Hitting the "y" button will cut the drive train's power by 50%.
            controlMode = 2;
        }

///DRIVER CODE
        switch(controlMode) {
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

        if (gamepad1.dpad_up) {
            gamepad1.left_stick_y = gamepad1.right_stick_y;
        }

        if (gamepad1.dpad_down) {
            gamepad1.left_stick_y = gamepad1.right_stick_y;
        }

        //ABRUPT STOP (Right Side)
        //Backwards
        if (gamepad1.right_stick_y > 0.01) {        //As soon as the joystick activates, set the brake variable to true.
            rightBackwardBrake = true;
        }

        if (gamepad1.right_stick_y == 0 && rightBackwardBrake == true) {
            timer.reset();
            rfDrive.setPower(-.3);          //If the brake variable is true and the joystick resets to 0, the robot will reverse power briefly to cause an abrupt stop.
            rbDrive.setPower(-.3);

            if (timer.milliseconds() > 150) {
                rfDrive.setPower(0);
                rbDrive.setPower(0);
            }
            rightBackwardBrake = false;     //As soon as the robot stops, reset the brake variable to false.
        }

        //Forwards
        if (gamepad1.right_stick_y < -0.01) {
            rightForwardBrake = true;
        }

        if (gamepad1.right_stick_y == 0 && rightForwardBrake == true) {
            timer.reset();
            rfDrive.setPower(.3);
            rbDrive.setPower(.3);

            if (timer.milliseconds() > 150) {
                rfDrive.setPower(0);
                rbDrive.setPower(0);
            }
            rightForwardBrake = false;
        }

        //ABRUPT STOP (Left Side)
        //Backwards
        if (gamepad1.left_stick_y < -0.01) {
            leftBackwardBrake = true;
        }

        if (gamepad1.left_stick_y == 0 && leftBackwardBrake == true) {
            timer.reset();
            lfDrive.setPower(.3);
            lbDrive.setPower(.3);

            if (timer.milliseconds() > 150) {
                lfDrive.setPower(0);
                lbDrive.setPower(0);
            }
            leftBackwardBrake = false;
        }

        //Forwards
        if (gamepad1.left_stick_y > 0.01) {
            leftForwardBrake = true;
        }

        if (gamepad1.left_stick_y == 0 && leftForwardBrake == true) {
            timer.reset();
            lfDrive.setPower(-.3);
            lbDrive.setPower(-.3);

            if (timer.milliseconds() > 150) {
                lfDrive.setPower(0);
                lbDrive.setPower(0);
            }
            leftForwardBrake = false;
        }

///OPERATOR CODE

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

        /*
        if (gamepad1.b) { //hitting the "b" button on Gamepad 2 will cause the glypher servos to grasp the glyph
            lServoArm.setPosition(lServoArmGrasp);
            rServoArm.setPosition(rServoArmGrasp);
        }
        if (gamepad1.x) { //hitting the "x" button on Gamepad 2 will cause the glypher servos to return to their original position
            lServoArm.setPosition(lServoArmInit);
            rServoArm.setPosition(rServoArmInit);
        }
        */

        if (gamepad2.b) { //hitting the "b" button on Gamepad 2 will cause the glypher servos to grasp the glyph
            lServoArm.setPosition(lServoArmGrasp);
            rServoArm.setPosition(rServoArmGrasp);
        }
        if (gamepad2.x) { //hitting the "x" button on Gamepad 2 will cause the glypher servos to return to their original position
            lServoArm.setPosition(lServoArmInit);
            rServoArm.setPosition(rServoArmInit);
        }

        /*
        if (gamepad1.y) {   //hitting the "y" button on Gamepad 1 will cause the glypher servos to expand slightly larger than grasping the glyphs.
            lServoArm.setPosition(lServoArmAlmostGrasp);
            rServoArm.setPosition(rServoArmAlmostGrasp);
        }
        */

        if (gamepad2.y) {   //hitting the "y" button on Gamepad 2 will cause the glypher servos to expand slightly larger than grasping the glyphs.
            lServoArm.setPosition(lServoArmAlmostGrasp);
            rServoArm.setPosition(rServoArmAlmostGrasp);
        }

        if (gamepad2.dpad_right) {
            jewelArm.setPosition(1);
        }
        else {
            jewelArm.setPosition(.5);
        }
        if (gamepad2.dpad_left) {
            jewelArm.setPosition(0);
        }
        else {
            jewelArm.setPosition(.5);
        }

    }
}