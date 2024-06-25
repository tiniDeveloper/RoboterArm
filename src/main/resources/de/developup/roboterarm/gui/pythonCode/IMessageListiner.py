from abc import ABC, abstractmethod


"""
    Klasse für ankommende Socket-Nachrichten
"""
class ISocketMessageListener(ABC):
    """
        Abstrakte Methode mit Aufruf bei Socket-Nachrichten
    """
    @abstractmethod
    def onMessage(self):
        pass
