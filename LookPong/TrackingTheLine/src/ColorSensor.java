import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

public class ColorSensor extends EV3ColorSensor {

	private final static int PATH_COLOR = Color.BLACK;
	private final static int PATH_COLOR1 = Color.RED;

    public ColorSensor(Port port)
    {
        super(port);
    }

	public boolean onPath() {
		// TODO Auto-generated method stub
		return getColorID() == PATH_COLOR;
	}

	public boolean onPath1() {
		// TODO Auto-generated method stub
		return getColorID() == PATH_COLOR1;
	}

}
