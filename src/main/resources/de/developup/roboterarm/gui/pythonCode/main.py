from Server import ServerPrvider
from IMessageListiner import ISocketMessageListener
import time
import threading
from RoboterArm import RobotArm
import math
import RPi.GPIO as GPIO

#Elektromagnet
def magnetAktiv(pin,state):
    GPIO.setup(pin,GPIO.OUT)
    GPIO.output(pin, state) 

#  beim Stoppen der Server braucht FIN_WAIT2

class MyMessageHandler(ISocketMessageListener):
    def __init__(self):
        self.stoppVorgang=False
        self.currentAngles=bytearray(9)
        global robotArm
        
    def onMessage(self, data):
        dataToSend=bytearray(9)
        
        print(data[0])
        if data[0]==0x00:# Vorgang stoppen
            self.stoppVorgang=True
            print("befehl")
            # stoppen
        elif data[0]==0x01:
            print("aktuelle daten senden")
            dataToSend[1]=self.currentAngles[1]
            dataToSend[2]=self.currentAngles[2]
            dataToSend[3]=self.currentAngles[3]
            dataToSend[4]=self.currentAngles[4]
            
            return self.setData(0x12,dataToSend)
            
        elif data[0]==0x02:
            #bewege zum Punkt
            print("Befehl: bewegen")
            koo=self.getData(data)
            dataToSend[1]=22
            dataToSend[2]=55
            dataToSend[3]=44
            dataToSend[4]=22
            print(koo[1])
            
            print(koo[2])
            print(koo[3])
            robotArm.moveToPos(koo[3],koo[1],koo[2])
            #magnetAktiv(25,1)
            return self.setData(0x12,dataToSend)
            #angles=inversChen(koo[1],[2])
        elif data[0]==0x05:
            print("befehl")
            # Joistickmodus
            
        elif data[0]==0x09:# startet
            print("befehl")
            robotArm.goHome();
            return self.setData(0x12,dataToSend)
        else:
            print("Unknown Byte Message "+str(data[0]))

    def setData(self,befehl, data):
        dataToSend=bytearray(9)
        dataToSend[0]=befehl
            
        dataToSend[1]=( data[1] & 0xFF)# r-Werte
        dataToSend[2]=(( data[1] >> 8)&0xFF)
                           
        dataToSend[3]=( data[2] & 0xFF)# h-Werte
        dataToSend[4]=(( data[2] >> 8)&0xFF)
            
        dataToSend[5]=( data[3] & 0xFF)# phi
        dataToSend[6]=((data[3] >> 8)&0xFF)
                           
        dataToSend[7]=( data[4] & 0xFF)
        dataToSend[8]=((data[4] >> 8)&0xFF)
        return dataToSend
    def getData(self, data):
        incomeningData=bytearray(5)
        incomeningData[0]=0x11
        incomeningData[1]=(data[1]&0xff)+((data[2]&0xff)<<8)
        incomeningData[2]=(data[3]&0xff)+((data[4]&0xff)<<8)
        incomeningData[3]=(data[5]&0xff)+((data[6]&0xff)<<8)
        incomeningData[4]=(data[7]&0xff)+((data[8]&0xff)<<8)
        return incomeningData
    def setAngles(self,angle, jNum):
        nangle=int(angle)
        self.currentAngles[jNum]=(nangle & 0xFF)# r-Werte
        self.currentAngles[jNum]=((( nangle) >> 8)&0xFF)


motor1Pins = (17, 27,22)
motor2Pins = (24, 23,22)
motor3Pins = (6, 5,22)
motor4Pins = (26, 16,22)
magnetAktiv(25,0)

listiner=MyMessageHandler()
sp=ServerPrvider( 9988) #Portnumber
thread1 = threading.Thread(target=sp.clientEinrichten,args=(listiner,))
thread1.start()

# Initialisierung des Roboterarms mit den Motoren
robotArm = RobotArm(motor1Pins, motor2Pins, motor3Pins,motor4Pins, 25,listiner) 

# Create a socket object

def main():
    global listiner
    
    motor1Pins = (17, 27,22)
    motor2Pins = (24, 23,22)
    motor3Pins = (6, 5,22)
    motor4Pins = (26, 16,22)
    magnetAktiv(25,0)

    print("main Schleife")  

    # Initialisierung des Roboterarms mit den Motoren
    #robotArm = RobotArm(motor1Pins, motor2Pins, motor3Pins,motor4Pins) 
    i=0

    while True:
        time.sleep(0.5)
        
#Pick and Place Demo
#         if(i<2):
#             robotArm.moveToPos(20,140,4)
#             magnetAktiv(25,1)
#             time.sleep(0.5)
#             
#             robotArm.moveToPos(20,140,100)
#             
#             robotArm.moveToPos(-30,100,100)
#             robotArm.moveToPos(-30,100,0)
#             magnetAktiv(25,0)
#             time.sleep(0.5)
#             
#             robotArm.moveToPos(-30,100,100)
#             robotArm.moveToPos(-30,100,0)
#             magnetAktiv(25,1)
#             time.sleep(0.5)
#             
#             robotArm.moveToPos(-30,100,100)
#             
#             robotArm.moveToPos(20,140,100)
#             
#             robotArm.moveToPos(20,140,4)
#             magnetAktiv(25,0)
#             time.sleep(0.5)
#             
#             robotArm.moveToPos(20,140,100)
#             magnetAktiv(25,0)
#             i+=1
#
#         elif(i==2):
#             i+=1
#             robotArm.goHome()
#             GPIO.cleanup()
            
    
        
if __name__ == "__main__":
    msg = main()
