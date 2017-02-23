package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name = "BasicGyro", group = "LinearOpMode")
//@Disabled

public class dev_MRGyroTurn_Range extends LinearOpMode {

    private DcMotor lDrive1;
    private DcMotor lDrive2;
    private DcMotor rDrive1;
    private DcMotor rDrive2;
    DcMotor catapult;
    private Servo button;
    private Servo hopper;
    private Servo belt;
    GyroSensor gyroX;
    ModernRoboticsI2cGyro gyro;
    byte[] range1Cache; //The read will return an array of bytes. They are stored in this variable
    byte[] range2Cache;
    TouchSensor touch;

    I2cAddr RANGE1ADDRESS = new I2cAddr(0x14); //Default I2C address for MR Range (7-bit)
    I2cAddr RANGE2ADDRESS = new I2cAddr(0x18);

    //private I2cAddr RANGE1ADDRESS = new I2cAddr(0x28); //Default I2C address for MR Range (7-bit)
    private static final int RANGE1_REG_START = 0x04; //Register to start reading
    private static final int RANGE1_READ_LENGTH = 2; //Number of byte to read
    //2nd range
    private static final int RANGE2_REG_START = 0x04; //Register to start reading
    private static final int RANGE2_READ_LENGTH = 2; //Number of byte to read

    private void setUpSensors() throws InterruptedException {

        // setup the Gyro
        // write some device information (connection info, name and type)
        // to the log file.
        hardwareMap.logDevices();
        // get a reference to our GyroSensor object.
        gyroX = hardwareMap.gyroSensor.get("gyro");
        gyro = (ModernRoboticsI2cGyro) gyroX;
        // calibrate the gyro.
        gyroX.calibrate();
        while (gyroX.isCalibrating())  {
            sleep(50);
        }
        // End of setting up Gyro and beginning of setting up Range sensors
        //prepare first range sensor
        I2cDevice RANGE1 = hardwareMap.i2cDevice.get("range");
        I2cDeviceSynch RANGE1Reader = new I2cDeviceSynchImpl(RANGE1, RANGE1ADDRESS, false);
        RANGE1Reader.engage();
        range1Cache = RANGE1Reader.read(RANGE1_REG_START, RANGE1_READ_LENGTH);
        RANGE1Reader.engage();
        //prepare second range sensor
        I2cDevice RANGE2 = hardwareMap.i2cDevice.get("range2");
        I2cDeviceSynch RANGE2Reader = new I2cDeviceSynchImpl(RANGE2, RANGE2ADDRESS, false);
        range2Cache = RANGE2Reader.read(RANGE2_REG_START, RANGE2_READ_LENGTH);
        RANGE2Reader.engage();
    }

    // This is the Drive Method
    // It will take in two static values: distance and maxSpeed.
    // It will then calculate the encoder counts to drive and drive the distance at the specified power,
    // accelerating to max speed for the first third of the distance, maintaining that speed for the second third,
    // and decelerating to a minimum speed for the last third.
    // If the robot deviates from the initial gyro heading, it will correct itself proportionally to the error.
    public void drive(double distance, double maxSpeed) {
        int ENCODER_CPR = 1120; // Encoder counts per Rev
        double gearRatio = 1.75; // [Gear Ratio]:1
        double circumference = 13.10; // Wheel circumference
        double ROTATIONS = distance / (circumference * gearRatio); // Number of rotations to drive
        double COUNTS = ENCODER_CPR * ROTATIONS; // Number of encoder counts to drive
        //double startPosition = rDrive1.getCurrentPosition();

        //double oldSpeed;
        double speed = 0;
        //double minSpeed = 0.3;
        //double acceleration = 0.01;
        double leftSpeed;
        double rightSpeed;

        rDrive1.setTargetPosition(rDrive1.getCurrentPosition() + (int) COUNTS);

        rDrive1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rDrive2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lDrive1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lDrive2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double heading = gyroX.getHeading();

        while (rDrive1.getCurrentPosition()<(rDrive1.getTargetPosition()-5)){

            leftSpeed = maxSpeed-((gyroX.getHeading()-heading)/10);
            rightSpeed = maxSpeed+((gyroX.getHeading()-heading)/10);

            leftSpeed = Range.clip(leftSpeed, -1, 1);
            rightSpeed = Range.clip(rightSpeed, -1, 1);

            lDrive1.setPower(leftSpeed);
            rDrive1.setPower(rightSpeed);
            lDrive2.setPower(leftSpeed);
            rDrive2.setPower(rightSpeed);

            telemetry.addData("1. speed", speed);
            telemetry.addData("2. leftSpeed", leftSpeed);
            telemetry.addData("3. rightSpeed", rightSpeed);
            telemetry.addData("4. IntegratedZValue", gyro.getIntegratedZValue());
            updateTelemetry(telemetry);
        }

        lDrive1.setPower(0);
        rDrive1.setPower(0);
        lDrive2.setPower(0);
        rDrive2.setPower(0);

        lDrive1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rDrive1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lDrive2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rDrive2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void basicTurn(double power, long time, int direction) {
        double motors = power * direction;

        lDrive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lDrive2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rDrive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rDrive2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lDrive1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lDrive2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rDrive1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rDrive2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lDrive1.setPower(motors);
        lDrive2.setPower(motors);
        rDrive1.setPower(-motors);
        rDrive2.setPower(-motors);
        sleep(time);
        lDrive1.setPower(0);
        lDrive2.setPower(0);
        rDrive1.setPower(0);
        rDrive2.setPower(0);
        sleep(400);
    }

    public void encoderTurn(double power, double distance) {
        int ENCODER_CPR = 1120; // Encoder counts per Rev
        double gearRatio = 1.75; // [Gear Ratio]:1
        double circumference = 13.10; // Wheel circumference
        double ROTATIONS = distance / (circumference * gearRatio); // Number of rotations to drive
        double COUNTS = ENCODER_CPR * ROTATIONS; // Number of encoder counts to drive

        int lTarget1 = lDrive1.getCurrentPosition() + (int) COUNTS;
        int lTarget2 = lDrive2.getCurrentPosition() + (int) COUNTS;
        int rTarget1 = rDrive1.getCurrentPosition() - (int) COUNTS;
        int rTarget2 = rDrive2.getCurrentPosition() - (int) COUNTS;

        lDrive1.setTargetPosition(lTarget1);
        lDrive2.setTargetPosition(lTarget2);
        rDrive1.setTargetPosition(rTarget1);
        rDrive2.setTargetPosition(rTarget2);

        lDrive1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lDrive2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rDrive1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rDrive2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        lDrive1.setPower(power);
        lDrive2.setPower(power);
        rDrive1.setPower(-power);
        rDrive2.setPower(-power);

        while(lDrive1.isBusy()) {
            telemetry.addData("lDrive1 Position", lDrive1.getCurrentPosition());
            telemetry.addData("lDrive2 Position", lDrive2.getCurrentPosition());
            telemetry.addData("rDrive1 Position", rDrive1.getCurrentPosition());
            telemetry.addData("rDrive2 Position", rDrive2.getCurrentPosition());
            telemetry.addData("Target Position", lTarget1);
            telemetry.update();
        }

        lDrive1.setPower(0);
        lDrive2.setPower(0);
        rDrive1.setPower(0);
        rDrive2.setPower(0);
    }

    // Function to use the gyro to do a spinning turn in place.
    // It points the robot at an absolute heading, not a relative turn.  0 will point robot to same
    // direction we were at the start of program.
    // Pass:
    // targetHeading = the new heading we want to point robot at,
    // maxSpeed = the max speed the motor can run in the range of 0 to 1
    // direction = the direction we will turn, 1 is clockwise, -1 is counter-clockwise
    // Returns:
    // heading = the new heading the gyro reports
    public void gyroTurn(int targetHeading, double maxSpeed, int direction) {
        int startHeading = gyroX.getHeading();
        int deceleration;
        int currentHeading;
        double oldSpeed;
        double speed = 0;
        double minSpeed = 0.05;
        double acceleration = 0.01;
        double ka = 0.01;             // Proportional acceleration constant

        targetHeading += gyroX.getHeading();
        targetHeading -= targetHeading > 360 ?
                360 : 0;
        targetHeading += targetHeading < 0 ?
                360 : 0;

        lDrive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lDrive2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rDrive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rDrive2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Calls turnCompeted function to determine if we need to keep turning
        while ((!turnCompleted(gyroX.getHeading(), targetHeading, 5, direction) && opModeIsActive())) {

            // Calculate the speed we should be moving at
            oldSpeed = speed;           // save our old speed for use later.

            currentHeading = gyroX.getHeading();
            // Reuses the degreesToTurn function by passing different values to obtain our error
            deceleration = degreesToTurn(targetHeading, currentHeading, direction);

            speed = deceleration * ka * direction;

            // Limit the acceleration of the motors speed at beginning of turns.
            if( Math.abs(speed) > Math.abs(oldSpeed))
                speed = oldSpeed + (direction * acceleration);

            // Set a minimum power for the motors to make sure they move
            if (Math.abs(speed) < minSpeed)
                speed = minSpeed * direction;

            // Don't exceed the maximium speed requested
            if (Math.abs(speed) > maxSpeed)
                speed = maxSpeed * direction;

            // Set the motor speeds
            lDrive1.setPower(speed);
            lDrive2.setPower(speed);
            rDrive1.setPower(-speed);
            rDrive2.setPower(-speed);
            telemetry.addData("Current Heading", gyroX.getHeading());
            telemetry.addData("Current Speed", speed);
            telemetry.addData("Target:", targetHeading);
            updateTelemetry(telemetry);
        }
        // Done with the turn so shut off motors
        lDrive1.setPower(0);
        lDrive2.setPower(0);
        rDrive1.setPower(0);
        rDrive2.setPower(0);

    }

    public void gyroTurnWithRange(int targetHeading, double maxSpeed, int direction) {
        double correction;
        double dist1 = range1Cache[0] & 0xFF;
        double dist2 = range2Cache[0] & 0xFF;

        if(dist2 > dist1) {
            correction = targetHeading + (Math.tan(1 / ((dist2 - dist1) / 14)));
        } else if(dist1 > dist2) {
            correction = targetHeading - (Math.tan(1 / ((dist1 - dist2) / 14)));
        } else {
            correction = targetHeading;
        }

        gyroTurn((int)correction, maxSpeed, direction);
    }

    // Function used by the turnCompleted function (which is used by the gyroTurn function) to determine the degrees to turn.
    // Also used to determine error for proportional speed control in the gyroTurn function
    // by passing targetHeading and currentHeading instead of startHeading and targetHeading, respectively
    // Pass:
    // startHeading = heading that we are at before we turn
    // targetHeading = heading we want to turn to
    // direction = the direction we want to turn (1 for right, -1 for left)
    public static int degreesToTurn(int startHeading, int targetHeading, int direction) {

        int degreesToTurn;

        // Turning right
        if (direction == 1)
            degreesToTurn = targetHeading - startHeading;
            // degreesToTurn = targetHeading - currentHeading
            // Turning left
        else
            degreesToTurn = startHeading - targetHeading;

        if (degreesToTurn < 0) // Changed from "while"
            degreesToTurn = degreesToTurn + 360;
        else if (degreesToTurn > 360) // Changed from "while"
            degreesToTurn = degreesToTurn + 360;

        return (degreesToTurn);
    }

    // Function used by the GyroTurn function to determine if we have turned far enough.
    // Pulls from degreesToTurn function to determine degrees to turn.
    // Pass:
    // currentHeading = the heading the robot is currently at (live value, changes during the turn)
    // targetHeading = the heading we want the robot to be at once the turn is completed
    // range = the degrees of error we are allowing so that the robot doesn't spin in circles
    // if it overshoots by a small amount
    // direction = direction the robot is turning (1 for right, -1 for left)
    // Returns:
    // result = true if we have reached target heading, false if we haven't
    public static boolean turnCompleted(int currentHeading, int targetHeading, int range, int direction)  {

        // Value we want to stop at
        int stop;
        // Will be sent to the GyroTurn function; robot stops turning when true
        boolean result;

        // Turn right
        if (direction >= 1) {
            stop = targetHeading - range;
            if (stop >= 0){
                result = (currentHeading >= stop && currentHeading <= targetHeading);
            }
            else{
                result = ((currentHeading >= (stop+360) && currentHeading <=359)|| (currentHeading >= 0 && currentHeading <= targetHeading));
            }
        }
        // Turn left
        else {
            stop = targetHeading + range;
            if (stop <=359){
                result = (currentHeading <= stop && currentHeading >= targetHeading);
            }
            else{
                result = (currentHeading >= targetHeading && currentHeading <= 359) || (currentHeading >=0 && currentHeading <= (stop-360));
            }
        }
        return (result);
    }

    // Function to reset the catapult to the launch position
    public void launchPosition() throws InterruptedException{
        while (!touch.isPressed()){
            catapult.setPower(0.5);
        }
        catapult.setPower(0);
    }

    public void fire() throws InterruptedException {
        launchBall();
        launchPosition();
        loadBall();
        launchBall();
    }
    // Function to load the catapult
    public void loadBall() throws InterruptedException {
        hopper.setPosition(0.5);
        sleep(1000);
        hopper.setPosition(0.8);
        sleep(1500);
    }
    // Fires the ball
    public void launchBall() throws InterruptedException {
        catapult.setPower(1);
        sleep(800);
        catapult.setPower(0);

    }

    public void runOpMode() throws InterruptedException {

        rDrive1 = hardwareMap.dcMotor.get("rDrive1");
        rDrive2 = hardwareMap.dcMotor.get("rDrive2");
        lDrive1 = hardwareMap.dcMotor.get("lDrive1");
        lDrive2 = hardwareMap.dcMotor.get("lDrive2");
        catapult = hardwareMap.dcMotor.get("catapult");
        hopper = hardwareMap.servo.get("hopper");
        button = hardwareMap.servo.get("button");
        belt = hardwareMap.servo.get("belt");
        touch = hardwareMap.touchSensor.get("t");
        lDrive1.setDirection(DcMotor.Direction.REVERSE);
        lDrive2.setDirection(DcMotor.Direction.REVERSE);
        hopper.setPosition(0.8);
        button.setPosition(0.5);
        belt.setPosition(0.5);
        setUpSensors();
        waitForStart();

        encoderTurn(-0.3, -11);

        sleep(500);

        drive(10, 0.3);
        sleep(300);

        fire();
    }
}