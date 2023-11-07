package org.example.management;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.utils.Constants.*;
import static org.example.utils.Encryption.*;

public class Block implements Serializable {
    private boolean initialized;
    private long id;
    private long timeStamp;
    private long magicNumber;
    private String previousHash;
    private TreeSet<Transaction> messages
            = new TreeSet<>(Comparator.comparingLong(Transaction::getId));

    private Block next;

    public Block() {
    }

    public void init(Block prev) {
        if (this.initialized) return;

        if(prev == null) {
            this.id = 1;
            this.previousHash = ZERO;
        } else {
            this.id = prev.id + 1;
            this.previousHash = prev.hash();
        }

        this.timeStamp = new Date().getTime();
        this.magicNumber = getRandomNumber(Long.MAX_VALUE);
    }

    public String hash() {
        StringBuilder hashInput = new StringBuilder(id + timeStamp + magicNumber + previousHash);
        messages.forEach(hashInput::append);
        return applySHA256(hashInput.toString());
    }

    boolean isProved(int zerosQuantityRequired) {
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

    public void setMessages(List<Transaction> messages) {
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

    boolean tryFixMessages() {
        if (isValid(this)) return true;

        Stack<Transaction> transactionStack = new Stack<>();
        messages.forEach(transactionStack::push);
        while (!transactionStack.isEmpty()) {
            Transaction last = transactionStack.pop();
            messages.remove(last);
            if (isValid(this)) return true;
        }
        return false;
    }

    Block getNext() {
        return next;
    }

    void setNext(Block next) {
        if (this.next != null) return;
        this.next = next;
    }

    void removeNext() {
        if (isValid(this.next)) return;
        this.next = this.next.next;
        restructureNext(this.next);
    }

    private static void restructureNext(Block restructured) {
        if (restructured.next == null) return;

        restructured.next.previousHash = restructured.hash();
        restructureNext(restructured.next);
    }

    private static boolean isValid(Block checked) {
        if (checked == null || checked.next == null) return true;
        return checked.next.previousHash.equals(checked.hash());
    }
}
