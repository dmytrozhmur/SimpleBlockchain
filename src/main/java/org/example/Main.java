package org.example;

import org.example.management.BlockChain;
import org.example.management.Miner;
import org.example.management.MoneyHandler;
import org.example.management.User;
import org.example.utils.InputHelper;

import java.util.ArrayList;
import java.util.List;

import static org.example.utils.Constants.SECOND_TO_MILLIS;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BlockChain blockChain = new BlockChain();
        List<User> users = List.of(
                new User("Dmytro", blockChain));
        List<MoneyHandler> moneyHandlers = new ArrayList<>(users);

        for (int i = 0; i < (Runtime.getRuntime().availableProcessors() - 2); i++) {
            moneyHandlers.add(new Miner(blockChain));
        }
        blockChain.setMoneyHandlers(moneyHandlers);

        moneyHandlers.forEach(Thread::start);
        Thread.sleep(SECOND_TO_MILLIS);

        for (MoneyHandler handler : moneyHandlers) {
            handler.join();
        }
        InputHelper.off();
    }
}
