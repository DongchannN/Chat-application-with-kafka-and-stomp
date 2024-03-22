package com.DongchannN.kafkachattingapplication.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class MessageDto {


    private String roomId;
    private String sender;
    private String content;

    public MessageDto(String roomId, String sender, String content) {
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
    }

    @Getter
    static public class KafkaSavedMessage {
        private String sender;
        private String content;

        KafkaSavedMessage() { }
        public KafkaSavedMessage(String sender, String content) {
            this.sender = sender;
            this.content = content;
        }
    }

    public String makeKafkaSavedMsg() throws JsonProcessingException {
        ObjectMapper ob = new ObjectMapper();
        return ob.writeValueAsString(new KafkaSavedMessage(this.sender, this.content));
    }
}
