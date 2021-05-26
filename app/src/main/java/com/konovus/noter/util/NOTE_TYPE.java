package com.konovus.noter.util;

public enum NOTE_TYPE {
    MEMO("MEMO"),
    JOURNAL("JOURNAL"),
    VAULT("VAULT"),
    DRAFT("DRAFT"),
    TRASH_MEMO("TRASH_MEMO"),
    TRASH_JOURNAL("TRASH_JOURNAL");

    private final String text;

    NOTE_TYPE(final String text) {
        this.text = text;
    }

//    @Override
//    public String toString() {
//        return text;
//    }
}
