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
    }

    @Override
    public void runOpMode() throws InterruptedException {
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

        // BEGIN SELECTION
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

        if(leftStone) {
            telemetry.addData("Stone", "Left");
        } else if(rightStone) {
            telemetry.addData("Stone", "Right");
        }

        telemetry.update();
        waitForStart();
        timer.reset();
        while(timer.seconds() < 30 || !AutoFinished) {                  // Begin Auto
            if(blue) {
                if(leftStone) {
                    // Auto
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
        while(timer.seconds() <= 8 || !gamepad1.a) {                    // Transition Period
            telemetry.addData("Auto finished!", timer.seconds());
            telemetry.addData("At 8s, Teleop will begin", "Override by pressing A"); // Just in case
            telemetry.update();                                                 // timer is off
            sleep(50);
        }
        timer.reset();
        while(opModeIsActive()) {                                   // TeleOp Begin!
            // Teleop Code
        }
    }
}
