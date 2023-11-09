package org.example.management;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.utils.Constants.*;
import static org.example.utils.Encryption.*;

public class Block implements Serializable {
    private boolean initialized = false;
    private long id;
    private long timeStamp;
    private long magicNumber;
    private String previousHash;
    private TreeSet<Transaction> messages
            = new TreeSet<>(Comparator.comparingLong(Transaction::getId));
    private String hash;

    private Block next;

    public void init(Block prev, List<Transaction> lastTransactions) {
        if (initialized) return;

        if(prev == null) {
            this.id = 1;
            this.previousHash = ZERO;
        } else {
            this.id = prev.id + 1;
            this.previousHash = prev.hash();
        }

        setMessages(lastTransactions);
        this.timeStamp = new Date().getTime();
        this.magicNumber = getRandomNumber(Long.MAX_VALUE);
        this.hash = detrmineHash();
        this.initialized = true;
    }

    public String hash() {
        return hash;
    }

    public boolean isProved(int zerosQuantityRequired) {
        String patternString = String.format("%s{%d}[^%s]{1}.{%d}",
                ZERO, zerosQuantityRequired, ZERO, SHA_LENGTH - zerosQuantityRequired - 1);
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(hash());
        return matcher.matches();
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public boolean checkMessages() {
        getMessages().forEach(transaction -> decrypt(messages, transaction));
        return messages.isEmpty();
    }

    public TreeSet<Transaction> getMessages() {
        return (TreeSet<Transaction>) messages.clone();
    }

    private void setMessages(List<Transaction> messages) {
        this.messages.addAll(messages);
    }

    long getId() {
        return id;
    }

    long getTimeStamp() {
        return timeStamp;
    }

    long getMagicNumber() {
        return magicNumber;
    }

    Block getNext() {
        return next;
    }

    void setNext(Block next) {
        if (this.next != null) return;
        this.next = next;
    }

    private String detrmineHash() {
        StringBuilder hashInput = new StringBuilder(id + timeStamp + magicNumber + previousHash);
        for (Transaction message : messages) {
            hashInput.append(message);
        }
        return applySHA256(hashInput.toString());
    }
}
