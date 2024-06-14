import socket
import threading
import time

# Create a socket object
        
class ServerPrvider:
    def __init__(self, port):
        self.port=port
        
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
   
        
        
class InitClient:
    data=bytearray(9)
    def __init__(self, conn):
        self.conn= conn
    def startCkient(self, listiner):
        counter=0
        while True:
            counter=counter+1
            data = self.conn.recv(9)
            if not data:
                continue
            data = listiner.onMessage(data)
            self.conn.sendall(data)
