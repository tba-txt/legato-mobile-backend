package com.floriano.legato_api.controllers.ChatController;

import com.floriano.legato_api.dto.ChatDTO.ChatMessageDTO;
import com.floriano.legato_api.dto.ChatDTO.ChatSummaryDTO;
import com.floriano.legato_api.dto.ChatDTO.TypingDTO;
import com.floriano.legato_api.model.Chat.Chat;
import com.floriano.legato_api.model.ChatMessage.ChatMessage;
import com.floriano.legato_api.model.ChatMessage.MessageStatus;
import com.floriano.legato_api.model.Post.TypeMedia;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.services.ChatService.ChatMessageService;
import com.floriano.legato_api.services.ChatService.ChatService;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import com.floriano.legato_api.services.PostService.Utils.DetermineMediaType;
import com.floriano.legato_api.services.UserSevice.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chats")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat", description = "Endpoints related to chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatService chatService,
                          ChatMessageService chatMessageService,
                          UserService userService,
                          CloudinaryService cloudinaryService) { 
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.chatMessageService = chatMessageService;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService; 
    }

    @Transactional
    @MessageMapping("/sendMessage")
    public void sendPrivateMessage(ChatMessage message,
                                   SimpMessageHeaderAccessor headerAccessor,
                                   Principal principal) {
        try {
            String fromEmail = principal.getName();
            User sender = userService.findByEmail(fromEmail);
            User receiver = userService.findById(message.getReceiver().getId());

            if (sender == null || receiver == null) {
                logger.error("Remetente ({}) ou destinatário inválido.", fromEmail);
                return;
            }

            Chat chat = chatService.getOrCreateChatBetween(sender, receiver);

            message.setChat(chat);
            message.setSender(sender);
            message.setTimestamp(LocalDateTime.now());

            ChatMessage saved = chatMessageService.saveMessage(message);
            chat.addMessage(saved);
            chatService.saveChat(chat);

            logger.info("Mensagem salva no chat {}: {}", chat.getId(), saved.getContent());

            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    ChatMessageDTO.from(saved)
            );

        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem: {}", e.getMessage(), e);
        }
    }

    // --- AQUI ESTÁ O CÓDIGO DO DIGITANDO ---
    @MessageMapping("/chat/{chatId}/typing")
    public void processTypingStatus(@DestinationVariable Long chatId, @Payload TypingDTO typingDTO) {
        // Envia o status de digitando para quem estiver inscrito no tópico deste chat
        messagingTemplate.convertAndSend("/topic/chats/" + chatId + "/typing", typingDTO);
    }
    // ---------------------------------------

    @GetMapping
    public ResponseEntity<List<ChatSummaryDTO>> getUserChats(Principal principal) {
        String userEmail = principal.getName();
        User currentUser = userService.findByEmail(userEmail);

        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Chat> userChats = chatService.getChatsByUser(currentUser);

        List<ChatSummaryDTO> chatSummaries = userChats.stream().map(chat -> {
            
            User otherUser = chat.getParticipants().stream()
                    .filter(participant -> !participant.getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(currentUser); 

            ChatMessage lastMessage = chat.getMessages().isEmpty() ? null : 
                                      chat.getMessages().get(chat.getMessages().size() - 1);

            return new ChatSummaryDTO(
                    chat.getId(),
                    otherUser.getId(),
                    otherUser.getUsername(),
                    otherUser.getProfilePicture(),
                    lastMessage != null ? lastMessage.getContent() : "",
                    lastMessage != null ? lastMessage.getTimestamp() : null
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(chatSummaries);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(@PathVariable Long chatId, Principal principal) {
        String userEmail = principal.getName();
        User currentUser = userService.findByEmail(userEmail);

        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Chat chat = chatService.getChatById(chatId);

            boolean isParticipant = chat.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(currentUser.getId()));

            if (!isParticipant) {
                return ResponseEntity.status(403).build();
            }

            chatMessageService.markMessagesAsRead(chatId, currentUser.getId());
            // -----------------------------------------------------------------------

            List<ChatMessageDTO> messages = chat.getMessages().stream()
                    .map(ChatMessageDTO::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(messages);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/chat/{chatId}/read")
    public void processReadReceipt(@DestinationVariable Long chatId, Principal principal) {
        String userEmail = principal.getName();
        User reader = userService.findByEmail(userEmail);

        if (reader != null) {
            // 1. Atualiza no banco que o 'reader' leu as mensagens do 'chatId'
            chatMessageService.markMessagesAsRead(chatId, reader.getId());

            // 2. Avisa o outro usuário (remetente original) que as mensagens foram lidas
            // O Front-end deve escutar esse tópico para pintar os checks de azul
            messagingTemplate.convertAndSend("/topic/chats/" + chatId + "/readReceipt", "READ");
        }
    }

    @Transactional
    @PostMapping(value = "/{chatId}/messages/media", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadMediaMessage(
            @PathVariable Long chatId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        
        try {
            String fromEmail = principal.getName();
            User sender = userService.findByEmail(fromEmail);
            Chat chat = chatService.getChatById(chatId);

            if (sender == null || chat == null) {
                return ResponseEntity.badRequest().body("Usuário ou Chat inválido.");
            }

            // Trava de segurança: garantir que quem está mandando pertence ao chat
            boolean isParticipant = chat.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(sender.getId()));
            if (!isParticipant) {
                return ResponseEntity.status(403).build();
            }

            User receiver = chat.getParticipants().stream()
                    .filter(p -> !p.getId().equals(sender.getId()))
                    .findFirst()
                    .orElse(null);

            // 1. Upload para o Cloudinary organizando por pasta do chat
            String folderName = "legato/chats/chat_" + chatId; 
            String fileUrl = cloudinaryService.uploadFile(file, folderName);

            // 2. Usa a sua classe utilitária que já existe para descobrir se é Áudio, Vídeo ou Imagem
            TypeMedia typeMedia = DetermineMediaType.determineMediaType(file);
            if (typeMedia == null) typeMedia = TypeMedia.NONE;

            // 3. Monta e salva a mensagem
            ChatMessage message = ChatMessage.builder()
                    .chat(chat)
                    .sender(sender)
                    .receiver(receiver)
                    .typeMedia(typeMedia)
                    .mediaUrl(fileUrl)
                    .content(typeMedia == TypeMedia.AUDIO ? "Mensagem de voz" : "Arquivo de mídia")
                    .timestamp(LocalDateTime.now())
                    .status(MessageStatus.SENT)
                    .build();

            ChatMessage saved = chatMessageService.saveMessage(message);
            chat.addMessage(saved);
            chatService.saveChat(chat);

            // 4. Avisa o destinatário via WebSocket em tempo real
            if (receiver != null) {
                messagingTemplate.convertAndSendToUser(
                        receiver.getEmail(),
                        "/queue/messages",
                        ChatMessageDTO.from(saved)
                );
            }

            // Retorna a mensagem processada pro remetente desenhar na tela
            return ResponseEntity.ok(ChatMessageDTO.from(saved));

        } catch (Exception e) {
            logger.error("Erro ao enviar mídia no chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro no upload: " + e.getMessage());
        }
    }
}

