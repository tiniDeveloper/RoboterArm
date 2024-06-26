from abc import ABC, abstractmethod

class ISocketMessageListener(ABC):       
    
    @abstractmethod
    def onMessage(self):
        pass
    
    #@abstractmethod
    #def move(self):
    #    pass
