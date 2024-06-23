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
            counter=counter+1
            data = self.conn.recv(9)
            if not data:
                x=bytearray(2)
                y=0x08
                x[0]=y
                listiner.onMessage(x)
                break
            data = listiner.onMessage(data)
            self.conn.sendall(data)
            
            
            
            


