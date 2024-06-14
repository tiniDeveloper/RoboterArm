import time
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)

#For A4988 Stepper Driver
#
class Joint:
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

    
    def setnull(self):
        self.step = 0;
    
    def getangle(self): #berechnen des aktuellen Winkels
        return ((360 / 4096) * self.step)
 
    def setangle(self, angle): #Winkel einstellen
        self.set_step = angle * (4096 / 360)
        
    def getsetangle(self): #berechnen des angestrebten Winkels
        return round(((360 / 4096) * self.set_step),3)
    
    def setspeed(self, speed):
        if speed <= 100 and speed > 0:
            self.current_delay = 1 / ((speed / 100) * (1 / 0.0005))
            self.speed_end = speed
        else:
            self.current_delay = 1 / ((20 / 100) * (1 / 0.0005))
            self.speed_end = 20    
    
    def enable(self,enable):
        if enable == True:
            GPIO.output(self.pin_en, 0)
        else:
            GPIO.output(self.pin_en, 1)
    
    def curve(self, end_step, current_step):
            if current_step < end_step:
                current_speed = ((self.speed_end-self.speed_end/2)/end_step)*current_step+(self.speed_end/5)
                return current_speed
            else:
                return self.speed_end
        
    
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
                
                            
# try:
#     joint1 = joint(17,27,22,0,50)
#     joint1.enable(True) #alle enables zusammengeführt
# 
#     joint1.setangle(-30)
#     joint1.start()
#     
#     joint2 = joint(24,23,22,0,50)
#     joint2.enable(True)
#     
#     joint2.setangle(-40)
#     joint2.start()
#     
#     joint3 = joint(6,5,22,0,50)
#     joint3.enable(True)
#     
#     joint3.setangle(-40)
#     joint3.start()
#      
#     joint4 = joint(26,16,22,0,50)
#     joint4.enable(True)
#     
#     joint4.setangle(-40)
#     joint4.start()
#     
#     time.sleep(2)
#     
#     joint1.setangle(0)
#     joint1.start()
#         
#     joint2.setangle(0)
#     joint2.start()
#     
#     joint3.setangle(0)
#     joint3.start()
#     
#     joint4.setangle(0)
#     joint4.start()
#         
# finally:
#     GPIO.cleanup()