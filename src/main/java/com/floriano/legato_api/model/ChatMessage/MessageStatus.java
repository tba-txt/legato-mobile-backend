package com.floriano.legato_api.model.ChatMessage;

public enum MessageStatus {
    SENT,       // Mensagem saiu do remetente e chegou no servidor (1 check)
    DELIVERED,  // Chegou no celular do destinatário, mas ele não abriu o app (2 checks cinzas)
    READ        // Destinatário abriu a tela do chat (2 checks azuis)
}