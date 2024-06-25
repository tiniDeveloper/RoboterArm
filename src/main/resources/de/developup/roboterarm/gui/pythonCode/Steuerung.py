import time
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)

"""
    Klasse zur Definition und Steuerung der Achsen.
"""
class Joint:
    """
        Initialisierungsmethoden
    """
    def __init__(self, pin_dir, pin_step, pin_en, step, speed_end,listiner,jNum):
        self.jNum=jNum
        self.listiner=listiner
        self.step = step
        self.set_step = 0
        self.pin_dir = pin_dir
        self.pin_step = pin_step
        self.pin_en = pin_en
        self.current_speed = 0
        
        if speed_end <= 100 and speed_end > 0:
            self.speed_end = speed_end
            self.speed_start = speed_end/2
        else:
            self.speed_end = 20
            self.speed_start = 10
        
        GPIO.setup(self.pin_dir, GPIO.OUT)
        GPIO.output(self.pin_dir, 0)
        
        GPIO.setup(self.pin_step, GPIO.OUT)
        GPIO.output(self.pin_step, 0)

        GPIO.setup(self.pin_en, GPIO.OUT)
        GPIO.output(self.pin_en, 1)

    """
        Methode zum zurücksetzen des Stepcounter
    """
    def setnull(self):
        self.step = 0;

    """
        Methode zur Bestimmung des Aktuellen Winkels.
        
        Returns:
        float: Der aktuelle Winkel des Motors in Grad.
    """
    def getangle(self): #berechnen des aktuellen Winkels
        return ((360 / 4096) * self.step)
 
    """
        Methode zum Festlegen des anzustrebenen Winkels
        
        Args:
        angle: Angabe des gewünschten Winkels
    """
    def setangle(self, angle): #Winkel einstellen
        self.set_step = angle * (4096 / 360)

    """
        Methode zur Rückgabe des angestrebten Winkels
        
        Returns:
        float: Winkel welcher vom Nutzer angestrebt wurde.
    """
    def getsetangle(self): #berechnen des angestrebten Winkels
        return round(((360 / 4096) * self.set_step),3)
    
    """
        Methode zur Einstellung der Achsengeschwindigkeit in Prozent.
        
        Args:
        speed: Geschwindigkeit in Prozent.
    """
    def setspeed(self, speed):
        if speed <= 100 and speed > 0:
            self.current_delay = 1 / ((speed / 100) * (1 / 0.0005))
            self.speed_end = speed
        else:
            self.current_delay = 1 / ((20 / 100) * (1 / 0.0005))
            self.speed_end = 20    

    """
        Methode zum aktivieren / deaktivieren der Achsen
        
        Args:
        enable: Status der Achse (True: aktiv, False: inaktiv)
    """
    def enable(self,enable):
        if enable == True:
            GPIO.output(self.pin_en, 0)
        else:
            GPIO.output(self.pin_en, 1)
    """
        Methode zur Bestimmung der Beschleunigungskurve.
        
        Args:
        end_step: Schritte bis wann die maximale Geschwindigkeit erreicht werden soll. 
        current_step: Anzahl der bisherigen schritte.
        
        Returns:
        float: Durch die Kurve festgelegte Geschwindigkeit bzw. Endgeschwindigkeit.
    """
    def curve(self, end_step, current_step):
            if current_step < end_step:
                current_speed = ((self.speed_end-self.speed_end/2)/end_step)*current_step+(self.speed_end/5)
                return current_speed
            else:
                return self.speed_end
        
    """
        Methode zum starten der Achsenbewegung. 
    """
    def start(self):
        step_relativ = 0
        
        if self.step < self.set_step:
            GPIO.output(self.pin_dir, 1)
            while self.step <= self.set_step:
                self.step += 1
                step_relativ += 1
                curve = self.curve(200,step_relativ)
                #print(curve)
                self.current_delay = 1 / ((curve / 100.0) * (1 / 0.0005))
                GPIO.output(self.pin_step, 1)
                time.sleep(self.current_delay)
                GPIO.output(self.pin_step, 0)
                time.sleep(self.current_delay)
                self.listiner.setAngles(self.getangle(), self.jNum)
        
        else:
            GPIO.output(self.pin_dir, 0)
            while self.step >= self.set_step:
                self.step -= 1
                step_relativ += 1
                curve = self.curve(200,step_relativ)
                #print(curve)
                self.current_delay = 1 / ((curve / 100.0) * (1 / 0.0005))
                GPIO.output(self.pin_step, 1)
                time.sleep(self.current_delay)
                GPIO.output(self.pin_step, 0)
                time.sleep(self.current_delay)
                self.listiner.setAngles(self.getangle(), self.jNum)