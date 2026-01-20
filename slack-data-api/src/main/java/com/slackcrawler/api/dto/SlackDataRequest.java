package com.slackcrawler.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SlackDataRequest {
    @JsonProperty("crawledAt")
    private String crawledAt;
    
    @JsonProperty("channels")
    private List<ChannelData> channels;

    public String getCrawledAt() {
        return crawledAt;
    }

    public void setCrawledAt(String crawledAt) {
        this.crawledAt = crawledAt;
    }

    public List<ChannelData> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelData> channels) {
        this.channels = channels;
    }

    public static class ChannelData {
        @JsonProperty("channelId")
        private String channelId;
        
        @JsonProperty("channelName")
        private String channelName;
        
        @JsonProperty("isPrivate")
        private Boolean isPrivate;
        
        @JsonProperty("created")
        private Long created;
        
        @JsonProperty("numMembers")
        private Integer numMembers;
        
        @JsonProperty("messages")
        private List<MessageData> messages;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public Boolean getIsPrivate() {
            return isPrivate;
        }

        public void setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public Long getCreated() {
            return created;
        }

        public void setCreated(Long created) {
            this.created = created;
        }

        public Integer getNumMembers() {
            return numMembers;
        }

        public void setNumMembers(Integer numMembers) {
            this.numMembers = numMembers;
        }

        public List<MessageData> getMessages() {
            return messages;
        }

        public void setMessages(List<MessageData> messages) {
            this.messages = messages;
        }
    }

    public static class MessageData {
        @JsonProperty("ts")
        private String ts;
        
        @JsonProperty("user")
        private String user;
        
        @JsonProperty("text")
        private String text;
        
        @JsonProperty("subtype")
        private String subtype;
        
        @JsonProperty("threadTs")
        private String threadTs;
        
        @JsonProperty("reactions")
        private List<Object> reactions;
        
        @JsonProperty("files")
        private List<Object> files;

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSubtype() {
            return subtype;
        }

        public void setSubtype(String subtype) {
            this.subtype = subtype;
        }

        public String getThreadTs() {
            return threadTs;
        }

        public void setThreadTs(String threadTs) {
            this.threadTs = threadTs;
        }

        public List<Object> getReactions() {
            return reactions;
        }

        public void setReactions(List<Object> reactions) {
            this.reactions = reactions;
        }

        public List<Object> getFiles() {
            return files;
        }

        public void setFiles(List<Object> files) {
            this.files = files;
        }
    }
}

