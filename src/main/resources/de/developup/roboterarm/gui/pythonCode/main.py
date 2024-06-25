from Server import ServerPrvider
from IMessageListiner import ISocketMessageListener
import time
import threading
from RoboterArm import RobotArm
import math
import RPi.GPIO as GPIO
i=-1

"""
    Klasse für Umgang mit Socket-Nachrichten
"""
class MyMessageHandler(ISocketMessageListener):
    """
        Initialisierungsmethoden
    """
    def __init__(self):
        self.stoppVorgang=False
        self.currentAngles=bytearray(9)
        global robotArm
        
    """
       Methode zur Verarbeitung eingehender Daten und ausführen entsprechende Aktionen.

       Args:
       data: Die empfangenen Daten als Byte-Array.

       Returns:
       bytearray: Daten zur Antwort an den Client.
    """
        
    def onMessage(self, data):
        dataToSend=bytearray(9)
        
        print(data[0])
        if data[0]==0x03:# Vorgang stoppen
            self.stoppVorgang=True
            print("befehl")
            # stoppen
        elif data[0]==0x01:
            print("aktuelle daten senden")
            dataToSend[1]=self.currentAngles[1]
            dataToSend[2]=self.currentAngles[2]
            dataToSend[3]=self.currentAngles[3]
            dataToSend[4]=self.currentAngles[4]
            
            return self.setData(0x11,dataToSend)
            
        elif data[0]==0x02:
            #bewege zum Punkt
            print("Befehl: bewegen")
            (phi,r,h)=self.getData(data)
            dataToSend[1]=self.currentAngles[1]
            dataToSend[2]=self.currentAngles[2]
            dataToSend[3]=self.currentAngles[3]
            dataToSend[4]=self.currentAngles[4]
            
            #print(self.currentAngles[3])
            print (phi)
            print(r)
            print(h)
            robotArm.moveToPos(phi,r,h)
            
            return self.setData(0x11,dataToSend)
        elif data[0]==0x05:
            print("Joistic")

        elif data[0]==0x07:# Pick and place
            print("Pick and Place")
            global i
            i=2
            dataToSend[1]=self.currentAngles[1]
            dataToSend[2]=self.currentAngles[2]
            dataToSend[3]=self.currentAngles[3]
            dataToSend[4]=self.currentAngles[4]
            return self.setData(0x11,dataToSend)

        elif data[0]==0x08:# Hier disconnecten
            print("disconnect")
            robotArm.goHome();
            dataToSend[1]=self.currentAngles[1]
            dataToSend[2]=self.currentAngles[2]
            dataToSend[3]=self.currentAngles[3]
            dataToSend[4]=self.currentAngles[4]
            return self.setData(0x11,dataToSend)
        elif data[0]==0x09:# go Home 
            robotArm.goHome();
            dataToSend[1]=self.currentAngles[1]
            dataToSend[2]=self.currentAngles[2]
            dataToSend[3]=self.currentAngles[3]
            dataToSend[4]=self.currentAngles[4]
            return self.setData(0x11,dataToSend)
        else:
            print("Unknown Byte Message "+str(data[0]))
    
    """
        Methode zum erstellen eines Byte-Array mit zu übergebenen Befehls- und Datenwerten.

        Args:
        befehl: Der Befehlscode als Byte.
        data: Die zu übertragenen Daten.

        Returns:
        bytearray: Das Byte-Array mit dem Befehl und den gewünschten Daten.   
    """
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
    
    """
        Methode zur Erstellung eines Byte-Array mit den empfangenen Daten.

        Args:
        data: Die empfangenen Daten.

        Returns:
        bytearray: Das Byte-Array mit den empfangenen Daten.
    """
    def getData(self, data):
        phi=0
        r=0
        h=0
        if (data[1]&0xff)+((data[2]&0xff)<<8)> 32767:
            r= (data[1]&0xff)+((data[2]&0xff)<<8)-65536
        else:
            r=(data[1]&0xff)+((data[2]&0xff)<<8)
        
        if (data[3]&0xff)+((data[4]&0xff)<<8)> 32767:
            h= (data[3]&0xff)+((data[4]&0xff)<<8)-65536
        else:
            h=(data[3]&0xff)+((data[4]&0xff)<<8)
            
        if (data[5]&0xff)+((data[6]&0xff)<<8)> 32767:
            phi= (data[5]&0xff)+((data[6]&0xff)<<8)-65536
        else:
            phi=(data[5]&0xff)+((data[6]&0xff)<<8)
        return (phi,r,h)#incomeningData
    
    """
        Methode zur angabe der gewünschten Winkel einer vorgegebenen Achse.

        Args:
        angle: Der gewünschte Winkel.
        jNum: Die Nummer der Achse.
    """
    def setAngles(self,angle, jNum):
        nangle=round(angle)
        self.currentAngles[jNum]=nangle
        

"""
        Einstellungen
        
"""
motor1Pins = (17, 27,22)
motor2Pins = (24, 23,22)
motor3Pins = (6, 5,22)
motor4Pins = (26, 16,22)

listiner=MyMessageHandler()
sp=ServerPrvider( 9988) #Portnumber
thread1 = threading.Thread(target=sp.clientEinrichten,args=(listiner,))
thread1.start()
"""
        Initialisierung des Roboterarms mit den Motoren
"""
robotArm = RobotArm(motor1Pins, motor2Pins, motor3Pins,motor4Pins, 25,listiner) 
robotArm.magnetAktiv(1)

# Create a socket object

"""
    Main-Funktion
"""
def main():
    global listiner

    print("main Schleife")  

    # Initialisierung des Roboterarms mit den Motoren
    #robotArm = RobotArm(motor1Pins, motor2Pins, motor3Pins,motor4Pins) 
    global i

    while True:
        time.sleep(0.5)
        
#Pick and Place Demo
        if(i>0):
            robotArm.moveToPos(20,140,4)
            robotArm.magnetAktiv(1)
            time.sleep(0.5)
            
            robotArm.moveToPos(20,140,100)
            
            robotArm.moveToPos(-30,100,100)
            robotArm.moveToPos(-30,100,0)
            robotArm.magnetAktiv(0)
            time.sleep(0.5)
            
            robotArm.moveToPos(-30,100,100)
            robotArm.moveToPos(-30,100,0)
            robotArm.magnetAktiv(1)
            time.sleep(0.5)
            
            robotArm.moveToPos(-30,100,100)
            
            robotArm.moveToPos(20,140,100)
            
            robotArm.moveToPos(20,140,4)
            robotArm.magnetAktiv(0)
            time.sleep(0.5)
            
            robotArm.moveToPos(20,140,100)
            robotArm.magnetAktiv(0)
            i-=1
#
        elif(i==0):
            i=-1
            robotArm.goHome()
            GPIO.output(22, 1)# deaktiviere die Motoren
            
    
"""
    Aufruf der main() Funktion
"""   
if __name__ == "__main__":
    msg = main()

