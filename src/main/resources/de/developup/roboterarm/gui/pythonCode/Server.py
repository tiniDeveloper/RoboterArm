import socket
import threading
import time

"""
    Klasse zur Bereitstellung des Servers und Verwaltung eingehender Client-Verbindungen
"""
        
class ServerPrvider:
    """
        Initialisierungsmethoden
    """
    def __init__(self, port):
        self.port=port
    """
        Methode zum Einrichten eines Clients und Starten eines neuen Threads für jeden verbundenen Client
        
        Args:
        listiner: Instanz des Nachricht-Listeners, der Nachrichten verarbeitet
    """
    def clientEinrichten(self, listiner):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind(('', self.port))
            s.listen()
            counter=0
            while True:
                conn, addr = s.accept()
                counter+=1
                client =InitClient(conn)
                thread1 = threading.Thread(target=client.startCkient,args=(listiner,))
                thread1.start()
                print(f"Connected by {addr}")
   
        
"""
    Klasse zur Verwaltung der Client-Verbindung und Kommunikation
"""      
class InitClient:
    data=bytearray(9)
    """
        Initialisierungsmethoden
    """
    def __init__(self, conn):
        self.conn= conn
    """
        Methode zum Starten der Client-Kommunikation.
        
        Args:
        listiner: Listener welcher die Nachrichten verarbeitet
    """
    def startCkient(self, listiner):
        counter=0
        while True:
            try:
                counter=counter+1
                data = self.conn.recv(9)
                data = listiner.onMessage(data)
                self.conn.sendall(data)
            except ConnectionResetError:
                x=bytearray(9)
                y=0x08
                x[0]=y
                y=0x01
                x[1]= y
                listiner.onMessage(x)
                print("Die Verbindung wurde vom Peer zurückgesetzt.")
                break
            except TimeoutError:
                x=bytearray(9)
                y=0x08
                x[0]=y
                y=0x01
                x[1]= y
                listiner.onMessage(x)
                print("Die Verbindung ist abgelaufen.")
                break
            except socket.error as e:
                x=bytearray(9)
                y=0x08
                x[0]=y
                y=0x01
                x[1]= y
                listiner.onMessage(x)
                print(f"Ein Socket-Fehler ist aufgetreten: {e}")
                break
            except Exception as e:
                x=bytearray(9)
                y=0x08
                x[0]=y
                y=0x01
                x[1]= y
                listiner.onMessage(x)
                print(f"Ein unerwarteter Fehler ist aufgetreten S: {e}")
                break 
            

