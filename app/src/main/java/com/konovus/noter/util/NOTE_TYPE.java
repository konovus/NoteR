package com.konovus.noter.util;

public enum NOTE_TYPE {
    MEMO("memo"),
    JOURNAL("journal"),
    VAULT("vault"),
    DRAFT("draft"),
    TRASH("trash");

    private final String text;

    NOTE_TYPE(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
