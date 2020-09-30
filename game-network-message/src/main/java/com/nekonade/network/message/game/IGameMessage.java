package com.nekonade.network.message.game;

public interface IGameMessage {
    
   GameMessageHeader getHeader();
   GameMessageHeader header();
   void setHeader(GameMessageHeader header);
   
    void read(byte[] body);
    byte[] write();
    
}
