package hr.fer.zemris.java.custom.scripting.exec;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

public class ObjectMultistack {

    private final Map<String, MultistackEntry> stack;

    public ObjectMultistack() {
        stack = new HashMap<>();
    }

    public void push(String keyName, ValueWrapper valueWrapper) {
        MultistackEntry entry = stack.get(keyName);
        stack.put(keyName, new MultistackEntry(valueWrapper, entry));
    }

    public ValueWrapper pop(String keyName) {
        MultistackEntry entry = stack.get(keyName);
        if (entry == null) {
            throw new EmptyStackException();
        }

        stack.put(keyName, entry.next);
        return entry.valueWrapper;
    }

    public ValueWrapper peek(String keyName) {
        MultistackEntry entry = stack.get(keyName);
        if (entry == null) {
            throw new EmptyStackException();
        }

        return entry.valueWrapper;
    }

    public boolean isEmpty(String keyName) {
        return !stack.containsKey(keyName);
    }

    private static class MultistackEntry {
        private final ValueWrapper valueWrapper;
        private final MultistackEntry next;

        public MultistackEntry(ValueWrapper valueWrapper, MultistackEntry next) {
            this.valueWrapper = valueWrapper;
            this.next = next;
        }
    }
}
