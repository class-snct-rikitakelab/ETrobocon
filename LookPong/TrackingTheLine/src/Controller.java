import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

public class Controller
{
    private final static int DELAY = 50;

    private final static int SKIP_FORWARD = 20;

    private final static double DELAYS_PER_DEG = 100.0 / 180;

    private final static int SMALL_ROT = 10;
    private final static int MED_ROT = 90;
    private final static int MAX_ROT = 135;

    private ColorSensor sensor;
    private DifferentialDrive drive;

    public Controller(Port sensor_port, Port left_port, Port right_port)
    {
        log("Initializing Controller");

        sensor = new ColorSensor(sensor_port);
        drive = new DifferentialDrive(left_port, right_port);
    }

    public void run()
    {
        log("Running controller");

        do
        {
            move();
        }
        while (wide_seek());

        end();
    }

    private void move()
    {
        log("Forward");

        do
        {
            naive_move();
        }
        while (narrow_seek());

        log("Stop");
    }

    private void naive_move()
    {
        drive.forward();

        while (sensor.onPath())
        {
            delay();
        }

        drive.stop();
    }

    private boolean narrow_seek()
    {
        log("Seeking Path in Narrow Arc.");

        return sweepClockwise(SMALL_ROT) || sweepCounterClockwise(2 * SMALL_ROT) || sweepClockwise(SMALL_ROT);
    }

    private void skip_forward(int duration)
    {
        drive.forward();

        for (int i = 0; i < duration; i++)
        {
            delay();
        }

        drive.stop();
    }

    private boolean wide_seek()
    {
        skip_forward(SKIP_FORWARD);

        return sweepClockwise(MED_ROT) ||
               sweepCounterClockwise(MED_ROT + MAX_ROT) ||
               sweepClockwise(2 * MAX_ROT) ||
               sweepCounterClockwise(MAX_ROT);
    }

    private boolean sweepClockwise(int rot_limit)
    {
        log("Sweeping clockwise. Limit: " + rot_limit + " degrees.");

        drive.rotateClockwise();

        return sweep(rot_limit);
    }

    private boolean sweepCounterClockwise(int rot_limit)
    {
        log("Sweeping counter-clockwise. Limit: " +  rot_limit + " degrees");

        drive.rotateCounterClockwise();

        return sweep(rot_limit);
    }

    private boolean sweep(int rot_limit)
    {
        for (int i = 0; i < (rot_limit * DELAYS_PER_DEG); i++)
        {
            delay();

            if (sensor.onPath())
            {
                log("Path Detected");

                drive.stop();

                return true;
            }
        }

        log("Path not detected. Rotation limit exceeded.");

        drive.stop();

        return false;
    }

    private void end()
    {
        Sound.beepSequence();

        log("Program ends");
    }

    private void delay()
    {
        Delay.msDelay(DELAY);
    }

    private static void log(String msg)
    {
        System.out.println("log>\t" + msg);
    }
}