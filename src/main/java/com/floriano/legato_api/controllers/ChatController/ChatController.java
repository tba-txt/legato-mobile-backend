package com.floriano.legato_api.controllers.ChatController;

import com.floriano.legato_api.dto.ChatDTO.ChatMessageDTO;
import com.floriano.legato_api.dto.ChatDTO.ChatMessageRequestDTO;
import com.floriano.legato_api.dto.ChatDTO.ChatSummaryDTO;
import com.floriano.legato_api.dto.ChatDTO.MessageStatusUpdateDTO;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    public void sendPrivateMessage(@Payload ChatMessageRequestDTO dto,
                                   SimpMessageHeaderAccessor headerAccessor,
                                   Principal principal) {
        
        logger.info("MENSAGEM RECEBIDA VIA WEBSOCKET! DTO: {}", dto);

        if (dto.receiverId() == null) {
            logger.error("ERRO FATAL: O receiverId chegou NULO do Front-end! Ignorando processamento.");
            return; 
        }

        try {
            String fromEmail = principal.getName();
            User sender = userService.findByEmail(fromEmail);
            User receiver = userService.findById(dto.receiverId());

            if (sender == null || receiver == null) {
                logger.error("Remetente ({}) ou destinatário (ID: {}) inválido.", fromEmail, dto.receiverId());
                return;
            }

            if (sender.getBlockedUsers().contains(receiver) || receiver.getBlockedUsers().contains(sender)) {
                logger.warn("Bloqueio ativo entre {} e {}", sender.getId(), receiver.getId());
                return; 
            }

            Chat chat = chatService.getOrCreateChatBetween(sender, receiver);

            ChatMessage message = new ChatMessage();
            message.setChat(chat);
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(dto.content());
            message.setTimestamp(LocalDateTime.now());
            message.setStatus(MessageStatus.SENT);
            message.setTypeMedia(dto.typeMedia() != null ? dto.typeMedia() : TypeMedia.NONE);
            message.setMediaUrl(dto.mediaUrl());

            if (dto.repliedMessageId() != null) {
                ChatMessage originalMessage = chatMessageService.findById(dto.repliedMessageId());
                message.setRepliedMessage(originalMessage);
            }

            ChatMessage saved = chatMessageService.saveMessage(message);
            chat.addMessage(saved);
            chatService.saveChat(chat);

            logger.info("SUCESSO: Mensagem salva no chat {}: {}", chat.getId(), saved.getContent());

            messagingTemplate.convertAndSend(
                    "/topic/chats/" + chat.getId() + "/messages",
                    ChatMessageDTO.from(saved)
            );

        } catch (Exception e) {
            logger.error("Erro fatal ao processar mensagem do WS: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/chat/{chatId}/typing")
    public void processTypingStatus(@DestinationVariable Long chatId, @Payload TypingDTO typingDTO) {
        messagingTemplate.convertAndSend("/topic/chats/" + chatId + "/typing", typingDTO);
    }

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
                    lastMessage != null ? lastMessage.getTimestamp() : null,
                    otherUser.getIsOnline(),
                    otherUser.getLastSeen()
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
            broadcastReadToSenders(chatId, currentUser.getId());

            List<ChatMessageDTO> messages = chat.getMessages().stream()
                    .map(ChatMessageDTO::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(messages);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/chat/{chatId}/message/{messageId}/delivered")
    public void processDeliveredReceipt(@DestinationVariable Long chatId,
                                        @DestinationVariable Long messageId,
                                        Principal principal) {
        chatMessageService.markAsDelivered(messageId);

        ChatMessage message = chatMessageService.findById(messageId);
        if (message != null && message.getSender() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/users/" + message.getSender().getId() + "/messages/status",
                    new MessageStatusUpdateDTO(chatId, messageId, MessageStatus.DELIVERED, LocalDateTime.now())
            );
        }
    }

    @MessageMapping("/chat/{chatId}/read")
    public void processReadReceipt(@DestinationVariable Long chatId, Principal principal) {
        if (principal == null) {
            logger.warn("processReadReceipt: principal nulo para chatId {}", chatId);
            return;
        }
        User reader = userService.findByEmail(principal.getName());
        if (reader == null) {
            logger.warn("processReadReceipt: usuário não encontrado para email {}", principal.getName());
            return;
        }

        logger.info("processReadReceipt: marcando mensagens como lidas no chat {} pelo usuário {}", chatId, reader.getId());
        chatMessageService.markMessagesAsRead(chatId, reader.getId());
        broadcastReadToSenders(chatId, reader.getId());
    }

    private void broadcastReadToSenders(Long chatId, Long readerId) {
        Chat chat = chatService.getChatByIdWithParticipants(chatId);
        chat.getParticipants().stream()
                .filter(p -> !p.getId().equals(readerId))
                .map(User::getId)
                .findFirst()
                .ifPresentOrElse(
                        senderId -> {
                            logger.info("broadcastReadToSenders: enviando READ do chat {} para o usuário {}", chatId, senderId);
                            messagingTemplate.convertAndSend(
                                    "/topic/users/" + senderId + "/messages/status",
                                    new MessageStatusUpdateDTO(chatId, null, MessageStatus.READ, LocalDateTime.now())
                            );
                        },
                        () -> logger.warn("broadcastReadToSenders: nenhum outro participante encontrado no chat {}", chatId)
                );
    }

    @PostMapping(value = "/{chatId}/messages/media", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadMediaMessage(
            @PathVariable Long chatId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "audioType", required = false) String audioType,
            Principal principal) {
        
        try {
            String fromEmail = principal.getName();
            User sender = userService.findByEmail(fromEmail);
            Chat chat = chatService.getChatById(chatId);

            if (sender == null || chat == null) {
                return ResponseEntity.badRequest().body("Usuário ou Chat inválido.");
            }

            boolean isParticipant = chat.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(sender.getId()));
            if (!isParticipant) {
                return ResponseEntity.status(403).build();
            }

            User receiver = chat.getParticipants().stream()
                    .filter(p -> !p.getId().equals(sender.getId()))
                    .findFirst()
                    .orElse(null);

            String folderName = "legato/chats/chat_" + chatId; 
            String fileUrl = cloudinaryService.uploadFile(file, folderName);

            TypeMedia typeMedia = DetermineMediaType.determineMediaType(file);
            if (typeMedia == null) typeMedia = TypeMedia.NONE;

            String content = switch (typeMedia) {
                case AUDIO -> "audio_file".equals(audioType) ? "Arquivo de áudio" : "Mensagem de voz";
                case IMAGE -> "Imagem";
                case VIDEO -> "Vídeo";
                case FILE  -> file.getOriginalFilename() != null ? file.getOriginalFilename() : "Arquivo";
                default    -> "Arquivo de mídia";
            };

            ChatMessage message = ChatMessage.builder()
                    .chat(chat)
                    .sender(sender)
                    .receiver(receiver)
                    .typeMedia(typeMedia)
                    .mediaUrl(fileUrl)
                    .audioType(typeMedia == TypeMedia.AUDIO ? audioType : null)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .status(MessageStatus.SENT)
                    .build();

            ChatMessage saved = chatMessageService.saveMessage(message);
            chat.addMessage(saved);
            chatService.saveChat(chat);

            // 🚀 SOLUÇÃO APLICADA AQUI TAMBÉM: Mídia enviada para a sala
            messagingTemplate.convertAndSend(
                    "/topic/chats/" + chat.getId() + "/messages",
                    ChatMessageDTO.from(saved)
            );

            return ResponseEntity.ok(ChatMessageDTO.from(saved));

        } catch (Exception e) {
            logger.error("Erro ao enviar mídia no chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro no upload: " + e.getMessage());
        }
    }

    @GetMapping("/{chatId}/messages/{messageId}/download-url")
    public ResponseEntity<?> getDownloadUrl(
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            Chat chat = chatService.getChatById(chatId);

            if (user == null || chat == null) return ResponseEntity.badRequest().build();

            boolean isParticipant = chat.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(user.getId()));
            if (!isParticipant) return ResponseEntity.status(403).build();

            ChatMessage message = chatMessageService.findById(messageId);
            if (message == null || message.getMediaUrl() == null)
                return ResponseEntity.notFound().build();

            String signedUrl = cloudinaryService.generateSignedDownloadUrl(message.getMediaUrl());
            return ResponseEntity.ok(Map.of("url", signedUrl));

        } catch (Exception e) {
            logger.error("Erro ao gerar URL de download: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao gerar URL de download");
        }
    }

    @GetMapping("/{chatId}/messages/{messageId}/download")
    public ResponseEntity<byte[]> downloadMedia(
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            Chat chat = chatService.getChatById(chatId);

            if (user == null || chat == null) return ResponseEntity.badRequest().build();

            boolean isParticipant = chat.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(user.getId()));
            if (!isParticipant) return ResponseEntity.status(403).build();

            ChatMessage message = chatMessageService.findById(messageId);
            if (message == null || message.getMediaUrl() == null)
                return ResponseEntity.notFound().build();

            byte[] fileBytes;
            if (message.getTypeMedia() == TypeMedia.FILE) {
                fileBytes = cloudinaryService.downloadFileDirectly(message.getMediaUrl());
            } else {
                fileBytes = cloudinaryService.downloadFileBytes(message.getMediaUrl());
            }

            String mediaUrl = message.getMediaUrl();
            String filename = mediaUrl != null
                    ? mediaUrl.substring(mediaUrl.lastIndexOf('/') + 1)
                    : "arquivo";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao fazer proxy de download: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}