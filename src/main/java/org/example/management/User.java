package org.example.management;

import org.example.exceptions.IllegalTransactionArgumentException;
import org.example.utils.InputHelper;

import static org.example.utils.Constants.BLOCKCHAIN_SIZE;
import static org.example.utils.Constants.NO_MESSAGES_BLOCKS_COUNT;
import static org.example.utils.InputHelper.getMessage;

public class User extends MoneyHandler {

    public User(String name, BlockChain blockChain) {
        super(name, blockChain);
    }

    @Override
    public void run() {
        while (blockChain.getSize() < BLOCKCHAIN_SIZE) {
            try {
                String userInput = getMessage();
                if (userInput.equalsIgnoreCase("balance")) {
                    System.out.printf("\n User %s has %d coins\n", nickName, getMoneyHeld());
                } else {
                    createTransaction(userInput.split(" "));
                }
            } catch (IllegalTransactionArgumentException itae) {
                System.err.println(itae.getMessage());
            }
        }
    }

}
