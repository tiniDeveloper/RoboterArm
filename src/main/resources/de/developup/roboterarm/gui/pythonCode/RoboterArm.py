from Steuerung import Joint
import math
import threading
import RPi.GPIO as GPIO

"""
    Klasse zur Kontrolle und Einrichtung des Roboterarms
"""

class RobotArm:
    
    """
        Initialisierungsmethoden
    """
    def __init__(self, motor1Pins, motor2Pins, motor3Pins, motor4Pins,magnetPin, listiner):
        self.listiner=listiner
        self.joint1=Joint(pin_dir=motor1Pins[0], pin_step=motor1Pins[1], pin_en=motor1Pins[2], step=0,    speed_end=35,listiner=self.listiner,jNum=1);
        self.joint2=Joint(pin_dir=motor2Pins[0], pin_step=motor2Pins[1], pin_en=motor2Pins[2], step=2048, speed_end=38,listiner=self.listiner,jNum=2);
        self.joint3=Joint(pin_dir=motor3Pins[0], pin_step=motor3Pins[1], pin_en=motor3Pins[2], step=2048, speed_end=38,listiner=self.listiner,jNum=3);
        self.joint4=Joint(pin_dir=motor4Pins[0], pin_step=motor4Pins[1], pin_en=motor4Pins[2], step=2048, speed_end=57,listiner=self.listiner,jNum=4);
        self.magnetPin=magnetPin
    """
        Methode zur Berechnung der Inverse Kinematik aus Radius und Höhe.
        
        Args:
        r: Angabe des Radius.
        h: Angabe der Höhe.
        
        Returns:
        array: Mit den 3 Winkeln der Achsen des Roboterarms.
        
    """
    def inversChen(self,r,h):
        try:
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
        except Exception as e:
            print(f"Ein unerwarteter Fehler ist aufgetreten R: {e}")
            return "Nan"
        return (joint1_degree, joint2_degree, joint3_degree)
    
    """
        Methode zum Start der Bewegung einer Achse zum angegebenen Winkel.
        
        Args:
        joint: Ausgewählte Achse.
        angle: Der gewünschte Winkel.
    """
    def moveToJoint(self, joint, angle):
        joint.setangle(angle)
        joint.enable(True)
        joint.start()
    """
        Methode zur Angabe der anzusteuernden Position des Roboterarms.
        
        Args:
        phi: Der Wert für die anzusteuernde Drehung.
        r: Die Angabe des gewünschten Radius.
        h: Die gewünschte Höhe.
    """
    def moveToPos(self, phi, r, h):
        
        angles=self.inversChen(r,h)
        if angles != "Nan":
            print("moving")
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
            return "OK"
        else:
            dataToSend=bytearray(9)
            print("fehler in der Berechnung") 
            dataToSend[0]=0x08
            dataToSend[1]=0x02
            return dataToSend
        
        
    """
        Methode zum zurückfahren in Grundeinstellung
    """
    def goHome(self):
        print("Homing")
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
    
    """
        Methode zum Ansteuern des Elektromagneten
        
        Args:
        state: Einzustellender Zustand de Magneten.
    """
    def magnetAktiv(self,state):
        GPIO.setup(self.magnetPin,GPIO.OUT)
        GPIO.output(self.magnetPin, state) 
