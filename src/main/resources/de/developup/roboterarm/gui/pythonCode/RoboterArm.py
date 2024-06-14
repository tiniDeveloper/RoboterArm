from Steuerung import Joint
import math
import threading
import RPi.GPIO as GPIO


class RobotArm:
    def __init__(self, motor1Pins, motor2Pins, motor3Pins, motor4Pins,magnetPin, listiner):
        self.joint1=Joint(pin_dir=motor1Pins[0], pin_step=motor1Pins[1], pin_en=motor1Pins[2], step=0, speed_end=35,listiner=listiner,jNum=1);
        self.joint2=Joint(pin_dir=motor2Pins[0], pin_step=motor2Pins[1], pin_en=motor2Pins[2], step=2048, speed_end=38,listiner=listiner,jNum=2);
        self.joint3=Joint(pin_dir=motor3Pins[0], pin_step=motor3Pins[1], pin_en=motor3Pins[2], step=2048, speed_end=38,listiner=listiner,jNum=3);
        self.joint4=Joint(pin_dir=motor4Pins[0], pin_step=motor4Pins[1], pin_en=motor4Pins[2], step=2048, speed_end=57,listiner=listiner,jNum=4);
        self.magnetPin=magnetPin
   
    def inversChen(self,r,h):
        link_2 = 90
        link_1 = 80
        magnet_offset= 65-34 #link3=65
        hypotenuse = math.sqrt(r**2 + (h - magnet_offset)**2)
        delta_rad = math.asin((h - magnet_offset) / hypotenuse)
        delta_degree = math.degrees(delta_rad)
        epsilon = math.asin(r / hypotenuse)

        joint1_rad_alpha = math.acos((link_2**2 - link_1**2 - hypotenuse**2) / (-2 * link_1 * hypotenuse))
        joint1_rad = joint1_rad_alpha + delta_rad

        joint1_degree_alpha = math.degrees(joint1_rad_alpha)
        joint1_degree = math.degrees(joint1_rad)

        joint3_rad_gamma = math.acos((link_1**2 - link_2**2 - hypotenuse**2) / (-2 * link_2 * hypotenuse))
        joint3_degree_gamma = math.degrees(joint3_rad_gamma)

        if h < magnet_offset:
            joint3_degree = joint3_degree_gamma + 90 - delta_degree
        else:
            joint3_rad = joint3_rad_gamma + epsilon
            joint3_degree = math.degrees(joint3_rad)

        joint2_degree = 180 - joint1_degree_alpha - joint3_degree_gamma
        return (joint1_degree, joint2_degree, joint3_degree)
    
    def moveToJoint(self, joint, angle):
        joint.setangle(angle)
        joint.enable(True)
        joint.start()

    def moveToPos(self, phi, r, h):
        print("moving")
        angles=self.inversChen(r,h)
        
        thread1 = threading.Thread(target=self.moveToJoint, args=(self.joint1, phi))
        thread2 = threading.Thread(target=self.moveToJoint, args=(self.joint2, angles[0]+90))
        thread3 = threading.Thread(target=self.moveToJoint, args=(self.joint3, angles[1]))
        thread4 = threading.Thread(target=self.moveToJoint, args=(self.joint4, angles[2]))
        
        # Starte die Threads
        thread1.start()
        thread2.start()
        thread3.start()
        thread4.start()
        # Warte darauf, dass alle Threads beendet sind
        thread1.join()
        thread2.join()
        thread3.join()
        thread4.join()
    
    def goHome(self):
        self.magnetAktiv(0)
        thread1 = threading.Thread(target=self.moveToJoint, args=(self.joint1, 0))
        thread2 = threading.Thread(target=self.moveToJoint, args=(self.joint2, 180))
        thread3 = threading.Thread(target=self.moveToJoint, args=(self.joint3, 180))
        thread4 = threading.Thread(target=self.moveToJoint, args=(self.joint4, 180))
        
        # Starte die Threads
        thread1.start()
        thread2.start()
        thread3.start()
        thread4.start()
        # Warte darauf, dass alle Threads beendet sind
        thread1.join()
        thread2.join()
        thread3.join()
        thread4.join()
    def magnetAktiv(self,state):
        GPIO.setup(self.magnetPin,GPIO.OUT)
        GPIO.output(self.magnetPin, state) 