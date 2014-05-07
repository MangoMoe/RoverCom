package code;

public class Rover	// static???
{
	// Drive info
	public short[] wheels = {0,0,0,0,0,0};	// do we even need any privacy for these members, can we make public? // DO WE NEED SYNCHRONIZED ACCESSORS???????
	
	// Arm info
	public short turret_pos = 0;	//UNSIGNED CONVERSION??? --> these are 2-s complememnt
	public short shoulder_pos = 0;	//unsinged
	public short elbow_pos = 0;	//unsinged
	public short wrist_flap_pos = 0;	//unsinged
	public short wrist_rotate_pos = 0;	//unsinged
	public short gripper_speed = 0;
	public short rotator_speed = 0;
	public short shoulder_fb_pot = 0;	//unsinged
	public short shoulder_fb_cur = 0;	//unsinged
	public short elbow_fb_pot = 0;	//unsinged
	public short elbow_fb_cur = 0;	//unsinged
	public short wrist_flap_fb = 0;	//unsinged
	public short wrist_rotate_fb = 0;	//unsinged
	public short gripper_fb = 0;	//unsinged
	// Gimbal info
	// Camera info
	// Battery info
	// IMU info
	// Misc info
	public synchronized short[] getWheels()
	{
		return wheels;
	}
	public synchronized short getValue(String variable)
	{
		switch(variable)
		{
		case "wheel1":
			return wheels[0];
		case "wheel2":
			return wheels[1];
		case "wheel3":
			return wheels[2];
		case "wheel4":
			return wheels[3];
		case "wheel5":
			return wheels[4];
		case "wheel6":
			return wheels[5];
		case "turret_pos":
			return turret_pos;
		case "shoulder_pos":
			return shoulder_pos;
		case "elbow_pos":
			return elbow_pos;
		case "wrist_flap_pos":
			return wrist_flap_pos;
		case "wrist_rotate_pos":
			return wrist_rotate_pos;
		case "gripper_speed":
			return gripper_speed;
		case "rotator_speed":
			return rotator_speed;
		case "shoulder_fb_pot":
			return shoulder_fb_pot;
		case "shoulder_fb_cur":
			return shoulder_fb_cur;
		case "elbow_fb_pot":
			return elbow_fb_pot;
		case "elbow_fb_cur":
			return elbow_fb_cur;
		case "wrist_flap_fb":
			return wrist_flap_fb;
		case "wrist_rotate_fb":
			return wrist_rotate_fb;
		case "gripper_fb":
			return gripper_fb;
		/*case "":
			return ;*/
		default:
			System.err.println("invalid input to rover.getValue()");
			return 0;
		}
	}
	
	public synchronized void setValue(String variable, short data)
	{
		switch(variable)
		{
		case "wheel1":
			wheels[0] = data;
			break;
		case "wheel2":
			wheels[1] = data;
			break;
		case "wheel3":
			wheels[2] = data;
			break;
		case "wheel4":
			wheels[3] = data;
			break;
		case "wheel5":
			wheels[4] = data;
			break;
		case "wheel6":
			wheels[5] = data;
			break;
		case "turret_pos":
			turret_pos = data;
			break;
		case "shoulder_pos":
			shoulder_pos = data;
			break;
		case "elbow_pos":
			elbow_pos = data;
			break;
		case "wrist_flap_pos":
			wrist_flap_pos = data;
			break;
		case "wrist_rotate_pos":
			wrist_rotate_pos = data;
			break;
		case "gripper_speed":
			gripper_speed = data;
			break;
		case "rotator_speed":
			rotator_speed = data;
			break;
		case "shoulder_fb_pot":
			shoulder_fb_pot = data;
			break;
		case "shoulder_fb_cur":
			shoulder_fb_cur = data;
			break;
		case "elbow_fb_pot":
			elbow_fb_pot = data;
			break;
		case "elbow_fb_cur":
			elbow_fb_cur = data;
			break;
		case "wrist_flap_fb":
			wrist_flap_fb = data;
			break;
		case "wrist_rotate_fb":
			wrist_rotate_fb = data;
			break;
		case "gripper_fb":
			gripper_fb = data;
			break;
		/*case "":
			return ;*/
		default:
			System.err.println("invalid input to rover.getValue()");
			break;
		}
	}
	
}
