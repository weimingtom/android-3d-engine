package dimyoux.engine.managers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import dimyoux.engine.core.Signal;
import dimyoux.engine.core.signals.ISensorLight;
import dimyoux.engine.core.signals.ISensorOrientation;
import dimyoux.engine.core.signals.ISensorProximity;
import dimyoux.engine.core.signals.ISensorTouchDoubleTap;
import dimyoux.engine.core.signals.ISensorTouchTap;
import dimyoux.engine.utils.Log;
/**
 * [Singleton]Sensor manager
 */
public class SensorManager extends SimpleOnGestureListener implements SensorEventListener 
{
	/**
	 * All sensors type
	 */
	public final static int TYPE_ALL = Sensor.TYPE_ALL;
	/**
	 * Accelerometer type
	 */
	public final static int TYPE_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
	/**
	 * Gyroscope type 
	 */
	public final static int TYPE_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
	/**
	 * Light type
	 */
	public final static int TYPE_LIGHT = Sensor.TYPE_LIGHT;
	
	/**
	 * Magnetic type
	 */
	public final static int TYPE_MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;
	/**
	 * Orientation type
	 */
	public final static int TYPE_ORIENTATION = Sensor.TYPE_ORIENTATION;
	/**
	 * Pressure type
	 */
	public final static int TYPE_PRESSURE = Sensor.TYPE_PRESSURE;
	/**
	 * Proximity type
	 */
	public final static int TYPE_PROXIMITY = Sensor.TYPE_PROXIMITY;
	/**
	 * Temperature type
	 */
	public final static int TYPE_TEMPERATURE = Sensor.TYPE_TEMPERATURE;
	/**
	 * Android native sensor manager
	 */
	private android.hardware.SensorManager _androidSensorManager;
	/**
	 * Instance of the sensor manager
	 */
	private static SensorManager _instance;
	/**
	 * Sensors
	 */
	private List<Sensor> sensors;
	/**
	 * Sensors Types
	 */
	final private static List<Integer> sensorsTypes = new ArrayList<Integer>() 
	{private static final long serialVersionUID = 6436584183297172115L;
	{
	    add(TYPE_ACCELEROMETER);
	    add(TYPE_GYROSCOPE);
	    add(TYPE_LIGHT);
	    add(TYPE_MAGNETIC_FIELD);
	    add(TYPE_ORIENTATION);
	    add(TYPE_PRESSURE);
	    add(TYPE_PROXIMITY);
	    add(TYPE_TEMPERATURE);
	    //gravity
	    add(9);
	    //linear acceleration
	    add(10);
	    //rotation vector
	    add(11);
	}}; 
	/**
	 * Names of Sensors Types
	 */
	final private Map<Integer, String> sensorsNames = new Hashtable<Integer, String>()
	{private static final long serialVersionUID = 837648960662696146L;
	{
		put(TYPE_ACCELEROMETER, "accelerometer");
		put(TYPE_GYROSCOPE, "gyroscope");
		put(TYPE_LIGHT, "light");
		put(TYPE_MAGNETIC_FIELD, "magnetic");
		put(TYPE_ORIENTATION, "orientation");
		put(TYPE_PRESSURE, "pressure");
		put(TYPE_PROXIMITY, "proximity");
		put(TYPE_TEMPERATURE, "temperature");
		put(9, "gravity");
		put(10, "linear acceleration");
		put(11, "rotation vector");
	}};
	/**
	 * Dispatches a signal when the proximity sensor status changes
	 * Signal<ISensorProximity>.dispatch(Boolean);
	 */
	private Signal<ISensorProximity> signalProximity;
	/**
	 * Dispatches a signal when orientation sensors status changed
	 * Signal<ISensorOrientation>.dispatch(float(yaw), float(pitch), float(roll));
	 */
	private Signal<ISensorOrientation> signalOrientation;
	/**
	 * Dispatches a signal when light sensors status changed
	 * Signal<ISensorLight>.dispatch(float(light));
	 */
	private Signal<ISensorLight> signalLight;
	/**
	 * Dispatches a signal when the user tap on the screen
	 * Signal<ISensorTouchTap>.dispatch();
	 */
	private Signal<ISensorTouchTap> signalTap;
	/**
	 * Dispatches a signal when the user double tap on the screen
	 * Signal<ISensorTouchDoubleTap>.dispatch();
	 */
	private Signal<ISensorTouchDoubleTap> signalDoubleTap;
	
	private float[] mag;
	private float[] acc;
	
	/**
	 * Constructor
	 */
	private SensorManager()
	{
		
		signalProximity = new Signal<ISensorProximity>(){
			@Override
        	protected void _dispatch(ISensorProximity listener, Object... params)
        	{
        		listener.onProximityEvent((Boolean)params[0]);
        	}
		};
		
		signalOrientation = new Signal<ISensorOrientation>(){
			@Override
        	protected void _dispatch(ISensorOrientation listener, Object... params)
        	{
        		listener.onOrientationChanged((Float)params[0],(Float)params[1],(Float)params[2], (Long)params[3]);
        	}
		};
		signalLight = new Signal<ISensorLight>(){
			@Override
        	protected void _dispatch(ISensorLight listener, Object... params)
        	{
        		listener.onLightChanged((Float)params[0]);
        	}
		};
		signalTap = new Signal<ISensorTouchTap>(){
			@Override
			protected void _dispatch(ISensorTouchTap listener, Object... params)
			{
				listener.onTap((Float)params[0], (Float) params[1], (Float) params[2], (Float) params[3]);
			}
		};
		signalDoubleTap = new Signal<ISensorTouchDoubleTap>(){
			@Override
			protected void _dispatch(ISensorTouchDoubleTap listener, Object... params)
			{
				listener.onDoubleTap((Float) params[0], (Float) params[1], (Float) params[2], (Float) params[3]);
			}
		};
		mag = new float[3];
		acc = new float[3];
		/*
		signalOrientation = new Signal<ISensorProximity>(){
			@Override
        	protected void _dispatch(ISensorProximity listener, Object... params)
        	{
        		listener.onProximityEvent((Boolean)params[0]);
        	}
		};*/
		_androidSensorManager = (android.hardware.SensorManager)ApplicationManager.getInstance().getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensors = _androidSensorManager.getSensorList(Sensor.TYPE_ALL);
		Log.info("Il y'a "+size()+" sensors");
		for(Integer i :sensorsTypes)
		{
			if(size(i)>0)
			{
				Log.info("Il y'a "+size(i)+" sensor(s) de type "+sensorsNames.get(i));
			}
		}
		for(Integer i :sensorsTypes)
		{
			if(size(i)>0)
			{
				if((size(TYPE_GYROSCOPE) > 0 && i!=TYPE_ACCELEROMETER && i!=TYPE_MAGNETIC_FIELD && i!=TYPE_ORIENTATION)|| (size(TYPE_GYROSCOPE)==0 && size(TYPE_MAGNETIC_FIELD)>0 && size(TYPE_ACCELEROMETER)>0 && i!=TYPE_ORIENTATION) || ((size(TYPE_MAGNETIC_FIELD)==0 || size(TYPE_ACCELEROMETER)==0) && size(TYPE_GYROSCOPE)==0 && i!=TYPE_ACCELEROMETER))
				{
					_androidSensorManager.registerListener(this, _androidSensorManager.getSensorList(i).get(0), android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
				}
			}
		}
		//_androidSensorManager.registerListener(this, getSensor(TYPE_ORIENTATION), android.hardware.SensorManager.SENSOR_DELAY_GAME);
		
		
	}
	/**
	 * Return the instance of SensorManager
	 * @return Instance of SensorManager
	 */
	public static SensorManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new SensorManager();
		}
		return _instance;
	}
	/**
	 * Return the sensor
	 * @param type Type of the sensor
	 * @return Sensor
	 */
	public Sensor getSensor(int type)
	{
		for(Sensor s:sensors)
		{
			if(s.getType() == type)
			{
				return s;
			}
		}
		return null;
	}
	/**
	 * Return true if the device has this type of sensor
	 * @param type Type of the sensor
	 * @return True or false
	 */
	public boolean hasSensor(int type)
	{
		for(Sensor s:sensors)
		{
			if(s.getType() == type)
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Number of sensors
	 * @return Quantity of sensors
	 */
	public int size()
	{
		return sensors.size();
	}
	/**
	 * Number of sensors of a type
	 * @param type Type of sensor
	 * @return Quantity of sensors of this type
	 */
	public int size(int type)
	{
		int count = 0;
		for(final Sensor sensor : sensors)
		{
			if(sensor.getType() == type)
			{
				count++;
			}
		}
		return count;
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Log.error("Accurency for "+sensorsNames.get(sensor.getType())+" : "+accuracy);
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		switch (event.sensor.getType()) {
		case TYPE_ACCELEROMETER:
			//Log.debug("acceleration : " + event.values[0] + ";" + event.values[1] + ";" + event.values[2]);
			acc = event.values.clone();
			
			if((event.values[0]==1 && event.values[1] == 0 && event.values[2] == 0) || (event.values[0]==0 && event.values[1] == 1 && event.values[2] == 0) || (event.values[0]==0 && event.values[1] == 0 && event.values[2] == 1) )
			{
				_androidSensorManager.unregisterListener(this, getSensor(TYPE_ACCELEROMETER));
				_androidSensorManager.unregisterListener(this, getSensor(TYPE_MAGNETIC_FIELD));
				_androidSensorManager.registerListener(this, getSensor(TYPE_ORIENTATION), android.hardware.SensorManager.SENSOR_DELAY_GAME);
				if(hasSensor(TYPE_ACCELEROMETER))
				{
					try {
						sensors.remove(getSensor(TYPE_ACCELEROMETER));
					} catch (UnsupportedOperationException ex) {}
				}
				Log.warning("Accelerometer sensor is a fake");
			}
			if(mag != null)
			{
				float[] result = new float[16];
				if(android.hardware.SensorManager.getRotationMatrix(result, null, acc, mag))
				{
					float[] rotation = new float[3];
					android.hardware.SensorManager.getOrientation(result, rotation);
					signalOrientation.dispatch(
							rotation[0] * dimyoux.engine.utils.math.Math.RAD2DEG, 
							rotation[1] * dimyoux.engine.utils.math.Math.RAD2DEG, 
							rotation[2] * dimyoux.engine.utils.math.Math.RAD2DEG,
							event.timestamp);
					mag = null;
					acc = null;
				}
			}
			break;
		case TYPE_MAGNETIC_FIELD:
			mag = event.values.clone();
			//Log.info("magnet :values: " + event.values[0] + ";" + event.values[1] + ";" + event.values[2]);
			if((event.values[0]==1 && event.values[1] == 0 && event.values[2] == 0) || (event.values[0]==0 && event.values[1] == 1 && event.values[2] == 0) || (event.values[0]==0 && event.values[1] == 0 && event.values[2] == 1) )
			{
				_androidSensorManager.unregisterListener(this, getSensor(TYPE_ACCELEROMETER));
				_androidSensorManager.unregisterListener(this, getSensor(TYPE_MAGNETIC_FIELD));
				_androidSensorManager.registerListener(this, getSensor(TYPE_ORIENTATION), android.hardware.SensorManager.SENSOR_DELAY_GAME);
				if(hasSensor(TYPE_MAGNETIC_FIELD))
				{
					try {
						sensors.remove(getSensor(TYPE_MAGNETIC_FIELD));
					} catch (UnsupportedOperationException ex) {}
				}
				Log.warning("Magnetic sensor is a fake");
			}
			if(acc != null)
			{
				float[] result = new float[16];
				if(android.hardware.SensorManager.getRotationMatrix(result, null, acc, mag))
				{
					float[] rotation = new float[3];
					android.hardware.SensorManager.getOrientation(result, rotation);
					signalOrientation.dispatch(
							rotation[0] * dimyoux.engine.utils.math.Math.RAD2DEG, 
							rotation[1] * dimyoux.engine.utils.math.Math.RAD2DEG, 
							rotation[2] * dimyoux.engine.utils.math.Math.RAD2DEG,
							event.timestamp);
					mag = null;
					acc = null;
				}
			}
			break;
		case TYPE_ORIENTATION:
			//signalOrientation.dispatch(event.values[0], event.values[1], event.values[2]);
			//Log.error(event.values[0] + ";" + event.values[1] + ";" + event.values[2]);
			break;
		case TYPE_GYROSCOPE:
			//TODO:signal with a value for compatible phones
			break;
		case TYPE_PROXIMITY:
			//TODO:more complicated signal with a value for compatible phones
			signalProximity.dispatch(event.values[0]==0.0?true:false);
			break;
		case TYPE_LIGHT:
			signalLight.dispatch(event.values[0]);
			break;
		}
	}
	/**
	 * Dispatch an signal when the proximity sensor status changes
	 * Signal<ISensorProximity>.dispatch(Boolean);
	 * @return Signal<ISensorProximity>.dispatch(Boolean);
	 */
	public Signal<ISensorProximity> getSignalProximity()
	{
		return signalProximity;
	}
	/**
	 * Dispatch an signal when the orientation is changed
	 * Signal<ISensorOrientation>.dispatch(float yaw, float pitch, float roll);
	 * @return Signal<ISensorOrientation>.dispatch(float yaw, float pitch, float roll);
	 */
	public Signal<ISensorOrientation> getSignalOrientation()
	{
		return signalOrientation;
	}
	/**
	 * Dispatch an signal when the light is changed
	 * Signal<ISensorLight>.dispatch(float light);
	 * @return Signal<ISensorLight>.dispatch(float light);
	 */
	public Signal<ISensorLight> getSignalLight()
	{
		return signalLight;
	}
	/**
	 * Dispatches an signal when the user taps the screen
	 * Signal<ISensorTouchTap>.dispatch(MotionEvent event);
	 * @return Signal<ISensorTouchTap>.dispatch(MotionEvent event);
	 */
	public Signal<ISensorTouchTap> getSignalTap()
	{
		return signalTap;
	}
	/**
	 * Dispatches an signal when the user double taps the screen
	 * Signal<ISensorTouchDoubleTap>.dispatch(MotionEvent event);
	 * @return Signal<ISensorTouchDoubleTap>.dispatch(MotionEvent event);
	 */
	public Signal<ISensorTouchDoubleTap> getSignalDoubleTap()
	{
		return signalDoubleTap;
	}
	/*
	  @Override
	  public boolean onSingleTapUp(MotionEvent ev) {

	    Log.d("onSingleTapUp"+ev);

	    return true;

	  }

	  @Override

	  public void onShowPress(MotionEvent ev) {

	    Log.d("onShowPress"+ev);

	  }

	  @Override

	  public void onLongPress(MotionEvent ev) {

	    Log.d("onLongPress"+ev);

	  }

	  @Override

	  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

	    Log.d("onScroll"+e1);

	    return true;

	  }

	  @Override

	  public boolean onDown(MotionEvent ev) {

	    Log.d("onDownd"+ev);

	    return true;

	  }

	  @Override

	  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

	    Log.d("d"+e1);

	    Log.d("e2"+e2);

	    return true;

	  }
	  public boolean onDoubleTapEvent(MotionEvent e)
	  {
		  Log.warning("doubletapevent:"+e);
		  return true;
	  }*/
	
	   /**
	   * Called then the user double taps the screen
	   * @param event MotionEvent
	   */
	  public boolean onDoubleTap(MotionEvent event)
	  {
		  signalDoubleTap.dispatch(event.getX(), event.getY(), event.getPressure(), event.getSize());
		  return true;
	  }
	  
	
	  
	  /**
	   * Called then the user single taps the screen
	   * @param event MotionEvent
	   */
	  public boolean onSingleTapConfirmed(MotionEvent event)
	  {
		  signalTap.dispatch(event.getX(), event.getY(), event.getPressure(), event.getSize());
		  return true;
	  }
}

